package tk.greydynamics.Resource.Frostbite3.ITEXTURE;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class ITexture {
	
	/******************************************************STATIC*********************************STATIC************************************************************/
	/*Texture Format*/
    public static final int TF_DXT1 = 0;
    public static final int TF_DXT1A = 1;
    public static final int TF_DXT3 = 2;
    public static final int TF_DXT5 = 3;
    public static final int TF_DXT5A = 4;
    public static final int TF_DXN = 5;
    public static final int TF_BC7 = 6;
    public static final int TF_RGB565 = 7;
    public static final int TF_RGB888 = 8;
    public static final int TF_ARGB1555 = 9;
    public static final int TF_ARGB4444 = 10;
    public static final int TF_ARGB8888 = 11;
    public static final int TF_L8 = 12;
    public static final int TF_L16 = 13;
    public static final int TF_ABGR16 = 14;
    public static final int TF_ABGR16F = 15;
    public static final int TF_ABGR32F = 16;
    public static final int TF_R16F = 17;
    public static final int TF_R32F = 18;
    public static final int TF_NormalDXN = 19;
    public static final int TF_NormalDXT1 = 20;
    public static final int TF_NormalDXT5 = 21;
    public static final int TF_NormalDXT5RGA = 22;
    public static final int TF_RG8 = 23;
    public static final int TF_GR16 = 24;
    public static final int TF_GR16F = 25;
    public static final int TF_D16 = 26;
    public static final int TF_D24 = 27;
    public static final int TF_D24S8 = 28;
    public static final int TF_D24FS8 = 29;
    public static final int TF_D32F = 30;
    public static final int TF_D32FS8 = 31;
    public static final int TF_S8 = 32;
    public static final int TF_ABGR32 = 33;
    public static final int TF_GR32F = 34;
    public static final int TF_A2R10G10B10 = 35;
    public static final int TF_R11G11B10F = 36;
    public static final int TF_ABGR16_SNORM = 37;
    public static final int TF_ABGR16_UINT = 38;
    public static final int TF_L16_UINT = 39;
    public static final int TF_L32 = 40;
    public static final int TF_GR16_UINT = 41;
    public static final int TF_GR32_UINT = 42;
    public static final int TF_ETC1 = 43;
    public static final int TF_ETC2_RGB = 44;
    public static final int TF_ETC2_RGBA = 45;
    public static final int TF_ETC2_RGB_A1 = 56;
    public static final int TF_PVRTC1_4BPP_RGBA = 57;
    public static final int TF_PVRTC1_4BPP_RGB = 58;
    public static final int TF_PVRTC1_2BPP_RGBA = 59;
    public static final int TF_PVRTC1_2BPP_RGB = 60;
    public static final int TF_PVRTC2_4BPP = 61;
    public static final int TF_PVRTC2_2BPP = 62;
    public static final int TF_R8 = 63;
    public static final int TF_R9G9B9E5F = 64;
    /*Unknown Textureformat!*/
    public static final int TF_Unknown = 255;
    
    /*DDS TextureFormat*/
    public static final int DDS_DXT1 = 827611204;
    public static final int DDS_NormalDXT1 = 827611204;
    public static final int DDS_DXT1A = 1093752900;
    public static final int DDS_DXT5 = 894720068;
    public static final int DDS_DXT5A = 826889281;
    public static final int DDS_ABGR32F = 116;
    public static final int DDS_NormalDXN = 843666497;//ATI2
    
    
    /*TextureFormat <-> DDS_Format*/
    public static HashMap<Integer, Integer> PixelFormatTypes = new HashMap<Integer, Integer>(){{
            put(TF_DXT1, DDS_DXT1);
            put(TF_NormalDXT1, DDS_NormalDXT1);
            put(TF_DXT1A, DDS_DXT1A);
            put(TF_DXT5, DDS_DXT5);
            put(TF_DXT5A, DDS_DXT5A);
            put(TF_ABGR32F, DDS_ABGR32F);
            put(TF_NormalDXN, DDS_NormalDXN);
	    };
    };
    
    
    /*Texture Type*/
    public static final int TT_2d = 0;
    public static final int TT_Cube = 1;
    public static final int TT_3d = 2;
    public static final int TT_2dArray = 3;
    public static final int TT_1dArray = 4;
    public static final int TT_1d = 5;
    /***********************************************END******************OF******************STATIC****************************************************************/
	
	
	
	private int /*unsigned*/ mipOneEndOffset;
	private int /*unsigned*/ mipTwoEndOffset;
	private int textureType;
	private int pixelFormat;
	private short /*unsigned*/ unknown;
	private short width;
	private short height;
	private short depth;
	private short sliceCount;
	private byte numSizes;
	private byte firstMip;
	private byte[] chunkID;
	private int[] mipSizes;
	private byte[] data;
	private int chunkSize;
	private int /*unsigned*/ nameHash;
	private byte[] name;
	
	public ITexture(){
		this.mipOneEndOffset = 0;
		this.mipTwoEndOffset = 0;
		this.textureType = 0;
		this.pixelFormat = 0;
		this.unknown = 0;
		this.width = 0;
		this.height = 0;
		this.depth = 0;
		this.sliceCount = 0;
		this.numSizes = 0;
		this.firstMip = 0;
		this.chunkID = null;
		this.mipSizes = null;
		this.data = null;
		this.chunkSize = 0;
		this.nameHash = 0;
		this.name = null;
	}
	
	public ITexture(byte[] itextureBytes, FileSeeker seeker) {
		if (seeker==null){seeker=new FileSeeker();}
		
		this.mipOneEndOffset = FileHandler.readInt(itextureBytes, seeker);
		this.mipTwoEndOffset = FileHandler.readInt(itextureBytes, seeker);
		this.textureType = FileHandler.readInt(itextureBytes, seeker);
		this.pixelFormat = FileHandler.readInt(itextureBytes, seeker);
		this.unknown = FileHandler.readShort(itextureBytes, seeker, ByteOrder.LITTLE_ENDIAN);
		this.width = FileHandler.readShort(itextureBytes, seeker, ByteOrder.LITTLE_ENDIAN);
		this.height = FileHandler.readShort(itextureBytes, seeker, ByteOrder.LITTLE_ENDIAN);
		this.depth = FileHandler.readShort(itextureBytes, seeker, ByteOrder.LITTLE_ENDIAN);
		this.sliceCount = FileHandler.readShort(itextureBytes, seeker, ByteOrder.LITTLE_ENDIAN);
		this.numSizes = FileHandler.readByte(itextureBytes, seeker);
		this.firstMip = FileHandler.readByte(itextureBytes, seeker);
		this.chunkID = FileHandler.readByte(itextureBytes, seeker, 16);
		/*Mips*/
		this.mipSizes = new int[15];
		for (int i=0; i<this.mipSizes.length; i++){
			this.mipSizes[i] = FileHandler.readInt(itextureBytes, seeker);
		}
		this.chunkSize = FileHandler.readInt(itextureBytes, seeker);
		this.nameHash = FileHandler.readInt(itextureBytes, seeker);
		this.name = FileHandler.readByte(itextureBytes, seeker, 16);		
	}
	public byte[] toBytes(){
		ArrayList<Byte> itextureBytes = new ArrayList<>();
		
		FileHandler.addBytes(FileHandler.toBytes(this.mipOneEndOffset, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.mipTwoEndOffset, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.textureType, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.pixelFormat, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		
		FileHandler.addBytes(FileHandler.toBytes(this.unknown /*short*/, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.width /*short*/, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.height /*short*/, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.depth /*short*/, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.sliceCount /*short*/, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		
		itextureBytes.add(this.numSizes);
		itextureBytes.add(this.firstMip);
		
		FileHandler.addBytes(this.chunkID, itextureBytes);
		
		for (int i=0; i<this.mipSizes.length; i++){
			FileHandler.addBytes(FileHandler.toBytes(this.mipSizes[i], ByteOrder.LITTLE_ENDIAN), itextureBytes);
		}
		
		FileHandler.addBytes(FileHandler.toBytes(this.chunkSize, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		FileHandler.addBytes(FileHandler.toBytes(this.nameHash, ByteOrder.LITTLE_ENDIAN), itextureBytes);
		
		FileHandler.addBytes(this.name, itextureBytes);
		return FileHandler.toByteArray(itextureBytes);
	}

	public int getMipOneEndOffset() {
		return mipOneEndOffset;
	}

	public void setMipOneEndOffset(int mipOneEndOffset) {
		this.mipOneEndOffset = mipOneEndOffset;
	}

	public int getMipTwoEndOffset() {
		return mipTwoEndOffset;
	}

	public void setMipTwoEndOffset(int mipTwoEndOffset) {
		this.mipTwoEndOffset = mipTwoEndOffset;
	}

	public int getTextureType() {
		return textureType;
	}

	public void setTextureType(int textureType) {
		this.textureType = textureType;
	}

	public int getPixelFormat() {
		return pixelFormat;
	}

	public void setPixelFormat(int pixelFormat) {
		this.pixelFormat = pixelFormat;
	}

	public short getUnknown() {
		return unknown;
	}

	public void setUnknown(short unknown) {
		this.unknown = unknown;
	}

	public short getWidth() {
		return width;
	}

	public void setWidth(short width) {
		this.width = width;
	}

	public short getHeight() {
		return height;
	}

	public void setHeight(short height) {
		this.height = height;
	}

	public short getDepth() {
		return depth;
	}

	public void setDepth(short depth) {
		this.depth = depth;
	}

	public short getSliceCount() {
		return sliceCount;
	}

	public void setSliceCount(short sliceCount) {
		this.sliceCount = sliceCount;
	}

	public byte getNumSizes() {
		return numSizes;
	}

	public void setNumSizes(byte numSizes) {
		this.numSizes = numSizes;
	}

	public byte getFirstMip() {
		return firstMip;
	}

	public void setFirstMip(byte firstMip) {
		this.firstMip = firstMip;
	}

	public byte[] getChunkID() {
		return chunkID;
	}

	public void setChunkID(byte[] chunkID) {
		this.chunkID = chunkID;
	}

	public int[] getMipSizes() {
		return mipSizes;
	}

	public void setMipSizes(int[] mipSizes) {
		this.mipSizes = mipSizes;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getNameHash() {
		return nameHash;
	}

	public void setNameHash(int nameHash) {
		this.nameHash = nameHash;
	}

	public byte[] getName() {
		return name;
	}

	public void setName(byte[] name) {
		this.name = name;
	}
	
	
	
}
