package tk.greydynamics.Resource.Frostbite3.Cas;

public class CasCatEntry {
	//Init
	public String sha1;
	public int Offset;
	public int ProcSize;
	public int casFile;
	
	//Getter and Setters
	public int getOffset() {
		return Offset;
	}
	public void setOffset(int offset) {
		Offset = offset;
	}
	public int getProcSize() {
		return ProcSize;
	}
	public void setProcSize(int procSize) {
		ProcSize = procSize;
	}
	public int getCasFile() {
		return casFile;
	}
	public void setCasFile(int casFile) {
		this.casFile = casFile;
	}
	
	
	
	public String getSHA1() {
		return sha1;
	}
	public void setSHA1(String sha1) {
		this.sha1 = sha1;
	}
	//Constructor
	public CasCatEntry(String sha1, int offset, int procSize, int casFile) {
		this.sha1 = sha1;
		Offset = offset;
		ProcSize = procSize;
		this.casFile = casFile;
	}
	
	public CasCatEntry(){
		//NULL CONSTRUCTOR
	}
}
