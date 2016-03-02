package tk.greydynamics.Resource.Frostbite3.Cas;

import java.nio.ByteOrder;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import tk.greydynamics.Maths.Bitwise;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.CompressionUtils;
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter;
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter.ResourceBundleType;

public class NonCasBundle2 extends Bundle{
	
	private NonCasBundleHeader header = null;
	private ArrayList<String> sha1List = new ArrayList<>();
	private ArrayList<NonCasBundleEntry> ebx = new ArrayList<>(), res = new ArrayList<>(), entries = new ArrayList<>();
	private ArrayList<NonCasBundleChunkEntry> chunks = new ArrayList<>();
	private byte[] chunkMeta = null;
	private int originalPayloadOffset = -1;
	private int originalChunkPayloadOffset = -1;
	private int originalBundleBaseSize = 0;
	private boolean isPatched = false;
		
	private boolean halfInitialized = true;
	
	private int entriesIndex = 0;
	
	public NonCasBundle2 (String basePath, String deltaPath, String name, int baseOffset, int deltaOffset, byte[] baseBytes, byte[] deltaBytes) {
		super(BundleType.UNDEFINED, basePath, deltaPath, name, baseOffset, deltaOffset);
		originalBundleBaseSize = baseBytes.length;
		ByteOrder order = ByteOrder.BIG_ENDIAN;
		
		FileSeeker baseSeeker=new FileSeeker(0);
		FileSeeker deltaSeeker=new FileSeeker(0);
				
		if (deltaBytes==null){//Unpatched
			this.isPatched = false;
			boolean success = setupBundle(baseBytes, baseSeeker, order);
			if (success){
				//seek trough all Blocks in order EBX, RES to optain the Offset.
				this.originalPayloadOffset = baseSeeker.getOffset();
				for (NonCasBundleEntry entry : entries){
					entry.setBaseOffset(baseSeeker.getOffset());
					int currentSize = 0;
//		System.err.println("Entry Payload at "+baseSeeker.getOffset());
					while (currentSize<entry.getOriginalSize()){
						currentSize += CompressionUtils.seekBlockData(baseBytes, baseSeeker);
					}
					entry.setBaseSize(baseSeeker.getOffset()-entry.getBaseOffset());
				}
				this.originalChunkPayloadOffset = baseSeeker.getOffset();
				
				for (NonCasBundleChunkEntry chunkEntry : chunks){
					chunkEntry.setRelBundleOffset(baseSeeker.getOffset()-originalChunkPayloadOffset);
//		System.err.println("Chunk start at "+baseSeeker.getOffset());
					int absOffset = baseSeeker.getOffset();
					int currentSize = 0;
					while (currentSize<chunkEntry.getLogicalSize()){
						currentSize += CompressionUtils.seekBlockData(baseBytes, baseSeeker);
					}
					chunkEntry.setRawPayloadSize(baseSeeker.getOffset()-absOffset);
				}
				this.setType(BundleType.NONCAS_UNPATCHED);
			}else{
				baseSeeker.setError(true);
			}
		}else{
			this.isPatched = true;
			System.out.println("Patched 'Non-Cas' currently not supported!");
			
//			Take a file handle from a delta and a base bundle. Use the delta to patch the base and return bundle containing ebx/res/chunk entries.
//
//			   Each entry has at least
//			        originalSize (uncompressed size of the payload)
//			        baseOffset
//			        baseSize (compressed size)
//			        deltaOffset
//			        deltaSize (compressed size)
//			        midInstructionSize (the remaining number of iterations when a file starts in the middle of an instruction of type 0 or 3)
//			        midInstructionType (0 or 3 if a file starts in the middle of a corresponding instruction, else -1)
//			    which (together with the delta and base file paths) are exactly what's necessary to retrieve, decompress and patch the payload."""
//			    
//			   The function does two things:
//			        1) Use the delta file to patch the metadata section of the base file, then create entries from the patched metadata.
//			        2) Next, go through the payload-related instructions of the delta file, and calculate offsets and (compressed) sizes for each entry.
//			    
//			    the delta file is split in three parts: The first 16 bytes are header, then there's a section to patch the base metadata and then one for the base payload
//			    
			
			long magic = FileHandler.readLong(deltaBytes, deltaSeeker, order);
			if (magic!=0x100000000L){
				System.out.println("The FourCC-Magic seems to be invalid! (Patched-NonCasBundle)\nFollowing operations prob. will fail!");
			}
			
			int deltaMetaSize = FileHandler.readInt(deltaBytes, deltaSeeker, order);
			int deltaPayloadSize = FileHandler.readInt(deltaBytes, deltaSeeker, order);
			
			int deltaPayloadOffset=16+deltaMetaSize; //16 is the size of the header | without deltaOffset, because we use a relative block.
			int deltaEOF=deltaPayloadOffset+deltaPayloadSize; //to break the payload loop later on
			
			//METADATA SECTION
		    int patchMetaSize=FileHandler.readInt(deltaBytes, deltaSeeker, order); //patch refers to the base after applying the delta
		    int baseMetaSize=FileHandler.readInt(baseBytes, baseSeeker, order); //used later on
		    
		    int baseMetaOffset = baseSeeker.getOffset();
		    
		    //Patch Metadata
		    ArrayList<Byte> patchedMeta = new ArrayList<>();
		    FileHandler.addBytes(FileHandler.toBytes(patchMetaSize, order), patchedMeta);	 
		    
		    int instructionType = 0, instructionSize = 0;
		    while (deltaSeeker.getOffset()<deltaPayloadOffset){
		    	Vector2f vec2f = null;
		    	try{
		    		vec2f = Bitwise.split1v7(FileHandler.readInt(deltaBytes, deltaSeeker, order));
		    		instructionType = (int) vec2f.x;
			    	instructionSize = (int) vec2f.y;
			    	//System.out.println("instructionType: "+instructionType+" instructionSize: "+instructionSize);
			    	switch (instructionType) {
						case 0:
							FileHandler.addBytes(FileHandler.readByte(baseBytes, baseSeeker, instructionSize), patchedMeta);//add base bytes
							break;
						case 4:
							baseSeeker.seek(instructionSize); //skip base bytes
							break;
						case 8:
							FileHandler.addBytes(FileHandler.readByte(deltaBytes, deltaSeeker, instructionSize), patchedMeta);//add delta bytes
							break;
						default:
							System.err.println("UNKNOWN PATCHED-NON-CAS PATCHED-META TYPE: "+instructionType);
							break;
					}
		    	}catch (NullPointerException e){
		    		e.printStackTrace();
		    		deltaSeeker.setOffset(deltaPayloadOffset+199999);
		    	}
		    }
		    
		    //the metadata is patched, now read it in to get the entries
		    boolean success = setupBundle(FileHandler.toByteArray(patchedMeta), new FileSeeker(), order);
		    if (success){
		    	int entriesIndex = 0;
		    	
		    	baseSeeker.setOffset(baseMetaSize+4); //bundle starts with int (containing metaSize incl. header but not itself) -> offset shifts +4!
		    	NonCasBundleEntry entry = updateEntryOffset(baseSeeker, deltaSeeker, baseOffset, deltaOffset);
		    	
		    	while (deltaSeeker.getOffset()<(deltaEOF-1)&&!deltaSeeker.hasError()&&!baseSeeker.hasError()){
		    		Vector2f vec2f = null;
			    	try{
			    		vec2f = Bitwise.split1v7(FileHandler.readInt(deltaBytes, deltaSeeker, order));
			    		instructionType = (int) vec2f.x;
				    	instructionSize = (int) vec2f.y;
//				    	System.out.println("instructionType: "+instructionType+" instructionSize: "+instructionSize);
				    	switch (instructionType) {
							case 0: //add base blocks without modification
								for(int i=0; i<instructionSize;i++){
									int fu = CompressionUtils.seekBlockData(baseBytes, baseSeeker);
					                entry.setCurrentSize(entry.getCurrentSize()+fu);
					                if (entry.getCurrentSize()==entry.getOriginalSize()){
					                    entry=next(baseSeeker, deltaSeeker, baseOffset, deltaOffset);//updateSize of current Entry and go to next!
					                    entry.setMidInstructionSize(instructionSize-i-1); //remaining iterations
					                    entry.setMidInstructionType(instructionType);
					                }
								}
								break;
							case 1: //make larger fixes in the base block
								int baseBlock = CompressionUtils.seekBlockData(baseBytes, baseSeeker);
								int prevOffset = 0;
								for (int i=0; i<instructionSize; i++){
									int targetOffset = FileHandler.readShort(deltaBytes, deltaSeeker, order)&0xFFFF;
									int skipSize = FileHandler.readShort(deltaBytes, deltaSeeker, order)&0xFFFF;
									entry.setCurrentSize(entry.getCurrentSize()+targetOffset+prevOffset);
									entry.setCurrentSize(entry.getCurrentSize()+CompressionUtils.seekBlockData(deltaBytes, deltaSeeker));
									prevOffset=targetOffset+skipSize;
									if (entry.getCurrentSize()==entry.getOriginalSize()){
										if (i!=instructionSize-1){
											System.err.println("should be the last instruction.");
											break;
										}
										if (baseBlock-prevOffset!=0){
											System.err.println("there should be no bytes left to read");
										}
									}
									entry.setCurrentSize(entry.getCurrentSize()+baseBlock-prevOffset);
									if (entry.getCurrentSize()==entry.getOriginalSize()){
						            	entry=next(baseSeeker, deltaSeeker, baseOffset, deltaOffset);//updateSize of current Entry and go to next!
						            }
								}
								break;
							case 2: //make tiny fixes in the base block
								CompressionUtils.seekBlockData(baseBytes, baseSeeker);
					            entry.setCurrentSize(entry.getCurrentSize()+(FileHandler.readShort(deltaBytes, deltaSeeker, order)&0xFFFF)+1);
					            deltaSeeker.seek(instructionSize);
					            if (entry.getCurrentSize()==entry.getOriginalSize()){
					            	entry=next(baseSeeker, deltaSeeker, baseOffset, deltaOffset);//updateSize of current Entry and go to next!
					            }
								break;
							case 3: //add delta blocks directly to the payload
								for (int i=0; i<instructionSize; i++){
									entry.setCurrentSize(entry.getCurrentSize()+CompressionUtils.seekBlockData(deltaBytes, deltaSeeker));
									if (entry.getCurrentSize()>=entry.getOriginalSize()){
					                    entry=next(baseSeeker, deltaSeeker, baseOffset, deltaOffset);//updateSize of current Entry and go to next!
					                    entry.setMidInstructionSize(instructionSize-i-1); //remaining iterations
					                    entry.setMidInstructionType(instructionType);
					                }
								}
								break;
							case 4: //skip entire blocks, do not increase currentSize at all
								for (int i=0; i<instructionSize; i++){
									CompressionUtils.seekBlockData(baseBytes, baseSeeker);
								}
								
							default:
								System.err.println("UNKNOWN PATCHED-NON-CAS PATCH TYPE: "+instructionType);
								break;
						}
			    	}catch (NullPointerException e){
			    		e.printStackTrace();
			    		deltaSeeker.setOffset(deltaEOF+199999);
			    	}
		    	}
		    	
		    	//The delta is fully read, but it's not over yet.
		        //Read remaining base blocks until all entries are satisfied (infinite instruction of type 0).
		            
		        //the current entry probably hasn't reached its full size yet and requires manual attention
		        while (entry.getCurrentSize()<entry.getOriginalSize()&&!deltaSeeker.hasError()&&!baseSeeker.hasError()){
		            entry.setCurrentSize(entry.getCurrentSize()+CompressionUtils.seekBlockData(baseBytes, baseSeeker));
		        }
		        
		        //all remaining entries go here
		        while(entry!=null&&!deltaSeeker.hasError()&&!baseSeeker.hasError()){
		        	entry = next(baseSeeker, deltaSeeker, baseOffset, deltaOffset);
		        	if (entry!=null){
			        	while (entry.getCurrentSize()<entry.getOriginalSize()){
				               entry.setCurrentSize(entry.getCurrentSize()+CompressionUtils.seekBlockData(baseBytes, baseSeeker));
			        	}
		        	}
		        }			    
				this.setType(BundleType.NONCAS_PATCHED);
		    }else{
		    	baseSeeker.setError(true);
		    }   
		}
		if (!baseSeeker.hasError()||!deltaSeeker.hasError()){
			this.halfInitialized = false;
		}
//		if (baseSeeker.hasError()||deltaSeeker.hasError()){
//			System.err.println("Unable to build a Non-Cas Bundle Block! (Type: "+bundleType+")");
//		}
	}
	private NonCasBundleEntry next(FileSeeker baseSeeker, FileSeeker deltaSeeker, int baseBundleOffset, int deltaBundleOffset){
		updateEntrySize(baseSeeker, deltaSeeker, baseBundleOffset, deltaBundleOffset);
		this.entriesIndex++;
		return updateEntryOffset(baseSeeker, deltaSeeker, baseBundleOffset, deltaBundleOffset);
	}
	
