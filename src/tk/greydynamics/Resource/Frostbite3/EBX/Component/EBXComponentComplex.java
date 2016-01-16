package tk.greydynamics.Resource.Frostbite3.EBX.Component;

import java.util.ArrayList;

public class EBXComponentComplex {
	private String name;
	private int totalSize;
	private int alignment;	
	private boolean occurredAsInstance;
	private ArrayList<EBXComponentEntry> entries;
	private boolean isNew;
	
	public EBXComponentComplex(boolean occurredAsInstance, String name, int totalSize, int alignment, boolean isNew, ArrayList<EBXComponentEntry> entries) {
		if (!this.occurredAsInstance){
			this.occurredAsInstance = occurredAsInstance;
		}
		this.name = name;
		this.totalSize = totalSize;
		this.alignment = alignment;
		if (entries!=null){
			this.entries = entries;
		}else{
			this.entries = new ArrayList<EBXComponentEntry>();
		}
		this.isNew = isNew;
	}
	
	
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public ArrayList<EBXComponentEntry> getEntries() {
		return entries;
	}
	public void setEntries(ArrayList<EBXComponentEntry> entries) {
		this.entries = entries;
	}
	public boolean isOccurredAsInstance() {
		return occurredAsInstance;
	}
	public void setOccurredAsInstance(boolean occurredAsInstance) {
		this.occurredAsInstance = occurredAsInstance;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
