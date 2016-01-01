package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXFieldDescriptor{
	String name;
	short type;
	short ref;
	int offset;
	int secondaryOffset;
	public EBXFieldDescriptor(String name, short type, short ref, int offset,
			int secondaryOffset) {
		this.name = name;
		this.type = type;
		this.ref = ref;
		this.offset = offset;
		this.secondaryOffset = secondaryOffset;
		if (this.name.equals("$")){
			this.offset -= 0x08;
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public int getRef() {
		return ref;
	}
	public void setRef(short ref) {
		this.ref = ref;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getSecondaryOffset() {
		return secondaryOffset;
	}
	public void setSecondaryOffset(int secondaryOffset) {
		this.secondaryOffset = secondaryOffset;
	}
}
