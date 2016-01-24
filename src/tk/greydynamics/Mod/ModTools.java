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
import tk.greydynamics.Resource.ResourceHandler;
import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.ResourceHandler.OriginType;
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
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter.ResourceBundleType;
import tk.greydynamics.Resource.Frostbite3.Toc.TocEntry;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager;

public class ModTools {
	public ArrayList<Mod> mods;
	public ArrayList<Package> packages;
	public static final String FOLDER_RESOURCE = "/resources/";
	public static final String FOLDER_PACKAGE = "/packages/";
	public static final String PACKTYPE = ".pack";
	public static final String FOLDER_COMPILEDDATA = "/compiled_data/";
	public static final String FILE_EDITOR_CONFIG = "/currentmod.cfg";
	public static final String FOLDER_ORIGINAL = "/original_backup/";
	public static final String FILE_MODFILE_LIST = "/modfiles.txt";

	public ModTools() {
		init();
	}
	public void init(){
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
				if (mod.getPath()!=null){
					File compiledFolder = new File(mod.getPath()+ModTools.FOLDER_COMPILEDDATA);
					if (compiledFolder.isDirectory()&&compiledFolder.exists()){
						mod.setCompiled(true);
					}
				}
				if (mod.getAuthor() != null){
//					String[] split = Core.gamePath.split("/");
//					int length = split.length;
//					if (Core.gamePath.endsWith("/")){
//						length--;
//					}
//					String destFolderPath = "";
//					for (int i=0; i<length-1;i++){
//						destFolderPath +=split[i]+"/";
//					}
//					destFolderPath += mod.getGame()+"_"+mod.getFolderName();
//					mod.setDestFolderPath(destFolderPath);
//					mod.setDestFolderPath(null);
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
		    mod.setGameVersion(br.readLine());
		    String line = "";
		    String text = "";
		    while ((line = br.readLine()) != null){
		    	text += line+"\n";
		    }
		    mod.setDesc(text);
		    br.close();
		    fr.close();
		    String installedMod = ModTools.getInstalledMod(Core.gamePath);
		    if (installedMod!=null){
		    	mod.setInstalled(installedMod.equals(mod.getFolderName()));
		    }else{
		    	mod.setInstalled(false);
		    }
		    
		    mod.setCompiled(new File(mod.getPath()+ModTools.FOLDER_COMPILEDDATA).exists());
		    
		    
		}catch (Exception e){
			System.err.println("Could not read Info from Mod. "+info.getAbsolutePath());
		}
	}

	
	public void fetchPackages(){
		int entries = 0;
		ArrayList<File> files = FileHandler.listf(Core.getGame().getCurrentMod().getPath()+FOLDER_PACKAGE, ".pack");
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
	public boolean installMod(String rootPath, Mod mod){
		if (mod.isCompiled&&getInstalledMod(rootPath)==null){
			setInstalledMod(rootPath, mod);
			
			//TODO LOGIC
			String compiledDataPath = FileHandler.normalizePath(mod.getPath()+FOLDER_COMPILEDDATA);
			ArrayList<File> modFiles = FileHandler.listf(compiledDataPath, null);
			ArrayList<String> relModFilePaths = new ArrayList<>();
			for (File modFile : modFiles){
				if (!modFile.isDirectory()){
					String relPath = FileHandler.normalizePath(modFile.getAbsolutePath()).replace(compiledDataPath, "/");
					relModFilePaths.add(relPath);
					File original = new File(rootPath+relPath);
					if (original.exists()){
						//Move to backup location.
						if (!FileHandler.move(original, new File(rootPath+FOLDER_ORIGINAL+relPath), false)){
							Core.getJavaFXHandler().getDialogBuilder().showError("Operation failed.", "A file can't be moved to backup location. \n"
									+ "Target does already exist."
									+ "\n\n"
									+ "You maybe have to repair with Origin.", null);
							return false;
						}
					}
					if (!FileHandler.copy(modFile, new File(rootPath+relPath), false)){
						//Copy mod file to target.
						Core.getJavaFXHandler().getDialogBuilder().showError("Operation failed.", "A mod file can't be copied. \n"
								+ "Target does already exist."
								+ "\n\n"
								+ "You maybe have to repair with Origin.", null);
						return false;
					}
				}
			}
			FileHandler.writeLine(relModFilePaths, new File(rootPath+FOLDER_ORIGINAL+FILE_MODFILE_LIST));
			mod.setInstalled(true);
			init();
			return true;
		}else{
			Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Only one Mod can be installed, once at the time!", null);
		}
		return false;
	}
	public boolean uninstallMod(String rootPath, Mod mod){
		ArrayList<String> modFilePaths = FileHandler.readTextFile(rootPath+FOLDER_ORIGINAL+FILE_MODFILE_LIST);
		if (modFilePaths!=null){
			for (String modFilePath : modFilePaths){
				File modFile = new File(rootPath+modFilePath);
				if (modFile.exists()){
					modFile.delete();
				}
			}
			ArrayList<File> originalFiles = FileHandler.listf(rootPath+FOLDER_ORIGINAL, null);
			for (File originalFile : originalFiles){
				if (!originalFile.isDirectory()&&!originalFile.getName().equals(FILE_MODFILE_LIST.replace("/", ""))){
					if (!FileHandler.move(originalFile, new File(FileHandler.normalizePath(originalFile.getAbsolutePath()).replace(FOLDER_ORIGINAL.replace("/", ""), "")), true)){
						Core.getJavaFXHandler().getDialogBuilder().showError("Operation failed.", "A original file could not be restored. \n"
								+ "\n\n"
								+ "You maybe have to repair with Origin or try to replace manually from "+FOLDER_ORIGINAL+".", null);
					}
				}
			}
			
			File installedModFile = new File(rootPath+FILE_EDITOR_CONFIG); 
			if (installedModFile.exists()){
				installedModFile.delete();
			}
			
			File backupFolder = new File(rootPath+FOLDER_ORIGINAL+"/"); 
			if (backupFolder.exists()&&backupFolder.isDirectory()){
				FileHandler.deleteFolder(backupFolder);
			}
			mod.setInstalled(false);
			init();
			return true;
		}
		System.err.println("If you have already uninstalled and still stuck, delete the "+FILE_EDITOR_CONFIG+" file to clean!");
		return false;
	}
	
	public static String getInstalledMod(String rootPath){
		try{
			FileReader fr = new FileReader(rootPath+FILE_EDITOR_CONFIG);
			
			BufferedReader br = new BufferedReader(fr);
			String installedMod = br.readLine();
			fr.close();
			br.close();
		    return installedMod;
		}catch (Exception e){
			return null;
		}
	}
	private static boolean setInstalledMod(String rootPath, Mod mod){
		try{
			ArrayList<String> stringArr = new ArrayList<>();
			stringArr.add(mod.getFolderName());
			FileHandler.writeLine(stringArr, new File(rootPath+FILE_EDITOR_CONFIG));
			mod.setInstalled(true);
		    return true;
		}catch (Exception e){
			return false;
		}
	}
	
	public boolean compileMod(String path){
		Boolean enabled = true;
		if (enabled&&Core.getGame().getCurrentMod()!=null){
			Mod mod = Core.getGame().getCurrentMod();

			if (path!=null){
				System.out.println("Compile Client...");
				String relCas_Path = "/Data/cas_99.cas";
				Mod currentMod = Core.getGame().getCurrentMod();
				
				for (Package pack : packages){
					Core.getGame().setCurrentFile(FileHandler.normalizePath((Core.gamePath+"/"+pack.getName())));
					String casCatPath = null;
					CasCatManager casCatMgr = null;
					OriginType origin = ResourceHandler.getOriginType(Core.getGame().getCurrentFile());
					
					if (origin==OriginType.PATCHED){
						casCatPath = FileHandler.normalizePath(path+Core.PATH_UPDATE_PATCH+relCas_Path);
						casCatMgr = Core.getGame().getResourceHandler().getPatchedCasCatManager();
					}else if (origin==OriginType.XPACK){
						System.err.println("XPacks currently not supported.");
						return false;
					}else if (origin==OriginType.BASE){
						casCatPath = FileHandler.normalizePath(path+relCas_Path);
						casCatMgr = Core.getGame().getResourceHandler().getCasCatManager();
					}
					
					
					CasManager.createCAS(casCatPath);
					
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
						LayoutFile toc = TocManager.readToc(Core.getGame().getCurrentFile());
						ConvertedTocFile convToc = TocConverter.convertTocFile(toc);

						CasBundle currentSBpart = null;
						for (TocEntry link : convToc.getBundles()){
							if (link.getID().equals(subPackageName)){
								//link.setSbPath(sbPath); change the sb path once one subpackage is already done
								LayoutFile casBundle = link.getLayout();
								currentSBpart = TocConverter.convertCASBundle(casBundle, false);
								break;
							}
						}
						if (currentSBpart==null){
							System.err.println("Mod.ModTools.playMod can't handle new subpackages at this time. ");
							return false;
						}
						
						/*SMASH LOGIC*/
						boolean test = false;
						if (test){
							LayoutFile toc2 = TocManager.readToc(Core.gamePath+"/Update/Patch/Data/Win32/Levels/SP/SP_Suez/SP_Suez");
							ConvertedTocFile convToc2 = TocConverter.convertTocFile(toc2);
							
							CasBundle currentSBpart2 = null;
							for (TocEntry link : convToc2.getBundles()){
								if (link.getID().equals("win32/levels/sp/sp_suez/bridge")){
									LayoutFile casBundle2 = link.getLayout();
									currentSBpart2 = TocConverter.convertCASBundle(casBundle2, false);
									break;
								}
							}
							if (currentSBpart2==null){
								System.err.println("smash failed ");
								return false;
							}
							for (ResourceLink ebxLink : currentSBpart2.getEbx()){
								boolean exists = false;
								for (ResourceLink ebxLink2 : currentSBpart.getEbx()){
									if (ebxLink.getName().equals(ebxLink2.getName())){
										exists = true;
										break;
									}
								}
								if (!exists){
									currentSBpart.getEbx().add(ebxLink);
								}
							}
							for (ResourceLink resLink : currentSBpart2.getRes()){
								boolean exists = false;
								for (ResourceLink resLink2 : currentSBpart.getRes()){
									if (resLink.getName().equals(resLink2.getName())){
										exists = true;
										break;
									}
								}
								if (!exists){
									currentSBpart.getRes().add(resLink);
								}
							}
							for (ResourceLink chunkLink : currentSBpart2.getChunks()){
								boolean exists = false;
								for (ResourceLink chunkLink2 : currentSBpart.getChunks()){
									if (chunkLink2.getId().equals(chunkLink.getId())){
										exists = true;
										break;
									}
								}
								if (!exists){
									currentSBpart.getChunks().add(chunkLink);
								}
							}
							for (ResourceLink chunkMeta : currentSBpart2.getChunkMeta()){
								boolean exists = false;
								for (ResourceLink chunkMeta2 : currentSBpart.getChunkMeta()){
									if (chunkMeta2.getH32()==chunkMeta.getH32()){
										exists = true;
										break;
									}
								}
								if (!exists){
									currentSBpart.getChunkMeta().add(chunkMeta);
								}
							}
						}
						/**/
						
						
						
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
								case EBX:
									data = FileHandler.readFile(currentMod.getPath()+FOLDER_RESOURCE+sortedEntry.getResourcePath());
									originalSize = data.length;
									casCatEntry = CasManager.extendCAS(data, new File(casCatPath), casCatMgr);
									break;
								case ITEXTURE:
									byte[] ddsFileBytes = /*DSS FILE*/FileHandler.readFile(currentMod.getPath()+FOLDER_RESOURCE+sortedEntry.getResourcePath());
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
										casCatEntry = CasManager.extendCAS(data, new File(casCatPath), casCatMgr);
	
										byte[] blockData = ITextureConverter.getBlockData(ddsFileBytes);
										casCatEntryChunk = CasManager.extendCAS(blockData, new File(casCatPath), casCatMgr);
	
										modifyChunkEntry(casCatEntryChunk, chunkID, blockData.length, newITexture.getNameHash(), currentSBpart, true /*isNew*/);
									}else{
										System.err.println("ITexture could not get applied!");
									}
									break;
								default:
									break;
							}

							if (sortedEntry.getResType()==ResourceType.EBX){
								modifyResourceLink(sortedEntry, casCatEntry, originalSize, currentSBpart.getEbx());
							}else if (sortedEntry.getResType()==ResourceType.ITEXTURE||sortedEntry.getResType()==ResourceType.MESH){
								modifyResourceLink(sortedEntry, casCatEntry, originalSize, currentSBpart.getRes());
							}else if (sortedEntry.getResType()==ResourceType.EDITOR_RESOURCELINK){
								ResourceLink link =  ResourceLink.importResourceLink(new File(currentMod.getPath()+FOLDER_RESOURCE+sortedEntry.getResourcePath()));
								if (link!=null){
									if (link.getBundleType()==ResourceBundleType.EBX){
										currentSBpart.getEbx().add(link);
									}else if (link.getBundleType()==ResourceBundleType.RES){
										currentSBpart.getRes().add(link);
									}else{
										System.err.println("ResourceLink can't be inported to subpackage. Unknown ResourceBundleType!");
									}
								}
							}else{
								System.err.println(sortedEntry.getResType()+" isn't defined in (Mod.ModTools.playMod) for modifyResourceL1nk!");
							}
							if (casCatEntryChunk!=null){
								casCatMgr.getEntries().add(casCatEntryChunk);
							}
							if (casCatEntry!=null){
								casCatMgr.getEntries().add(casCatEntry);
							}
						}
						//TODO convToc.setTotalSize(totalSize);
						String newPath = ((String) Core.getGame().getCurrentFile()+".sb").replace(Core.gamePath, path);
						LayoutCreator.createModifiedSBFile(convToc, currentSBpart, false/*TODO*/, newPath, true/*delete first*/);
						byte[] tocBytes = LayoutCreator.createTocFile(convToc);
						File newTocFile = new File(((String) Core.getGame().getCurrentFile()+".toc").replace(Core.gamePath, path));
						FileHandler.writeFile(newTocFile.getAbsolutePath(), tocBytes);
					}
					//Create new CasCat
					byte[] casCatBytes = casCatMgr.getCat();
					String relPart = null;
					if (origin==OriginType.BASE){
						relPart = "";
					}else if (origin==OriginType.PATCHED){
						relPart = Core.PATH_UPDATE_PATCH;
					}
					File casCatFile = new File(path+relPart+"/Data/cas.cat");
					FileHandler.writeFile(casCatFile.getAbsolutePath(), casCatBytes);
				}
				
