package tk.greydynamics.Resource.Frostbite3.Toc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter.ResourceBundleType;

public class ResourceLink {
	// Global
	public String name;
	public long size;
	public long originalSize;
	public ResourceType type;
	public String sha1;
	public int casPatchType; //2 if patched
	public String baseSha1;
	public String deltaSha1;

	// ++res
	public int resType;
	public byte[] resMeta; // 0x13 RAW2
	public long resRid;
	public byte[] idata; // 0x13 RAW2

	// ++chunks
	public String id;
	public int logicalOffset;
	public int logicalSize;
	public int rangeStart;
	public int rangeEnd;

	// ++chunksMeta
	public int h32;
	public byte[] meta; // 0x02 RAW1
	public int firstMip;

	// **EBX EXTRA
	public String ebxFileGUID;

	// additional
	public ResourceBundleType bundleType;
	public boolean hasModFile;
	
	/**
	 * Save file with the relative path given by name, skip ... if file with same sha1 hash does exist.
	 * @return
	 */
	public static boolean exportResourceLink(ResourceLink resLink, String origin, String resLinkRootPath, String fileType){
		if (resLink!=null){
			ArrayList<String> list = new ArrayList<>();
			list.add(origin);
			list.add(resLink.getName());
			list.add(resLink.getSize()+"");
			list.add(resLink.getOriginalSize()+"");
			list.add(resLink.getType().toString());
			list.add(resLink.getSha1());
			list.add(resLink.getCasPatchType()+"");
			list.add(resLink.getBaseSha1());
			list.add(resLink.getDeltaSha1());
			list.add(resLink.getResType()+"");
			list.add(FileHandler.bytesToHex(resLink.getResMeta()));
			list.add(resLink.getResRid()+"");
			list.add(FileHandler.bytesToHex(resLink.getIdata()));
			list.add(resLink.getId());
			list.add(resLink.getLogicalOffset()+"");
			list.add(resLink.getLogicalSize()+"");
			list.add(resLink.getRangeStart()+"");
			list.add(resLink.getRangeEnd()+"");
			list.add(resLink.getH32()+"");
			list.add(FileHandler.bytesToHex(resLink.getMeta()));
			list.add(resLink.getFirstMip()+"");
			list.add(resLink.getEbxFileGUID()+"");
			list.add(resLink.getBundleType().toString());
			File temp = new File(Core.EDITOR_PATH_TEMP+UUID.randomUUID());
			if (FileHandler.writeLine(list, temp)){
				String hash = FileHandler.checkSumSHA1(temp);
				if (hash!=null){
					File target = new File(resLinkRootPath+"/"+FileHandler.normalizePath(resLink.getName())+"/"+hash+"_"+origin+fileType);
					if (target.exists()){
						//ALREADY EXISTS, SKIP
						temp.delete();
						return true;
					}else if (FileHandler.copy(temp, target, false)){
						temp.delete();
						return true;
					}
				}
				temp.delete();
			}
		}
		return false;
	}
	public static ResourceLink importResourceLink(File file){
		try {

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			ResourceLink resLink = new ResourceLink();
			
		    resLink.setName(br.readLine());
		    resLink.setSize(Long.valueOf(br.readLine()));
		    resLink.setOriginalSize(Long.valueOf(br.readLine()));
		    resLink.setType(ResourceType.valueOf(br.readLine()));
		    resLink.setSha1(br.readLine());
		    resLink.setCasPatchType(Integer.valueOf(br.readLine()));
		    resLink.setBaseSha1(br.readLine());
		    resLink.setDeltaSha1(br.readLine());
		    
		    resLink.setResType(Integer.valueOf(br.readLine()));
		    resLink.setResMeta(FileHandler.hexStringToByteArray(br.readLine()));
		    resLink.setResRid(Long.valueOf(br.readLine()));
		    resLink.setIdata(FileHandler.hexStringToByteArray(br.readLine()));
		    
		    resLink.setId(br.readLine());
		    resLink.setLogicalOffset(Integer.valueOf(br.readLine()));
		    resLink.setLogicalSize(Integer.valueOf(br.readLine()));
		    resLink.setRangeStart(Integer.valueOf(br.readLine()));
		    resLink.setRangeEnd(Integer.valueOf(br.readLine()));
		    
		    resLink.setH32(Integer.valueOf(br.readLine()));
		    resLink.setMeta(FileHandler.hexStringToByteArray(br.readLine()));
		    resLink.setFirstMip(Integer.valueOf(br.readLine()));
		    
		    resLink.setEbxFileGUID(br.readLine());
		    resLink.setBundleType(ResourceBundleType.valueOf(br.readLine()));
		    
		    br.close();
		    fr.close();
		    return resLink; 
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("Could not import ResLink "+file.getAbsolutePath());
		}
		return null;
	}
	
	
	public ResourceLink(/* USING NULLCONSTRUCTOR */) {
		this.name = "";
		this.size = 0;
		this.originalSize = 0;
		this.type = ResourceType.UNDEFINED;
		this.sha1 = "";
		this.bundleType = null;
		this.ebxFileGUID = "";
		this.casPatchType = 0;
		this.baseSha1 = null;
		this.deltaSha1 = null;
		this.rangeStart = -1; //This can be 0, so we choose a negative value
		this.rangeEnd = -1;
		this.firstMip = -1;
		this.hasModFile = false;
	}
	

