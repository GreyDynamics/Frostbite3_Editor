package tk.greydynamics.Resource.Frostbite3.Layout;

import java.io.File;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundleCompiler;
import tk.greydynamics.Resource.Frostbite3.Toc.ConvertedTocFile;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;
import tk.greydynamics.Resource.Frostbite3.Toc.TocEntry;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutEntryType;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutFieldType;

public class LayoutCreator {
	public static byte[] createTocFile(ConvertedTocFile cToc){
		ArrayList<Byte> file = new ArrayList<>();

		//Header 556Bytes == Type - Sig - Key
		byte[] header = FileHandler.readFile("res/toc/header.hex");
		for (byte b : header){file.add(b);}


		LayoutEntry rootEntry = new LayoutEntry(LayoutEntryType.ORDINARY);

		//Entries
		if (!cToc.getTag().equals("")){
			LayoutField tag = new LayoutField(cToc.getTag(), LayoutFieldType.GUID, "tag");
			rootEntry.getFields().add(tag);
		}

		LayoutEntry bundleEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
		for (TocEntry link : cToc.getBundles()){
			LayoutEntry linkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);

			LayoutField fieldID = new LayoutField(link.getID(), LayoutFieldType.STRING, "id");
			linkEntry.getFields().add(fieldID);
			LayoutField fieldOffset = new LayoutField(link.getOffset(), LayoutFieldType.LONG, "offset");
			linkEntry.getFields().add(fieldOffset);
			LayoutField fieldSize = null;
			if (link.getSize()==-1){
				fieldSize = new LayoutField(link.getSizeLong(), LayoutFieldType.LONG, "size");
			}else{
				fieldSize = new LayoutField(link.getSize(), LayoutFieldType.INTEGER, "size");
			}
			linkEntry.getFields().add(fieldSize);
			
			if (link.isBase()){
				LayoutField base = new LayoutField(link.isBase(), LayoutFieldType.BOOL, "base");
				linkEntry.getFields().add(base);
			}
			
			if (link.isDelta()){
				LayoutField delta = new LayoutField(link.isDelta(), LayoutFieldType.BOOL, "delta");
				linkEntry.getFields().add(delta);
			}
			
			LayoutField linkField = new LayoutField(linkEntry, LayoutFieldType.ENTRY, null);
			bundleEntry.getFields().add(linkField);
			
		}
		
		LayoutField bundles = new LayoutField(bundleEntry, LayoutFieldType.LIST, "bundles");
		rootEntry.getFields().add(bundles);

		LayoutEntry chunkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
		for (TocEntry link : cToc.getChunks()){
			LayoutEntry linkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);

			LayoutField fieldID = new LayoutField(link.getGuid(), LayoutFieldType.GUID, "id");
			linkEntry.getFields().add(fieldID);
			LayoutField fieldOffset = new LayoutField(link.getOffset(), LayoutFieldType.LONG, "offset");
			linkEntry.getFields().add(fieldOffset);
			LayoutField fieldSize = new LayoutField(link.getSize(), LayoutFieldType.INTEGER, "size");
			linkEntry.getFields().add(fieldSize);
			

			if (link.isBase()){
				LayoutField base = new LayoutField(link.isBase(), LayoutFieldType.BOOL, "base");
				linkEntry.getFields().add(base);
			}
			
			if (link.isDelta()){
				LayoutField delta = new LayoutField(link.isDelta(), LayoutFieldType.BOOL, "delta");
				linkEntry.getFields().add(delta);
			}
			

