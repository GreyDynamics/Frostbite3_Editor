package tk.greydynamics.Resource.Frostbite3.Layout;

import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutFieldType;


public class LayoutField {
	public Object obj;
	public LayoutFieldType type;
	public String name;
	
	public LayoutField(Object obj, LayoutFieldType type, String name) {
		this.obj = obj;
		this.type = type;
		this.name = name;
	}
	
	public LayoutField(){
		//NULL CONSTRUCTOR
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public LayoutFieldType getType() {
		return type;
	}

	public void setType(LayoutFieldType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