	public ResourceLink(String name, long size, long originalSize, ResourceType type, String sha1, int casPatchType,
			String baseSha1, String deltaSha1, int resType, byte[] resMeta, long resRid, byte[] idata, String id,
			int logicalOffset, int logicalSize, int rangeStart, int rangeEnd, int h32, byte[] meta, int firstMip,
			String ebxFileGUID) {
		this.name = name;
		this.size = size;
		this.originalSize = originalSize;
		this.type = type;
		this.sha1 = sha1;
		this.casPatchType = casPatchType;
		this.baseSha1 = baseSha1;
		this.deltaSha1 = deltaSha1;
		this.resType = resType;
		this.resMeta = resMeta;
		this.resRid = resRid;
		this.idata = idata;
		this.id = id;
		this.logicalOffset = logicalOffset;
		this.logicalSize = logicalSize;
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
		this.h32 = h32;
		this.meta = meta;
		this.firstMip = firstMip;
		this.ebxFileGUID = ebxFileGUID;
		this.bundleType = null;
		this.hasModFile = false;
	}


	public String getEbxFileGUID() {
		return ebxFileGUID;
	}
	

	public String getBaseSha1() {
		return baseSha1;
	}

	public void setBaseSha1(String baseSha1) {
		this.baseSha1 = baseSha1;
	}


	public String getDeltaSha1() {
		return deltaSha1;
	}

	public void setDeltaSha1(String deltaSha1) {
		this.deltaSha1 = deltaSha1;
	}

	public int getCasPatchType() {
		return casPatchType;
	}

	public void setCasPatchType(int casPatchType) {
		this.casPatchType = casPatchType;
	}

	public int getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(int rangeStart) {
		this.rangeStart = rangeStart;
	}

	public int getFirstMip() {
		return firstMip;
	}

	public void setFirstMip(int firstMip) {
		this.firstMip = firstMip;
	}

	public int getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(int rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	public void setEbxFileGUID(String ebxFileGUID) {
		this.ebxFileGUID = ebxFileGUID;
	}

	public ResourceBundleType getBundleType() {
		return bundleType;
	}

	public void setBundleType(ResourceBundleType bundleType) {
		this.bundleType = bundleType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getOriginalSize() {
		return originalSize;
	}

	public void setOriginalSize(long originalSize) {
		this.originalSize = originalSize;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(ResourceType type) {
		this.type = type;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public int getResType() {
		return resType;
	}

	public void setResType(int resType) {
		this.resType = resType;
	}

	public byte[] getResMeta() {
		return resMeta;
	}

	public void setResMeta(byte[] resMeta) {
		this.resMeta = resMeta;
	}

	public long getResRid() {
		return resRid;
	}

	public void setResRid(long resRid) {
		this.resRid = resRid;
	}

	public byte[] getIdata() {
		return idata;
	}

	public void setIdata(byte[] idata) {
		this.idata = idata;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLogicalOffset() {
		return logicalOffset;
	}

	public void setLogicalOffset(int logicalOffset) {
		this.logicalOffset = logicalOffset;
	}

	public int getLogicalSize() {
		return logicalSize;
	}

	public void setLogicalSize(int logicalSize) {
		this.logicalSize = logicalSize;
	}

	public int getH32() {
		return h32;
	}

	public void setH32(int h32) {
		this.h32 = h32;
	}

	public byte[] getMeta() {
		return meta;
	}

	public void setMeta(byte[] meta) {
		this.meta = meta;
	}

	public boolean isHasModFile() {
		return hasModFile;
	}

	public void setHasModFile(boolean hasModFile) {
		this.hasModFile = hasModFile;
	}
	

}