				mod.setCompiled(true);
				init();
				return true;
			}
			Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Something went wrong :(", null, null);
			return false;
			//			}else{
			//				FileHandler.openFolder(Core.getGame().getCurrentMod().getDestFolderPath());
			//				Core.getJavaFXHandler().getDialogBuilder().showInfo("INFO", "Have fun =)");
			//				return false;
			//			}
		}
		Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "This feature is currently not working or available for public.", null);
		return false;
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
//		
//	/*NEW ONE*/
//		String targetObject = packEntry.getTargetPath();//has a special targetPath defined, use this.
//		if (targetObject==null){
//			targetObject = packEntry.getResourcePath();//otherwise use the resourcePath as target.
//		}
//		ResourceLink link = new ResourceLink();
//		link.setName(targetObject.replace(".", "-").split("-")[0]);
//		link.setType(packEntry.getResType());
//		//link.setResType(resType);
//		//link.setLogicalOffset(logicalOffset);
//		link.setBaseSha1(null);
//		link.setDeltaSha1(null);
//		link.setCasPatchType(1);//Patching using data from update cas
//		link.setSha1(casCatEntry.getSHA1().toLowerCase());
//		link.setSize(casCatEntry.getProcSize());
//		link.setOriginalSize(originalSize);
//		targetList.add(link);
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
