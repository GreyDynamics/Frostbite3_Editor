package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXDynamicModelEntityData extends EBXStructureEntry{

	//$
	private EBXExternalGUID mesh = null;
	//DestructiblePartCount
	private boolean noCollision = false;
	
	
	public EBXDynamicModelEntityData(EBXStructureEntry parent, EBXComplex complex) {
		super(parent, EntryType.DynamicModelEntityData);
		
		for (EBXField field : complex.getFields()) {
			switch (field.getFieldDescritor().getName()) {
			case "Mesh": /* -------------- Mesh -------------- */
				this.mesh = new EBXExternalGUID(field);
				break;
			case "NoCollision": /* -------------- NoCollision -------------- */
				this.noCollision = (boolean) field.getValue();
				break;
			}
		}
	}


	public EBXExternalGUID getMesh() {
		return mesh;
	}




	public boolean hasNoCollision() {
		return noCollision;
	}


	public void setNoCollision(boolean noCollision) {
		this.noCollision = noCollision;
	}
	
	
	
	
	

}
