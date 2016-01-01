package tk.greydynamics.Resource.Frostbite3.EBX;

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
	
	
}
