//package tk.greydynamics.Entity.Layer;
//
//import org.lwjgl.util.vector.Vector3f;
//
//import tk.greydynamics.Entity.Entity;
//import tk.greydynamics.Entity.Entity.Type;
//import tk.greydynamics.Entity.EntityHandler;
//import tk.greydynamics.Entity.ObjectEntity;
//import tk.greydynamics.Game.Core;
//import tk.greydynamics.Maths.VectorMath;
//import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
//import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
//import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
//import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureInstance;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXBlueprintTransform;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXBreakableModelEntityData;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXDynamicModelEntityData;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjArray;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjArray.ArrayType;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjInstanceGUID;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjectBlueprint;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXReferencedObjectData;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXSpatialPrefabBlueprint;
//import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXStaticModelEntityData;
//import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;
//
//public class EntityLayerConverter_bak {
//	
//	public static final int scaleMultiplier = 4;
//	
//	public static EntityLayer getEntityLayer(EBXFile ebxFile){
//		boolean loadOriginal = false;
//		
//		EntityHandler entityHandler = Core.getGame().getEntityHandler();
//		EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
//		
//		EBXStructureFile structFile = ebxHandler.getStructureFileByGUID(ebxFile.getGuid(), true, loadOriginal/*don't load original*/);
//		if (structFile!=null){
//			String[] strArray = ebxFile.getTruePath().split("/");		
//			//EntityLayer layer = new EntityLayer(strArray[strArray.length-1]+" "+ebxFile.getGuid());
//			EntityLayer layer = new EntityLayer(ebxFile.getTruePath()+" "+ebxFile.getGuid());
//			
//			for (EBXStructureInstance instance : structFile.getInstances()){
//				Entity en = getEntity(instance.getEntry(), null, new EBXExternalGUID(instance.getParentFile().getEBXGUID(), instance.getGuid()), loadOriginal);	
//				if (en!=null){
//					layer.getEntities().add(en);
//				}
//			}
//			return layer;
//		}
//		System.err.println("Can not create EntityLayer in EntityLayerConverter from EBXFile!");
//		return null;
//	}
//	
//	private static Entity getEntity(EBXStructureEntry entry, Entity parentEntity, EBXExternalGUID meshInstanceGUID, boolean loadOriginal){
//		if (entry==null){return null;}
//		
//		EBXStructureInstance parentInstance = (EBXStructureInstance) entry.getParent();
//		EBXStructureFile parentFile = null;
//		if (entry.getType()==EntryType.EBXInstance){
//			EBXStructureInstance thisInstance = (EBXStructureInstance) entry;
//			parentFile = thisInstance.getParentFile();
//		}else{
//			if (parentInstance!=null){
//				parentFile = parentInstance.getParentFile();
//			}else{
//				System.err.println("Parent not found in EntityLayerConverter!");
//			}
//		}
//	
//		Entity en = new ObjectEntity(meshInstanceGUID.getBothGUIDs(), entry, parentEntity, null, null, null);
//		
//		switch(entry.getType()){
//			case EBXInstance:
//				EBXStructureInstance instance = (EBXStructureInstance) entry;
//				EBXStructureEntry instanceEntry = instance.getEntry();
//				Entity instanceEntity = getEntity(instanceEntry, en, new EBXExternalGUID(instance.getParentFile().getEBXGUID(), instance.getGuid()), loadOriginal);
//				if (instanceEntity!=null){
//					//en.getChildrens().add(instanceEntity);
//					en = instanceEntity;
//				}
//				break;
//			case ReferenceObjectData:
//				EBXReferencedObjectData refObjData = (EBXReferencedObjectData) entry;
//				EBXBlueprintTransform transform = refObjData.getBlueprintTransform();
//				
//				/*Vector3f scaling = transform.getScaling();
//				if (scaling!=null){
//					en.setScaling(scaling);
//				}
//				*/
//				
//				Vector3f rotation = transform.getRotation();
//				if (rotation!=null){
//					en.setRotation(rotation);
//				}
//				
//				Vector3f position = transform.getTranformation();
//				if (position!=null){
//					position = VectorMath.multiply(position, new Vector3f(scaleMultiplier, scaleMultiplier, scaleMultiplier), null);
//					en.setPosition(position);
//				}
//				
//				handleExternalGUID(refObjData.getBlueprint(), en, loadOriginal);
//				break;
//			case SpatialPrefabBlueprint:
//				EBXSpatialPrefabBlueprint spaPreBlueprint = (EBXSpatialPrefabBlueprint) entry;
//				
//				EBXObjArray objects = spaPreBlueprint.getObjectArray();
//				if (objects.getType()==ArrayType.InstanceGUID){
//					for (Object obj : objects.getObjects()){
//						handleInternalGUID((EBXObjInstanceGUID) obj, en, parentFile, loadOriginal);
//					}
//				}else{
//					//Other ArrayTypes
//				}
//				break;
//			case ObjectBlueprint:
//				EBXObjectBlueprint objBlueprint = (EBXObjectBlueprint) entry;
//				handleInternalGUID((EBXObjInstanceGUID) objBlueprint.getObject(), en, parentFile, loadOriginal);
//				break;
//			case StaticModelEntityData:
//				EBXStaticModelEntityData smed = (EBXStaticModelEntityData) entry;
//				
//				en = getEntity(smed.getMesh(), parentEntity, Type.Object, entry);
//				break;
//			case DynamicModelEntityData:
//				EBXDynamicModelEntityData dmed = (EBXDynamicModelEntityData) entry;
//				
//				en = getEntity(dmed.getMesh(), parentEntity, Type.Object, entry);
//				break;
//			case BreakableModelEntityData:
//				EBXBreakableModelEntityData bmed = (EBXBreakableModelEntityData) entry;
//				
//				en = getEntity(bmed.getMesh(), parentEntity, Type.Object, entry);
//				break;
//		}
//		if (en!=null){
//			calculateNewBoxSize(parentEntity, en);
//			return en;
//		}
//		return null;
//	}
//	
//	private static Entity getEntity(EBXExternalGUID externalGUID, Entity parentEntity, Type type, EBXStructureEntry structEntry){
//		if (externalGUID==null){return null;};
//		ResourceLink ebxLink = Core.getGame().getResourceHandler().getResourceLinkByEBXGUID(externalGUID.getFileGUID());
//		if (ebxLink!=null){
//			String resLinkname = ebxLink.getName();
//			CasBundle casBundle = (CasBundle) Core.getGame().getCurrentBundle();
//			for (ResourceLink resLink : casBundle.getRes()){
//				if (resLink.getName().equalsIgnoreCase(resLinkname)){
//					byte[] meshData = Core.getGame().getResourceHandler().readResourceLink(resLink);
//					if (meshData!=null){
//						return Core.getGame().getEntityHandler().createEntity(meshData, type, structEntry, externalGUID, parentEntity, "EntityLayerConverter's getEntity-Method!");
//					}
//				}
//			}
//		}
//		return null;
//	}
//	
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
//	private static void calculateNewBoxSize(Entity parentEntity, Entity childEntity){
//		if (parentEntity!=null && childEntity !=null){
//			
//			Vector3f maxCoordsParent = parentEntity.getMaxCoords();
//			Vector3f minCoordsParent = parentEntity.getMinCoords();
//			
//			Vector3f maxRelCoordsChild = Vector3f.add(childEntity.getPosition(), childEntity.getMaxCoords(), null);
//			Vector3f minRelCoordsChild = Vector3f.add(childEntity.getPosition(), childEntity.getMinCoords(), null);
//					
//			if (maxRelCoordsChild.x > maxCoordsParent.x){
//				maxCoordsParent.x = maxRelCoordsChild.x;
//			}
//			if (maxRelCoordsChild.y > maxCoordsParent.y){
//				maxCoordsParent.y = maxRelCoordsChild.y;
//			}
//			if (maxRelCoordsChild.z > maxCoordsParent.z){
//				maxCoordsParent.z = maxRelCoordsChild.z;
//			}
//			
//			if (minRelCoordsChild.x < minCoordsParent.x){
//				minCoordsParent.x = minRelCoordsChild.x;
//			}
//			if (minRelCoordsChild.y < minCoordsParent.y){
//				minCoordsParent.y = minRelCoordsChild.y;
//			}
//			if (minRelCoordsChild.z < minCoordsParent.z){
//				minCoordsParent.z = minRelCoordsChild.z;
//			}
//		}
//	}
//}
