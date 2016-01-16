package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXSpatialPrefabBlueprint extends EBXStructureEntry {
	//Skip Events
	//Skip Name
	private EBXObjArray objects = null;//Instance Reference!
	
	public EBXSpatialPrefabBlueprint(EBXStructureEntry parent, EBXComplex complex) {
		super(parent, EntryType.SpatialPrefabBlueprint);
		
		for (EBXField field : complex.getFieldAsComplex(0).getFields()) {//Everything is inside a PrefabBlueprint
			switch (field.getFieldDescritor().getName()) {
				case "Objects": /* -------------- Objects Array -------------- */
					this.objects = new EBXObjArray(field.getValueAsComplex().getFields(), parent);
					break;
				case "$":
					System.err.println("EBXSpatialPrefabBlueprint's construcotor found an unhandled type while read in '$'");
					break;
				default:
					System.err.println(field.getFieldDescritor().getName()+" is not handled in EBXSpatialPrefabBlueprint's Constructor for readIn!");
					break;
			}
		}
	}

	public EBXObjArray getObjectArray() {
		return objects;
	}
}


