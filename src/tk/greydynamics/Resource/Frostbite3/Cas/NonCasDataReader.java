package tk.greydynamics.Resource.Frostbite3.Cas;

import java.nio.ByteOrder;

import org.lwjgl.util.vector.Vector2f;

import tk.greydynamics.Maths.Bitwise;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.Patcher;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.CompressionUtils;

public class NonCasDataReader {
	public static byte[] readNonCasBundleData(NonCasBundle nonCasBundle, NonCasBundleEntry bundleEntry){
		if (bundleEntry!=null&&nonCasBundle!=null){
//			If patched gets converted to base, this if logic is not needed.
//			if (nonCasBundle.isPatched()){
//				ByteOrder order = ByteOrder.BIG_ENDIAN;
//				byte[] baseBlocks = FileHandler.readFile(nonCasBundle.getBasePath(), nonCasBundle.getBaseOffset()+bundleEntry.getBaseOffset(), bundleEntry.getBaseSize());
//				if (baseBlocks!=null){
//					byte[] decompressedBase = CompressionUtils.convertToRAWData(baseBlocks);
//					baseBlocks=null;
////					System.out.println("Read Delta: "+nonCasBundle.getDeltaOffset()+bundleEntry.getDeltaOffset()+" "+bundleEntry.getDeltaSize());
//					byte[] deltaBytes = FileHandler.readFile(nonCasBundle.getBasePath(), nonCasBundle.getDeltaOffset()+bundleEntry.getDeltaOffset(), bundleEntry.getDeltaSize());
//					if (decompressedBase!=null&&deltaBytes!=null){
//						return Patcher.getPatchedNONCASData(decompressedBase, deltaBytes, order);
//					}
//				}
//			}else{
				byte[] blocks = FileHandler.readFile(nonCasBundle.getBasePath(), nonCasBundle.getBaseOffset()+bundleEntry.getBaseOffset(), bundleEntry.getBaseSize());
				if (blocks!=null){
					byte[] decompressed = CompressionUtils.convertToRAWData(blocks);
					return decompressed;
				}
//			}
		}
		return null;
	}
	
	public static byte[] readRawNonCasBundleChunk(NonCasBundle nonCasBundle, NonCasBundleChunkEntry bundleChunkEntry){
		if (bundleChunkEntry!=null&&nonCasBundle!=null){
			byte[] blocks = FileHandler.readFile(nonCasBundle.getBasePath(),
					nonCasBundle.getBaseOffset()+nonCasBundle.getOriginalChunkPayloadOffset()+
						bundleChunkEntry.getRelBundleOffset(),
					bundleChunkEntry.getRawPayloadSize());
			return blocks;
		}
		return null;
	}
}
