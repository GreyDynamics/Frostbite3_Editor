package tk.greydynamics.Resource.Frostbite3.Layout;

import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutFileType;

public class LayoutFile {
	private ArrayList<LayoutEntry> entries;
	//entries could be only one root entry, but may needed in other frostbite games :)
	private LayoutFileType type;
	private String sbpath;
	
	public LayoutFile(LayoutFileType type, String sbpath){
		this.entries = new ArrayList<LayoutEntry>();
		this.type = type;
		this.sbpath = FileHandler.normalizePath(sbpath);
	}
	public LayoutFile(LayoutFileType type){
		this.entries = new ArrayList<LayoutEntry>();
		this.type = type;
		this.sbpath = "";
	}
	
	public ArrayList<LayoutEntry> getEntries() {
		return entries;
	}

	public LayoutFileType getType() {
		return type;
	}

	public void setType(LayoutFileType type) {
		this.type = type;
	}
	public String getSBPath() {
		return sbpath;
	}
	public void setSBPath(String sbpath) {
		this.sbpath = FileHandler.normalizePath(sbpath);
	}		
	
	
}
