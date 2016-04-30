package tk.greydynamics.Resource.Frostbite3.Cas.Data;

import java.nio.ByteOrder;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class BlockHeader {
	private int decompressedSize = -1;
	private short compressionType = -1;
	private int compressedSize = -1;
	
	public static short BlockType_Compressed_LZ4 = 0x0970;//Custom Compressed using a LZ4 algorithm, part of the LZ77 Family!
	public static short BlockType_UnCompressed = 0x0070;
	public static short BlockType_UnCompressed2 = 0x0071;
	public static short BlockType_EmtryPayload = 0x0;
	public static short BlockType_Compressed_DAI = 0x0270;//Dragon Age Inquisition
	
	public static short BlockHeader_Size = 8;
	
	public static BlockHeader readHeader(byte[] encodedEntry, FileSeeker seeker){
		if (encodedEntry!=null&&seeker!=null){
			if (seeker.getOffset()+BlockHeader_Size<=encodedEntry.length){
				return new BlockHeader(
					FileHandler.readInt(encodedEntry, seeker, ByteOrder.BIG_ENDIAN), //decompressedSize
					FileHandler.readShort(encodedEntry, seeker, ByteOrder.BIG_ENDIAN), //compressionType
					FileHandler.readShort(encodedEntry, seeker, ByteOrder.BIG_ENDIAN) & 0xFFFF //compressedSize
				);
			}
		}
		return null;
	}
	
	public BlockHeader(int decompressedSize, short compressionType, int compressedSize) {
		this.decompressedSize = decompressedSize;
		this.compressionType = compressionType;
		this.compressedSize = compressedSize;
	}
	
	public int getDecompressedSize() {
		return decompressedSize;
	}
	public void setDecompressedSize(int decompressedSize) {
		this.decompressedSize = decompressedSize;
	}
	public short getCompressionType() {
		return compressionType;
	}
	public void setCompressionType(short compressionType) {
		this.compressionType = compressionType;
	}
	public int getCompressedSize() {
		return compressedSize;
	}
	public void setCompressedSize(int compressedSize) {
		this.compressedSize = compressedSize;
	}
	
	
	
}
