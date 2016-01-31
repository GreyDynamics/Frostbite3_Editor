package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXComplex{
	private EBXComplexDescriptor complexDescriptor;
	private EBXField[] fields;
	private int offset;
	private boolean emtyPayload = false;

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

	public boolean isEmtyPayload() {
		return emtyPayload;
	}

	public void setEmtyPayload(boolean emtyPayload) {
		this.emtyPayload = emtyPayload;
	}
	public void extendFields(EBXField newField){
		EBXField[] newFieldArray = new EBXField[this.fields.length+1];
		for (int i=0;i<newFieldArray.length-1;i++){
			newFieldArray[i] = this.fields[i];
		}
		newFieldArray[newFieldArray.length-1] = newField;
		this.fields = newFieldArray;
	}

	public static EBXComplex clone(EBXComplex complex) {
		EBXComplex newComplex = new EBXComplex(EBXComplexDescriptor.clone(complex.getComplexDescriptor()));
		EBXField[] newEBXFields = new EBXField[complex.getFields().length];
		for (int i=0; i<newEBXFields.length;i++){
			EBXField newEBXField = EBXField.clone(complex.getFields()[i]);
			newEBXFields[i] = newEBXField;
		}
		newComplex.setFields(newEBXFields);
		newComplex.setOffset(Integer.valueOf(complex.getOffset()));
		newComplex.setEmtyPayload(Boolean.valueOf(complex.isEmtyPayload()));
		
		return newComplex;
	}


	
	
}