			LayoutField linkField = new LayoutField(linkEntry, LayoutFieldType.ENTRY, null);
			chunkEntry.getFields().add(linkField);
		}
		LayoutField chunks = new LayoutField(chunkEntry, LayoutFieldType.LIST, "chunks");
		rootEntry.getFields().add(chunks);

		if (cToc.isCas()){
			LayoutField cas = new LayoutField(cToc.isCas(), LayoutFieldType.BOOL, "cas");
			rootEntry.getFields().add(cas);
		}
		if (!(cToc.getTotalSize() == -1)){
			LayoutField totalsize = new LayoutField(cToc.getTotalSize(), LayoutFieldType.LONG, "totalSize");
			rootEntry.getFields().add(totalsize);
		}
		if (cToc.getName()!=null){
			LayoutField name = new LayoutField(cToc.getName(), LayoutFieldType.STRING, "name");
			rootEntry.getFields().add(name);
		}
		if (cToc.alwaysEmitSuperBundle){
			LayoutField emitSuperBundle = new LayoutField(cToc.isAlwaysEmitSuperBundle(), LayoutFieldType.BOOL, "alwaysEmitSuperbundle");
			rootEntry.getFields().add(emitSuperBundle);
		}


		file.addAll(createEntry(rootEntry));
		
		while(file.size()%16!=0){
			file.add((byte) 0x0);
		}
		//TODO is this a check ??
		for(int i=0;i<444;i++){
			file.add((byte) 0x0);
		}
		
		return FileHandler.toByteArray(file);
	}

	public static byte[] createSBpart(CasBundle part){
		ArrayList<Byte> out = new ArrayList<>();

		LayoutEntry rootEntry = new LayoutEntry(LayoutEntryType.ORDINARY);

		LayoutField path = new LayoutField(part.getBasePath(), LayoutFieldType.STRING, "path");
		rootEntry.getFields().add(path);

		LayoutField magicSalt = new LayoutField(part.getMagicSalt(), LayoutFieldType.INTEGER, "magicSalt");
		rootEntry.getFields().add(magicSalt);
		
		//TEST-TOTALSIZE
		long totalSize = 0;

		//EBX
		LayoutEntry ebxEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
		for (ResourceLink link : part.getEbx()){
			LayoutEntry linkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);

			LayoutField name = new LayoutField(link.getName(), LayoutFieldType.STRING, "name");
			linkEntry.getFields().add(name);

			LayoutField sha1 = new LayoutField(link.getSha1(), LayoutFieldType.SHA1, "sha1");
			linkEntry.getFields().add(sha1);

			LayoutField size = new LayoutField(link.getSize(), LayoutFieldType.LONG, "size");
			totalSize += link.getSize();
			linkEntry.getFields().add(size);

			LayoutField originalSize = new LayoutField(link.getOriginalSize(), LayoutFieldType.LONG, "originalSize");
			linkEntry.getFields().add(originalSize);
			
			if (link.getCasPatchType() != 0){
				LayoutField casPatchType = new LayoutField(link.getCasPatchType(), LayoutFieldType.INTEGER, "casPatchType");
				linkEntry.getFields().add(casPatchType);
			}

			if (link.getBaseSha1() != null){
				LayoutField baseSha1 = new LayoutField(link.getBaseSha1(), LayoutFieldType.SHA1, "baseSha1");
				linkEntry.getFields().add(baseSha1);
			}
			if (link.getDeltaSha1() != null){
				LayoutField deltaSha1 = new LayoutField(link.getDeltaSha1(), LayoutFieldType.SHA1, "deltaSha1");
				linkEntry.getFields().add(deltaSha1);
			}


			LayoutField linkField = new LayoutField(linkEntry, LayoutFieldType.ENTRY, null);
			ebxEntry.getFields().add(linkField);
		}
		if (ebxEntry.getFields().size()>0){
			LayoutField ebxs = new LayoutField(ebxEntry, LayoutFieldType.LIST, "ebx");
			rootEntry.getFields().add(ebxs);
		}

		//DBX
		LayoutEntry dbxEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
		for (ResourceLink link : part.getDbx()){
			LayoutEntry linkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);

			LayoutField name = new LayoutField(link.getName(), LayoutFieldType.STRING, "name");
			linkEntry.getFields().add(name);

			LayoutField sha1 = new LayoutField(link.getSha1(), LayoutFieldType.SHA1, "sha1");
			linkEntry.getFields().add(sha1);

			LayoutField size = new LayoutField(link.getSize(), LayoutFieldType.LONG, "size");
			totalSize += link.getSize();
			linkEntry.getFields().add(size);

			LayoutField originalSize = new LayoutField(link.getOriginalSize(), LayoutFieldType.LONG, "originalSize");
			linkEntry.getFields().add(originalSize);
			
			if (link.getCasPatchType() != 0){
				LayoutField casPatchType = new LayoutField(link.getCasPatchType(), LayoutFieldType.INTEGER, "casPatchType");
				linkEntry.getFields().add(casPatchType);
			}

			if (link.getBaseSha1() != null){
				LayoutField baseSha1 = new LayoutField(link.getBaseSha1(), LayoutFieldType.SHA1, "baseSha1");
				linkEntry.getFields().add(baseSha1);
			}
			if (link.getDeltaSha1() != null){
				LayoutField deltaSha1 = new LayoutField(link.getDeltaSha1(), LayoutFieldType.SHA1, "deltaSha1");
				linkEntry.getFields().add(deltaSha1);
			}
			
			LayoutField linkField = new LayoutField(linkEntry, LayoutFieldType.ENTRY, null);
			dbxEntry.getFields().add(linkField);
		}
		if (dbxEntry.getFields().size()>0){
			LayoutField dbxs = new LayoutField(dbxEntry, LayoutFieldType.LIST, "dbx");
			rootEntry.getFields().add(dbxs);
		}

		//RES
		LayoutEntry resEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
		for (ResourceLink link : part.getRes()){
			LayoutEntry linkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);

			LayoutField name = new LayoutField(link.getName(), LayoutFieldType.STRING, "name");
			linkEntry.getFields().add(name);

			LayoutField sha1 = new LayoutField(link.getSha1(), LayoutFieldType.SHA1, "sha1");
			linkEntry.getFields().add(sha1);

			LayoutField size = new LayoutField(link.getSize(), LayoutFieldType.LONG, "size");
			totalSize += link.getSize();
			linkEntry.getFields().add(size);

			LayoutField originalSize = new LayoutField(link.getOriginalSize(), LayoutFieldType.LONG, "originalSize");
			linkEntry.getFields().add(originalSize);	
			
			

			//RES-SPEC
			LayoutField resType = new LayoutField(link.getResType(), LayoutFieldType.INTEGER, "resType");
			linkEntry.getFields().add(resType);		

			LayoutField resMeta = new LayoutField(link.getResMeta(), LayoutFieldType.RAW2, "resMeta");
			linkEntry.getFields().add(resMeta);	

			LayoutField resRid = new LayoutField(link.getResRid(), LayoutFieldType.LONG, "resRid");
			linkEntry.getFields().add(resRid);	

			if (link.getIdata() != null){
				LayoutField idata = new LayoutField(link.getIdata(), LayoutFieldType.RAW2, "idata");
				linkEntry.getFields().add(idata);
			}
			
			//DEFAULT
			if (link.getCasPatchType() != 0){
				LayoutField casPatchType = new LayoutField(link.getCasPatchType(), LayoutFieldType.INTEGER, "casPatchType");
				linkEntry.getFields().add(casPatchType);
			}
			
			if (link.getBaseSha1() != null){
				LayoutField baseSha1 = new LayoutField(link.getBaseSha1(), LayoutFieldType.SHA1, "baseSha1");
				linkEntry.getFields().add(baseSha1);
			}
			
			if (link.getDeltaSha1() != null){
				LayoutField deltaSha1 = new LayoutField(link.getDeltaSha1(), LayoutFieldType.SHA1, "deltaSha1");
				linkEntry.getFields().add(deltaSha1);
			}


			LayoutField linkField = new LayoutField(linkEntry, LayoutFieldType.ENTRY, null);
			resEntry.getFields().add(linkField);
		}
		if (resEntry.getFields().size()>0){
			LayoutField ress = new LayoutField(resEntry, LayoutFieldType.LIST, "res");
			rootEntry.getFields().add(ress);
		}

		//CHUNKS
		LayoutEntry chunksEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
		for (ResourceLink link : part.getChunks()){
			LayoutEntry linkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
			
			LayoutField id = new LayoutField(link.getId(), LayoutFieldType.GUID, "id");
			linkEntry.getFields().add(id);
			
			LayoutField sha1 = new LayoutField(link.getSha1(), LayoutFieldType.SHA1, "sha1");
			linkEntry.getFields().add(sha1);

			LayoutField size = new LayoutField(link.getSize(), LayoutFieldType.LONG, "size");
			totalSize += link.getSize();
			linkEntry.getFields().add(size);
			
			if (link.getOriginalSize()!=0){
				LayoutField originalSize = new LayoutField(link.getOriginalSize(), LayoutFieldType.LONG, "originalSize");
				linkEntry.getFields().add(originalSize);
			}
			if (link.getRangeStart()>=0){
				LayoutField rangeStart = new LayoutField(link.getRangeStart(), LayoutFieldType.INTEGER, "rangeStart");
				linkEntry.getFields().add(rangeStart);
			}
			if (link.getRangeEnd()>=0){
				LayoutField rangeEnd = new LayoutField(link.getRangeEnd(), LayoutFieldType.INTEGER, "rangeEnd");
				linkEntry.getFields().add(rangeEnd);
			}

			LayoutField logicalOffset = new LayoutField(link.getLogicalOffset(), LayoutFieldType.INTEGER, "logicalOffset");
			linkEntry.getFields().add(logicalOffset);

			LayoutField logicalSize = new LayoutField(link.getLogicalSize(), LayoutFieldType.INTEGER, "logicalSize");
			linkEntry.getFields().add(logicalSize);

			if (link.getCasPatchType() != 0){
				LayoutField casPatchType = new LayoutField(link.getCasPatchType(), LayoutFieldType.INTEGER, "casPatchType");
				linkEntry.getFields().add(casPatchType);
			}
			if (link.getBaseSha1() != null){
				LayoutField baseSha1 = new LayoutField(link.getBaseSha1(), LayoutFieldType.SHA1, "baseSha1");
				linkEntry.getFields().add(baseSha1);
			}
			if (link.getDeltaSha1() != null){
				LayoutField deltaSha1 = new LayoutField(link.getDeltaSha1(), LayoutFieldType.SHA1, "deltaSha1");
				linkEntry.getFields().add(deltaSha1);
			}


			LayoutField linkField = new LayoutField(linkEntry, LayoutFieldType.ENTRY, null);
			chunksEntry.getFields().add(linkField);
		}
		if (chunksEntry.getFields().size()>0){
			LayoutField chunks = new LayoutField(chunksEntry, LayoutFieldType.LIST, "chunks");
			rootEntry.getFields().add(chunks);
		}

		//CHUNKMETA
		LayoutEntry chunkMetaEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
		for (ResourceLink link : part.getChunkMeta()){
			LayoutEntry linkEntry = new LayoutEntry(LayoutEntryType.ORDINARY);
						
			//CHUNKMETA-SPEC

			LayoutField h32 = new LayoutField(link.getH32(), LayoutFieldType.INTEGER, "h32");
			linkEntry.getFields().add(h32);

			LayoutField meta = new LayoutField(link.getMeta(), LayoutFieldType.RAW, "meta");
			linkEntry.getFields().add(meta);
			if (link.getFirstMip()!=-1){
				LayoutField firstMip = new LayoutField(link.getFirstMip(), LayoutFieldType.INTEGER, "firstMip");
				linkEntry.getFields().add(firstMip);
			}

			
			//DEFAULT does it even exist here ?
			if (link.getCasPatchType() != 0){
				LayoutField casPatchType = new LayoutField(link.getCasPatchType(), LayoutFieldType.INTEGER, "casPatchType");
				linkEntry.getFields().add(casPatchType);
			}

			if (link.getBaseSha1() != null){
				LayoutField baseSha1 = new LayoutField(link.getBaseSha1(), LayoutFieldType.SHA1, "baseSha1");
				linkEntry.getFields().add(baseSha1);
			}
			if (link.getDeltaSha1() != null){
				LayoutField deltaSha1 = new LayoutField(link.getDeltaSha1(), LayoutFieldType.SHA1, "deltaSha1");
				linkEntry.getFields().add(deltaSha1);
			}
			
			LayoutField linkField = new LayoutField(linkEntry, LayoutFieldType.ENTRY, null);
			chunkMetaEntry.getFields().add(linkField);
			
		}
		if (chunkMetaEntry.getFields().size()>0){
			LayoutField chunkMetas = new LayoutField(chunkMetaEntry, LayoutFieldType.LIST, "chunkMeta");
			rootEntry.getFields().add(chunkMetas);
		}

		//FIELDS
		LayoutField alignMembers = new LayoutField(part.isAlignMembers(), LayoutFieldType.BOOL, "alignMembers");
		rootEntry.getFields().add(alignMembers);

		LayoutField ridSupport = new LayoutField(part.isRidSupport(), LayoutFieldType.BOOL, "ridSupport");
		rootEntry.getFields().add(ridSupport);

		LayoutField storeCompressedSizes = new LayoutField(part.isStoreCompressedSizes(), LayoutFieldType.BOOL, "storeCompressedSizes");
		rootEntry.getFields().add(storeCompressedSizes);
		
		//Battlefield does not validate this value, but may needed for "INTERNAL-MEMORY-CALCULATIONS"!
		LayoutField totalSizeF = new LayoutField(totalSize, LayoutFieldType.LONG, "totalSize");
		//TocField totalSizeF = new TocField(part.getTotalSize(), TocFieldType.LONG, "totalSize");
		rootEntry.getFields().add(totalSizeF);

		LayoutField dbxTotalSize = new LayoutField(part.getDbxTotalSize(), LayoutFieldType.LONG, "dbxTotalSize");
		rootEntry.getFields().add(dbxTotalSize);

		//PAYLOAD
		out.addAll(createEntry(rootEntry));
		
	
		return FileHandler.toByteArray(out);
	}

	static ArrayList<Byte> createEntry(LayoutEntry tocE){
		ArrayList<Byte> entry = new ArrayList<>();
		switch(tocE.getType()){
		case ORDINARY:
			entry.add((byte) 0x82);
			ArrayList<Byte> entryData = new ArrayList<Byte>();
			for (LayoutField field : tocE.getFields()){
				ArrayList<Byte> data = createField(field);
				entryData.addAll(data);
			}
			entry.addAll(FileHandler.toLEB128List((entryData.size()+1) & 0xFFFFFFFF));
			for (byte b : entryData){
				entry.add(b);
			}
			entry.add((byte) 0x00);
			break;
		default:
			System.err.println("Unknown type of Entry found in TocCreator :( "+tocE.getType());
			return null;
		}
		return entry;
	}

	static ArrayList<Byte> createField(LayoutField field){
		if (field.getType() == null){return null;}

		ArrayList<Byte> data = new ArrayList<Byte>();

		//name
		String name = field.getName();
		ArrayList<Byte> nameBytes = new ArrayList<Byte>();
		for (byte b : name.getBytes()){
			nameBytes.add(b);
		}
		nameBytes.add((byte) 0x00); //tailing null

		//type
		switch(field.getType()){
		case BOOL:
			data.add((byte) 0x06);
			data.addAll(nameBytes);
			boolean v = (boolean) field.getObj();
			if (v){
				data.add((byte) 0x01);
			}else{
				data.add((byte) 0x00);
			}
			break;
		case GUID:
			data.add((byte) 0x0F);
			data.addAll(nameBytes);
			byte[] guid = FileHandler.hexStringToByteArray(((String) field.getObj()).toUpperCase());
			for (byte b : guid){
				data.add(b);
			}
			break;
		case INTEGER:
			data.add((byte) 0x08);
			data.addAll(nameBytes);
			byte[] intB = FileHandler.toBytes((int) field.getObj(), ByteOrder.LITTLE_ENDIAN);
			for (byte b : intB){
				data.add(b);
			}
			break;
		case LIST:
			data.add((byte) 0x01);
			data.addAll(nameBytes);
			LayoutEntry entries = (LayoutEntry) field.getObj();
			ArrayList<Byte> list = new ArrayList<Byte>();
			for (LayoutField f : entries.getFields()){
				if (f.getType() == LayoutFieldType.ENTRY){
					LayoutEntry fieldEntry = (LayoutEntry) f.getObj();
					ArrayList<Byte> entry = createEntry(fieldEntry);
					list.addAll(entry);
				}else{
					ArrayList<Byte> entry = createField(f);
					list.addAll(entry);
				}
			}
			data.addAll(FileHandler.toLEB128List((list.size()+1) & 0xFFFFFFFF));
			data.addAll(list);
			data.add((byte) 0x00);
			break;
		case LONG:
			data.add((byte) 0x09);
			data.addAll(nameBytes);
			byte[] longB = FileHandler.toBytes((long) field.getObj(), ByteOrder.LITTLE_ENDIAN);
			for (byte b : longB){
				data.add(b);
			}
			break;
		case RAW:
			data.add((byte) 0x02);
			data.addAll(nameBytes);
			byte[] raw = (byte[]) field.getObj();
			data.addAll(FileHandler.toLEB128List(raw.length));
			for (byte b : raw){
				data.add(b);
			}
			break;
		case RAW2:
			data.add((byte) 0x13);
			data.addAll(nameBytes);
			byte[] raw2 = (byte[]) field.getObj();
			if (raw2 == null){
				System.err.println("NULL RAW2 FOUND :/");
				break;
			}
			data.addAll(FileHandler.toLEB128List(raw2.length));
			for (byte b : raw2){
				data.add(b);
			}
			break;
		case SHA1:
			data.add((byte) 0x10);
			data.addAll(nameBytes);
			byte[] sha1 = FileHandler.hexStringToByteArray(((String) field.getObj()).toUpperCase());
			for (byte b : sha1){
				data.add(b);
			}
			break;
		case STRING:
			data.add((byte) 0x07);
			data.addAll(nameBytes);
			String value = (String) field.getObj();
			if (value == null){
				System.err.println("NULL STRING FOUND :/");
				break;
			}
			data.addAll(FileHandler.toLEB128List((value.length()+1) & 0xFFFFFFFF));
			//tailing null is also inculded!
			for (byte b : value.getBytes()){
				data.add(b);
			}
			data.add((byte) 0x00);
			break;
		case ENTRY:
			System.err.println("ONLY USED FOR RECREATION, NORMALY THIS SHOULD NOT HAPPEN :(");
			break;
		default:
			break;
		}
		return data;
	}
	public static boolean createModifiedNonCasSuperbundle(ConvertedTocFile toc, NonCasBundle newBundle, boolean isNew, String destination){
		/**NON CAS SUPERBUNDLE!****NON CAS SUPERBUNDLE!****NON CAS SUPERBUNDLE!****NON CAS SUPERBUNDLE!****NON CAS SUPERBUNDLE!**/
		FileSeeker seeker = new FileSeeker("CreateModifiedNonCasSuperbundle-Seeker");
		if (new File(destination).exists()){
			System.err.println("File does already exist!");
			return false;
		}
		for (TocEntry bundle : toc.getBundles()){
			long currentOffset = seeker.getOffset();
			if (bundle.getID().equals(newBundle.getName())){
				System.out.println("Bundle replace/add: "+bundle.getID());
				File newNonCasBundleFile = new File(NonCasBundleCompiler.compileUnpatchedBundle(newBundle, ByteOrder.BIG_ENDIAN, "temp/nonCasSuperbundleCreator_"+UUID.randomUUID().toString().replace("-", "")));
				if (!newNonCasBundleFile.exists()){
					System.err.println("new compiled noncas bundle not found..");
					return false;
				}
				FileHandler.extendFileFromFile(newNonCasBundleFile.getAbsolutePath(), 0, newNonCasBundleFile.length(), destination, new FileSeeker());
								
				bundle.setOffset(currentOffset);
				if (bundle.getSize()==-1){
					bundle.setSizeLong(newNonCasBundleFile.length());
				}else{
					bundle.setSize((int) newNonCasBundleFile.length());
				}
				seeker.seek((int) newNonCasBundleFile.length());
				
				bundle.setBase(false);
				//bundle.setDelta(true);
				//TODO bundle.setDelta(true) only if its patched!;
				
			}else{
				if (bundle.isBase() && !bundle.isDelta()){
					System.out.println("Bundle link: "+bundle.getID());
				}else{
					System.out.println("Bundle copy: "+bundle.getID());
					long size = bundle.getSize();
					if (size==-1){
						size = bundle.getSizeLong();
					}
					boolean success = FileHandler.extendFileFromFile(bundle.getBundlePath()/*.replace("/Updata/Patch/", "/")*/, bundle.getOffset(), size, destination, seeker);
					if (!success){
						System.err.println("Abort: something went wrong while creating new NON-CAS Superbundle!");
						return false;
					}else{
						bundle.setOffset(currentOffset);
					}
				}
			}
		}
		
		for (TocEntry chunk : toc.getChunks()){
			long currentOffset = seeker.getOffset();
			if (chunk.isBase() && !chunk.isDelta()){
				System.out.println("Chunk link: "+chunk.getID());
			}else{
				System.out.println("Chunk copy: "+chunk.getID());
				boolean success = FileHandler.extendFileFromFile(chunk.getBundlePath(), chunk.getOffset(), chunk.getSizeLong(), destination, seeker);
				if (!success){
					System.err.println("Abort: something went wrong while creating modified sb file :( (CHUNKS)");
					return false;
				}else{
					chunk.setOffset(currentOffset);
				}
			}
		}
		for(int i=0;i<444;i++){
			FileHandler.extendFileFromFile("res/filler/zero", 0, 1, destination, seeker);
		}
		return true;
	}

	public static boolean createModifiedCasSuperbundle(ConvertedTocFile toc, CasBundle newBundle, boolean isNew, String destination, boolean override){
		//**CAS SUPERBUNDLE!****CAS SUPERBUNDLE!****CAS SUPERBUNDLE!****CAS SUPERBUNDLE!****CAS SUPERBUNDLE!****CAS SUPERBUNDLE!**//
		//creates modi. sb file and updates toc file for that.
		byte[] header2 = new byte[]{0x53, 0x70, 0x6C, 0x65, 0x78, 0x58, 0x5F,
				0x4D, 0x6F, 0x64, 0x5F, 0x46, 0x69, 0x6C,
				0x65, 0x21};
		System.out.println("Creating ModifiedSBFile!");
		FileSeeker seeker = new FileSeeker();
		destination = FileHandler.normalizePath(destination);
		File destFile = new File(destination);
		if (destFile.exists()){
			if (!override){
				System.err.println("Can't create a modified SB file. File does already exists.");
				return false;
			}else{
				System.out.println("File already exist. Using overriding on: "+destination);
				destFile.delete();
			}
		}
		destFile = null;//Not needed anymore, freeUp memory!

		if (isNew){
			TocEntry link = new TocEntry();
			link.setID(newBundle.getBasePath());
			link.setType(LinkBundleType.BUNDLES);
			toc.getBundles().add(link);
		}
		FileHandler.writeFile(destination, header2, false);
		seeker.seek(header2.length);
		for (TocEntry bundle : toc.getBundles()){
			long currentOffset = seeker.getOffset();
			if (bundle.getID().equals(newBundle.getBasePath())){
				System.out.println("Bundle replace/add: "+bundle.getID());
				byte[] newBundleBytes = createSBpart(newBundle);
				if (newBundleBytes.length == 0){
					System.err.println("zero length");
				}
				FileHandler.writeFile(destination, newBundleBytes, true);
								
				bundle.setOffset(currentOffset);
				bundle.setSize(newBundleBytes.length);
				seeker.seek(newBundleBytes.length);
				
				bundle.setBase(false);
				bundle.setDelta(true);
				//TODO bundle.setDelta(true);
				System.err.println("TODO: Do the bundle.setDelta(true) ?");
				
			}else{
				if (bundle.isBase() && !bundle.isDelta()){
					System.out.println("Bundle link: "+bundle.getID());
				}else{
					System.out.println("Bundle copy: "+bundle.getID());
					boolean success = FileHandler.extendFileFromFile(bundle.getBundlePath()/*.replace("/Updata/Patch/", "/")*/, bundle.getOffset(), bundle.getSize(), destination, seeker);
					if (!success){
						System.err.println("Abort: something went wrong while creating modified sb file :( (BUNDLES)");
						return false;
					}else{
						bundle.setOffset(currentOffset);
					}
				}
			}
		}
		
		for (TocEntry chunk : toc.getChunks()){
			long currentOffset = seeker.getOffset();
			if (chunk.isBase() && !chunk.isDelta()){
				System.out.println("Chunk link: "+chunk.getID());
			}else{
				System.out.println("Chunk copy: "+chunk.getID());
				boolean success = FileHandler.extendFileFromFile(chunk.getBundlePath(), chunk.getOffset(), chunk.getSizeLong(), destination, seeker);
				if (!success){
					System.err.println("Abort: something went wrong while creating modified sb file :( (CHUNKS)");
					return false;
				}else{
					chunk.setOffset(currentOffset);
				}
			}
		}
		for(int i=0;i<444;i++){
			FileHandler.extendFileFromFile("res/filler/zero", 0, 1, destination, seeker);
		}
		return true;
	}

}
