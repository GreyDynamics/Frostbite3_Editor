package tk.greydynamics.Resource.Frostbite3.EBX;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;


public class EBXField{
	private EBXFieldDescriptor fieldDescritor;
	private int offset;
	private Object value;
	private FieldValueType type;
	
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
	public static EBXField clone(EBXField ebxField) {
		EBXField newEBXField = new EBXField(EBXFieldDescriptor.clone(ebxField.getFieldDescritor()), Integer.valueOf(ebxField.getOffset()));
		Object newValue = null;
		if (ebxField.getValue()!=null){
			switch (ebxField.getType()){
				case ArrayComplex:
					if (ebxField.getValue() instanceof EBXArrayRepeater){
						newValue = EBXArrayRepeater.clone((EBXArrayRepeater) ebxField.getValue());
					}else{
						newValue = EBXComplex.clone(ebxField.getValueAsComplex());
					}
					break;
				case Bool:
					newValue = Boolean.valueOf((boolean) ebxField.getValue());
					break;
				case Byte:
					newValue = Byte.valueOf((byte) ebxField.getValue());
					break;
				case ChunkGuid:
					newValue = String.valueOf(((String) (ebxField.getValue())).toCharArray());
					break;
				case Complex:
					newValue = EBXComplex.clone(ebxField.getValueAsComplex());
					break;
				case Enum:
					if (ebxField.getValue() instanceof EBXComplexDescriptor){
						newValue = EBXComplexDescriptor.clone((EBXComplexDescriptor) ebxField.getValue());
					}else{
						newValue = EBXEnumHelper.clone((EBXEnumHelper) ebxField.getValue());
					}
					break;
				case ExternalGuid:
					newValue = String.valueOf(((String) (ebxField.getValue())).toCharArray());
					break;
				case Float:
					newValue = Float.valueOf((Float) ebxField.getValue());
					break;
				case Guid:
					newValue = String.valueOf(((String) (ebxField.getValue())).toCharArray());
					break;
				case Hex8:
					newValue = String.valueOf(((String) (ebxField.getValue())).toCharArray());
					break;
				case Integer:
					newValue = Integer.valueOf((String) ebxField.getValue());
					break;
				case Short:
					newValue = Short.valueOf((short) ebxField.getValue());
					break;
				case String:
					newValue = String.valueOf(((String) (ebxField.getValue())).toCharArray());
					break;
				case UInteger:
					newValue = Long.valueOf((Long) ebxField.getValue());
					break;
				case Unknown:
					return null;
				default:
					return null;
			}
		}
		newEBXField.setValue(newValue, FieldValueType.valueOf(ebxField.getType().toString()));
		return newEBXField;
	}	
}
