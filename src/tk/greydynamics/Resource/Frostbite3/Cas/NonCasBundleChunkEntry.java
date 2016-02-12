package tk.greydynamics.Resource.Frostbite3.Cas;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class NonCasBundleChunkEntry {
	private String id;
	private short rangeStart;
	private short logicalOffset;
	private int logicalSize;
	private String sha1;
	
	private int relBundleOffset;
	private int rawPayloadSize;
	
	private String modFilePath = null;
	
	
	public NonCasBundleChunkEntry(String id, short rangeStart, short logicalOffset, int logicalSize) {
		this.id = id;
		this.rangeStart = rangeStart;
		this.logicalOffset = logicalOffset;
		this.logicalSize = logicalSize;
		this.relBundleOffset = 0;
		this.rawPayloadSize = 0;
	}
	
	public static NonCasBundleChunkEntry readChunkEntry(byte[] bundleBytes, FileSeeker seeker, ByteOrder order){
		if (bundleBytes!=null&&seeker!=null){
			if (seeker.getOffset()+24<=bundleBytes.length){
				return new NonCasBundleChunkEntry(
					FileHandler.bytesToHex(FileHandler.readByte(bundleBytes, seeker, 16)), //id
					FileHandler.readShort(bundleBytes, seeker, order), //rangeStart
					FileHandler.readShort(bundleBytes, seeker, order), //logicalOffset
					FileHandler.readInt(bundleBytes, seeker, order)//logicalSize (decompressed size ?)//TODO
				);
			}
		}
		return null;
	}
	public ArrayList<Byte> getChunkEntryBytes(ByteOrder order) {
		ArrayList<Byte> chunkEntryBytes = new ArrayList<>();
		FileHandler.addBytes(FileHandler.hexStringToByteArray(id), chunkEntryBytes);
		FileHandler.addBytes(FileHandler.toBytes((short) rangeStart, order), chunkEntryBytes);
		FileHandler.addBytes(FileHandler.toBytes((short) logicalOffset, order), chunkEntryBytes);
		FileHandler.addBytes(FileHandler.toBytes((int) logicalSize, order), chunkEntryBytes);
		return chunkEntryBytes;
	}

	public int getRelBundleOffset() {
		return relBundleOffset;
	}

	public void setRelBundleOffset(int relBundleOffset) {
		this.relBundleOffset = relBundleOffset;
	}

	public String getModFilePath() {
		return modFilePath;
	}

	public void setModFilePath(String modFilePath) {
		this.modFilePath = modFilePath;
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public short getRangeStart() {
		return rangeStart;
	}


	public void setRangeStart(short rangeStart) {
		this.rangeStart = rangeStart;
	}

	public void setLogicalSize(short logicalSize) {
		this.logicalSize = logicalSize;
	}


	public int getLogicalOffset() {
		return logicalOffset;
	}

	public int getLogicalSize() {
		return logicalSize;
	}

	public void setLogicalSize(int logicalSize) {
		this.logicalSize = logicalSize;
	}

	public void setLogicalOffset(short logicalOffset) {
		this.logicalOffset = logicalOffset;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public int getRawPayloadSize() {
		return rawPayloadSize;
	}

	public void setRawPayloadSize(int rawPayloadSize) {
		this.rawPayloadSize = rawPayloadSize;
	}
	
}
