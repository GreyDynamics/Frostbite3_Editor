package tk.greydynamics.Resource.Frostbite3.ITEXTURE;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.DDS.DDS_HEADER;
import tk.greydynamics.Resource.Frostbite3.Cas.CasDataReader;
import tk.greydynamics.Resource.Frostbite3.Toc.ConvertedTocFile;
import tk.greydynamics.Resource.Frostbite3.Toc.TocEntry;

public class ITextureHandler {
	
	public static byte[] getDSS(byte[] itextureData) {
		if (itextureData==null){return null;}
		FileSeeker seeker = new FileSeeker();
		ITexture itexture = new ITexture(itextureData, seeker);
		String itextureChunkID = FileHandler.bytesToHex(itexture.getChunkID()).toLowerCase();
		
		String sha1 = Core.getGame().getChunkGUIDSHA1().get(itextureChunkID);
		/*
		for (ResourceLink currentChunk : Core.getGame().getCurrentSB().getChunks()){
			if (currentChunk.getId().equalsIgnoreCase(itextureChunkID)){
				System.out.println("DEBUG -> ITEXTURE HANDLER!");
			}
		}*/

		if (sha1 == null) {
			for (ConvertedTocFile commonChunk : Core.getGame()
					.getCommonChunks()) {
				for (TocEntry chunk : commonChunk.getChunks()) {
					if (chunk.getGuid().equalsIgnoreCase(itextureChunkID)) {
						sha1 = chunk.getSha1();
						System.out.println("Chunk successfully found!");
						break;
					}
				}
				if (sha1 != null) {
					break;
				}
			}
		}
		if (sha1 == null) {
			System.err.println("DDS not created from ITexture because BlockData could't be found!\n"
							 + "ChunkID: "+itextureChunkID);
			return null;
		}
		byte[] data = CasDataReader.readCas(null, null, sha1, 666);
		if (data != null) {
			/*BlockData found :)*/
			
			DDS_HEADER newDDSHeader = ITextureConverter.getDDSHeader(itexture);
			byte[] ddsHeaderArray = newDDSHeader.toBytes();
			
			byte[] ddsFileData = new byte[ddsHeaderArray.length + data.length];
			FileSeeker ddsTargetSeeker = new FileSeeker();
			FileHandler.addBytes(ddsHeaderArray, ddsFileData, ddsTargetSeeker);
			FileHandler.addBytes(data, ddsFileData, ddsTargetSeeker);
			data = null;
			ddsHeaderArray = null;
			return ddsFileData;
		} else {
			System.err.println("Could not get data from cas for SHA1: "+sha1+"\nITexture to DDS conv. failed!");
			return null;
		}
	}
}
