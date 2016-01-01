package tk.greydynamics.Resource.DDS;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class DDS_HEADER {
	public static byte[] DDSfourCC = new byte[]{0x44, 0x44, 0x53, 0x20};
	
	
	private int dwSize;
	private int dwFlags;
	private int dwHeight;
	private int dwWidth;
	private int dwPitchOrLinearSize;
	private int dwDepth;
	private int dwMipMapCount;
	private int[] dwReserved1;
	private DDS_PIXELFORMAT pixelformat;
	private int dwCaps;
	private int dwCaps2;
	private int dwCaps3;
	private int dwCaps4;
	private int dwReserved2;
	
	
	/**
	 * @param byteArray
	 * @param seeker If not used -> pass 'null'!
	*/
	public DDS_HEADER(byte[] fileArray, FileSeeker seeker){
		if (seeker==null){seeker = new FileSeeker();}
		
		int dds4CC = FileHandler.readInt(fileArray, seeker);
		
		this.dwSize = FileHandler.readInt(fileArray, seeker);
		this.dwFlags = FileHandler.readInt(fileArray, seeker);
		this.dwHeight = FileHandler.readInt(fileArray, seeker);
		this.dwWidth = FileHandler.readInt(fileArray, seeker);
		this.dwPitchOrLinearSize = FileHandler.readInt(fileArray, seeker);
		this.dwDepth = FileHandler.readInt(fileArray, seeker);
		this.dwMipMapCount = FileHandler.readInt(fileArray, seeker); //32 with dds4cc
		
		/*Reserved1*/
		this.dwReserved1 = new int[11];
		for (int i=0;i<this.dwReserved1.length;i++){
			this.dwReserved1[i] = FileHandler.readInt(fileArray, seeker);//44
		}
		
		/*Pixelformat*/
		this.pixelformat = new DDS_PIXELFORMAT(
				/*dwSize*/			FileHandler.readInt(fileArray, seeker),
				/*dwFlags*/			FileHandler.readInt(fileArray, seeker),
				/*dwFourCC*/		FileHandler.readInt(fileArray, seeker),
				/*dwRGBBitCount*/	FileHandler.readInt(fileArray, seeker),
				/*dwRBitMask*/		FileHandler.readInt(fileArray, seeker),
				/*dwGBitMask*/		FileHandler.readInt(fileArray, seeker),
				/*dwBBitMask*/		FileHandler.readInt(fileArray, seeker),
				/*dwABitMask*/		FileHandler.readInt(fileArray, seeker)//32
		);
				
		this.dwCaps = FileHandler.readInt(fileArray, seeker);
		this.dwCaps2 = FileHandler.readInt(fileArray, seeker);
		this.dwCaps3 = FileHandler.readInt(fileArray, seeker);
		this.dwCaps4 = FileHandler.readInt(fileArray, seeker);
		this.dwReserved2 = FileHandler.readInt(fileArray, seeker);//20
	}
	
	public byte[] toBytes(){
		ArrayList<Byte> headerBytes = new ArrayList<>();
		
		FileHandler.addBytes(DDSfourCC, headerBytes);
		
		FileHandler.addBytes(FileHandler.toBytes(this.dwSize, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwFlags, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwHeight, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwWidth, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwPitchOrLinearSize, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwDepth, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwMipMapCount, ByteOrder.LITTLE_ENDIAN), headerBytes);
		
		/*Reserved 1*/
		for (int i=0; i<this.dwReserved1.length; i++){
			FileHandler.addBytes(FileHandler.toBytes(this.dwReserved1[i], ByteOrder.LITTLE_ENDIAN), headerBytes);
		}
			
		/*PixelFormat*/
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwSize(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwFlags(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwFourCC(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwRGBBitCount(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwRBitMask(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwGBitMask(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwBBitMask(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelformat.getDwABitMask(), ByteOrder.LITTLE_ENDIAN), headerBytes);
		
		
		FileHandler.addBytes(FileHandler.toBytes(this.dwCaps, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwCaps2, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwCaps3, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwCaps4, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.dwReserved2, ByteOrder.LITTLE_ENDIAN), headerBytes);
		
		return FileHandler.toByteArray(headerBytes);		
	}
		
	public DDS_HEADER() {
		dwSize = 0;
		dwFlags = 0;
		dwHeight = 0;
		dwWidth = 0;
		dwPitchOrLinearSize = 0;
		dwDepth = 0;
		dwMipMapCount = 0;
		dwReserved1 = null;
		pixelformat = null;
		dwCaps = 0;
		dwCaps2 = 0;
		dwCaps3 = 0;
		dwCaps4 = 0;
		dwReserved2 = 0;
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
	public int getDwHeight() {
		return dwHeight;
	}
	public void setDwHeight(int dwHeight) {
		this.dwHeight = dwHeight;
	}
	public int getDwWidth() {
		return dwWidth;
	}
	public void setDwWidth(int dwWidth) {
		this.dwWidth = dwWidth;
	}
	public int getDwPitchOrLinearSize() {
		return dwPitchOrLinearSize;
	}
	public void setDwPitchOrLinearSize(int dwPitchOrLinearSize) {
		this.dwPitchOrLinearSize = dwPitchOrLinearSize;
	}
	public int getDwDepth() {
		return dwDepth;
	}
	public void setDwDepth(int dwDepth) {
		this.dwDepth = dwDepth;
	}
	public int getDwMipMapCount() {
		return dwMipMapCount;
	}
	public void setDwMipMapCount(int dwMipMapCount) {
		this.dwMipMapCount = dwMipMapCount;
	}
	public int[] getDwReserved1() {
		return dwReserved1;
	}
	public void setDwReserved1(int[] dwReserved1) {
		this.dwReserved1 = dwReserved1;
	}
	public DDS_PIXELFORMAT getPixelformat() {
		return pixelformat;
	}
	public void setPixelformat(DDS_PIXELFORMAT pixelformat) {
		this.pixelformat = pixelformat;
	}
	public int getDwCaps() {
		return dwCaps;
	}
	public void setDwCaps(int dwCaps) {
		this.dwCaps = dwCaps;
	}
	public int getDwCaps2() {
		return dwCaps2;
	}
	public void setDwCaps2(int dwCaps2) {
		this.dwCaps2 = dwCaps2;
	}
	public int getDwCaps3() {
		return dwCaps3;
	}
	public void setDwCaps3(int dwCaps3) {
		this.dwCaps3 = dwCaps3;
	}
	public int getDwCaps4() {
		return dwCaps4;
	}
	public void setDwCaps4(int dwCaps4) {
		this.dwCaps4 = dwCaps4;
	}
	public int getDwReserved2() {
		return dwReserved2;
	}
	public void setDwReserved2(int dwReserved2) {
		this.dwReserved2 = dwReserved2;
	}
}
