package tk.greydynamics.Maths;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.CompressionUtils;

public class Patcher {
	public static byte[] getPatchedData(byte[] decompressedBase, byte[] delta){
		ArrayList<Byte> patchedData = getPatchedData(null, decompressedBase, delta, new FileSeeker("BASE in PATCHER"), new FileSeeker("DELTA in PATCHER"), false);
		if (patchedData!=null){
			return FileHandler.toByteArray(patchedData);
		}
		return null;
	}
	
	private static ArrayList<Byte> getPatchedData(ArrayList<Byte> patchedData, byte[] decompressedBase, byte[] delta, FileSeeker baseSeeker, FileSeeker deltaSeeker, boolean isSubordinated){
		if (decompressedBase.length == 0 || delta.length == 0){
			System.err.println("Could not patch data, because of 0 length.");
			return null;
		}
		int newBaseOffset = baseSeeker.getOffset();
		int newDeltaOffset = deltaSeeker.getOffset();
		
		/*FileHandler.writeFile("output/debug/patchType2_base", decompressedBase);
		FileHandler.writeFile("output/debug/patchType2_delta", delta);
		*/
		
		int type = 0;
		int procSize = 0;
		int patchedSize = 0;
		
		int offset = 0;
		int removeBytes = 0;
		int addBytes = 0;
		
		if (patchedData==null){
			patchedData = new ArrayList<>();
		}
		
		System.out.println("Type Check @ "+deltaSeeker.getOffset());
		System.out.println("Output bytes: "+patchedData.size());
		type = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN);// Type 0x2000 if its not compressed. 0x1000 if its compressed.
		System.out.println("Type "+type);
		if (deltaSeeker.getOffset()>=0){
			System.out.println("BREAK!");
		}
		if (type==0x1000){
			int numEntries = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF;
			System.out.println("Num Entries "+numEntries);
			byte[] compressedDelta = null;
			
			for (int i=0; i<numEntries; i++){
				System.out.println("Entrie "+i+" @ "+deltaSeeker.getOffset());
				/*get entry information*/
				offset = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF;
				removeBytes = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF;
				System.out.println("Offset "+offset);
				if (deltaSeeker.hasError()){return null;}
				
				/*fill up to offset*/
				while(baseSeeker.getOffset()<offset+newBaseOffset){
					patchedData.add(FileHandler.readByte(decompressedBase, baseSeeker));
					if (baseSeeker.hasError()){return null;}
				}
								
				/*take a look into block logic to obtain the compressed size*/
				deltaSeeker.seek(6);//4 bytes decompressed size, 2 bytes type
				int compressedSize = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF;
				compressedDelta = new byte[compressedSize+8/*compressed size is without header, so add decompsize, type and compressedsize to it!*/];
				if (deltaSeeker.hasError()){return null;}
				
				/*go back the beginning of the block logic and fill up the byte array*/
				deltaSeeker.seek(-8);
				for (int ix=0; ix<compressedDelta.length; ix++){
					compressedDelta[ix] = FileHandler.readByte(delta, deltaSeeker);
				}
				
				/*decompress the extracted block logic and add it to return data.*/
				byte[] rawBlock = CompressionUtils.convertToRAWData(compressedDelta);
				if (rawBlock==null||!FileHandler.addBytes(rawBlock, patchedData)){return null;}
				System.out.println("RawBlockSize "+rawBlock.length);
				/*skip bytes from base, defined by entry information*/
				baseSeeker.seek(removeBytes);		
				System.out.println("Base Seeking "+removeBytes);
			}
		}else if (type==0x2000){
			procSize = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF;
			patchedSize = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF; // (CONTAINS PATCHED SIZE)
			System.out.println("procSize "+procSize);
			System.out.println("patchedSize "+patchedSize);
			
			int procOffset = deltaSeeker.getOffset();
			System.out.println("procOffset "+procOffset);
			
			byte basedata = 0;
			byte deltadata = 0;
			
			//fill spaces - patch data
			while(deltaSeeker.getOffset()<procOffset+procSize){
				
				//*MAY USING LEB128 (DOES EVEN BIG_END. ENCODING EXIST ?)*//
				offset = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF;
				removeBytes = FileHandler.readByte(delta, deltaSeeker)&0xFF;
				addBytes = FileHandler.readByte(delta, deltaSeeker)&0xFF;
				System.out.println("Offset "+offset);
				System.out.println("addBytes "+addBytes);
				
				//System.out.println("Offset: "+offset+" Rem: "+removeBytes+" Add: "+addBytes);
				
				//filldata up to offset
				while(baseSeeker.getOffset()<offset+newBaseOffset){
					basedata = FileHandler.readByte(decompressedBase, baseSeeker);
					if (!baseSeeker.hasError()){
						patchedData.add(basedata);
					}else{
						System.err.println("Error while patching, can't fill up bytes. (maybe out of bounds)");
						dump(decompressedBase, delta);
						return null;
					}
				}
				//remove
				baseSeeker.seek(removeBytes);
				System.out.println("Base Seeking "+removeBytes);
				
				//add
				for (int patchIndex=0; patchIndex<addBytes; patchIndex++){
					deltadata = FileHandler.readByte(delta, deltaSeeker);
					if (!deltaSeeker.hasError()){
						patchedData.add(deltadata);
					}else{
						dump(decompressedBase, delta);
						System.err.println("Error while patching, can't get delta bytes. (maybe out of bounds)");
						return null;
					}
				}	
			}
			
			//fill up left over data
			if (patchedData.size()<patchedSize){
				System.out.println("Fillup Data: "+(patchedSize-patchedData.size()));
				while (patchedData.size()<patchedSize){
					basedata = FileHandler.readByte(decompressedBase, baseSeeker);
					if (!baseSeeker.hasError()){
						patchedData.add(basedata);
					}else{
						System.err.println("Error while patching, can't fill up the ---leftover--- bytes. (maybe out of bounds)");
						dump(decompressedBase, delta);
						return null;
					}
				}
				/*
				if (baseSeeker.getOffset() < decompressedBase.length){
					while (baseSeeker.getOffset() < decompressedBase.length){
						basedata = FileHandler.readByte(decompressedBase, baseSeeker);
						if (!baseSeeker.hasError()){
							patchedData.add(basedata);
						}else{
							System.err.println("Error while patching, can't fill up the ---leftover--- bytes. (maybe out of bounds)");
							dump(decompressedBase, delta);
							return null;
						}
					}
				}else{
					dump(decompressedBase, delta);
					System.err.println("Patched size is smaller as given one :( ["+patchedData.size()+"/"+patchedSize+"]");
					return null;
				}*/
			}
			if (baseSeeker.hasError() || deltaSeeker.hasError()){
				dump(decompressedBase, delta);
				return null;
			}
			//System.err.println("DELTA LEFT OVER: "+(delta.length-procSize)+", starting at "+deltaSeeker.getOffset());
		}else if (type==0x3000){
			
			//Same as 0x1000 but without offset and remove bytes for each entry!
			int numEntries = FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)&0xFFFF;
			System.out.println("Num Entries: "+numEntries);
			
