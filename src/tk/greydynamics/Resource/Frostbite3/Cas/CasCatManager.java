package tk.greydynamics.Resource.Frostbite3.Cas;

import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class CasCatManager {
	public byte[] header = new byte[] {
			(byte) 0x4E, (byte) 0x79, (byte) 0x61, (byte) 0x6E, 
			(byte) 0x4E, (byte) 0x79, (byte) 0x61, (byte) 0x6E,
			(byte) 0x4E, (byte) 0x79, (byte) 0x61, (byte) 0x6E,
			(byte) 0x4E, (byte) 0x79, (byte) 0x61, (byte) 0x6E}; //  << NyanNyanNyanNyan >>
	public ArrayList<CasCatEntry> entries;
	public FileSeeker seeker;
	public byte[] cat;
	
	public boolean readCat(byte[] fileArray, String description) {
		System.out.println("Reading "+description+" cas.cat file!");
		for (int i = 0; i <header.length; i++){
			if (header[i] != fileArray[i]){
				System.err.println("given fileArray does not match header of cat.cas file :/");
				return false;
			}
		}
		entries = new ArrayList<CasCatEntry>();
		seeker = new FileSeeker();
		seeker.seek(16);
		
		/* Starwars Battlefront contains the number of entries in a long behind the header!
		 * So, calculate number of entries by filesize, if there are 8 bytes left. Skip 8!
		 * 
		 * SHA1 = 20 Bytes
		 * Offset = 4 Bytes
		 * ProcSize = 4 Bytes
		 * CasFile = 4 Bytes
		 * ------------------> Total Size = 32 Bytes == 0x20
		 * */
		
		int dataSize = fileArray.length-header.length;
		int leftOver = dataSize%0x20;
		if (leftOver!=0){
			System.out.println("New CasCat-File was detected (aka. 'Starwars Battlefront change')");
			seeker.seek(leftOver);//Skip additional header data.
		}
		
		while (seeker.getOffset() < fileArray.length){
			CasCatEntry en = new CasCatEntry();
			en.setSHA1(FileHandler.readSHA1(fileArray, seeker));
			en.setOffset(FileHandler.readInt(fileArray, seeker));
			en.setProcSize(FileHandler.readInt(fileArray, seeker));
			en.setCasFile(FileHandler.readInt(fileArray, seeker));
			/*if (en.getSHA1().equalsIgnoreCase("337A2C248C20171E2575CADFFFAEF9805F0E255C")){
				System.err.println("Found 337A2C248C20171E2575CADFFFAEF9805F0E255C SHA1!");
			}
			System.out.println(en.getSHA1());*/
			entries.add(en);
		}//EOF
		System.out.println(entries.size()+" entries where found.");
		return true;
	}
	public byte[] getCat(){
		System.out.println("Generating cas.cat file with a size of: "+(entries.size()*32)+16+" Byte!");
		seeker = new FileSeeker();
		cat = new byte[(entries.size()*32)+16];
		addBytes(header); //HEADER 16bytes
		for (CasCatEntry e : entries){
			addBytes(FileHandler.hexStringToByteArray(e.getSHA1())); //SHA1 20bytes
			addBytes(toBytes(e.getOffset())); //OFFSET 4bytes
			addBytes(toBytes(e.getProcSize())); //PROCSIZE 4bytes
			addBytes(toBytes(e.getCasFile())); //CASFILE 4bytes
		}
		return cat;
	}
	void addBytes(byte[] arr){
		for (Byte b : arr){
			cat[seeker.getOffset()] = b;
			seeker.seek(1);
		}
	}
	byte[] toBytes(int i)
	{
		byte[] result = new byte[4]; //LITTLE
		result[3] = (byte) (i >> 24);
		result[2] = (byte) (i >> 16);
		result[1] = (byte) (i >> 8);
		result[0] = (byte) (i /*>> 0*/);
		return result;
	}
	
	//DEBUG PRINT
	public void dumpDebug(){
		for (CasCatEntry e : entries){
			System.out.println(e.getSHA1()+" "+e.getCasFile()+"\n");
		}
	}
		
	//GETTER AND SETTERS
	public ArrayList<CasCatEntry> getEntries() {
		return entries;
	}
}
