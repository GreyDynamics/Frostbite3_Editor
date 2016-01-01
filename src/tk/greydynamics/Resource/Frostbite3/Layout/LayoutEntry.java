package tk.greydynamics.Resource.Frostbite3.Layout;

import java.util.ArrayList;

import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutEntryType;

public class LayoutEntry {
	public LayoutEntryType type;
	public ArrayList<LayoutField> fields;
	
	public LayoutEntry(LayoutEntryType type) {
		this.type = type;
		this.fields = new ArrayList<LayoutField>();
	}

	public LayoutEntryType getType() {
		return type;
	}

	public void setType(LayoutEntryType type) {
		this.type = type;
	}

	public ArrayList<LayoutField> getFields() {
		return fields;
	}
}
