package tk.greydynamics.Resource;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.*;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase.*;

//Imports for MyKernel32
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Pointer;

import java.util.ArrayList;

import com.sun.jna.Memory;

public class MemoryHelper {

	static Kernel32 kernel32 = (Kernel32) Native.loadLibrary(Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
	static MyKernel32 myKernel32 = (MyKernel32) Native.loadLibrary("kernel32", MyKernel32.class);
	private WinNT.HANDLE processHandle = null;
	private MEMORY_BASIC_INFORMATION mem_info = new MEMORY_BASIC_INFORMATION();
	private SYSTEM_INFO sys_info =  new SYSTEM_INFO();

	private long proc_min_address_l = -1;
	private long proc_max_address_l = -1;

	interface MyKernel32 extends StdCallLibrary {
		boolean WriteProcessMemory(WinNT.HANDLE hProcess, long address, Pointer buffer, int size, IntByReference written);

		boolean ReadProcessMemory(WinNT.HANDLE hProcess, long inBaseAddress, Pointer outputBuffer, int nSize,
				IntByReference outNumberOfBytesRead);

		int VirtualQueryEx(WinNT.HANDLE hProcess, Pointer lpMinimumApplicationAddress, MEMORY_BASIC_INFORMATION mem_info, int dwLength);

		void GetSystemInfo(SYSTEM_INFO lpSystemInfo);

		WinNT.HANDLE OpenProcess(int desired, boolean inherit, int pid);

		int GetLastError();
	}

	public MemoryHelper(WinNT.HANDLE processHandle) {
		this.processHandle = processHandle;
		myKernel32.GetSystemInfo(sys_info);
		proc_min_address_l = Pointer.nativeValue(sys_info.lpMinimumApplicationAddress);
		proc_max_address_l = Pointer.nativeValue(sys_info.lpMaximumApplicationAddress);
	}

	public ArrayList<Long> findByteArr(byte[] searchArr){
		ArrayList<Long> addresses = new ArrayList<Long>();
		boolean consoleMsg = false;
		while (proc_min_address_l < proc_max_address_l)
		{
			if (myKernel32.VirtualQueryEx(processHandle, sys_info.lpMinimumApplicationAddress, mem_info, mem_info.size())==0){
				System.err.println("VirtualQueryEx failed!");
			}
			//            if (mem_info.regionSize.longValue()>0){
			//            	System.out.println("Memory Region found: "+Pointer.nativeValue(mem_info.baseAddress)+" protection: "+mem_info.protect);
			//            }
			if (mem_info.protect.equals(new DWORD(WinNT.PAGE_READWRITE)) && mem_info.state.equals(new DWORD(WinNT.MEM_COMMIT))){
				byte[] buffer = readMemory(Pointer.nativeValue(mem_info.baseAddress), (int) mem_info.regionSize.longValue(), consoleMsg);
				int vBytes = 0;
				for (int i=0; i<buffer.length; i++){
					if (buffer[i] == searchArr[vBytes]){
						vBytes++;
					}else{
						vBytes = 0;
					}
					if (vBytes>=searchArr.length){
						addresses.add(Pointer.nativeValue(mem_info.baseAddress)+i-searchArr.length+1);
						System.out.println("Array found at "+Long.toHexString(addresses.get(addresses.size()-1)));
						vBytes = 0;
					}
				}
			}

			// move to the next memory chunk
			proc_min_address_l += mem_info.regionSize.longValue();
			sys_info.lpMinimumApplicationAddress = new Pointer(proc_min_address_l);
		}
		return addresses;
	}

	public static MemoryHelper createHelper(String processName) {
		long processId = FindProcessId(processName);
		if (processId == 0L) {
			System.err.println("The searched process was not found : " + processName);
			return null;
		}
		System.out.println(processName + " id : " + processId);

		WinNT.HANDLE hProcess = myKernel32.OpenProcess(WinNT.PAGE_EXECUTE + WinNT.PAGE_EXECUTE_READ + WinNT.PAGE_WRITECOPY + WinNT.PROCESS_QUERY_INFORMATION, true, (int) processId);
		return new MemoryHelper(hProcess);
	}

	public static MemoryHelper createHelper(long processId) {
		WinNT.HANDLE hProcess = myKernel32.OpenProcess(WinNT.PAGE_EXECUTE + WinNT.PAGE_EXECUTE_READ + WinNT.PAGE_WRITECOPY + WinNT.PROCESS_QUERY_INFORMATION, true, (int) processId);
		if (hProcess!=null){
			return new MemoryHelper(hProcess);
		}else{
			System.err.println("ProcessId is invalid!");
			return null;
		}
	}

	public byte[] readMemory(long readAddress, int size, boolean consoleMsg) {
		Memory output = new Memory(size);
		if (myKernel32.ReadProcessMemory(processHandle, readAddress, output, size, new IntByReference(0))){
			if (consoleMsg==true){
				System.out.println(size + " bytes has been read at 0x" + Long.toHexString(readAddress).toUpperCase());
			}
			return output.getByteArray(0, size);
		}
		System.err.println("readMememory Error.");
		return null;
	}
	public boolean writeMemory(byte[] data, long writeAddress, boolean consoleMsg){
		Memory input = new Memory(data.length);
		input.write(0, data, 0, data.length);
		if (myKernel32.WriteProcessMemory(processHandle, writeAddress, input, data.length, new IntByReference(0))){
			if (consoleMsg==true){
				System.out.println("Wrote " + data.length + " bytes to 0x"+Long.toHexString(writeAddress).toUpperCase());
			}
			return true;
		}
		System.err.println("writeMemory Error");
		return false;
	}

	public static long FindProcessId(String processName) {
		Tlhelp32.PROCESSENTRY32.ByReference processInfo = new Tlhelp32.PROCESSENTRY32.ByReference();
		WinNT.HANDLE processesSnapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new DWORD(0L));

		try {
			kernel32.Process32First(processesSnapshot, processInfo);
			if (processName.equals(Native.toString(processInfo.szExeFile))) {
				return processInfo.th32ProcessID.longValue();
			}

			while (kernel32.Process32Next(processesSnapshot, processInfo)) {
				if (processName.equals(Native.toString(processInfo.szExeFile))) {
					return processInfo.th32ProcessID.longValue();
				}
			}
			return 0L;
		} finally {
			kernel32.CloseHandle(processesSnapshot);
		}
	}

}