	private NonCasBundleEntry updateEntryOffset(FileSeeker baseSeeker, FileSeeker deltaSeeker, int baseBundleOffset, int deltaBundleOffset){
		if (this.entriesIndex<this.entries.size()){
			NonCasBundleEntry entry = this.entries.get(this.entriesIndex);
			entry.setBaseOffset(baseSeeker.getOffset());
			entry.setDeltaOffset(deltaSeeker.getOffset());			
			return entry;
		}
		return null;
	}
	
	private NonCasBundleEntry updateEntrySize(FileSeeker baseSeeker, FileSeeker deltaSeeker, int baseBundleOffset, int deltaBundleOffset){
		if (this.entriesIndex<this.entries.size()){
			NonCasBundleEntry entry = this.entries.get(this.entriesIndex);
			entry.setBaseSize(baseSeeker.getOffset()-entry.getBaseOffset());
			entry.setDeltaSize(deltaSeeker.getOffset()-entry.getDeltaOffset());
//			if (entry.getDeltaSize()>1000){
//				System.out.println(" "+entry.getName()+" "+entry.getDeltaSize());
//			}
			return entry;
		}
		return null;
	}
	
	private boolean setupBundle(byte[] data, FileSeeker seeker, ByteOrder order){
		int metaSize = -1, metaOffset = -1;
		long absStringOffset = -1;
		
		metaSize = FileHandler.readInt(data, seeker, order);
		metaOffset = seeker.getOffset();
		header = new NonCasBundleHeader(data, seeker, order); //8x Integer == 32 Bytes
		if (header.getMagic()!=NonCasBundleHeader.FOURCC_BF4){
			System.out.println("You are trying to use an NON CAS Bundle with a Game THAT IS NOT Battlefield 4,\nMaybe this won't work!");
//			return false;
		}
		if(seeker.hasError()){return false;}
		
		//SHA1's
		byte[] sha1Buffer = null;
		for (int i=0; i<header.getTotalCount();i++){// #one sha1 for each ebx+res+chunk.
			sha1Buffer = FileHandler.readByte(data, seeker, 20);//SHA1 == 20 Bytes each!
			if (sha1Buffer!=null){
				String sha1 = FileHandler.bytesToHex(sha1Buffer);
				sha1List.add(sha1);
			}
			if(seeker.hasError()){return false;}
		}
		
		//EBX
		for (int i=0; i<header.getEbxCount();i++){
			NonCasBundleEntry entry = NonCasBundleEntry.readEntry(data, seeker, order);
			if (entry!=null){
				entry.setBundleType(ResourceBundleType.EBX);
			}
			ebx.add(entry);
			if(seeker.hasError()){return false;}
		}
		//RES
		for (int i=0; i<header.getResCount();i++){
			NonCasBundleEntry entry = NonCasBundleEntry.readEntry(data, seeker, order);
			if (entry!=null){
				entry.setBundleType(ResourceBundleType.RES);
			}
			res.add(entry);
			if(seeker.hasError()){return false;}
		}
		
		//--->ebx are done, but res have extra content<---//
		
		//resType as ascii: E.g. \IT. for ITexture
		for (NonCasBundleEntry resEntry : res){
			int resType = FileHandler.readInt(data, seeker, order);
			resEntry.setResType(TocConverter.toResourceType(resType));
			resEntry.setResTypeInt(resType);
			if(seeker.hasError()){return false;}
		}
		//resMeta has often 16 nulls (always null for IT)
		for (NonCasBundleEntry resEntry : res){
			resEntry.setResMeta(FileHandler.readByte(data, seeker, 16));
			if(seeker.hasError()){return false;}
		}
		//resRid
		for (NonCasBundleEntry resEntry : res){
			resEntry.setResRid(FileHandler.readLong(data, seeker, order));
			if(seeker.hasError()){return false;}
		}
		
		
		//Chunks, There is one chunkMeta entry for every chunk (i.e. chunks and chunkMeta both have the same number of elements).
		for (int i=0; i<header.getChunkCount();i++){
			NonCasBundleChunkEntry chunkEntry = NonCasBundleChunkEntry.readChunkEntry(data, seeker, order);
			chunks.add(chunkEntry);
			if(seeker.hasError()){return false;}
		}
		//ChunkMeta is is a TOC structure.
		chunkMeta = FileHandler.readByte(data, seeker, header.getChunkMetaSize());
		
		absStringOffset = metaOffset+header.getStringOffset();
		
		//--->Set for each Entry the name and add to entries<---//
		//ebx and res have a filename (chunks only have a 16 byte id)
		
		for (NonCasBundleEntry ebxBundleEntry : ebx){
			int stringOffset = (int) (absStringOffset+ebxBundleEntry.getNameOffset());
			ebxBundleEntry.setName(FileHandler.readString(data, stringOffset, 250));
			entries.add(ebxBundleEntry);
			if(seeker.hasError()){return false;}
		}
		for (NonCasBundleEntry resBundleEntry : res){
			int stringOffset = (int) (absStringOffset+resBundleEntry.getNameOffset());
			resBundleEntry.setName(FileHandler.readString(data, stringOffset, 250));
			entries.add(resBundleEntry);
			if(seeker.hasError()){return false;}
		}
					
		//go to the start of the payload section
		seeker.setOffset(metaOffset+metaSize);
		
		
		//assign SHA1 Hashes to each Entry and Chunk (EBX, RES & CHUNK)
		for (int index=0; index<entries.size(); index++){
			if (index<sha1List.size()){
				entries.get(index).setSha1(sha1List.get(index));
				//System.out.println(entries.get(index).getSha1()+" "+index);
			}else{
				System.out.println("More Entries as Hashes!");
			}
		}
		int absSha1Index = 0;
		for (int index=0; index<chunks.size(); index++){
			absSha1Index = index+entries.size();
			if (absSha1Index<sha1List.size()){
				//TODO maybe its for chunkMeta ? (TOC)
				chunks.get(index).setSha1(sha1List.get(absSha1Index));
			}else{
				System.out.println("More Chunks as Hashes!");
			}
		}
		return !seeker.hasError();
	}
	
