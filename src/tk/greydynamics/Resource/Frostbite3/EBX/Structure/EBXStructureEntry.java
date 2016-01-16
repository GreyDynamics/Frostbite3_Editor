package tk.greydynamics.Resource.Frostbite3.EBX.Structure;

import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public abstract class EBXStructureEntry {
	private EBXStructureEntry parent;
	private EntryType type;
	
	public EBXStructureEntry(EBXStructureEntry parent, EntryType type) {
		this.parent = parent;
		this.type = type;
	}

	public EntryType getType() {
		return type;
	}

	public void setType(EntryType type) {
		this.type = type;
	}
	


	public EBXStructureEntry getParent() {
		return parent;
	}
	
	
	
	
}
