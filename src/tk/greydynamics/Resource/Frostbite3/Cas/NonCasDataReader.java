package tk.greydynamics.Resource.Frostbite3.Cas;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.CompressionUtils;

public class NonCasDataReader {
	public static byte[] readNonCasBundleData(NonCasBundle nonCasBundle, NonCasBundleEntry bundleEntry){
		if (bundleEntry!=null&&nonCasBundle!=null){
			byte[] blocks = FileHandler.readFile(nonCasBundle.getBasePath(), nonCasBundle.getBaseOffset()+bundleEntry.getBaseOffset(), bundleEntry.getBaseSize());
			if (blocks!=null){
				byte[] decompressed = CompressionUtils.convertToRAWData(blocks);
				return decompressed;
			}
		}
		return null;
	}
}
