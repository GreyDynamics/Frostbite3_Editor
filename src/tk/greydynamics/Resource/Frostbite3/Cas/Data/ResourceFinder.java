package tk.greydynamics.Resource.Frostbite3.Cas.Data;

import java.io.File;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;

public class ResourceFinder {
	
	public static File findXPackPatch(String xpackPath){
		File patched = null;
		String path = FileHandler.normalizePath(xpackPath).replace(FileHandler.normalizePath(Core.gamePath+Core.PATH_UPDATE), "");
		String[] split = path.split("/", 2);
		if (split.length==2){
			patched = new File(Core.gamePath+Core.PATH_UPDATE_PATCH+split[1]);
		}
		return patched;
	}
	
	public static File findPatch(String dataPath){
		return new File(FileHandler.normalizePath(dataPath).replace(Core.gamePath, Core.gamePath+Core.PATH_UPDATE_PATCH));
	}
	
	public static File findUnpatchedData(String patchPath){
		return new File(FileHandler.normalizePath(patchPath).replace(FileHandler.normalizePath(Core.gamePath+Core.PATH_UPDATE_PATCH), Core.gamePath+"/"));
	}
	
	public static File findUnpatchedXPackData(String patchxpackPath){
		for (File file : new File(Core.gamePath+Core.PATH_UPDATE).listFiles()){
			if (file.isDirectory()){
				String test = file.getName();
				if (!file.getName().contains(Core.PATH_PATCH.replace("/", ""))){
					File packFile = new File(FileHandler.normalizePath(patchxpackPath).replace(
							FileHandler.normalizePath(FileHandler.normalizePath(Core.gamePath+Core.PATH_UPDATE_PATCH)), Core.gamePath+Core.PATH_UPDATE+"/"+file.getName()+"/"));
					if (packFile.exists()){
						return packFile;
					}
				}
			}
		}
		return null;
	}
}
