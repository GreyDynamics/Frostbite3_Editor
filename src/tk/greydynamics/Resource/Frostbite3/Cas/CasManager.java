package tk.greydynamics.Resource.Frostbite3.Cas;

import java.io.File;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Random;

import tk.greydynamics.Maths.Hash;
import tk.greydynamics.Resource.FileHandler;

public class CasManager {
	
	static byte[] header = new byte[] { (byte) 0xFA, (byte) 0xCE, (byte) 0x0F, (byte) 0xF0,
		(byte) 0x5F, (byte) 0x5F, (byte) 0x5F, (byte) 0x53,
		(byte) 0x50, (byte) 0x4C, (byte) 0x45, (byte) 0x58,
		(byte) 0x58, (byte) 0x5F, (byte) 0x5F, (byte) 0x5F};
	
	static byte[] compressionType = new byte[] {(byte) 0x00, (byte) 0x70};
	static int blockSize = 0x10000;
	static int blockContent = 0xFAD8&0xFFFF;
	
	public static int blockHeaderNumBytes = 8;//4 size, 2 type, 2 compressed size
	
	public static boolean createCAS(String path99cas){
		try{
			File casFile = new File(FileHandler.normalizePath(path99cas));
			if (casFile.exists()){
				System.err.println("Cas file does already exist.");
				return false;
			}
			if (FileHandler.writeFile(path99cas, header)){
				return true;
			}
			return false;
		}catch (Exception e){
			System.err.println("Could not create a new CAS file! "+path99cas);
			return false;
		}
	}
	
	public static CasCatEntry extendCAS(byte[] decompressedBytes, File cas, CasCatManager casCatMan){
		if (!cas.exists()){return null;}
		System.err.println("CasManager is extending the CAS using a custom max. block size\n"+
				"we should change it to the default and set each block compressed size to 0x0!");
		
		
		ArrayList<Byte> procEntries = new ArrayList<Byte>();	
		
		/*HANDLE BLOCK LOGIC*/
		int blocks = calculateNumberOfBlocks(decompressedBytes.length);
		//System.out.println(blockContent);
		System.out.println("Building "+(blocks+1)+" blocks in total.");
		int restLen = decompressedBytes.length - (blocks * blockContent);
		for (int i=0; i<blocks; i++){
			FileHandler.addBytes(FileHandler.toBytes(blockSize, ByteOrder.BIG_ENDIAN), procEntries);//int
			FileHandler.addBytes(compressionType, procEntries);
			FileHandler.addBytes(FileHandler.toBytes((short) (blockContent&0xFFFF), ByteOrder.BIG_ENDIAN), procEntries);/*short -- this can may be even 0x00, because
															FrankElster said: "..compressed size (null for type 0071 and type 0000) of the payload .. without the header"*/
			FileHandler.addBytes(decompressedBytes, procEntries, i*blockContent, blockContent);
		}
		
		/*FILL REST*/
		FileHandler.addBytes(FileHandler.toBytes(restLen, ByteOrder.BIG_ENDIAN), procEntries);
		FileHandler.addBytes(compressionType, procEntries); // 0x0070 -- uncompressed || 0x0970 lz4 compressed || 0x0071 -- uncompressed no payload || 0x0000 -- empty payload
		FileHandler.addBytes(FileHandler.toBytes((short) restLen, ByteOrder.BIG_ENDIAN), procEntries);
		FileHandler.addBytes(decompressedBytes, procEntries, blocks*blockContent, restLen);
		
		/*EXTEND AND RETURN NEW ENTRY!*/
		int casEntryOffset = (int) cas.length(); //Max file size is anyways less than 2GB's so we can go with integers.
		if (FileHandler.writeFile(FileHandler.normalizePath(cas.getAbsolutePath()), FileHandler.toByteArray(procEntries), true)){
			CasCatEntry entry = new CasCatEntry(genSHA1(casCatMan), casEntryOffset, procEntries.size(), 99);
			System.out.println("New CatCatEntry "+entry.getSHA1()+" in cas_99 created!");
			return entry;
		}
		System.err.println("Could not extend CASFile :(");
		return null;
	}
	
	static String genSHA1(CasCatManager casCatMan){
		Random random = new Random();
		String sha1 = Hash.getSHA1(random.nextInt(1231222223)+random.nextInt(1231222223)+"SPLEXX");
		for (CasCatEntry entry : casCatMan.getEntries()){
			if (entry.getSHA1().equalsIgnoreCase(sha1)){
				return genSHA1(casCatMan);
			}
		}
		return sha1;
	}
	
	public static int calculateNumberOfBlocks(int rawLength){
		return rawLength / blockContent;
	}
}