	public NonCasBundleHeader getHeader() {
		return header;
	}

	public void setHeader(NonCasBundleHeader header) {
		this.header = header;
	}

	public ArrayList<String> getSha1List() {
		return sha1List;
	}

	public void setSha1List(ArrayList<String> sha1List) {
		this.sha1List = sha1List;
	}

	public ArrayList<NonCasBundleEntry> getEbx() {
		return ebx;
	}

	public void setEbx(ArrayList<NonCasBundleEntry> ebx) {
		this.ebx = ebx;
	}

	public ArrayList<NonCasBundleEntry> getRes() {
		return res;
	}

	public void setRes(ArrayList<NonCasBundleEntry> res) {
		this.res = res;
	}

	public ArrayList<NonCasBundleChunkEntry> getChunks() {
		return chunks;
	}

	public void setChunks(ArrayList<NonCasBundleChunkEntry> chunks) {
		this.chunks = chunks;
	}

	public ArrayList<NonCasBundleEntry> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<NonCasBundleEntry> entries) {
		this.entries = entries;
	}
	public boolean isHalfInitialized() {
		return halfInitialized;
	}
	public void setHalfInitialized(boolean halfInitialized) {
		this.halfInitialized = halfInitialized;
	}
	public byte[] getChunkMeta() {
		return chunkMeta;
	}
	public void setChunkMeta(byte[] chunkMeta) {
		this.chunkMeta = chunkMeta;
	}
	public int getOriginalChunkPayloadOffset() {
		return originalChunkPayloadOffset;
	}
	public void setOriginalChunkPayloadOffset(int originalChunkPayloadOffset) {
		this.originalChunkPayloadOffset = originalChunkPayloadOffset;
	}
	public int getOriginalPayloadOffset() {
		return originalPayloadOffset;
	}
	public void setOriginalPayloadOffset(int originalPayloadOffset) {
		this.originalPayloadOffset = originalPayloadOffset;
	}
	public int getOriginalBundleBaseSize() {
		return originalBundleBaseSize;
	}
	public void setOriginalBundleBaseSize(int originalBundleBaseSize) {
		this.originalBundleBaseSize = originalBundleBaseSize;
	}
	public boolean isPatched() {
		return isPatched;
	}

	
}
