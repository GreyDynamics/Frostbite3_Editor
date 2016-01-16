package tk.greydynamics.Resource.Frostbite3.EBX.Component;

public class EBXComponentEntry {
	private String name;
	private String type;
	private int totalSize;
	private int alignment;
	private Object value;
	
	public EBXComponentEntry(String name, String type, int totalSize, int alignment, Object value) {
		this.name = name;
		this.type = type;
		this.totalSize = totalSize;
		this.alignment = alignment;
		this.value = null;
	}
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getAlignment() {
		return alignment;
	}
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	
	
}
