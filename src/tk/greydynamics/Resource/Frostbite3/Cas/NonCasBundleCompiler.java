package tk.greydynamics.Resource.Frostbite3.Cas;

import java.io.File;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.Block;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.BlockHeader;

public class NonCasBundleCompiler {
	public static String compileUnpatchedBundle(NonCasBundle nonCasBundle, ByteOrder order, String targetFolder){
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~//
		String targetPath = targetFolder+"/"+nonCasBundle.getName().replace(".", "_").replace("/", "_").replace("\\", "_");
		File targetFile = new File(targetPath);
		int fileNr = 1;
		while (targetFile.exists()){
			targetFile = new File(targetPath+"_"+fileNr);
		}
		targetPath = FileHandler.normalizePath(targetFile.getAbsolutePath());
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~//
		
		if (nonCasBundle.getHeader()==null){
			System.err.println("Compiling NonCasBundle: require a predefined Header!");
			//This is needed, because FourCC varies for each game.
			return null;
		}
		System.out.println("CompileUnpatchedBundle: "+nonCasBundle.getName());
		/*
		 Integer MetaSize. (Size of Meta + String | Starting after this Integer.)
		  Meta (Header, SHA1, EBX, RES, RESType, RESMeta, RESRid, Chunks)
		  String
		 Payload
		 Chunk Payload (offsets!)
		*/
		ArrayList<Byte> bundleBytes = new ArrayList<>();
		ArrayList<Byte> metaBytes = new ArrayList<>();
		HashMap<String, Integer> stringMap = new HashMap<>();
		ArrayList<Byte> stringBytes = new ArrayList<>();
		ArrayList<String> strings = new ArrayList<>();
		ArrayList<Byte> payload = new ArrayList<>();
		
		//Make sure its all right and nothing is missing!
		nonCasBundle.getEntries().clear();
		System.out.println("CompileUnpatchedBundle: Create Stringsection and recompile offsets.");
		for (NonCasBundleEntry ebxBundleEntry : nonCasBundle.getEbx()){
			int nameOffset = getNameOffset(ebxBundleEntry.getName(), stringMap, stringBytes);
			ebxBundleEntry.setNameOffset(nameOffset);
			nonCasBundle.getEntries().add(ebxBundleEntry);
		}
		for (NonCasBundleEntry resBundleEntry : nonCasBundle.getRes()){
			int nameOffset = getNameOffset(resBundleEntry.getName(), stringMap, stringBytes);
			resBundleEntry.setNameOffset(nameOffset);
			nonCasBundle.getEntries().add(resBundleEntry);
		}
		System.out.println("CompileUnpatchedBundle: Creating SHA1 List.");
		nonCasBundle.getSha1List().clear();
		int sha1ListSize = 0;
		String sha1tempFilePath = Core.EDITOR_PATH_TEMP+UUID.randomUUID().toString().replace("-", "")+"_sha1list.tmp";
		for (NonCasBundleEntry bundleEntry : nonCasBundle.getEntries()){
			if (bundleEntry.getSha1()!=null){
				byte[] sha1Bytes = FileHandler.hexStringToByteArray(bundleEntry.getSha1());
				sha1ListSize+=sha1Bytes.length;
				FileHandler.writeFile(sha1tempFilePath, sha1Bytes, true, false);
			}else{
				System.err.println("Compiling NonCasBundle: missing sha1 for entry!");
				return null;
			}
		}
		for (NonCasBundleChunkEntry bundleChunkEntry : nonCasBundle.getChunks()){
			if (bundleChunkEntry.getSha1()!=null){
				byte[] sha1Bytes = FileHandler.hexStringToByteArray(bundleChunkEntry.getSha1());
				sha1ListSize+=sha1Bytes.length;
				FileHandler.writeFile(sha1tempFilePath, sha1Bytes, true, false);
			}else{
				System.err.println("Compiling NonCasBundle: missing sha1 for chunkEntry!");
				return null;
			}
		}
		System.out.println("CompileUnpatchedBundle: Recalculating Header.");
		nonCasBundle.getHeader().setTotalCount(nonCasBundle.getEntries().size() + nonCasBundle.getChunks().size());
		nonCasBundle.getHeader().setEbxCount(nonCasBundle.getEbx().size());
		nonCasBundle.getHeader().setResCount(nonCasBundle.getRes().size());
		nonCasBundle.getHeader().setChunkCount(nonCasBundle.getChunks().size());
		
		System.out.println("CompileUnpatchedBundle: Create Metasection.");
		//Fill Meta from Entries
		for (NonCasBundleEntry ebxBundleEntry : nonCasBundle.getEbx()){
			ArrayList<Byte> ebxEntry = ebxBundleEntry.getEntryBytes(order);
			FileHandler.addBytes(ebxEntry, metaBytes);
		}
		for (NonCasBundleEntry resBundleEntry : nonCasBundle.getRes()){
			ArrayList<Byte> resEntry = resBundleEntry.getEntryBytes(order);
			FileHandler.addBytes(resEntry, metaBytes);
		}
		
		for (NonCasBundleEntry resBundleEntry : nonCasBundle.getRes()){
			//resType
			byte[] resType = FileHandler.toBytes((int) resBundleEntry.getResTypeInt(), order);
			FileHandler.addBytes(resType, metaBytes);
		}
		for (NonCasBundleEntry resBundleEntry : nonCasBundle.getRes()){
			//resMeta
			byte[] resMeta = resBundleEntry.getResMeta();
			FileHandler.addBytes(resMeta, metaBytes);
		}
		for (NonCasBundleEntry resBundleEntry : nonCasBundle.getRes()){
			//resRid
			byte[] resRid = FileHandler.toBytes((long) resBundleEntry.getResRid(), order);
			FileHandler.addBytes(resRid, metaBytes);
		}
			//chunkEntry
		String chunkPayloadtempFilePath = Core.EDITOR_PATH_TEMP+UUID.randomUUID().toString().replace("-", "")+"_chunkPayload.tmp";
		int chunkPayloadSize = 0;
		int indexDebug = 0;
		for (NonCasBundleChunkEntry bundleChunkEntry : nonCasBundle.getChunks()){
			System.out.println("CompileUnpatchedBundle: Create ChunkEntries and Chunkpayload for "+bundleChunkEntry.getId()+".");
			byte[] chunkData = null;
			if (bundleChunkEntry.getModFilePath()!=null){
			   //Modded Entry		
				byte[] uncompressedChunkData = FileHandler.readFile(bundleChunkEntry.getModFilePath());//uncompressed
//				chunkData = null;//TODO some compression LOGIC ??;
				//setLogicalSize! (decompressed size)
				System.err.println("Modify Chunks not implemented in nonCas compiler!!");
				chunkData = null;
				return null;
			}else{	
			   //Original UNCHANGED Entry
				chunkData = NonCasDataReader.readRawNonCasBundleChunk(nonCasBundle, bundleChunkEntry);
					//size is the same.
			}
			indexDebug++;
			//Chunks Payload
			FileHandler.writeFile(chunkPayloadtempFilePath, chunkData, true, false);
			chunkPayloadSize += chunkData.length;
			ArrayList<Byte> bundleChunkEntryBytes = bundleChunkEntry.getChunkEntryBytes(order);
			FileHandler.addBytes(bundleChunkEntryBytes, metaBytes);
		}
			//chunkMeta (TOC Structure)
		System.out.println("CompileUnpatchedBundle: Create/Copy ChunkMeta information.");
		for (byte b : nonCasBundle.getChunkMeta()){
			metaBytes.add(b);
		}
		
		//Names (Strings zero canceled out)
		nonCasBundle.getHeader().setStringOffset(NonCasBundleHeader.HEADER_BYTESIZE+sha1ListSize+metaBytes.size());//metaOffset()+header.getStringOffset() -> so its relative
			//Padding
		while ((4+nonCasBundle.getHeader().getStringOffset()+stringBytes.size())%16!=0){
			stringBytes.add((byte) 0x0);
		}
		System.out.println("CompileUnpatchedBundle: Combine String- and Meta Sections.");
		FileHandler.addBytes(stringBytes, metaBytes);
		int stringBytesSize = stringBytes.size();
		stringBytes = null;
			

		System.out.println("CompileUnpatchedBundle: Compile Header.");
		ArrayList<Byte> metaHeaderBytes = nonCasBundle.getHeader().getHeaderBytes(order);
		if (metaHeaderBytes==null){
			System.err.println("Compiling NonCasBundle: unable to fetch header bytes!");
		}
		System.out.println("CompileUnpatchedBundle: Append and write Header to Bundle.");
		
		//Glue together
		FileHandler.addBytes(FileHandler.toBytes((int) (nonCasBundle.getHeader().getStringOffset()+stringBytesSize), order), bundleBytes);//total Meta size
		FileHandler.addBytes(metaHeaderBytes, bundleBytes);//Header
		FileHandler.writeFile(targetPath, bundleBytes, true, true);//write header
		bundleBytes.clear();
		metaHeaderBytes = null;
		System.out.println("CompileUnpatchedBundle: Append and write SHA1List to Bundle");
		FileHandler.extendFileFromFile(sha1tempFilePath, 0, sha1ListSize, targetPath, new FileSeeker());//write SHA1's
		FileHandler.addBytes(metaBytes, bundleBytes);//contains meta + string
		metaBytes = null;
		System.out.println("CompileUnpatchedBundle: Write Meta to Bundle.");
		FileHandler.writeFile(targetPath, bundleBytes, true, true);//write meta from bundle to file.
		bundleBytes.clear();
		System.out.println("CompileUnpatchedBundle: Append Payload to Bundle.");
		//Payload Section (Append to existing file!)
		for (NonCasBundleEntry entry : nonCasBundle.getEntries()){
			byte[] entryData = null;
			if (entry.getModFilePath()!=null){
			   //Modded Entry
				byte[] uncompressedEntryData = FileHandler.readFile(entry.getModFilePath());//uncompressed
				entryData = FileHandler.toByteArray(Block.compressBlock(uncompressedEntryData, BlockHeader.BlockType_UnCompressed));				
			}else{
			   //Original UNCHANGED Entry				
				entryData = FileHandler.readFile(
						nonCasBundle.getBasePath(),
						nonCasBundle.getBaseOffset()+entry.getBaseOffset(),//Entries BaseOffset is relative to the Bundles start offset.
						entry.getBaseSize());
			}
			FileHandler.addBytes(entryData, payload);
			FileHandler.writeFile(targetPath, payload, true, false);//write payload to file.
			payload.clear();
		}
		System.out.println("CompileUnpatchedBundle: Append ChunkPayload to Bundle.");
		FileHandler.extendFileFromFile(chunkPayloadtempFilePath, 0, chunkPayloadSize, targetPath, new FileSeeker());

		
		
		System.out.println("CompileUnpatchedBundle: Cleaning temporary data.");
		new File(chunkPayloadtempFilePath).delete();
		new File(sha1tempFilePath).delete();
		System.out.println("CompileUnpatchedBundle: DONE!");
		return targetPath;
	}
	
	public static int getNameOffset(String name, HashMap<String, Integer> stringMap, ArrayList<Byte> stringBytes){
		for (String s : stringMap.keySet()){
			if (s.equals(name)){
				return stringMap.get(s);
			}
		}
		int newNameOffset = stringBytes.size();
		stringMap.put(name, newNameOffset);
		byte[] newNameBytes = name.getBytes();
		FileHandler.addBytes(newNameBytes, stringBytes);
		if (newNameBytes[newNameBytes.length-1]!=0x0){
			stringBytes.add((byte) 0x0);
		}
		return newNameOffset;
	}
}
