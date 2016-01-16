package tk.greydynamics.Resource.Frostbite3.EBX;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;


public class EBXField{
	EBXFieldDescriptor fieldDescritor;
	int offset;
	Object value;
	FieldValueType type;
	
	public int indexDEBUG = 0;
	
	public EBXField(EBXFieldDescriptor fieldDescritor, int offset) {
		this.fieldDescritor = fieldDescritor;
		this.offset = offset;
	}
	public EBXFieldDescriptor getFieldDescritor() {
		return fieldDescritor;
	}
	public void setFieldDescritor(EBXFieldDescriptor fieldDescritor) {
		this.fieldDescritor = fieldDescritor;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public Object getValue() {
		return value;
	}
	public FieldValueType getType() {
		return type;
	}
	
	
	public void setType(FieldValueType type) {
		this.type = type;
	}
	public void setValue(Object value, FieldValueType type) {
		this.value = value;
		this.type = type;
	}
	
	public EBXComplex getValueAsComplex(){
		return (EBXComplex) value;
	}	
}
