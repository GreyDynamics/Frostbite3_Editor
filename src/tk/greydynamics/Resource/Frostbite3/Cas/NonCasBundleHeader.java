package tk.greydynamics.Resource.Frostbite3.Cas;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class NonCasBundleHeader {
	public static final int HEADER_BYTESIZE = 32;
	public static final int FOURCC_BF4 = 0x9D798ED5;

	private int magic = -1; //970d1c13 for bf3, 9D798ED5 for bf4
	private int totalCount = -1; //total entries = ebx + res + chunks
	private int ebxCount = -1;
	private int resCount = -1;
	private int chunkCount = -1;
	private int stringOffset = -1; //offset of string section, relative to metadata start (i.e. 4 bytes into the file)
	private int chunkMetaOffset = -1; //redundant
	private int chunkMetaSize = -1; //redundant
	
	public NonCasBundleHeader(byte[] bundleBytes, FileSeeker seeker, ByteOrder order) {
		this.magic = FileHandler.readInt(bundleBytes, seeker, order);
		this.totalCount = FileHandler.readInt(bundleBytes, seeker, order);
		this.ebxCount = FileHandler.readInt(bundleBytes, seeker, order);
		this.resCount = FileHandler.readInt(bundleBytes, seeker, order);
		this.chunkCount = FileHandler.readInt(bundleBytes, seeker, order);
		this.stringOffset = FileHandler.readInt(bundleBytes, seeker, order);
		this.chunkMetaOffset = FileHandler.readInt(bundleBytes, seeker, order);
		this.chunkMetaSize = FileHandler.readInt(bundleBytes, seeker, order);
	}
	
	public ArrayList<Byte> getHeaderBytes(ByteOrder order){
		ArrayList<Byte> headerBytes = new ArrayList<>();
		FileHandler.addBytes(FileHandler.toBytes(this.magic, order), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.totalCount, order), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.ebxCount, order), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.resCount, order), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.chunkCount, order), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.stringOffset, order), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.chunkMetaOffset, order), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.chunkMetaSize, order), headerBytes);
		if (headerBytes.size()==HEADER_BYTESIZE){
			return headerBytes;
		}else{
			System.err.println("NonCasBundleHeader couldn't be created.");//check HEADER_BYTESIZE(if changed) and written data.
			return null;
		}
	}
	
	
	public int getMagic() {
		return magic;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public int getEbxCount() {
		return ebxCount;
	}


	public int getResCount() {
		return resCount;
	}


	public int getChunkCount() {
		return chunkCount;
	}


	public int getStringOffset() {
		return stringOffset;
	}


	public int getChunkMetaOffset() {
		return chunkMetaOffset;
	}


	public int getChunkMetaSize() {
		return chunkMetaSize;
	}


	public void setMagic(int magic) {
		this.magic = magic;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setEbxCount(int ebxCount) {
		this.ebxCount = ebxCount;
	}

	public void setResCount(int resCount) {
		this.resCount = resCount;
	}

	public void setChunkCount(int chunkCount) {
		this.chunkCount = chunkCount;
	}

	public void setStringOffset(int stringOffset) {
		this.stringOffset = stringOffset;
	}

	public void setChunkMetaOffset(int chunkMetaOffset) {
		this.chunkMetaOffset = chunkMetaOffset;
	}

	public void setChunkMetaSize(int chunkMetaSize) {
		this.chunkMetaSize = chunkMetaSize;
	}
	
}
