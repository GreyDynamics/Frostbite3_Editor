package tk.greydynamics.Resource.Frostbite3.MESH;

import java.util.ArrayList;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabase;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseMaterial;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseRedirectEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjArray;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXTextureShaderParameter;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXTextureShaderParameter.ParameterName;

public class MeshVariationDatabaseHandler {
	public ArrayList<EBXStructureFile> databases;
	
	public void reset(){
		databases = new ArrayList<>();
		Core.getJavaFXHandler().getMainWindow().updateMeshvariationDatabaseComboBox(databases);
	}
	
	public void deleteDatabase(String name){
		EBXStructureFile db = getDatabaseByName(name);
		if (db!=null){
			databases.remove(db);
			Core.getJavaFXHandler().getMainWindow().updateMeshvariationDatabaseComboBox(databases);
		}
	}
	
	public void addDatabase(EBXStructureFile structFile){
		databases.add(structFile);
		Core.getJavaFXHandler().getMainWindow().updateMeshvariationDatabaseComboBox(databases);
	}
	
	public EBXStructureFile getDatabaseByName(String name){
		for (EBXStructureFile db : databases){
			if (db.getStructureName().equalsIgnoreCase(name)){
				return db;
			}
		}
		return null;
	}
	
	public EBXStructureFile getDatabaseByEBXGUID(String ebxGUID){
		for (EBXStructureFile db : databases){
			if (db.getEBXGUID().equalsIgnoreCase(ebxGUID)){
				return db;
			}
		}
		return null;
	}
	
	public EBXMeshVariationDatabaseEntry getVariationDatabaseEntry(EBXStructureFile databaseFile, long variationAssetNameHash, EBXExternalGUID meshExternalGUID, boolean excludeRedirect){
		if (variationAssetNameHash<1&&meshExternalGUID==null){
			System.err.println("MeshVariationDataBaseHander can't fetch the Entry, no GUID or AssetNameHash given!");
			return null;
		}
		EBXStructureInstance instance = databaseFile.getFirstInstance(EntryType.MeshVariationDatabase);
		if (instance!=null){
			EBXMeshVariationDatabase database = (EBXMeshVariationDatabase) instance.getEntry();
			Object[] entries = database.getEntries().getObjects();
			if (entries!=null){
				if (meshExternalGUID!=null){
					//Try to fetch using mesh guid
					for (Object obj : entries){
						EBXMeshVariationDatabaseEntry entry = (EBXMeshVariationDatabaseEntry) obj;
						if (entry.getMesh().getInstanceGUID().equalsIgnoreCase(meshExternalGUID.getInstanceGUID())){
							return entry;
						}else{
							continue;
						}
					}
				}
				else if(variationAssetNameHash>1){
					//Try to fetch using AssetNameHash
					for (Object obj : entries){
						EBXMeshVariationDatabaseEntry entry = (EBXMeshVariationDatabaseEntry) obj;
						if (entry.getVariationAssetNameHash()==variationAssetNameHash){
							return entry;
						}else{
							continue;
						}
					}
				}
				if (!excludeRedirect){
					//Try to find the redirect
					EBXMeshVariationDatabaseRedirectEntry[] redirectEntries = (EBXMeshVariationDatabaseRedirectEntry[]) database.getRedirectEntries().getObjects();
					if (redirectEntries!=null){
						for (EBXMeshVariationDatabaseRedirectEntry redi : redirectEntries){
							if (redi.getMesh().getInstanceGUID().equalsIgnoreCase(meshExternalGUID.getInstanceGUID())){
								return getVariationDatabaseEntry(databaseFile, redi.getVariationAssetNameHash(), null, true);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public EBXMeshVariationDatabaseMaterial getVariationDatabaseMaterial(EBXMeshVariationDatabaseEntry dbEntry, EBXExternalGUID materialExternalGUID){
		EBXObjArray materialsArr = dbEntry.getMaterials();
		if (materialsArr!=null){
			Object[] materials = materialsArr.getObjects();
			if (materials!=null){
				for (Object obj : materials){
					EBXMeshVariationDatabaseMaterial dbMaterial = (EBXMeshVariationDatabaseMaterial) obj;
					if (dbMaterial.getMaterial().getInstanceGUID().equalsIgnoreCase(materialExternalGUID.getInstanceGUID())){
						return dbMaterial;
					}else{
						continue;
					}
				}
			}			
		}
		return null;
	}
	
	public EBXExternalGUID getTexture(EBXMeshVariationDatabaseMaterial material, ParameterName parameterName){
		EBXObjArray parameters = material.getTextureParameters();
		if (parameters!=null){
			Object[] tspArr = parameters.getObjects();
			if (tspArr!=null){
				for (Object obj : tspArr){
					EBXTextureShaderParameter parameter = (EBXTextureShaderParameter) obj;
					if (parameter.getParameterName()!=null){
						if (parameter.getParameterName().equalsIgnoreCase(parameterName.toString())){
							return parameter.getValue();
						}else{
							continue;
						}
					}
				}
			}
		}
		return null;
	}
}
