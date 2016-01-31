package tk.greydynamics.Resource.Frostbite3.EBX;

import java.nio.ByteOrder;
import java.util.ArrayList;

public class EBXFile {
	private String truePath;
	private String guid;
	private ArrayList<EBXInstance> instances;
	private ByteOrder order;
	
	private ArrayList<EBXExternalGUID> externalGUIDs;
	
	public EBXFile(String truePath, ArrayList<EBXInstance> instances, String guid, ByteOrder order, ArrayList<EBXExternalGUID> externalGUIDs) {
		this.truePath = truePath;
		this.instances = instances;
		this.guid = guid;
		this.order = order;
		if (externalGUIDs!=null){
			this.externalGUIDs = externalGUIDs;
		}else{
			this.externalGUIDs = new ArrayList<>();
		}	
	}

	public String getTruePath() {
		return truePath;
	}

	public ArrayList<EBXInstance> getInstances() {
		return instances;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public void setTruePath(String truePath) {
		this.truePath = truePath;
	}

	public ByteOrder getByteOrder() {
		return order;
	}

	public ArrayList<EBXExternalGUID> getExternalGUIDs() {
		return externalGUIDs;
	}
	
}
