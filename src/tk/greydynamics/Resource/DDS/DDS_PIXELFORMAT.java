package tk.greydynamics.Resource.DDS;

public class DDS_PIXELFORMAT {
	private int dwSize;
	private int dwFlags;
	private int dwFourCC;
	private int dwRGBBitCount;
	private int dwRBitMask;//unsigned
	private int dwGBitMask;//unsigned
	private int dwBBitMask;//unsigned
	private int dwABitMask;
	
	
	public DDS_PIXELFORMAT(int dwSize, int dwFlags, int dwFourCC, int dwRGBBitCount, int dwRBitMask, int dwGBitMask,
			int dwBBitMask, int dwABitMask) {
		this.dwSize = dwSize;
		this.dwFlags = dwFlags;
		this.dwFourCC = dwFourCC;
		this.dwRGBBitCount = dwRGBBitCount;
		this.dwRBitMask = dwRBitMask;
		this.dwGBitMask = dwGBitMask;
		this.dwBBitMask = dwBBitMask;
		this.dwABitMask = dwABitMask;
	}
	
	public DDS_PIXELFORMAT(){
		//USING NULLCONSTRUCTOR!
		this.dwSize = 0;
		this.dwFlags = 0;
		this.dwFourCC = 0;
		this.dwRGBBitCount = 0;
		this.dwRBitMask = 0;
		this.dwGBitMask = 0;
		this.dwBBitMask = 0;
		this.dwABitMask = 0;
	}
	
	public int getDwSize() {
		return dwSize;
	}
	public void setDwSize(int dwSize) {
		this.dwSize = dwSize;
	}
	public int getDwFlags() {
		return dwFlags;
	}
	public void setDwFlags(int dwFlags) {
		this.dwFlags = dwFlags;
	}
	public int getDwFourCC() {
		return dwFourCC;
	}
	public void setDwFourCC(int dwFourCC) {
		this.dwFourCC = dwFourCC;
	}
	public int getDwRGBBitCount() {
		return dwRGBBitCount;
	}
	public void setDwRGBBitCount(int dwRGBBitCount) {
		this.dwRGBBitCount = dwRGBBitCount;
	}
	public int getDwRBitMask() {
		return dwRBitMask;
	}
	public void setDwRBitMask(int dwRBitMask) {
		this.dwRBitMask = dwRBitMask;
	}
	public int getDwGBitMask() {
		return dwGBitMask;
	}
	public void setDwGBitMask(int dwGBitMask) {
		this.dwGBitMask = dwGBitMask;
	}
	public int getDwBBitMask() {
		return dwBBitMask;
	}
	public void setDwBBitMask(int dwBBitMask) {
		this.dwBBitMask = dwBBitMask;
	}
	public int getDwABitMask() {
		return dwABitMask;
	}
	public void setDwABitMask(int dwABitMask) {
		this.dwABitMask = dwABitMask;
	}
	
	
}
