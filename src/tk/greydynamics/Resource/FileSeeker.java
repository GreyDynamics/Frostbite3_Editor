package tk.greydynamics.Resource;

public class FileSeeker {
	private int offset;
	private String description;
	private boolean error;
	
	public FileSeeker(int offset){
		this.offset = offset;
		this.description = null;
		this.error = false;
	}
	public FileSeeker(){
		this.offset = 0;
		this.description = null;
		this.error = false;
	}
	
	public FileSeeker(String description){
		this.offset = 0;
		this.description = description;
		this.error = false;
	}
	
	public void seek(int bytes){
		this.offset += bytes;
	}
	
	public int getOffset(){
		return offset;
	}
	public void setOffset(int offset){
		this.offset = offset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public boolean hasError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	
	
	
}
