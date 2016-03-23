package tk.greydynamics.Resource.Frostbite3.Toc;

import java.io.File;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutFile;

public class TocEntry {
	private String id;
	private String guid;
	private String sha1;
	private long offset;
	private int size;
	private long sizeLong;
	private LinkBundleType type;
	private String bundlePath;
	private boolean delta;
	private boolean base;
	
	public TocEntry(/*USING NULLCONSTUCTOR*/){
		this.id = "";
		this.offset = -1;
		this.size = -1;
		this.type = null;
		this.bundlePath = "";
		this.sha1 = "";
		this.sizeLong = -1;
		this.guid = "";
		this.delta = false;
		this.base = false;
	}
	
	public LayoutFile getLayout(){
		try{
			byte[] data = null;
//			String fixPath = bundlePath;
//			if (bundlePath.contains(ModTools.FOLDER_COMPILEDDATA)){
//				String[] parts = bundlePath.split(ModTools.FOLDER_COMPILEDDATA);
//				if (parts.length>1){
//					fixPath = Core.gamePath+"/"+parts[1];
//					System.out.println("Fixing Layout path from "+bundlePath+" to "+fixPath);
//				}else{
//					System.err.println("ERROR: "+ModTools.FOLDER_COMPILEDDATA+" is (still) part of the path for unpatched layout.");
//				}
//			}
			if (!delta && base){
				System.out.println("Delta: "+delta+" Base: "+base);
				//Link to unpached
				File unpatched = new File(bundlePath.replace("Update/Patch/", ""));
				String normPath = FileHandler.normalizePath(unpatched.getAbsolutePath());
				if (!unpatched.exists()){
					System.err.println("Could not find unpatched file. ("+normPath+")");
					return null;
				}
				data = FileHandler.readFile(normPath, (int) this.offset, this.size);
			}else{
				//In current sb file exists.
				System.out.println("Delta: "+delta+" Base: "+base);
				data = FileHandler.readFile(bundlePath, (int) this.offset, this.size);
			}
			
			if (data==null){
				return null;
			}
			return TocManager.readCASBundleLayout(data);
		}catch (Exception e){
			//e.printStackTrace();
			System.err.println("Could not read Sb part from "+bundlePath+" at "+this.offset);
			return null;
		}
	}
	
	
	/*GETTER AND SETTER*/
	
	public boolean isBase() {
		return base;
	}

	public void setBase(boolean base) {
		this.base = base;
	}


	public boolean isDelta() {
		return delta;
	}


	public void setDelta(boolean delta) {
		this.delta = delta;
	}


	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}


	public long getSizeLong() {
		return sizeLong;
	}


	public void setSizeLong(long sizeLong) {
		this.sizeLong = sizeLong;
	}


	public LinkBundleType getType() {
		return type;
	}

	public void setType(LinkBundleType type) {
		this.type = type;
	}

	public String getID() {
		return id;
	}
	
	public String getBundlePath() {
		return bundlePath;
	}

	public void setBundlePath(String bundlePath) {
		this.bundlePath = bundlePath;
	}

	public void setID(String id) {
		this.id = id;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	
	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}
}
