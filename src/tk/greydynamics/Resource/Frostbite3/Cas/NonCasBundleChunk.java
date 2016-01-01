package tk.greydynamics.Resource.Frostbite3.Cas;

import java.nio.ByteOrder;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class NonCasBundleChunk {
	private String id;
	private short rangeStart;
	private short logicalSize;
	private int logicalOffset;
	private int originalSize;
	
	
	public NonCasBundleChunk(String id, short rangeStart, short logicalSize, int logicalOffset) {
		this.id = id;
		this.rangeStart = rangeStart;
		this.logicalSize = logicalSize;
		this.logicalOffset = logicalOffset;
		this.originalSize = this.logicalSize+this.logicalOffset;
	}
	
	public static NonCasBundleChunk readChunk(byte[] bundleBytes, FileSeeker seeker, ByteOrder order){
		if (bundleBytes!=null&&seeker!=null){
			if (seeker.getOffset()+24<=bundleBytes.length){
				return new NonCasBundleChunk(
					FileHandler.bytesToHex(FileHandler.readByte(bundleBytes, seeker, 16)), //id
					FileHandler.readShort(bundleBytes, seeker, order), //rangeStart
					FileHandler.readShort(bundleBytes, seeker, order), //logicalSize
					FileHandler.readInt(bundleBytes, seeker, order)//logicalOffset
				);
			}
		}
		return null;
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


	public short getLogicalSize() {
		return logicalSize;
	}


	public void setLogicalSize(short logicalSize) {
		this.logicalSize = logicalSize;
	}


	public int getLogicalOffset() {
		return logicalOffset;
	}


	public void setLogicalOffset(int logicalOffset) {
		this.logicalOffset = logicalOffset;
	}


	public int getOriginalSize() {
		return originalSize;
	}


	public void setOriginalSize(int originalSize) {
		this.originalSize = originalSize;
	}
	
}
