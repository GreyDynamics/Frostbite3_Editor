package tk.greydynamics.Resource.Frostbite3.Cas.Data;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;

public class Block {
//	static byte[] compressionType = new byte[] {(byte) 0x00, (byte) 0x70};
	static int blockSize = 0x10000;
	static int blockContent = 0xFAD8&0xFFFF;
	public static int blockHeaderNumBytes = 8;//4 size, 2 type, 2 compressed size
	
	
	public static ArrayList<Byte> compressBlock(byte[] decompressedBytes, short compression){
		if (decompressedBytes==null){return null;}
		ArrayList<Byte> procEntries = new ArrayList<Byte>();
		if (compression == BlockHeader.BlockType_Compressed_LZ4||compression == BlockHeader.BlockType_Compressed_DAI){
			System.err.println("Compression for "+compression+" not implemented yet.");
			System.err.println("Trying to go uncompressed.");
			compression = BlockHeader.BlockType_UnCompressed;
		}
		byte[] compressionType = FileHandler.toBytes((short) compression, ByteOrder.BIG_ENDIAN);
		
		/*HANDLE BLOCK LOGIC*/
		int blocks = calculateNumberOfBlocks(decompressedBytes.length);
		//System.out.println(blockContent);
		System.out.println("Building "+(blocks+1)+" blocks in total.");
		int restLen = decompressedBytes.length - (blocks * blockContent);
		for (int i=0; i<blocks; i++){
			FileHandler.addBytes(FileHandler.toBytes(blockSize, ByteOrder.BIG_ENDIAN), procEntries);//int
			FileHandler.addBytes(compressionType, procEntries);
			FileHandler.addBytes(FileHandler.toBytes((short) (blockContent&0xFFFF), ByteOrder.BIG_ENDIAN), procEntries);
			FileHandler.addBytes(decompressedBytes, procEntries, i*blockContent, blockContent);
		}
		
		/*FILL REST*/
		FileHandler.addBytes(FileHandler.toBytes(restLen, ByteOrder.BIG_ENDIAN), procEntries);
		FileHandler.addBytes(compressionType, procEntries); // 0x0070 -- uncompressed || 0x0970 lz4 compressed || 0x0071 -- uncompressed no payload || 0x0000 -- empty payload
		FileHandler.addBytes(FileHandler.toBytes((short) restLen, ByteOrder.BIG_ENDIAN), procEntries);
		FileHandler.addBytes(decompressedBytes, procEntries, blocks*blockContent, restLen);
		
		return procEntries;
	}
	
	public static int calculateNumberOfBlocks(int rawLength){
		return rawLength / blockContent;
	}

}
