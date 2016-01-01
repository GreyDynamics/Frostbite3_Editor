package tk.greydynamics.Resource.Frostbite3.EBX.Structure;

import java.util.ArrayList;

import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXStructureFile {
	private ArrayList<EBXStructureInstance> instances;
	private String structureName;
	private String ebxGUID;
	
	public EBXStructureFile(String structureName, String ebxGUID){
		this.instances = new ArrayList<>();
		this.structureName = structureName;
		this.ebxGUID = ebxGUID;
	}
	public EBXStructureFile(String structureName, String ebxGUID, ArrayList<EBXStructureInstance> instances){
		this.structureName = structureName;
		this.instances = instances;
		this.ebxGUID = ebxGUID;
	}
	public String getStructureName() {
		return structureName;
	}
	public void setStructureName(String structureName) {
		this.structureName = structureName;
	}
	
	public ArrayList<EBXStructureInstance> getInstances() {
		return instances;
	}
	public String getEBXGUID() {
		return ebxGUID;
	}
	
	public EBXStructureInstance getInstanceByGUID(String guid){
		for (EBXStructureInstance instance : instances){
			if (instance.getGuid().equalsIgnoreCase(guid)){
				return instance;
			}
		}
		System.err.println("Instance "+guid+" does not exist in "+structureName+"!");
		return null;
	}
	
	public EBXStructureInstance getFirstInstance(EntryType instanceEntryType){
		for (EBXStructureInstance instance : instances){
			if (instance.getEntry().getType()==instanceEntryType){
				return instance;
			}
		}
		return null;
	}
		
}
