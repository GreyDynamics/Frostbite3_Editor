package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader;

public class EBXObjArray {
	private Object[] objects = null;
	private ArrayType type = null;
	private EBXStructureEntry parent;
	
	public static enum ArrayType{
		InstanceGUID, ExternalGUID, Complex//Complex, Objects, blah blah
	};
	
	public EBXObjArray(Object[] objects, EBXStructureEntry parent){
		this.objects = objects;
		this.parent = parent;		
	}
	
	public EBXObjArray(EBXField[] fields, EBXStructureEntry parent){
		this.parent = parent;
		if (fields.length>0){
			switch (fields[0].getType()){
				case ExternalGuid:
					this.type = ArrayType.ExternalGUID;
					this.objects = new Object[fields.length];
					for (int i=0; i<fields.length; i++){
						objects[i] = new EBXExternalGUID(fields[i]);
					}
					break;
				case Guid:
					this.type = ArrayType.InstanceGUID;
					this.objects = new Object[fields.length];
					for (int i=0; i<fields.length; i++){
						objects[i] = new EBXObjInstanceGUID((String) fields[i].getValue());
					}
					break;
				/*case Hex8:
					this.type = ArrayType.InstanceGUID;
					this.objects = new Object[fields.length];
					for (int i=0; i<fields.length; i++){
						objects[i] = new EBXObjInstanceGUID((String) fields[i].getValue());
					}
					break;*/
				case Complex:
					this.type = ArrayType.Complex;
					this.objects = new Object[fields.length];
					for (int i=0; i<fields.length; i++){
						objects[i] = EBXStructureReader.readEntry(parent, (EBXComplex) fields[i].getValue());
					}
					break;
				default:
					System.err.println("EBXObjArray has unhandled types in his constructor! "+fields[0].getType().toString());
					break;
			}
		}
	}

	public Object[] getObjects() {
		return objects;
	}

	public ArrayType getType() {
		return type;
	}

	public EBXStructureEntry getParent() {
		return parent;
	}
	
	
	
	
	
}
