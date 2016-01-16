package tk.greydynamics.Mod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.CasCatEntry;
import tk.greydynamics.Resource.Frostbite3.Cas.CasCatManager;
import tk.greydynamics.Resource.Frostbite3.Cas.CasDataReader;
import tk.greydynamics.Resource.Frostbite3.Cas.CasManager;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ITexture;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ITextureConverter;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutCreator;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutFile;
import tk.greydynamics.Resource.Frostbite3.Toc.ConvertedTocFile;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter;
import tk.greydynamics.Resource.Frostbite3.Toc.TocEntry;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager;

public class ModTools {
	public ArrayList<Mod> mods;
	public ArrayList<Package> packages;
	public static final String RESOURCEFOLDER = "/resources/";
	public static final String PACKAGEFOLDER = "/packages/";
	public static final String PACKTYPE = ".pack";

	public ModTools() {
		init();
	}
	public void init(/*AKA. RESET*/){
		this.mods = new ArrayList<>();
		this.packages = new ArrayList<>();
		fetchMods();
		Core.getJavaFXHandler().getMainWindow().updateModsList();
	}
	
	public void fetchMods(){
		File modsDir = new File("mods/");
		for (File f : modsDir.listFiles()){
			if (f.isDirectory()){
				Mod mod = new Mod();
				mod.setPath(FileHandler.normalizePath(f.getAbsolutePath()));
				String[] splitPath = mod.getPath().split("/");
				mod.setFolderName(splitPath[splitPath.length-1]);
				File info = new File(f.getAbsolutePath()+"\\info.txt");
				if (info.exists()){
					readModInfo(mod, info);
				}
				if (mod.getAuthor() != null){
					String[] split = Core.gamePath.split("/");
					int length = split.length;
					if (Core.gamePath.endsWith("/")){
						length--;
					}
					String destFolderPath = "";
					for (int i=0; i<length-1;i++){
						destFolderPath +=split[i]+"/";
					}
					destFolderPath += mod.getGame()+"_"+mod.getFolderName();
					mod.setDestFolderPath(destFolderPath);
					mods.add(mod);
				}
			}
		}
	}
	
	void readModInfo(Mod mod, File info){
		try {
			FileReader fr = new FileReader(info);
	
			BufferedReader br = new BufferedReader(fr);
		    mod.setName(br.readLine());
		    mod.setAuthor(br.readLine());
		    mod.setGame(br.readLine());
		    String line = "";
		    String text = "";
		    while ((line = br.readLine()) != null){
		    	text += line+"\n";
		    }
		    mod.setDesc(text);
		    br.close();
		    fr.close();
		    
		    
		}catch (Exception e){
			System.err.println("Could not read Info from Mod. "+info.getAbsolutePath());
		}
	}

	
	public void fetchPackages(){
		int entries = 0;
		ArrayList<File> files = FileHandler.listf(Core.getGame().getCurrentMod().getPath()+PACKAGEFOLDER, ".pack");
		for (File f : files){
			if (!f.isDirectory()){
				Package pack = readPackageInfo(f);
				if (pack!=null){
					packages.add(pack);
					entries+=pack.getEntries().size();
				}
			}
		}
		System.out.println(packages.size()+" Packages where found in current mod with a total of "+entries+" entries.");
	}
	
