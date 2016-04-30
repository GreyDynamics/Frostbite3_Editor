package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXComplexDescriptor{
	String name;
	int fieldStartIndex;
	char numField;
	char alignment;
	short type;
	short size;
	short secondarySize;
	
	//additinal
	int nameHash;
	public EBXComplexDescriptor(String name){
		this.name = name;
		this.size = 0;
		this.nameHash = -1;
	}
	
	public EBXComplexDescriptor(String name, int fieldStartIndex, char numField,
			char alignment, short type, short size, short secondarySize) {
		this.name = name;
		this.fieldStartIndex = fieldStartIndex;
		this.numField = numField;
		this.alignment = alignment;
		this.type = type;
		this.size = size;
		this.secondarySize = secondarySize;
		
		this.nameHash = -1;
	}
	
	public int getNameHash() {
		return nameHash;
	}

	public void setNameHash(int nameHash) {
		this.nameHash = nameHash;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFieldStartIndex() {
		return fieldStartIndex;
	}
	public void setFieldStartIndex(int fieldStartIndex) {
		this.fieldStartIndex = fieldStartIndex;
	}
	public int getNumField() {
		return numField;
	}
	public void setNumField(char numField) {
		this.numField = numField;
	}
	public int getAlignment() {
		return alignment;
	}
	public void setAlignment(char alignment) {
		this.alignment = alignment;
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public short getSize() {
		return size;
	}
	public void setSize(short size) {
		this.size = size;
	}
	public short getSecondarySize() {
		return secondarySize;
	}
	public void setSecondarySize(short secondarySize) {
		this.secondarySize = secondarySize;
	}

	public static EBXComplexDescriptor clone(EBXComplexDescriptor complexDescriptor) {
		EBXComplexDescriptor newComplex = new EBXComplexDescriptor(complexDescriptor.getName(),
				Integer.valueOf(complexDescriptor.getFieldStartIndex()), 
				Character.valueOf((char) complexDescriptor.getNumField()),
				Character.valueOf((char) complexDescriptor.getAlignment()),
				Short.valueOf(complexDescriptor.getType()),
				Short.valueOf(complexDescriptor.getSize()),
				Short.valueOf(complexDescriptor.getSecondarySize())
			);;
		newComplex.setNameHash(Integer.valueOf(complexDescriptor.getNameHash()));
		return newComplex;
	}
}