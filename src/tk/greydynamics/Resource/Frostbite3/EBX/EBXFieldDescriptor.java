package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXFieldDescriptor{
	String name;
	short type;
	short ref;
	int offset;
	int secondaryOffset;
	
	//additional
	int size;
	int nameHash;
	public EBXFieldDescriptor(String name, short type, short ref, int offset,
			int secondaryOffset) {
		this.name = name;
		this.type = type;
		this.ref = ref;
		this.offset = offset;
		this.secondaryOffset = secondaryOffset;
//		if (this.name.equals("$")){
//		THIS IS NOW HANDLED IN READFIELD!!!
//			this.offset -= 0x08;
//		}
		this.size = 0;
		this.nameHash = -1;
	}
	
	/**
	 * Size is just to keep track in the creator.
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Size is just to keep track in the creator.
	 */
	public void setSize(int size) {
		this.size = size;
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
	
	public int getNameHash() {
		return nameHash;
	}
	public void setNameHash(int nameHash) {
		this.nameHash = nameHash;
	}

	public static EBXFieldDescriptor clone(EBXFieldDescriptor fieldDescritor) {
		EBXFieldDescriptor newFieldDescriptor = new EBXFieldDescriptor(
				fieldDescritor.getName(),
				Short.valueOf((short) fieldDescritor.getType()),
				Short.valueOf((short) fieldDescritor.getRef()),
				Integer.valueOf(fieldDescritor.getOffset()),
				Integer.valueOf(fieldDescritor.getSecondaryOffset())
			);
		newFieldDescriptor.setSize(Integer.valueOf(fieldDescritor.getSize()));
		newFieldDescriptor.setNameHash(Integer.valueOf(fieldDescritor.getNameHash()));
		return newFieldDescriptor;
	}
}
