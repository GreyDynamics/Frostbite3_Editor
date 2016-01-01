package tk.greydynamics.Resource.Frostbite3.EBX.Modify;

import tk.greydynamics.JavaFX.Windows.MainWindow.EntryType;

public class ChangeEntry {
	private Object value; 
	private int offset;
	private EntryType type;
	
	public ChangeEntry(Object value, EntryType type, int offset) {
		this.value = value;
		this.offset = offset;
		this.type = type;
	}
	
	public ChangeEntry(){
		this.value = null;
		this.type = null;
		this.offset = -1;
	}



	public EntryType getType() {
		return type;
	}

	public void setType(EntryType type) {
		this.type = type;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getOffset() {
		return offset;
	}

	
	
}
