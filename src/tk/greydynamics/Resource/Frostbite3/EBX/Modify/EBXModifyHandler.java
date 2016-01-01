package tk.greydynamics.Resource.Frostbite3.EBX.Modify;

import java.nio.ByteOrder;
import java.util.ArrayList;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.Windows.MainWindow.EntryType;
import tk.greydynamics.Resource.FileHandler;

public class EBXModifyHandler {
	private ArrayList<ChangeFile> files = new ArrayList<>();
	
	public void addChange(String ebxFileGUID, ByteOrder order, boolean isOriginal, int offset, Object obj, EntryType type){
		ebxFileGUID = ebxFileGUID.toLowerCase();
		ChangeFile file = null;
		for (ChangeFile f : files){
			if (f.getEbxFileGUID()!=null){
				if (f.getEbxFileGUID().equalsIgnoreCase(ebxFileGUID)){
					file = f;
					break;
				}
			}
		}
		if (file!=null){
			file.addChange(obj, type, offset);
		}else{
			file = new ChangeFile(ebxFileGUID, order, isOriginal);
			file.addChange(obj, type, offset);
			files.add(file);
		}
		System.out.println(type +" (offset: "+offset+", EBXFileGUID: "+ebxFileGUID+") got applied to modify list!");
	}
	
	public ChangeFile getChangeFileByEBXGuid(String ebxFileGUID){
		ebxFileGUID = ebxFileGUID.toLowerCase();
		for (ChangeFile f : files){
			if (f.getEbxFileGUID().equals(ebxFileGUID)){
				return f;
			}
		}
		return null;
	}
	
	public void applyChanges(boolean deleteAfter){
		for (ChangeFile f : files){
			f.applyChanges(false);
		}
		files.clear();
	}

	public ArrayList<ChangeFile> getFiles() {
		return files;
	}	
}
