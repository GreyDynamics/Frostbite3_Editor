package tk.greydynamics.Entity.Layer;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Messages;
import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.EntityHandler;
import tk.greydynamics.Entity.ObjectEntity;
import tk.greydynamics.Game.Core;
import tk.greydynamics.Maths.VectorMath;
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
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class EntityLayerConverter {
	
	public static final int scaleMultiplier = 4;
	
	public static EntityLayer getEntityLayer(EBXFile ebxFile){
		boolean loadOriginal = false;
		
		EntityHandler entityHandler = Core.getGame().getEntityHandler();
		EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
		
		if (ebxFile!=null){
			String[] strArray = ebxFile.getTruePath().split("/");		 
			//EntityLayer layer = new EntityLayer(strArray[strArray.length-1]+" "+ebxFile.getGuid());
			EntityLayer layer = new EntityLayer(ebxFile.getTruePath()+" "+ebxFile.getGuid()); 
			
			for (EBXInstance instance : ebxFile.getInstances()){
				Entity en = getEntity(true, null, instance, null, new EBXExternalGUID(ebxFile.getGuid(), instance.getGuid()), loadOriginal);	
				if (en!=null){
					layer.getEntities().add(en);
				}
				break;
			}
			return layer;
		}
		System.err.println(Messages.getString("EntityLayerConverter.2")); //$NON-NLS-1$
		return null;
	}
	private static Entity getEntity(boolean isMaster, Vector3f pickingColors, EBXFile file, Entity parentEntity, boolean loadOriginal){
		//EBXFile
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (file==null){return null;}
		Entity en = new ObjectEntity(file.getTruePath(), file, parentEntity, null, null, pickingColors);
		for (EBXInstance instance : file.getInstances()){
			Entity child = getEntity(isMaster, pickingColors, instance, en, new EBXExternalGUID(file.getGuid(), instance.getGuid()), loadOriginal);
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
	private static Entity getEntity(boolean isMaster, Vector3f pickingColors, EBXInstance instance, Entity parentEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		//INSTANCE
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (instance==null){return null;}
		Entity en = new ObjectEntity(instance.getGuid(), instance, parentEntity, null, null, pickingColors);
		Entity child = getEntity(isMaster, pickingColors, instance.getComplex(), en, en, parentInstance, loadOriginal);
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
	private static Entity getEntity(boolean isMaster, Vector3f pickingColors, EBXComplex complex, Entity parentEntity, Entity instanceEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		//COMPLEX
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (complex==null){return null;}
		Entity en = new ObjectEntity(complex.getComplexDescriptor().getName(), complex, parentEntity, null, null, pickingColors);
//		int test = 0;
		for (EBXField field : complex.getFields()){
			Entity child = getEntity(isMaster, pickingColors, field, en, instanceEntity, parentInstance, loadOriginal);
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
	private static Entity getEntity(boolean isMaster, Vector3f pickingColors, EBXField field, Entity parentEntity, Entity instanceEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		//FIELD
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		if (field==null){return null;}
		Entity en = new ObjectEntity(field.getFieldDescritor().getName(), field, parentEntity, null, null, pickingColors);
		if (field.getValue() instanceof EBXComplex){
			if (field.getValueAsComplex().getComplexDescriptor().getName().equals("LinearTransform")){ 
				EBXLinearTransform linearTransform = new EBXLinearTransform(field.getValueAsComplex());
				instanceEntity.setPosition(VectorMath.multiply(linearTransform.getTranformation(), new Vector3f(scaleMultiplier, scaleMultiplier, scaleMultiplier), null));
				instanceEntity.setRotation(linearTransform.getRotation());
				//instanceEntity.setScaling(linearTransform.getScaling());
				//TODO scaling!
			}else if (field.getFieldDescritor().getName().equals("Materials")){ 
				System.out.println("MATERIAL!"); 
			}else{
				Entity child = getEntity(isMaster, pickingColors, field.getValueAsComplex(), en, instanceEntity, parentInstance, loadOriginal);
				if (child!=null){
					en.getChildrens().add(child);
				}
			}
		}else if (field.getValue() instanceof EBXField){
			Entity child = getEntity(isMaster, pickingColors, (EBXField) field.getValue(), en, instanceEntity, parentInstance, loadOriginal);
			if (child!=null){
				en.getChildrens().add(child);
			}
		}else if (field.getValue() instanceof EBXExternalGUID){
			Entity child = getEntity(false, pickingColors, (EBXExternalGUID) field.getValue(), en, parentInstance, loadOriginal);
			if (child!=null){
				en.getChildrens().add(child);
			}
		}else if (field.getType()==FieldValueType.Guid){
//			System.err.println("DEBUG: "+field.getFieldDescritor().getName()+" "+field.getType());
			if (field.getFieldDescritor().getName().equals("member")||field.getFieldDescritor().getName().equalsIgnoreCase("Object")){  
				Entity child = getEntity(false, pickingColors, new EBXExternalGUID(parentInstance.getFileGUID(), (String) field.getValue()), en, parentInstance, loadOriginal);
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
	private static Entity getEntity(boolean isMaster, Vector3f pickingColors, EBXExternalGUID externalGUID, Entity parentEntity, EBXExternalGUID parentInstance, boolean loadOriginal){
		if (isMaster){
			pickingColors = Entity.randomizedPickerColors();
		}
		Entity en = new ObjectEntity(externalGUID.getBothGUIDs(), externalGUID, parentEntity, null, null, pickingColors);
		Entity meshEntity = getEntity(false, pickingColors, externalGUID, en, Type.Object, externalGUID);
		if (meshEntity!=null){
			en.getChildrens().add(meshEntity);
		}
		EBXFile targetFile = Core.getGame().getResourceHandler().getEBXHandler().getEBXFileByGUID(externalGUID.getFileGUID(), true, loadOriginal);
		if (targetFile!=null){
			boolean found = false;
			for (EBXInstance instance : targetFile.getInstances()){
				if (instance.getGuid().equalsIgnoreCase(externalGUID.getInstanceGUID())){
					Entity child = getEntity(false, pickingColors, instance, en, externalGUID, loadOriginal);
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
	
	private static Entity getEntity(boolean isMaster, Vector3f pickingColors, EBXExternalGUID externalGUID, Entity parentEntity, Type type, Object entityData){
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
								return Core.getGame().getEntityHandler().createEntity(pickingColors, meshData, type, entityData, externalGUID, parentEntity, Messages.getString("EntityLayerConverter.8")); //$NON-NLS-1$
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
							System.err.println(Messages.getString("EntityLayerConverter.9")); //$NON-NLS-1$
							return Core.getGame().getEntityHandler().createEntity(pickingColors, meshData, type, entityData, externalGUID, parentEntity, Messages.getString("EntityLayerConverter.10")); //$NON-NLS-1$
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
