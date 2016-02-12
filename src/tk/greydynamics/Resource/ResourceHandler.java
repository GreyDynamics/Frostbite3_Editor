package tk.greydynamics.Resource;

import java.io.File;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Mod.Package;
import tk.greydynamics.Mod.PackageEntry;
import tk.greydynamics.Render.TextureHandler;
import tk.greydynamics.Resource.Frostbite3.Cas.Bundle;
import tk.greydynamics.Resource.Frostbite3.Cas.Bundle.BundleType;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.CasCatManager;
import tk.greydynamics.Resource.Frostbite3.Cas.CasDataReader;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundleEntry;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasDataReader;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.Component.EBXComponentHandler;
import tk.greydynamics.Resource.Frostbite3.MESH.MeshChunkLoader;
import tk.greydynamics.Resource.Frostbite3.MESH.MeshVariationDatabaseHandler;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class ResourceHandler {	
	public static enum ResourceType{ EBX, CHUNK, ITEXTURE, MESH, HKDESTRUCTION, HKNONDESTRUCTION, ANT,
				ANIMTRACKDATA, RAGDOLL, OCCLUSIONMESH,
			LIGHTINGSYSTEM, GFX, STREAIMINGSTUB, ENLIGHTEN, PROBESET, STATICENLIGHTEN,
		SHADERDATERBASE, SHADERDB, SHADERPROGRAMDB, LUAC, UNDEFINED, EDITOR_RESOURCELINK
	};
		
	public static enum LinkBundleType{BUNDLES, CHUNKS};
	public static enum OriginType{BASE, PATCHED, XPACK};
	
	MeshChunkLoader mcL;
	MeshVariationDatabaseHandler mvdH;
	EBXHandler ebxHandler;
	EBXComponentHandler ebxComponentHandler;
	TextureHandler textureHandler;
	CasCatManager cCatManager; 
	CasCatManager patchedCasCatManager;

	public ResourceHandler() {
		this.mcL = new MeshChunkLoader();
		this.ebxHandler = new EBXHandler();
		this.mvdH = new MeshVariationDatabaseHandler();
		this.textureHandler = new TextureHandler();
		this.cCatManager = new CasCatManager();
		this.patchedCasCatManager = new CasCatManager();
		this.ebxComponentHandler = null;
	}
	
	public void createEBXComponentHandler(String gameName){
		this.ebxComponentHandler = new EBXComponentHandler(Core.EDITOR_PATH_GAMEDATA+gameName+"/EBXLibrary/");
	}
	
	public void resetEBXRelated(){
		ebxHandler.reset();
		mvdH.reset();
		Core.getJavaFXHandler().getMainWindow().destroyEBXWindows();
		Core.getJavaFXHandler().getMainWindow().destroyEBXComponentWindows();
	}
	
	public ResourceLink getResourceLinkByEBXGUID(String ebxGUID){
		//CAS
		if (Core.getGame().getCurrentBundle().getType()==Bundle.BundleType.CAS){
			CasBundle casBundle = (CasBundle) Core.getGame().getCurrentBundle();
			for (ResourceLink link : casBundle.getEbx()){
				if (link.getEbxFileGUID()!=null){
					if (link.getEbxFileGUID().equalsIgnoreCase(ebxGUID)){
						return link;
					}
				}
			}
			//System.err.println("ResourceLink not found for EBXGUID "+ebxGUID+"!");
		}
		return null;
	}
	public NonCasBundleEntry getNonCasBundleEntrykByEBXGUID(String ebxGUID){
		//NONCAS
		if (Core.getGame().getCurrentBundle().getType()!=Bundle.BundleType.CAS){
			NonCasBundle nonCasBundle = (NonCasBundle) Core.getGame().getCurrentBundle();
			for (NonCasBundleEntry entry : nonCasBundle.getEbx()){
				if (entry.getEbxFileGUID()!=null){
					if (entry.getEbxFileGUID().equalsIgnoreCase(ebxGUID)){
						return entry;
					}
				}
			}
		}
		return null;
	}
	
	public ResourceLink getResourceLink(String resourceName, ResourceType resourceType){
		if (Core.getGame().getCurrentBundle().getType()==Bundle.BundleType.CAS){
			CasBundle casBundle = (CasBundle) Core.getGame().getCurrentBundle();
			if (resourceType==ResourceType.EBX){
				for (ResourceLink resLinkEBX : casBundle.getEbx()){
					if (resLinkEBX.getName().equalsIgnoreCase(resourceName)){
						return resLinkEBX;
					}
				}
			}else{
				for (ResourceLink resLinkRES : casBundle.getRes()){
					if (resLinkRES.getName().equalsIgnoreCase(resourceName)){
						return resLinkRES;
					}
				}
			}
		}
		return null;
	}
		
	public byte[] readResourceLink(ResourceLink link, boolean useOriginal){
		System.out.println("Reading Link: "+link.getName()+" - Original only:("+useOriginal+")");
		return readResource(link.getBaseSha1(), link.getDeltaSha1(), link.getSha1(), link.getCasPatchType(),
				link.getName(), link.getType(), useOriginal);
	}	
	public byte[] readResourceLink(ResourceLink link){
		return readResourceLink(link, false);
	}
	public byte[] readNonCasBundleEntry(NonCasBundleEntry nonCasBundleEntry){
		if (Core.getGame().getCurrentBundle().getType()==BundleType.CAS){
			System.err.println(Core.getGame().getCurrentBundle().getName()+" is not a NON-CAS Bundle!");
			return null;
		}
		return NonCasDataReader.readNonCasBundleData((NonCasBundle) Core.getGame().getCurrentBundle(), nonCasBundleEntry);
	}
	public byte[] readResource(String baseSHA1, String deltaSHA1, String SHA1, int casPatchType,
			String name, ResourceType resourceType, boolean useOriginal)
		{
		byte[] data = null;
		if (!useOriginal && name!=null){//should get patched files used ? (default: true)
			File modFilePack = new File(FileHandler.normalizePath(
					Core.getGame().getCurrentMod().getPath()+ModTools.FOLDER_PACKAGE+
					Core.getGame().getCurrentFile().replace(Core.gamePath, "")+ModTools.PACKTYPE)
			);
			Package modPackage = null;
			if (modFilePack.exists()){
				modPackage = Core.getModTools().readPackageInfo(modFilePack);
			}
			
			if (modPackage!=null){
				//package was found. letz find the entry!
				for (PackageEntry entry : modPackage.getEntries()){
					if (entry.getSubPackage().equalsIgnoreCase(Core.getGame().getCurrentBundle().getBasePath())&&//mp_playground/content
						entry.getResourcePath().equalsIgnoreCase(name+"."+resourceType))//levels/mp_playground/content/layer2_buildings.ebx .itexture .mesh
					{
						//we are in the right package and an entry was found ;)
						System.err.println("Mod file was found, this is our resource!");
						data = FileHandler.readFile(Core.getGame().getCurrentMod().getPath()+ModTools.FOLDER_RESOURCE+entry.getResourcePath());
						if (data!=null){
							return data;
						}
					}
				}
			}
		}
		System.err.println("No Mod file does exist for this resource, use ORIGINAL data from Game.");
		data = CasDataReader.readCas(baseSHA1, deltaSHA1, SHA1, casPatchType);
		//hope here, that it was found!
		return data;
	}
	
	
	public CasCatManager getPatchedCasCatManager() {
		return patchedCasCatManager;
	}


	public MeshChunkLoader getMeshChunkLoader() {
		return mcL;
	}
	

	public MeshVariationDatabaseHandler getMeshVariationDatabaseHandler() {
		return mvdH;
	}

	public EBXHandler getEBXHandler() {
		return ebxHandler;
	}

	public TextureHandler getTextureHandler() {
		return textureHandler;
	}


	public CasCatManager getCasCatManager() {
		return cCatManager;
	}

	public EBXComponentHandler getEBXComponentHandler() {
		return ebxComponentHandler;
	}
	
	public static String getOrigin(String tocFile){
		String normalizedPath = FileHandler.normalizePath(tocFile);
		if (normalizedPath.contains(Core.PATH_DATA)&&!normalizedPath.contains(Core.PATH_PATCH)){
			//Unpatched
			return Core.PATH_DATA.replace("/", "");
		}else if (normalizedPath.contains(Core.PATH_PATCH)&&!normalizedPath.contains(Core.PATH_UPDATE)){
			//DLC
			return normalizedPath.replace(Core.gamePath, "").replace(Core.PATH_PATCH, "").split("/")[0];
		}else if (normalizedPath.contains(Core.PATH_UPDATE_PATCH)){
			//PATCHED
			return Core.PATH_UPDATE.replace("/", "");
		}else{
			return null;
		}
	}
	public static OriginType getOriginType(String filePath){
		String normalizedPath = FileHandler.normalizePath(filePath);
		if (normalizedPath.contains(Core.PATH_UPDATE_PATCH)){
			return OriginType.PATCHED;
		}else if (normalizedPath.contains(Core.PATH_PATCH)){
			return OriginType.XPACK;
		}else{
			return OriginType.BASE;
		}
	}
	
	
}
