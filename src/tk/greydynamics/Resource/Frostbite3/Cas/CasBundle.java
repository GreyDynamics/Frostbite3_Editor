package tk.greydynamics.Resource.Frostbite3.Cas;

import java.util.ArrayList;

import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class CasBundle extends Bundle{
	public Integer magicSalt;
	public ArrayList<ResourceLink> ebx;
	public ArrayList<ResourceLink> dbx;
	public ArrayList<ResourceLink> res;
	public ArrayList<ResourceLink> chunks;
	public ArrayList<ResourceLink> chunkMeta;
	public boolean alignMembers;
	public boolean ridSupport;
	public boolean storeCompressedSizes;
	public long totalSize;
	public long dbxTotalSize;
		
	public CasBundle(String basePath, String deltaPath, String name, int baseOffset, int deltaOffset) {
		super(Bundle.BundleType.CAS, basePath, deltaPath, name, baseOffset, deltaOffset);
		
		this.magicSalt = 0;
		this.ebx = new ArrayList<ResourceLink>();
		this.dbx = new ArrayList<ResourceLink>();
		this.res = new ArrayList<ResourceLink>();
		this.chunks = new ArrayList<ResourceLink>();
		this.chunkMeta = new ArrayList<ResourceLink>();
		this.alignMembers = false;
		this.ridSupport = false;
		this.storeCompressedSizes = false;
		this.totalSize = 0;
		this.dbxTotalSize = 0;
	}

	public ArrayList<ResourceLink> getChunks() {
		return chunks;
	}

	public ArrayList<ResourceLink> getChunkMeta() {
		return chunkMeta;
	}
	public Integer getMagicSalt() {
		return magicSalt;
	}
	public void setMagicSalt(Integer magicSalt) {
		this.magicSalt = magicSalt;
	}
	public ArrayList<ResourceLink> getEbx() {
		return ebx;
	}
	public void setEbx(ArrayList<ResourceLink> ebx) {
		this.ebx = ebx;
	}
	public ArrayList<ResourceLink> getDbx() {
		return dbx;
	}
	public void setDbx(ArrayList<ResourceLink> dbx) {
		this.dbx = dbx;
	}
	public ArrayList<ResourceLink> getRes() {
		return res;
	}
	public void setRes(ArrayList<ResourceLink> res) {
		this.res = res;
	}
	public boolean isAlignMembers() {
		return alignMembers;
	}
	public void setAlignMembers(boolean alignMembers) {
		this.alignMembers = alignMembers;
	}
	public boolean isRidSupport() {
		return ridSupport;
	}
	public void setRidSupport(boolean ridSupport) {
		this.ridSupport = ridSupport;
	}
	public boolean isStoreCompressedSizes() {
		return storeCompressedSizes;
	}
	public void setStoreCompressedSizes(boolean storeCompressedSizes) {
		this.storeCompressedSizes = storeCompressedSizes;
	}
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public long getDbxTotalSize() {
		return dbxTotalSize;
	}
	public void setDbxTotalSize(long dbxTotalSize) {
		this.dbxTotalSize = dbxTotalSize;
	}
	
	
	
	
}
