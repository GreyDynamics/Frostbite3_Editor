package tk.greydynamics.Resource.Frostbite3.Cas.Data;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Maths.LZ4;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class CompressionUtils {
	public static byte[] convertToRAWData(byte[] data){
		FileSeeker seeker = new FileSeeker("CompressionUtils");
		ArrayList<Byte> output = new ArrayList<Byte>();
		while(seeker.getOffset()<data.length){
			byte[] decompressedBlock = readBlock(data, seeker);
			if (decompressedBlock != null && !seeker.hasError()){
				for (Byte b : decompressedBlock){
					output.add(b);
					/*DEBUG
					if (output.size()==1398120){
						System.out.println(seeker.getOffset());
					}*/
				}
			}else{
				System.err.println("CompressionUtils was not able to decode Block! - Following operations will fail!");
				return null;
			}
		}//End of InputStream
		return FileHandler.convertFromList(output);
	}
	
	public static byte[] readBlock(byte[] encodedEntry, FileSeeker seeker){
		//FileHandler.writeFile("output/readBlock", encodedEntry);
		BlockHeader header = BlockHeader.readHeader(encodedEntry, seeker);
		if (header==null){
			return null;
		}
		
		if (header.getCompressionType() == BlockHeader.BlockType_Compressed){//COMPRESSED
			//rawData = LZ4.decompress(FileHandler.readByte(encodedEntry, seeker, procSize-seeker.getOffset()));
			byte[] lz4data = FileHandler.readByte(encodedEntry, seeker, header.getCompressedSize());
			if (lz4data==null){return null;}
			byte[] rawData = LZ4.decompress(lz4data);
			if (rawData.length<header.getDecompressedSize()){
				System.err.println("Decompressed file size does not match the cas.cat given one. "+rawData.length+" of "+header.getDecompressedSize()+" Bytes loaded.");
			}
			return rawData;
		}else if (header.getCompressionType() == BlockHeader.BlockType_UnCompressed || header.getCompressionType() == BlockHeader.BlockType_UnCompressed2){//UNCOMPRESSED
			if (header.getCompressedSize()==0x0){
				header.setCompressedSize(header.getDecompressedSize());
			}
			//return FileHandler.readByte(encodedEntry, seeker, procSize-seeker.getOffset());
			return FileHandler.readByte(encodedEntry, seeker, header.getCompressedSize());
		}else if (header.getCompressionType() == BlockHeader.BlockType_EmtryPayload){ // 0x0000 - emty payload
			//seeker.setOffset(seeker.getOffset()-2); //NULL compressionSize
			System.err.println("CompressionUtils needs some help. 0x0000 emty payload"); //TODO
			//return FileHandler.readByte(encodedEntry, seeker, compressedSize);
			return null;
		}else if (header.getCompressionType() == BlockHeader.BlockType_DAI_Compressed){
			System.err.println("'Dragon Age Inquisition' is not supported yet. Please be patient! \n If you know the Compression type of it, let me know via twittah ;)");
			return null;
		}else{
			System.err.println("Compression type " + FileHandler.bytesToHex(FileHandler.toBytes(header.getCompressionType(), ByteOrder.LITTLE_ENDIAN))+" is not defined in CompressionUtils.");
			FileHandler.writeFile("output/debug/error_readBlock.tmp", encodedEntry);
			return null;
		}
	}
	
	public static int seekBlockData(byte[] encodedEntry, FileSeeker seeker){
		BlockHeader header = BlockHeader.readHeader(encodedEntry, seeker);
		if (header!=null){
			if (header.getCompressionType()==BlockHeader.BlockType_Compressed){
				seeker.seek(header.getCompressedSize());//seek to next block.
				return header.getDecompressedSize();
			}else if (header.getCompressionType()==BlockHeader.BlockType_UnCompressed || header.getCompressionType()==BlockHeader.BlockType_UnCompressed2 || header.getCompressionType()==BlockHeader.BlockType_EmtryPayload){
				seeker.seek(header.getDecompressedSize());//seek to next block.
				return header.getDecompressedSize();
			}else{
				System.err.println("Can't Seek DataBlock, unknown Format: "+header.getCompressionType()+"!");
			}
		}
		return 0xFFFFFFFF;
	}
}
