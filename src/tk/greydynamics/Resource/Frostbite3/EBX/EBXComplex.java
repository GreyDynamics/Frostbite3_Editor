package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXComplex{
	EBXComplexDescriptor complexDescriptor;
	EBXField[] fields;
	int offset;

	public EBXComplex(EBXComplexDescriptor complexDescriptor) {
		this.complexDescriptor = complexDescriptor;
	}

	public EBXComplexDescriptor getComplexDescriptor() {
		return complexDescriptor;
	}

	public void setComplexDescriptor(EBXComplexDescriptor complexDescriptor) {
		this.complexDescriptor = complexDescriptor;
	}

	public EBXField[] getFields() {
		return fields;
	}

	public void setFields(EBXField[] fields) {
		this.fields = fields;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public EBXField getField(int i){
		return fields[i];
	}
	
	public EBXComplex getFieldAsComplex(Integer i){
		return fields[i].getValueAsComplex();
	}
	
}
