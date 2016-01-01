package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXExternalFileReference {
	private String guid;
	private String trueFileName;
	
	public EBXExternalFileReference(String guid, String trueFileName) {
		this.guid = guid;
		this.trueFileName = trueFileName;
	}
	
	public String getTrueFileNameAndGUID(){
		return trueFileName+" "+guid;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getTrueFileName() {
		return trueFileName;
	}

	public void setTrueFileName(String trueFileName) {
		this.trueFileName = trueFileName;
	}

	
	
	
	
}
