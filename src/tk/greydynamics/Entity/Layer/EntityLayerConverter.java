package tk.greydynamics.Entity.Layer;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityHandler;
import tk.greydynamics.Entity.Entities.InstanceEntity;
import tk.greydynamics.Entity.Entities.ObjectEntity;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.Windows.EBXWindow;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;
import tk.greydynamics.Resource.Frostbite3.Cas.Bundle.BundleType;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundleEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXLinearTransform;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXBlueprintTransform;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class EntityLayerConverter {
		
	public static EntityLayer getEntityLayer(EBXFile ebxFile, EBXWindow ebxWindow){
		boolean loadOriginal = false;
		
		EntityHandler entityHandler = Core.getGame().getEntityHandler();
		EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
		
		if (ebxFile!=null){
			String[] strArray = ebxFile.getTruePath().split("/");		 
			//EntityLayer layer = new EntityLayer(strArray[strArray.length-1]+" "+ebxFile.getGuid());
			EntityLayer layer = new EntityLayer(ebxFile.getTruePath()+" "+ebxFile.getGuid(), ebxWindow); 
			
			for (EBXInstance instance : ebxFile.getInstances()){
				Entity en = getEntity(layer, true, null, instance, null, new EBXExternalGUID(ebxFile.getGuid(), instance.getGuid()), loadOriginal);	
				if (en!=null){
					layer.getEntities().add(en);
				}
				break;
			}
			return layer;
		}
		System.err.println("Can not create EntityLayer in EntityLayerConverter from EBXFile!");
		return null;
	}
	private static Entity getEntity(EntityLayer layer, boolean isMaster, Vector3f pickingColors, EBXFile file, Entity parentEntity, boolean loadOriginal){
		//EBXFile
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (file==null){return null;}
		Entity en = new ObjectEntity(layer, file.getTruePath(), file, parentEntity, null, null, pickingColors);
		for (EBXInstance instance : file.getInstances()){
			Entity child = getEntity(layer, isMaster, pickingColors, instance, en, new EBXExternalGUID(file.getGuid(), instance.getGuid()), loadOriginal);
			if (child!=null){
				en.getChildrens().add(child);
			}
			break;
		}
		if (en.getChildrens().isEmpty()){
			return null;
		}
//		if (!isMaster){
//			calculateNewBoxSize(parentEntity, en);
//		}
		return en;
	}
	private static Entity getEntity(EntityLayer layer, boolean isMaster, Vector3f pickingColors, EBXInstance instance, Entity parentEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		//INSTANCE
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (instance==null){return null;}
		Entity en = new InstanceEntity(layer, instance.getGuid(), instance, parentEntity, pickingColors, null);
		Entity child = getEntity(layer, isMaster, pickingColors, instance.getComplex(), en, en, parentInstance, loadOriginal);
		if (child!=null){
			en.getChildrens().add(child);
		}
		if (en.getChildrens().isEmpty()){
			return null;
		}
//		if (!isMaster){
//			calculateNewBoxSize(parentEntity, en);
//		}
		return en;
	}
	private static Entity getEntity(EntityLayer layer, boolean isMaster, Vector3f pickingColors, EBXComplex complex, Entity parentEntity, Entity instanceEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		//COMPLEX
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (complex==null){return null;}
		Entity en = new ObjectEntity(layer, complex.getComplexDescriptor().getName(), complex, parentEntity, null, null, pickingColors);
//		int test = 0;
		for (EBXField field : complex.getFields()){
			Entity child = getEntity(layer, isMaster, pickingColors, field, en, instanceEntity, parentInstance, loadOriginal);
			if (child!=null){
//				child.setPosition(new Vector3f(test*100f, 0f, 0f));
				en.getChildrens().add(child);
//				test++;
			}
		}
		if (en.getChildrens().isEmpty()){
			return null;
		}
//		if (!isMaster){
//			calculateNewBoxSize(parentEntity, en);
//		}
		return en;
	}
	private static Entity getEntity(EntityLayer layer, boolean isMaster, Vector3f pickingColors, EBXField field, Entity parentEntity, Entity instanceEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		//FIELD
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (field==null){return null;}
		Entity en = new ObjectEntity(layer, field.getFieldDescritor().getName(), field, parentEntity, null, null, pickingColors);
		if (field.getValue() instanceof EBXComplex){
			if (field.getValueAsComplex().getComplexDescriptor().getName().equals("LinearTransform")){ 
				EBXLinearTransform linearTransform = new EBXLinearTransform(field.getValueAsComplex());
				InstanceEntity instance = (InstanceEntity) instanceEntity;
				instance.setLinearTransformField(field);
				instanceEntity.setPosition(linearTransform.getTranformation(), true);
				instanceEntity.setRotation(linearTransform.getRotation(EBXBlueprintTransform.IsDIRECT3D), true);
				instanceEntity.setScaleing(linearTransform.getScaling(EBXBlueprintTransform.IsDIRECT3D), true);
				
				//Temporary solution until scaling is fixed.
				instanceEntity.setRelMatrix(linearTransform.getMatrix4f(EBXBlueprintTransform.IsDIRECT3D));
			}else if (field.getFieldDescritor().getName().equals("Materials")){ 
				System.out.println("MATERIAL!"); 
			}else{
				Entity child = getEntity(layer, isMaster, pickingColors, field.getValueAsComplex(), en, instanceEntity, parentInstance, loadOriginal);
				if (child!=null){
					en.getChildrens().add(child);
				}
			}
		}else if (field.getValue() instanceof EBXField){
			Entity child = getEntity(layer, isMaster, pickingColors, (EBXField) field.getValue(), en, instanceEntity, parentInstance, loadOriginal);
			if (child!=null){
				en.getChildrens().add(child);
			}
		}else if (field.getValue() instanceof EBXExternalGUID){
			Entity child = getEntity(layer, isMaster, pickingColors, (EBXExternalGUID) field.getValue(), en, parentInstance, loadOriginal);
			if (child!=null){
				en.getChildrens().add(child);
			}
		}else if (field.getType()==FieldValueType.Guid){
//			System.err.println("DEBUG: "+field.getFieldDescritor().getName()+" "+field.getType());
			if (field.getFieldDescritor().getName().equals("member")||field.getFieldDescritor().getName().equalsIgnoreCase("Object")){  
				Entity child = getEntity(layer, isMaster, pickingColors, new EBXExternalGUID(parentInstance.getFileGUID(), (String) field.getValue()), en, parentInstance, loadOriginal);
				if (child!=null){
					en.getChildrens().add(child);
				}
			}
//			else if (parentEntity.getEntityObject() instanceof EBXComplex){
//				EBXComplex parentComplex = (EBXComplex) parentEntity.getEntityObject();
//				if (parentComplex.getComplexDescriptor().getName().equals("array")){
//					en.getChildrens().add(getEntity(new EBXExternalGUID(parentInstance.getFileGUID(), (String) field.getValue()), en, parentInstance, loadOriginal));
//				}
//			}
		}else{
			return null;
		}
		if (en.getChildrens().isEmpty()){
			return null;
		}
//		if (!isMaster){
//			calculateNewBoxSize(parentEntity, en);
//		}
		return en;
	}
	private static Entity getEntity(EntityLayer layer, boolean isMaster, Vector3f pickingColors, EBXExternalGUID externalGUID, Entity parentEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		Entity en = new ObjectEntity(layer, externalGUID.getBothGUIDsWithSlash(), externalGUID, parentEntity, null, null, pickingColors);
		Entity meshEntity = getEntity(layer, false, pickingColors, externalGUID, en, Type.Object, externalGUID);
		if (meshEntity!=null){
			en.getChildrens().add(meshEntity);
		}
		EBXFile targetFile = null;
		try{
			if (Core.getGame().getCurrentBundle().getType()==BundleType.CAS){
				ResourceLink link = Core.getGame().getResourceHandler().getResourceLinkByEBXGUID(externalGUID.getFileGUID());
				if (link!=null){
					if (!link.getName().contains("fx/")&&!link.getName().contains("fx_")&&!link.getName().contains("sound/")){
						targetFile = Core.getGame().getResourceHandler().getEBXHandler().getEBXFileByGUID(externalGUID.getFileGUID(), true, loadOriginal);
					}else{
						System.out.println("Skipping FX/Sounds in EntityLayerConverter: "+link.getName());
					}
				}
			}else{
				System.err.println("TODO: Skipping FX in EntityLayerConverter not supported for NONCAS");
				targetFile = Core.getGame().getResourceHandler().getEBXHandler().getEBXFileByGUID(externalGUID.getFileGUID(), true, loadOriginal);
			}
		}catch (Exception e){
			System.err.println("EntityLayerConverter couldn't load the EBX File: ");
			e.printStackTrace();
			targetFile = null;
		}
		if (targetFile!=null){
			boolean found = false;
			for (EBXInstance instance : targetFile.getInstances()){
				if (instance.getGuid().equalsIgnoreCase(externalGUID.getInstanceGUID())){
					if (isMaster){
						pickingColors=Entity.randomizedPickerColors();//bypass isMaster -> instance must have its own color, even its not master anymore, but only applie when master.
					}
					Entity child = getEntity(layer, false, pickingColors, instance, en, externalGUID, loadOriginal);
					if (child!=null){
						en.getChildrens().add(child);
					}
					found = true;
					break;
				}
			}
			if (!found){
//				This could end in a infinite loop!
//				System.err.println("EBXExternalGuid's instnace was not found inside EBXFile. Letz use the whole file!");
//				en.getChildrens().add(getEntity(targetFile, en, meshInstanceGUID, loadOriginal));
			}
		}
		if (en.getChildrens().isEmpty()){
			return null;
		}
		return en;
	}
	
	private static Entity getEntity(EntityLayer layer, boolean isMaster, Vector3f pickingColors, EBXExternalGUID externalGUID, Entity parentEntity, Type type, Object entityData){
		if (externalGUID==null){return null;};
		if (Core.getGame().getCurrentBundle().getType()==BundleType.CAS){
			//CAS
			ResourceLink ebxLink = Core.getGame().getResourceHandler().getResourceLinkByEBXGUID(externalGUID.getFileGUID());
			if (ebxLink!=null){
				String resLinkname = ebxLink.getName();
				CasBundle casBundle = (CasBundle) Core.getGame().getCurrentBundle();
				for (ResourceLink resLink : casBundle.getRes()){
					if (resLink.getName().equalsIgnoreCase(resLinkname)){
						if (resLink.getType()==ResourceType.MESH||resLink.getType()==ResourceType.OCCLUSIONMESH){
							byte[] meshData = Core.getGame().getResourceHandler().readResourceLink(resLink);
							if (meshData!=null){
								return Core.getGame().getEntityHandler().createEntity(layer, pickingColors, meshData, type, entityData, externalGUID, parentEntity, "EntityLayerConverter's getEntity-Method!"); //$NON-NLS-1$
							}
						}else{
							System.err.println("Found resource with same name, but its not an mesh. "+resLink.getName());
						}
					}
				}
			}
		}else{
			//NON CAS
			NonCasBundleEntry entry = Core.getGame().getResourceHandler().getNonCasBundleEntrykByEBXGUID(externalGUID.getFileGUID());
			if (entry!=null){
				String resLinkname = entry.getName();
				NonCasBundle nonCasBundle = (NonCasBundle) Core.getGame().getCurrentBundle();
				for (NonCasBundleEntry resEntry : nonCasBundle.getRes()){
					if (resEntry.getName().equalsIgnoreCase(resLinkname)){
						byte[] meshData = Core.getGame().getResourceHandler().readNonCasBundleEntry(resEntry);
						if (meshData!=null){
							System.err.println("Can't create mesh! Mesh Loader can't handle non-cas chunks!");
							return Core.getGame().getEntityHandler().createEntity(layer, pickingColors, meshData, type, entityData, externalGUID, parentEntity, "EntityLayerConverter's getEntity-Method!"); //$NON-NLS-1$
						}
					}
				}
			}
		}
		return null;
	}
	
//	private static void handleInternalGUID(EBXObjInstanceGUID instanceGUID, Entity parentEntity, EBXStructureFile parentFile, boolean loadOriginal){
//		EBXStructureInstance followedInstance = (EBXStructureInstance) instanceGUID.followInternal(parentFile);
//		if (followedInstance!=null){
//			Entity iEntity = getEntity(followedInstance, parentEntity, new EBXExternalGUID(followedInstance.getParentFile().getEBXGUID(), followedInstance.getGuid()), loadOriginal);
//			if (iEntity!=null){
//				parentEntity.getChildrens().add(iEntity);
//			}
//		}
//	}
//	
//	private static void handleExternalGUID(EBXExternalGUID externalGUID, Entity parentEntity, boolean loadOriginal){
//		EBXStructureInstance target = (EBXStructureInstance) externalGUID.follow(true, loadOriginal);
//		if (target!=null){
//			Entity iEntity = getEntity(target.getEntry(), parentEntity, externalGUID, loadOriginal);
//			if (iEntity!=null){
//				parentEntity.getChildrens().add(iEntity);
//			}
//		}
//	}
	private static void calculateNewBoxSize(Entity parentEntity, Entity childEntity){
		if (parentEntity!=null && childEntity !=null){
			
			Vector3f maxCoordsParent = parentEntity.getMaxCoords();
			Vector3f minCoordsParent = parentEntity.getMinCoords();
			
			Vector3f maxRelCoordsChild = Vector3f.add(childEntity.getPosition(), childEntity.getMaxCoords(), null);
			Vector3f minRelCoordsChild = Vector3f.add(childEntity.getPosition(), childEntity.getMinCoords(), null);
					
			if (maxRelCoordsChild.x > maxCoordsParent.x){
				maxCoordsParent.x = maxRelCoordsChild.x;
			}
			if (maxRelCoordsChild.y > maxCoordsParent.y){
				maxCoordsParent.y = maxRelCoordsChild.y;
			}
			if (maxRelCoordsChild.z > maxCoordsParent.z){
				maxCoordsParent.z = maxRelCoordsChild.z;
			}
			
			if (minRelCoordsChild.x < minCoordsParent.x){
				minCoordsParent.x = minRelCoordsChild.x;
			}
			if (minRelCoordsChild.y < minCoordsParent.y){
				minCoordsParent.y = minRelCoordsChild.y;
			}
			if (minRelCoordsChild.z < minCoordsParent.z){
				minCoordsParent.z = minRelCoordsChild.z;
			}
		}
	}
}
