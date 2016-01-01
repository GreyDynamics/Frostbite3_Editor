package tk.greydynamics.Mod;

import java.io.File;

import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.CasCatEntry;
import tk.greydynamics.Resource.Frostbite3.Cas.CasCatManager;
import tk.greydynamics.Resource.Frostbite3.Cas.CasManager;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class ResourceModifier {
	
	public static boolean updateSBPart(CasBundle sbPart){
		/*
		alignMembers
		ridSupport
		storeCompressedSizes
		totalSize ------> get updated in tocCreator :)
		dbxTotalSize
		*/
		return false;
	}
	
	//_EBX, DBX, _RES, _CHUNKS, CHUNKMETA
	public static boolean updateResLink(ResourceLink resLink, byte[] data, CasBundle sbPart, File cas, CasCatManager casCatMan){
		CasCatEntry casCatEntry = CasManager.extendCAS(data, cas, casCatMan);
		if (casCatEntry != null){
			for (ResourceLink res : sbPart.getRes()){
				if (res.equals(resLink)){
					res.setBaseSha1(null);
					res.setDeltaSha1(null);
					res.setCasPatchType(1); //->Replace
					res.setSha1(casCatEntry.getSHA1());
					
					res.setOriginalSize(data.length);//TEST
					res.setSize(casCatEntry.getProcSize());//TEST
					
					casCatMan.getEntries().add(casCatEntry);
					return true;
				}
			}
			/*
			int resType;
			byte[] resMeta; // 0x13 RAW2
			long resRid;
			byte[] idata; // 0x13 RAW2*/
			System.err.println("Cannot handle new RES-Resources at this time. -> Please use replace instead.");
			return false;
		}
		System.err.println("Could't update RES-ResourceLink. CasManager->ExtendCAS returns null.");
		return false;
	}
	
	
	public static boolean updateEbxLink(ResourceLink ebxLink, byte[] data, CasBundle sbPart, File cas, CasCatManager casCatMan){
		CasCatEntry casCatEntry = CasManager.extendCAS(data, cas, casCatMan);
		if (casCatEntry != null){
			for (ResourceLink ebx : sbPart.getEbx()){
				if (ebx.equals(ebxLink)){
					ebx.setBaseSha1(null);
					ebx.setDeltaSha1(null);
					ebx.setCasPatchType(1); //->Replace
					ebx.setSha1(casCatEntry.getSHA1());
					casCatMan.getEntries().add(casCatEntry);
					
					ebx.setOriginalSize(data.length);//TEST
					ebx.setSize(casCatEntry.getProcSize());//TEST
					
					return true;
				}
			}
			System.err.println("Cannot handle new EBX-Resources at this time. -> Please use replace instead.");
			return false;
		}
		System.err.println("Could't update EBX-ResourceLink. CasManager->ExtendCAS returns null.");
		return false;
	}
	public static boolean updateChunkLink(ResourceLink chunkLink, byte[] data, CasBundle sbPart, File cas, CasCatManager casCatMan){
		CasCatEntry casCatEntry = CasManager.extendCAS(data, cas, casCatMan);
		if (casCatEntry != null){
			for (ResourceLink chunk : sbPart.getChunks()){
				if (chunk.equals(chunkLink)){
					chunk.setBaseSha1(null);
					chunk.setDeltaSha1(null);
					chunk.setCasPatchType(1); //->Replace
					chunk.setSha1(casCatEntry.getSHA1());
					
					chunk.setSize(casCatEntry.getProcSize());//TEST
					chunk.setLogicalOffset(0);//TEST
					chunk.setLogicalSize(data.length);//TEST
					
					casCatMan.getEntries().add(casCatEntry);
					return true;
				}
			}
			System.err.println("Cannot handle new Chunks at this time. -> Please use replace instead.");
			return false;
		}
		System.err.println("Could't update Chunks. CasManager->ExtendCAS returns null.");
		return false;
	}
	//+chunkMeta ?
}
