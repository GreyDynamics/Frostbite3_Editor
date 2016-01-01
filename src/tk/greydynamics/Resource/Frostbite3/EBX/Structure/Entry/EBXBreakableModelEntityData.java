package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXBreakableModelEntityData extends EBXStructureEntry{
	
	
		/* Skips:
		 * $
		 * DecalVolumeShader guid
		 * DecalVolumeScaleFactor 2.0
		 * EdgeModelLightMapData*/
	
	private EBXExternalGUID mesh = null;
	private long boneCount;
	
	public EBXBreakableModelEntityData(EBXStructureEntry parent, EBXComplex complex) {
		super(parent, EntryType.BreakableModelEntityData);
	
		for (EBXField field : complex.getFields()) {
			switch (field.getFieldDescritor().getName()) {
			case "BoneCount": /* -------------- BoneCount -------------- */
				this.boneCount = (Long) field.getValue();
				break;
			case "Mesh": /* -------------- Mesh -------------- */
				this.mesh = new EBXExternalGUID(field);
				break;
			default:
				System.err.println(field.getFieldDescritor().getName()+" is not handled in EBXBreakableModelEntityData's Constructor for readIn!");
				break;
			}
		}
		
	}

	public EBXExternalGUID getMesh() {
		return mesh;
	}

	public long getBoneCount() {
		return boneCount;
	}


}
