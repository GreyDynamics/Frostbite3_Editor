package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXMeshVariationDatabase extends EBXStructureEntry{
	//skip $
	private EBXObjArray entries = null;
	private EBXObjArray redirectEntries = null;
	
	public EBXMeshVariationDatabase(EBXStructureEntry parent, EBXComplex complex) {
		super(parent, EntryType.MeshVariationDatabase);
		
		for (EBXField field : complex.getFields()) {
			switch (field.getFieldDescritor().getName()) {
			case "Entries": /* -------------- Entries -------------- */
				if (field.getValue() instanceof String){
					this.entries = null;
				}else{
					this.entries = new EBXObjArray(field.getValueAsComplex().getFields(), parent);
				}
				break;
			case "RedirectEntries": /* -------------- RedirectEntries -------------- */
				if (field.getValue() instanceof String){
					this.redirectEntries = null;
				}else{
					this.redirectEntries = new EBXObjArray(field.getValueAsComplex().getFields(), parent);
				}
				break;
			}
		}
	}

	public EBXObjArray getEntries() {
		return entries;
	}

	public EBXObjArray getRedirectEntries() {
		return redirectEntries;
	}
	
}