			byte[] compressedDelta = null;
			
			for (int i=0; i<numEntries; i++){
				System.out.println("Entry: "+i+" @ "+deltaSeeker.getOffset());
				/*take a look into block logic to obtain the compressed size*/
				deltaSeeker.seek(6);//4 bytes decompressed size, 2 bytes type
				compressedDelta = new byte[FileHandler.readShort(delta, deltaSeeker, ByteOrder.BIG_ENDIAN)+8/*compressed size is without header, so add decompsize, type and compressedsize to it!*/];
				if (deltaSeeker.hasError()){return null;}
				
				/*go back the beginning of the block logic and fill up the byte array*/
				deltaSeeker.seek(-8);
				for (int ix=0; ix<compressedDelta.length; ix++){
					compressedDelta[ix] = FileHandler.readByte(delta, deltaSeeker);
				}
				
				/*decompress the extracted block logic and add it to return data.*/
				byte[] rawBlock = CompressionUtils.convertToRAWData(compressedDelta);
				if (rawBlock==null||!FileHandler.addBytes(rawBlock, patchedData)){return null;}
				System.out.println("RawBlock Size: "+rawBlock.length);
				
				//tested, dont skip here!
			}
		}else{
			System.out.println("Type "+type+" is not known inside Patcher!");
			return null;
		}
		if (deltaSeeker.getOffset()<delta.length){
			System.out.println("Data in Delta left, starting new round with the left "+(delta.length-deltaSeeker.getOffset())+" bytes!");
			/*There has to be more!*/
			/*
			WAY 1.
			byte[] morePatchedData = getPatchedData(decompressedBase, delta, baseSeeker, deltaSeeker, true);
			
			
			WAY 2.
			byte[] leftOverBase = new byte[decompressedBase.length-baseSeeker.getOffset()];
			for (int i=0;i<leftOverBase.length;i++){
				leftOverBase[i] = FileHandler.readByte(decompressedBase, baseSeeker);
			}
			byte[] leftOverDelta = new byte[delta.length-deltaSeeker.getOffset()];
			for (int i=0;i<leftOverDelta.length;i++){
				leftOverDelta[i] = FileHandler.readByte(delta, deltaSeeker);
			}
			byte[] morePatchedData = getPatchedData(leftOverBase, leftOverDelta, new FileSeeker("Base"), new FileSeeker("Delta"), true);
			
			WAY 3.
			pass same delta and base but calculate new base offset for relative offsets!
			*/
			//System.out.println("BASE CHANGE: "+(baseSeeker.getOffset()-newBaseOffset));
			return getPatchedData(patchedData, decompressedBase, delta, baseSeeker, deltaSeeker, true);
			
		}
		if (!patchedData.isEmpty()){
			//return FileHandler.convertFromList(patchedData);
			return patchedData;
		}
		return null;
	}
	
	static private void dump(byte[] decompressedBase, byte[] delta){
		FileHandler.writeFile("output/debug/base_in_patcher", decompressedBase);
		FileHandler.writeFile("output/debug/delta_in_patcher", delta);
	}
}
