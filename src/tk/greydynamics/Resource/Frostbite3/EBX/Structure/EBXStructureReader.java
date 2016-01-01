package tk.greydynamics.Resource.Frostbite3.EBX.Structure;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXBreakableModelEntityData;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXDynamicModelEntityData;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabase;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseMaterial;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXMeshVariationDatabaseRedirectEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjectBlueprint;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXReferencedObjectData;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXSpatialPrefabBlueprint;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXStaticModelEntityData;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXTextureShaderParameter;

public class EBXStructureReader {
	
	public static enum EntryType {
		EBXInstance,
		
		ReferenceObjectData, WorldPartReferenceObjectData, SubWorldInclusionSetting,
		InterfaceDescriptorData, SubWorldData, LightProbeVolumeData, PointLightEntityData,
		CameraEntityData, EffectReferenceObjectData, GroundHeightEntityData, TerrainPhysicsComponentData,
		TerrainEntityData, LocatorEntityData, MapMarkerEntityData, VehicleSpawnReferenceObjectData,
		OBBData, VolumeVectorShapeData, BoolEntityData, AndEntityData, LogicReferenceObjectData, DelayEntityData,
		RandomMultiEventEntityData, LogicVisualEnvironmentEntityData, PlatformSplitterEntityData,
		OrEntityData, TransformEntityData, SyncedBoolEntityData, FadeEntityData,
		UINPXTooltipEntityData, SequenceEntityData, CompareBoolEntityData, TransformPartPropertyTrackData,
		UINPXTooltipLine, UINPXTextLine, UINPXPaddingLine, WorldPartData, SpatialPrefabBlueprint, ObjectBlueprint,
		VegetationTreeEntityData, StaticModelEntityData, DynamicModelEntityData, BreakableModelEntityData,
		TextureShaderParameter, TextureParameters, MeshVariationDatabaseMaterial, MeshVariationDatabaseEntry,
		MeshVariationDatabase, MeshVariationDatabaseRedirectEntry
	}
	
	public static EntryType getEntryTypeByName(String name) {
		try{
			return EntryType.valueOf(name);
		}catch (Exception e){
			return null;
		}
	}
		
	public static EBXStructureFile readStructure(EBXFile ebxFile){
		EBXStructureFile structFile = new EBXStructureFile(ebxFile.getTruePath(), ebxFile.getGuid());
		for (EBXInstance instance : ebxFile.getInstances()){
			EBXStructureInstance strInstance = readInstance(structFile, instance);
			if (strInstance!=null){
				structFile.getInstances().add(strInstance);
			}else{
				//return null;
			}
		}		
		return structFile;
	}
	
	private static EBXStructureInstance readInstance(EBXStructureFile parent, EBXInstance ebxInstance){
		EBXStructureInstance instance = new EBXStructureInstance(parent, ebxInstance.getGuid(), null) {};
		EBXStructureEntry entry = readEntry(instance, ebxInstance.getComplex());
		if (entry!=null){
			instance.setEntry(entry);
			return instance;
		}else{
			return null;
		}
	}
	
	public static EBXStructureEntry readEntry(EBXStructureEntry parent, EBXComplex ebxComplex){
		try{
			EBXStructureEntry entry = null;
			String name = ebxComplex.getComplexDescriptor().getName();
			EntryType type = getEntryTypeByName(name);
			if (type!=null){
				switch (type) {
					case ReferenceObjectData:
						entry = new EBXReferencedObjectData(parent, ebxComplex);
						break;
					case SpatialPrefabBlueprint:
						entry = new EBXSpatialPrefabBlueprint(parent, ebxComplex);
						break;
					case ObjectBlueprint:
						entry = new EBXObjectBlueprint(parent, ebxComplex);
						break;
					case StaticModelEntityData:
						entry = new EBXStaticModelEntityData(parent, ebxComplex);
						break;
					case DynamicModelEntityData:
						entry = new EBXDynamicModelEntityData(parent, ebxComplex);
						break;
					case BreakableModelEntityData:
						entry = new EBXBreakableModelEntityData(parent, ebxComplex);
						break;
					case TextureShaderParameter:
						if (ebxComplex.getFields().length>0){
							entry = new EBXTextureShaderParameter(parent, ebxComplex);
						}else{
							entry = null;
						}
						break;
					case MeshVariationDatabaseMaterial:
						entry = new EBXMeshVariationDatabaseMaterial(parent, ebxComplex);
						break;
					case MeshVariationDatabaseEntry:
						entry = new EBXMeshVariationDatabaseEntry(parent, ebxComplex);
						break;
					case MeshVariationDatabaseRedirectEntry:
						entry = new EBXMeshVariationDatabaseRedirectEntry(parent, ebxComplex);
						break;
					case MeshVariationDatabase:
						entry = new EBXMeshVariationDatabase(parent, ebxComplex);
				}
			}else{
				System.err.println("EBXStructureReader is INCLOMPLETE: "+name);
				return null;
			}
			if (entry==null){
				System.err.println("Unhandled Type in EBXStructureEntry-Reader: "+name);
				return null;
			}
			return entry;
		}catch (Exception e){
			System.err.println("EBXStructureEntry could not get read in!");
			e.printStackTrace();
			return null;
		}
	}	
}
