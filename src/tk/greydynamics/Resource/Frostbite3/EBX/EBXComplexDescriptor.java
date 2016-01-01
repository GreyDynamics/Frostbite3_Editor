package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXComplexDescriptor{
	String name;
	int fieldStartIndex;
	char numField;
	char alignment;
	short type;
	short size;
	short secondarySize;
	public EBXComplexDescriptor(String name){
		this.name = name;
		this.size = 0;
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
}