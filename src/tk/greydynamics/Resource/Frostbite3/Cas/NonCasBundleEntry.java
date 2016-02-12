package tk.greydynamics.Resource.Frostbite3.Cas;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter.ResourceBundleType;

public class NonCasBundleEntry {
	public static final int BYTESIZE = 8;
	
	private String name = null;
	private int nameOffset; //relative to the string section
	private int originalSize; //uncompressed size of the payload
	private String sha1 = null;
	
	private int baseOffset = -1;
	private int baseSize = -1;
	
	private int deltaOffset = -1;
	private int deltaSize = -1;
	
	private int midInstructionSize = -1;
	private int midInstructionType = -1;
	
	private int currentSize = -1;
	
	private ResourceBundleType bundleType = null;
	
	//additional Entries if its a res!
	private ResourceType resType = null;
	private int resTypeInt = -1;
	private byte[] resMeta = null;
	private long resRid = -1;
	
	
	private String modFilePath = null;
	private String ebxFileGUID = null;
	
	public NonCasBundleEntry(int nameOffset, int originalSize) {
		this.nameOffset = nameOffset;
		this.originalSize = originalSize;
	}
	
	public static NonCasBundleEntry readEntry(byte[] bundleBytes, FileSeeker seeker, ByteOrder order){
		if (bundleBytes!=null&&seeker!=null){
			if (seeker.getOffset()+8<=bundleBytes.length){
				return new NonCasBundleEntry(FileHandler.readInt(bundleBytes, seeker, order), FileHandler.readInt(bundleBytes, seeker, order));
			}
		}
		return null;
	}

	public ArrayList<Byte> getEntryBytes(ByteOrder order){
		ArrayList<Byte> entryBytes = new ArrayList<>();
		FileHandler.addBytes(FileHandler.toBytes(this.nameOffset, order), entryBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.originalSize, order), entryBytes);
		if (entryBytes.size()!=BYTESIZE){//issues ? - change BYTESIZE(if wrong or changed) and check parsing
			return null;
		}
		return entryBytes;
	}
	
	public String getModFilePath() {
		return modFilePath;
	}

	public void setModFilePath(String modFilePath) {
		this.modFilePath = modFilePath;
	}

	public int getBaseOffset() {
		return baseOffset;
	}

	public void setBaseOffset(int baseOffset) {
		this.baseOffset = baseOffset;
	}

	public int getBaseSize() {
		return baseSize;
	}

	public void setBaseSize(int baseSize) {
		this.baseSize = baseSize;
	}

	public int getDeltaOffset() {
		return deltaOffset;
	}

	public void setDeltaOffset(int deltaOffset) {
		this.deltaOffset = deltaOffset;
	}

	public int getDeltaSize() {
		return deltaSize;
	}

	public void setDeltaSize(int deltaSize) {
		this.deltaSize = deltaSize;
	}

	public int getMidInstructionSize() {
		return midInstructionSize;
	}

	public void setMidInstructionSize(int midInstructionSize) {
		this.midInstructionSize = midInstructionSize;
	}

	public int getMidInstructionType() {
		return midInstructionType;
	}

	public void setMidInstructionType(int midInstructionType) {
		this.midInstructionType = midInstructionType;
	}

	public ResourceType getResType() {
		return resType;
	}

	public void setResType(ResourceType resType) {
		this.resType = resType;
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

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
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

	public int getNameOffset() {
		return nameOffset;
	}

	public int getOriginalSize() {
		return originalSize;
	}

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}

	public int getResTypeInt() {
		return resTypeInt;
	}

	public void setResTypeInt(int resTypeInt) {
		this.resTypeInt = resTypeInt;
	}

	public void setNameOffset(int nameOffset) {
		this.nameOffset = nameOffset;
	}

	public void setOriginalSize(int originalSize) {
		this.originalSize = originalSize;
	}

	public String getEbxFileGUID() {
		return ebxFileGUID;
	}

	public void setEbxFileGUID(String ebxFileGUID) {
		this.ebxFileGUID = ebxFileGUID;
	}

	
	
	
}
