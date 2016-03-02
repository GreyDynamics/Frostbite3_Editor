package tk.greydynamics.Resource.Frostbite3.Cas;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import tk.greydynamics.Maths.Hash;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.Block;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.BlockHeader;

public class CasManager {
	
	static byte[] header = new byte[] { (byte) 0xFA, (byte) 0xCE, (byte) 0x0F, (byte) 0xF0,
		(byte) 0x5F, (byte) 0x5F, (byte) 0x5F, (byte) 0x53,
		(byte) 0x50, (byte) 0x4C, (byte) 0x45, (byte) 0x58,
		(byte) 0x58, (byte) 0x5F, (byte) 0x5F, (byte) 0x5F};
	
	
	
	public static boolean createCAS(String path99cas){
		try{
			File casFile = new File(FileHandler.normalizePath(path99cas));
			if (casFile.exists()){
//				System.err.println("Cas file does already exist.\n");
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
		
		ArrayList<Byte> procEntries = Block.compressBlock(decompressedBytes, null, BlockHeader.BlockType_UnCompressed);
		/*EXTEND AND RETURN NEW ENTRY!*/
		int casEntryOffset = (int) cas.length(); //Max file size is anyways less than 2GB's so we can go with integers.
		if (FileHandler.writeFile(FileHandler.normalizePath(cas.getAbsolutePath()), FileHandler.toByteArray(procEntries), true)){
			CasCatEntry entry = new CasCatEntry(genSHA1(casCatMan), casEntryOffset, procEntries.size(), 99);
			System.out.println("New CatCatEntry "+entry.getSHA1()+" in "+cas.getAbsolutePath()+" created!");
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
	
	
}
