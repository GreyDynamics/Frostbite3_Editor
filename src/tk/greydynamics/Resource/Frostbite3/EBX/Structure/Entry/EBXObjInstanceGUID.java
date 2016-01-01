package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;

public class EBXObjInstanceGUID {
	private String guid;
	
	
	public EBXObjInstanceGUID(String internalGuid){
		this.guid = internalGuid;
	}
	
	public EBXStructureEntry followInternal(EBXStructureFile ebxStructureFile){
		return ebxStructureFile.getInstanceByGUID(guid);		
	}	
	
	public String getGUID(){
		return guid;
	}
}
