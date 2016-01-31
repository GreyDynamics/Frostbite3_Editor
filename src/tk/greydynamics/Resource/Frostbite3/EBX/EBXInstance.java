package tk.greydynamics.Resource.Frostbite3.EBX;

import java.util.UUID;

public class EBXInstance {
	String guid;
	EBXComplex complex;
	public EBXInstance(String guid, EBXComplex complex) {
		this.guid = guid;
		this.complex = complex;
	}
	public String getGuid() {
		return guid;
	}
	public EBXComplex getComplex() {
		return complex;
	}
	
	public static EBXInstance clone(EBXInstance instance){
		return new EBXInstance(instance.getGuid(), EBXComplex.clone(instance.getComplex()));
	}
	
	public String assignRandomGUID(){
		char[] randomUUID = UUID.randomUUID().toString().replace("-", "").toCharArray();
		String id = "";
		for (int i=0;i<this.guid.length();i++){
			id = id+randomUUID[i];
		}
		this.guid = id;
		return id;
	}
	
}
