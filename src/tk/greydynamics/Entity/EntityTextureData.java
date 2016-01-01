package tk.greydynamics.Entity;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Resource.Frostbite3.Cas.CasDataReader;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseMaterial;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjArray;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXTextureShaderParameter.ParameterName;
import tk.greydynamics.Resource.Frostbite3.MESH.MeshVariationDatabaseHandler;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;

public class EntityTextureData {
	private int[] diffuseIDs;
	
	private EBXExternalGUID meshGUID = null;
	//private EBXExternalGUID materialGUID = null;
	
	public EntityTextureData(EBXExternalGUID meshGUID, /*EBXExternalGUID materialGUID,*/ EBXStructureFile meshVariationDatabase) {
		this.meshGUID = meshGUID;
		//this.materialGUID = materialGUID;
		updateTextures(meshVariationDatabase);
	}
	
	public boolean updateTextures(EBXStructureFile meshVariationDatabase){
		if (meshGUID!=null&&meshVariationDatabase!=null){
			MeshVariationDatabaseHandler dbH = Core.getGame().getResourceHandler().getMeshVariationDatabaseHandler();
			EBXMeshVariationDatabaseEntry entry = dbH.getVariationDatabaseEntry(meshVariationDatabase, -1, this.meshGUID, false);
			if (entry!=null){
				EBXObjArray materialsArr = entry.getMaterials();
				if (materialsArr!=null){
					Object[] materials = materialsArr.getObjects();
					diffuseIDs = new int[materials.length];
					int counter = 0;
					for (Object obj : materials){
						EBXMeshVariationDatabaseMaterial material = (EBXMeshVariationDatabaseMaterial) obj;
						boolean diffuseSuccess = false;
						if (material!=null){
							//Diffuse
							EBXExternalGUID diffuseTextureGUID = dbH.getTexture(material, ParameterName.Diffuse);
							if (diffuseTextureGUID!=null){
							ResourceLink diffuseLinkEBX = Core.getGame().getResourceHandler().getResourceLinkByEBXGUID(diffuseTextureGUID.getFileGUID());
								if (diffuseLinkEBX!=null){
									ResourceLink diffuseLinkITEXTURE = Core.getGame().getResourceHandler().getResourceLink(diffuseLinkEBX.getName(), ResourceType.ITEXTURE);
									if (diffuseLinkITEXTURE!=null){
										diffuseIDs[counter] = Core.getGame().getResourceHandler().getTextureHandler().loadITexture(diffuseLinkITEXTURE);
										diffuseSuccess = true;
									}
								}
							}
							
							//Specular
								
								
							//Normal
						}
						if (!diffuseSuccess){
							diffuseIDs[counter] = Core.getGame().getModelHandler().getLoader().getNotFoundID();
						}
						counter++;
					}
				}
			}
			return true;
		}
		this.diffuseIDs = null;
		return false;
	}

	public int[] getDiffuseIDs() {
		return diffuseIDs;
	}

	public EBXExternalGUID getMeshGUID() {
		return meshGUID;
	}
	
	
	
}