	public Package readPackageInfo(File file){
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			Package pack = new Package(FileHandler.normalizePath(file.getAbsolutePath()).replace(Core.getGame().getCurrentMod().getPath()+"/packages", "").replace(PACKTYPE, ""));
			String line = "";
			while ((line = br.readLine()) != null){
					if (!line.startsWith("#")){
						String[] parts = line.split("\\|");
						
						/*| is treated as an OR in RegEx. So you need to escape it:
						 * String[] separated = line.split("\\|");
						 */
						
						if (parts.length<4||parts.length>5){continue;}
						PackageEntry entry = new PackageEntry(
							LinkBundleType.valueOf(parts[0]),
							parts[1],
							ResourceType.valueOf(parts[2]),
							parts[3]
						);
						if (parts.length==5){//additional
							entry.setTargetPath(parts[4]);
						}
						pack.getEntries().add(entry);
					}
			}
			br.close();
			fr.close();
			
			return pack;
		}catch (Exception e){
			System.err.println("Could not read Info from Package. "+file);
			return null;
		}
	}
	
	public boolean writePackage(Package pack, File file){
		if (file.exists()){
			file.delete();
		}
		ArrayList<String> entries = new ArrayList<>();
		for (PackageEntry entry : pack.getEntries()){
			String fullEntry = entry.getBundleType()+"|"+entry.getSubPackage()+"|"+entry.getResType()+"|"+entry.getResourcePath();
			if (entry.getTargetPath()!=null){
				fullEntry += "|"+entry.getTargetPath();
			}
			entries.add(fullEntry);
		}
		Collections.sort(entries);
		
		if (!FileHandler.writeLine(entries, file)){
			return false;
		}
		return true;
	}
	
	public boolean writePackages(){
		if (Core.getGame().getCurrentMod()==null){
			return false;
		}
		for (Package pack : packages){
			if (!writePackage(pack, new File(Core.getGame().getCurrentMod().getPath()+"/packages/"+pack.getName()+PACKTYPE))){
				return false;
			}
		}
		return false;
	}
	
	public Package getPackage(String name){
		for (Package pack : packages){
			if (pack.getName().equals(name)){
				return pack;
			}
		}
		Package pack = new Package(name);
		packages.add(pack);
		return null;
	}
	
	public boolean playMod(boolean recompile){
		if (recompile){
			String path = Client.cloneClient(Core.gamePath+"/", Core.getGame().getCurrentMod().getGame()+"_"+Core.getGame().getCurrentMod().getFolderName(), true);
			/*String path = Core.getGame().getCurrentMod().getGame()+"_"+Core.getGame().getCurrentMod().getFolderName();*/
			if (path!=null){
				System.out.println("Compile Client...");
				String casCatPath = path+"/Update/Patch/Data/cas_99.cas";
				CasCatManager manPatched = Core.getGame().getResourceHandler().getPatchedCasCatManager();
				CasManager.createCAS(casCatPath);
				Mod currentMod = Core.getGame().getCurrentMod();
				//TODO MOD CLIENT LOGIC! multi subpackages doesnt work ;(
				for (Package pack : packages){
					Core.getGame().setCurrentFile(FileHandler.normalizePath((Core.gamePath+"/"+pack.getName())));
					LayoutFile toc = TocManager.readToc(Core.getGame().getCurrentFile());
					ConvertedTocFile convToc = TocConverter.convertTocFile(toc);
					
					//SORT
					HashMap<String, ArrayList<PackageEntry>> sorted = new HashMap<>();
					for (PackageEntry entry : pack.getEntries()){
						ArrayList<PackageEntry> subPackageEntries = sorted.get(entry.getSubPackage());
						if (subPackageEntries==null){
							subPackageEntries = new ArrayList<PackageEntry>();
							sorted.put(entry.getSubPackage(), subPackageEntries);
							entry.setSubPackage(null);//why not free up some memory here :)
						}
						subPackageEntries.add(entry);
					}
					
					//PROCC
					for (String subPackageName : sorted.keySet()){
						CasBundle currentSBpart = null;
						for (TocEntry link : convToc.getBundles()){
							if (link.getID().equals(subPackageName)){
																				//link.setSbPath(sbPath); change the sb path once one subpackage is already done
								LayoutFile casBundle = link.getLayout();
								currentSBpart = TocConverter.convertCASBundle(casBundle);
								break;
							}
						}
						if (currentSBpart==null){
							System.err.println("Mod.ModTools.playMod can't handle new subpackages at this time. ");
							return false;
						}
						int originalSize = 0;
						ArrayList<PackageEntry> subPackageEntries = sorted.get(subPackageName);
						byte[] data = null;
						CasCatEntry casCatEntry;
						CasCatEntry casCatEntryChunk;
						String chunkID;
						for (PackageEntry sortedEntry : subPackageEntries){
							casCatEntry = null;
							casCatEntryChunk = null;
							chunkID = null;
							switch(sortedEntry.getResType()){
								case ANIMTRACKDATA:
									break;
								case ANT:
									break;
								case CHUNK:
									break;
								case EBX:
									data = FileHandler.readFile(currentMod.getPath()+RESOURCEFOLDER+sortedEntry.getResourcePath());
									originalSize = data.length;
									casCatEntry = CasManager.extendCAS(data, new File(casCatPath), manPatched);
									break;
								case ENLIGHTEN:
									break;
								case GFX:
									break;
								case HKDESTRUCTION:
									break;
								case HKNONDESTRUCTION:
									break;
								case ITEXTURE:
									byte[] ddsFileBytes = /*DSS FILE*/FileHandler.readFile(currentMod.getPath()+RESOURCEFOLDER+sortedEntry.getResourcePath());
									chunkID = UUID.randomUUID().toString().replace("-", "");
																		
									String[] split = sortedEntry.getResourcePath().split("\\.");
									byte[] originalHeaderBytes = readOrignalData(split[0], currentSBpart.getRes());
									if (originalHeaderBytes!=null){
										
										FileHandler.writeFile("output/debug/originalHeaderBytes", originalHeaderBytes);
										
										ITexture newITexture = ITextureConverter.getITextureHeader(ddsFileBytes, new ITexture(originalHeaderBytes, null), chunkID);
																				
										/*Temp debug test
										System.err.println("Texture Replacement does not work at the moment :(\n"
												+ "use originalHeaderBytes instead!");
										ITexture oldITexture = new ITexture(originalHeaderBytes, new FileSeeker());
										newITexture.setChunkID(oldITexture.getChunkID());*/
																			
										
										data = newITexture.toBytes();
										
										FileHandler.writeFile("output/debug/newITexture", data);
										
										
										originalSize = data.length;
										casCatEntry = CasManager.extendCAS(data, new File(casCatPath), manPatched);
										
										byte[] blockData = ITextureConverter.getBlockData(ddsFileBytes);
										casCatEntryChunk = CasManager.extendCAS(blockData, new File(casCatPath), manPatched);
										
										modifyChunkEntry(casCatEntryChunk, chunkID, blockData.length, newITexture.getNameHash(), currentSBpart, true /*isNew*/);
									}else{
										System.err.println("ITexture could not get applied!");
									}
									break;
								case LIGHTINGSYSTEM:
									break;
								case LUAC:
									break;
								case MESH:
									break;
								case OCCLUSIONMESH:
									break;
								case PROBESET:
									break;
								case RAGDOLL:
									break;
								case SHADERDATERBASE:
									break;
								case SHADERDB:
									break;
								case SHADERPROGRAMDB:
									break;
								case STATICENLIGHTEN:
									break;
								case STREAIMINGSTUB:
									break;
								case UNDEFINED:
									break;
								default:
									break;
							}
							
							if (sortedEntry.getResType()==ResourceType.EBX){
								modifyResourceLink(sortedEntry, casCatEntry, originalSize, currentSBpart.getEbx());
							}else if (sortedEntry.getResType()==ResourceType.ITEXTURE||sortedEntry.getResType()==ResourceType.MESH){
								modifyResourceLink(sortedEntry, casCatEntry, originalSize, currentSBpart.getRes());
							}else{
								System.err.println(sortedEntry.getResType()+" isn't defined in (Mod.ModTools.playMod) for modifyResourceL1nk!");
							}
							if (casCatEntryChunk!=null){
								manPatched.getEntries().add(casCatEntryChunk);
							}
							if (casCatEntry!=null){
								manPatched.getEntries().add(casCatEntry);
							}
						}
						//TODO convToc.setTotalSize(totalSize);
						String newPath = ((String) Core.getGame().getCurrentFile()+".sb").replace(Core.gamePath, path);
						LayoutCreator.createModifiedSBFile(convToc, currentSBpart, false/*TODO*/, newPath, true/*delete first*/);
					}
					byte[] tocBytes = LayoutCreator.createTocFile(convToc);
					File newTocFile = new File(((String) Core.getGame().getCurrentFile()+".toc").replace(Core.gamePath, path));
					if (newTocFile.exists()){
						newTocFile.delete();//delete do remove hardlink.
					}
					FileHandler.writeFile(newTocFile.getAbsolutePath(), tocBytes);
					
				}
				//CREATE CAS.CAT
				byte[] patchedCasCatBytes = manPatched.getCat();
				File casCatFile = new File(path+"/Update/Patch/Data/cas.cat");
				if (casCatFile.exists()){
					casCatFile.delete();
				}
				FileHandler.writeFile(casCatFile.getAbsolutePath(), patchedCasCatBytes);
				
				//DONE OPEN FOLDER!
				FileHandler.openFolder(path);
				//Core.getJavaFXHandler().getDialogBuilder().showInfo("INFO", "Ready to Play!\nOrigin DRM Files needs to be replaced manually!");
				//Core.getJavaFXHandler().getMainWindow().toggleModLoaderVisibility();
				Core.keepAlive = false;
				return true;
			}
			Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Something went wrong :(", null, null);
			return false;
		}else{
			FileHandler.openFolder(Core.getGame().getCurrentMod().getDestFolderPath());
			Core.getJavaFXHandler().getDialogBuilder().showInfo("INFO", "Have fun =)");
			return false;
		}
	}
	/*public ResourceLink modifyChunkEntry(CasCatEntry chunkCatEntry, String chunkGuid, Integer chunkSize, ConvertedSBpart convertedSBpart, boolean isNew){
		return modifyChunkEntry(chunkCatEntry, chunkGuid, chunkSize, 0, convertedSBpart, isNew);
	}*/
	
	public boolean modifyChunkEntry(CasCatEntry chunkCatEntry, String chunkGuid, Integer chunkSize, int h32NameHash, CasBundle convertedSBpart, boolean isNew){
		if (isNew){
			//Confirmed! Everything working fine!!
			
			ResourceLink chunkLink = new ResourceLink();
			
			/*we are going for patched default without mip settings
			 * and set the itexture starting map to 0 or 1. (whatever its starts at)*/
			
			chunkLink.setId(chunkGuid);
			chunkLink.setSha1(chunkCatEntry.getSHA1());
			chunkLink.setLogicalOffset(0);//start at 0 or first mip
			chunkLink.setLogicalSize(chunkSize);
			/*chunkLink.setRangeStart(0); we not need mip mapping
			chunkLink.setRangeEnd(chunkSize);*/
			chunkLink.setCasPatchType(1);//patch it using patched cas.
			
			
			/* We need to put in the compressed size.
			 * We not using any compression but have compression headers
			 * The file is bigger as one block. So we have to calculate the
			 * total number of blocks to get the header size we can add to the
			 * raw dds block data size.*/
			int sizeHeaders = CasManager.calculateNumberOfBlocks(chunkSize) * CasManager.blockHeaderNumBytes;
			chunkLink.setSize(sizeHeaders+chunkSize);
			
			
			/*
			SBENTRY-CHUNK:
				ID: 01 10 96 C2 D2 DA DF 9B 39 31 23 20 14 07 C1 E7
				SHA1: 78 3A 38 9D E8 F9 E8 FE E6 F3 35 C1 D9 D5 7A C9 0A 9C A9 33
					the 3 following offset are based on the compressed entries.
					Thats kinda wierd....
					SIZE: DE 77 10 00 00 00 00 00 == 1.079.262   ---> Same as SBENTRY(RANGEEND)
																	--> Compressed size with headers
					RANGESTART: 02 6E 0F 00 == 1.011.202 ---> Same as MipONEnedoffset from ITEXTURE!
					
								--> I've checked this value. its the position inside compressed block array.
									So the offset contains the block header too!
					
								--> the delta is 0x109DC aka. 68.060... the offset is 0x91FE smaller as the size of (3rd counting form 1.) mip
					RANGEEND: DE 77 10 00 == 1.079.262 ---> Same as SBENTRY(SIZE)
								-->range end to logical offset has a space of 0x40000-0x77DE which is the delta from range end to the next mip ??
				LOGICALOFFSET: 00 00 14 00 == 1.310.720 ---> first mip size + second mip size
				LOGICALSIZE: 68 55 01 00 == 87.400 ---> 
				has no idata or h32 or meta
			
			ITEXTURE:
				FirstMip: 02
				MipONEnedoffset: 3B 52 0C 00 == 807.483 --> as chunk's rangestart its the compressed offset with block
															headers
												
				MipTWOendoffset: 02 6E 0F 00 == 1.011.202 ---> Same as SBENTRY(RANGESTART) || This is the first MipMap Level -2-
				ChunkSize: 68 55 15 00 == 1.398.120 ---> SBENTRY(LOGICALOFFSET) + SBENTRY(LOGICALOFFSET) == this
				
				
			
			*This seems to be the default one
			
			id
			sha1
			size
			logicalOffset
			logicalSize

			-----------------
			*And i guess this is the patched default one.
			
			id
			sha1
			size
			logicalOffset
			logicalSize
			casPatchType

			-----------------

			*This resource can be extracted by given range arguments.
			*ITexture is prob. using this for starting on a lower mip map
			
			id
			sha1
			size
			rangeStart
			rangeEnd
			logicalOffset
			logicalSize
			
			-----------------
			
			*the patched one with range
			
			id
			sha1
			size
			rangeStart
			rangeEnd
			logicalOffset
			logicalSize
			casPatchType
			*/
			
			
			
			/*if (Game is DragonAge){
				chunkLink.setH32(h32NameHash);
				chunkLink.setMeta(new byte[] {8, 102, 105, 114, 115, 116, 77, 105, 112, 0, 0, 0, 0, 0, 0});
			}*/
			
			convertedSBpart.getChunks().add(chunkLink);
			return true;
		}else{
			System.err.println("TODO modify Chunk Entry that already exist!");
		}
		return false;
		
	}
	
	public boolean modifyResourceLink(PackageEntry packEntry, CasCatEntry casCatEntry, int originalSize, ArrayList<ResourceLink> targetList){
	/*ALREADY EXIST*/
		for (ResourceLink link : targetList){
			String targetObject = packEntry.getTargetPath();//has a special targetPath defined, use this.
			if (targetObject==null){
				targetObject = packEntry.getResourcePath();//otherwise use the resourcePath as target.
			}
			if (link.getName().equals(targetObject.replace(".", "-").split("-")[0])){
				link.setCasPatchType(1);//Patching using data from update cas
				//link.setResType(resType);
				//link.setLogicalOffset(logicalOffset);
				link.setSha1(casCatEntry.getSHA1().toLowerCase());
				link.setBaseSha1(null);
				link.setDeltaSha1(null);
				link.setSize(casCatEntry.getProcSize());
				link.setOriginalSize(originalSize);
				return true;
			}
		}
		
	/*NEW ONE*/
		String targetObject = packEntry.getTargetPath();//has a special targetPath defined, use this.
		if (targetObject==null){
			targetObject = packEntry.getResourcePath();//otherwise use the resourcePath as target.
		}
		ResourceLink link = new ResourceLink();
		link.setName(targetObject.replace(".", "-").split("-")[0]);
		link.setType(packEntry.getResType());
		//link.setResType(resType);
		//link.setLogicalOffset(logicalOffset);
		link.setBaseSha1(null);
		link.setDeltaSha1(null);
		link.setCasPatchType(1);//Patching using data from update cas
		link.setSha1(casCatEntry.getSHA1().toLowerCase());
		link.setSize(casCatEntry.getProcSize());
		link.setOriginalSize(originalSize);
		targetList.add(link);
		return false;
	}
	public boolean extendCurrentPackage(LinkBundleType bundle, String sbPart, ResourceType type, String path){
		String currentTocName = Core.getGame().getCurrentToc().getName();
		Package currentPackage = Core.getModTools().getPackage(currentTocName);
		if (currentPackage!=null){
			return extendPackage(bundle, sbPart, type, path, currentPackage);
		}
		return false;
	}
	
	public boolean extendPackage(LinkBundleType bundle, String sbPart, ResourceType type, String path, Package pack){
		return extendPackage(bundle, sbPart, type, path, null, pack);
	}
	
	public boolean extendPackage(LinkBundleType bundle, String sbPart, ResourceType type, String path, String targetPath, Package pack){
		PackageEntry entry = new PackageEntry(bundle, sbPart, type, path);
		if (targetPath!=null){
			/*Additional, if someone whats to have a diffrent resources for a package with the same path.*/
			entry.setTargetPath(targetPath);
		}
		for (PackageEntry pEntry: pack.getEntries()){
			if (pEntry.getBundleType()==bundle && pEntry.getSubPackage().equals(sbPart) &&
					pEntry.getResourcePath().equals(path) && pEntry.getResType()==type &&
						pEntry.getTargetPath()==targetPath){
				//entry does already exist.
				System.err.println("Entry does allready exist. So we dont have to extend the package!");
				return true;
			}
		}
		pack.getEntries().add(entry);
		return true;
	}
	
	
	public byte[] readOrignalData(String resourceName, ArrayList<ResourceLink> resourceList){
		for (ResourceLink link : resourceList){
			if (link.getName().equalsIgnoreCase(resourceName)){
				return CasDataReader.readCas(link.getBaseSha1(), link.getDeltaSha1(), link.getSha1(), link.getCasPatchType());
			}
		}
		System.err.println("Original Data could not get found for "+resourceName);
		return null;
	}
	
	//i dont really have to create a function for removing one line from a ".pack" file? huh.
	
	//Getter n Setter
	public ArrayList<Mod> getMods() {
		return mods;
	}
	public ArrayList<Package> getPackages() {
		return packages;
	}
	
}
