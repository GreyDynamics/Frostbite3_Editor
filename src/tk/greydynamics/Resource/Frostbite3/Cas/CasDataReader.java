package tk.greydynamics.Resource.Frostbite3.Cas;

import java.util.ArrayList;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Game.Game;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler;
import tk.greydynamics.Resource.Frostbite3.Patcher;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.CompressionUtils;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class CasDataReader { //casPath == folderPath
	public static byte[] readCas(String baseSHA1, String deltaSHA1, String SHA1, Integer patchType){
		return readCas(baseSHA1, deltaSHA1, SHA1, patchType, null);
	}
		
	public static byte[] readCas(String baseSHA1, String deltaSHA1, String SHA1, Integer patchType, ResourceHandler resHandler){
		ResourceHandler rs = null;
		if (resHandler==null){
			Game game = Core.getGame();
			rs = game.getResourceHandler();
		}else{
			rs = resHandler;
		}
		if (patchType == 666){
			System.err.println("'UNKNOWN SOURCE' search started!");
			//Unknown
			byte[] data = CasDataReader.readCas(SHA1, Core.gamePath+"/Update/Patch/Data", rs.getPatchedCasCatManager().getEntries(), false);
			if (data != null){
				System.out.println("SHA1 was found in patched CasCat!");
				return data;
			}else{
				byte[] data2 = CasDataReader.readCas(SHA1, Core.gamePath+"/Data", rs.getCasCatManager().getEntries(), false);
				if (data2 != null){
					System.out.println("SHA1 was found in unpatched CasCat!");
					return data2;
				}else{
					System.err.println("SHA could not be found in any CasCat!");
					return null;
				}
			}
		}else if (patchType == 2){
			//Patched using delta
			byte[] base = CasDataReader.readCas(baseSHA1, Core.gamePath+"/Data", rs.getCasCatManager().getEntries(), false);
			byte[] delta = CasDataReader.readCas(deltaSHA1, Core.gamePath+"/Update/Patch/Data", rs.getPatchedCasCatManager().getEntries(), true);
			
			if (base==null||delta==null){
				System.err.println("Base or Delta contains null data.");
				return null;
			}
//			FileHandler.writeFile("output/debug/patcher_base", base);
//			FileHandler.writeFile("output/debug/patcher_delta", delta);
			
			byte[] data = Patcher.getPatchedCASData(base, delta);
//			FileHandler.writeFile("output/debug/patcher_data", data);

			if (data != null){
				return data;
			}else{
				System.err.println("The patcher return null data. Something went wrong!");
				return null;
			}
		}else if(patchType == 1){
			//Patched using data from update cas
			byte[] data = CasDataReader.readCas(SHA1, Core.gamePath+"/Update/Patch/Data", rs.getPatchedCasCatManager().getEntries(), false);
			if (data != null){
				return data;
			}else{
				return null;
			}
		}else{
			//Unpatched
			byte[] data = CasDataReader.readCas(SHA1, Core.gamePath+"/Data", rs.getCasCatManager().getEntries(), false);
			if (data != null){
				return data;
			}else{
				System.err.println("null data... in CasDataReader (unpatched data)");
				return null;
			}
		}
	}
	
	
	public static byte[] readCas(String SHA1, String casFolderPath, ArrayList<CasCatEntry> casCatEntries, boolean hasNoBlockLogic){
		try{
			SHA1 = SHA1.replaceAll("\\s","");
			for (CasCatEntry e : casCatEntries){
				if (!e.getSHA1().equals(SHA1.toLowerCase())){continue;}
				
				String casFile = "";
				if (e.getCasFile()<10){casFile+="0";}
				casFile += e.getCasFile() + "";
				String casFilePath = casFolderPath;
				if (!casFilePath.endsWith("/")){casFilePath+="/";}
				casFilePath += "cas_"+ casFile + ".cas";
				
				System.out.println("Reading CAS: "+ casFilePath+" for SHA1: "+SHA1);
				if (!hasNoBlockLogic){//hasBlockLogic :)
					if (e.getProcSize()>=0x010000){
						System.out.println("WARNING: Decompress each block and glue the decompressed parts together to obtain the file.");
					}
					return CompressionUtils.convertToRAWData(FileHandler.readFile(casFilePath, e.getOffset(), e.getProcSize()));
				}else{
					return FileHandler.readFile(casFilePath, e.getOffset(), e.getProcSize());
				}
			}
			System.err.println("SHA "+SHA1+" not found in "+casFolderPath);
			return null;
		}catch (NullPointerException e){
			e.printStackTrace();
			return null;
		}
	}
	public static byte[] readOrignalData(String resourceName, ArrayList<ResourceLink> resourceList){
		for (ResourceLink link : resourceList){
			if (link.getName().equalsIgnoreCase(resourceName)){
				return CasDataReader.readCas(link.getBaseSha1(), link.getDeltaSha1(), link.getSha1(), link.getCasPatchType());
			}
		}
		System.err.println("Original Data could not get found for "+resourceName);
		return null;
	}
	
}
