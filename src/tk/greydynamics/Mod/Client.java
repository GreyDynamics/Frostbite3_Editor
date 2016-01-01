package tk.greydynamics.Mod;

import java.io.File;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;

public class Client {
	public static String cloneClient(String sourceFolder, String newFolderName, boolean deleteExisiting){
		sourceFolder = FileHandler.normalizePath(sourceFolder);
		ArrayList<File> sourceFiles = null;
		File folder = new File(sourceFolder);
		if (!folder.isDirectory()){
			System.err.println("Not a vaild folder given.");
			return null;
		}
		String[] split = sourceFolder.split("/");
		int length = split.length;
		if (sourceFolder.endsWith("/")){
			length--;
		}
		String destFolderPath = "";
		for (int i=0; i<length;i++){
			destFolderPath +=split[i]+"/";
		}
		destFolderPath += newFolderName;
		File destFolder = new File(destFolderPath);
		if (destFolder.isDirectory()){
			if (deleteExisiting){
				destFolder.delete();
				return null;//TODO does not work :(
			}else{
				System.err.println("New folder does already exist.");
				return null;
			}
		}
		System.out.println("Client get currently cloned...\n"
				+"(Windows-Hardlink's will be used. It will take 'no' space!)");
		sourceFiles = FileHandler.listf(sourceFolder, "");
		for (File f : sourceFiles){
			if (!f.getAbsolutePath().contains(".par")){
				String linkPath = FileHandler.normalizePath(f.getAbsolutePath()).replace(sourceFolder, destFolderPath+"/");
				
				FileHandler.prepareDir(linkPath);
				FileHandler.createLink(linkPath, FileHandler.normalizePath(f.getAbsolutePath()));
			}
		}
		return destFolderPath;
	}
}
