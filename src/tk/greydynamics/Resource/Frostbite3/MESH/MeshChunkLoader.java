package tk.greydynamics.Resource.Frostbite3.MESH;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.Cas.Bundle;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.CasDataReader;
import tk.greydynamics.Resource.Frostbite3.Toc.ConvertedTocFile;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;
import tk.greydynamics.Resource.Frostbite3.Toc.TocEntry;

public class MeshChunkLoader {
	public byte[] MeshBytes;
	public byte[] ChunkBytes;
	
	
	private int First_Data_Block_Offset = 0;
	private int Mesh_Object_Count = 0;
	private String ObjectFullName= "";
	private int ObjectFullNameOffset = 0;
	private int Submesh_Mat_Name_Offset;
	private long FaceIndiceOffset=0;
	private int submesh_vert_count=0;
	private float Model_Scale = 4f;
	private String Submesh_Material_Name="";
	private long Submesh_Face_Indice_Count = 0;
	private int Submesh_Face_Indice_lastOffset = 0;
	
	public int[] VB_Offset;
	public int[][] VB_Data_Offset;
	public int[] VB_Sizes;
	public int VB_Datas = 30;
	public int Increment_For_Vert_position;
	
	public int[] Indice_Positions;
	public int[] Indice_Count;
	public int[] Indice_CountFull;
	public int[] Vertices_Count;
	public String[] SubmeshNames;
	
	public float[] minCoords;
	public float[] maxCoords;
	
    	
	public boolean loadFile(byte[] mesh, Bundle bundle){
		this.MeshBytes = mesh;
		this.ChunkBytes = null;
		String chunkID = FileHandler.bytesToHex(readByte(MeshBytes, 0xC8, 16));
		CasBundle casBundle = null;
		
		if (bundle.getType()==Bundle.BundleType.CAS){
			casBundle = (CasBundle) bundle;
		}else{
			return false;
		}
		for (ResourceLink chunk : casBundle.getChunks()){
			//System.out.println(chunk.getId());
			if (chunk.getId().equalsIgnoreCase(chunkID)){
				this.ChunkBytes = CasDataReader.readCas(chunk.getBaseSha1(), chunk.getDeltaSha1(), chunk.getSha1(), chunk.getCasPatchType());
				System.out.println("Chunk successfully found!");
				break;
			}
		}
		if (ChunkBytes==null){
			for (ConvertedTocFile commonChunk : Core.getGame().getCommonChunks()){
				for (TocEntry chunk : commonChunk.getChunks()){
					if (chunk.getGuid().equalsIgnoreCase(chunkID)){
						this.ChunkBytes = CasDataReader.readCas(null, null, chunk.getSha1(), 666);
						System.out.println("Chunk successfully found!");
						break;
					}
				}
				if (ChunkBytes!=null){
					break;
				}
			}
		}
		if (ChunkBytes==null){
			System.err.println("Chunk could not be found: "+chunkID);
			return false;
		}
		
		this.ObjectFullNameOffset = readShort(MeshBytes, 0x58); // 88 Bytes
		this.ObjectFullName = readString(MeshBytes,ObjectFullNameOffset);
		this.Mesh_Object_Count = readShort(MeshBytes, 0x78); // 120 Bytes
		
		//TEST OUT =)
		this.Mesh_Object_Count -= 1;
						
		this.First_Data_Block_Offset = readShort(MeshBytes, 0x7C); // 124 Bytes
		this.FaceIndiceOffset = readLong(MeshBytes, 0xC0); // 192 Bytes	
				
		System.out.println("ObjectFullNameOffset "+ ObjectFullNameOffset);
		System.out.println("ObjectFullName "+ ObjectFullName);
		System.out.println("Mesh_Object_Count "+ Mesh_Object_Count);
		System.out.println("First_Data_Block_Offset "+ First_Data_Block_Offset);
		System.out.println("FaceIndiceOffset "+ FaceIndiceOffset);
		
		Indice_Positions = new int[Mesh_Object_Count];
		Indice_Count = new int[Mesh_Object_Count];
		Indice_CountFull = new int[Mesh_Object_Count];
		SubmeshNames = new String[Mesh_Object_Count];
		Vertices_Count = new int[Mesh_Object_Count];
		
		VB_Offset = new int[Mesh_Object_Count];
		VB_Sizes = new int[Mesh_Object_Count];
		
		Increment_For_Vert_position = 0;
		
		Submesh_Face_Indice_lastOffset = (int) FaceIndiceOffset;
		
		VB_Data_Offset = new int[Mesh_Object_Count][];
		for (int submesh=0; submesh<Mesh_Object_Count; submesh++){
			VB_Offset[submesh] = Increment_For_Vert_position;
			System.out.println(submesh+" VB_Offset "+VB_Offset[submesh]);
			
			this.Submesh_Mat_Name_Offset = readShort(MeshBytes, First_Data_Block_Offset+(0xC0*submesh)+0x08); // +192 each submesh +8 bytes
			System.out.println(submesh+" Submesh_Mat_Name_Offset "+ Submesh_Mat_Name_Offset);
			
			this.Submesh_Material_Name = readString(MeshBytes, Submesh_Mat_Name_Offset);
			SubmeshNames[submesh] = Submesh_Material_Name;
			System.out.println(submesh+" Submesh_Material_Name "+ Submesh_Material_Name);
			
			this.submesh_vert_count = readShort(MeshBytes, First_Data_Block_Offset+(0xC0*submesh)+0x20); // +192 each submesh +32 bytes
			Vertices_Count[submesh] = submesh_vert_count;
			System.out.println(submesh+" submesh_vert_count "+ submesh_vert_count);
			
			VB_Sizes[submesh] = readShort(MeshBytes, First_Data_Block_Offset+(0xC0*submesh)+0x70); //+192 each submesh +112 bytes
			System.out.println(submesh+" VB_Size "+ VB_Sizes[submesh]);
			
			VB_Data_Offset[submesh] = new int[VB_Sizes[submesh]/2];
			for (int i=0; i < VB_Data_Offset[submesh].length; i++){
				VB_Data_Offset[submesh][i] = readShort(MeshBytes, First_Data_Block_Offset+(0xC0*submesh)+0x2E+(i*0x02)); // +192 each submesh +44 bytes (+2bytes for VB_Blocks)
				System.out.println(submesh+" VB_Data_Offset"+i+" "+VB_Data_Offset[submesh][i]);
			}
						
			//first represents the correct count, all >= 1 need to be subtracted from previous one.	
			this.Submesh_Face_Indice_Count = readInt(MeshBytes, First_Data_Block_Offset+(0xC0*submesh)+0xD8); // +192 each submesh +216 bytes
			if (submesh >= 1){
				Indice_CountFull[submesh] = (int) Submesh_Face_Indice_Count;
				Submesh_Face_Indice_Count -= Indice_CountFull[submesh-1];
			}else{
				Indice_CountFull[0] = (int) Submesh_Face_Indice_Count;
			}
			System.out.println(submesh+" Submesh_Face_Indice_Count "+ Submesh_Face_Indice_Count);
			
			//current Indice Offset
			this.Indice_Positions[submesh] = Submesh_Face_Indice_lastOffset;
			this.Indice_Count[submesh] = (int) Submesh_Face_Indice_Count;
			System.out.println(submesh+" Indice_Positions "+ Indice_Positions[submesh]);
			//Calculation next Indice offset.
			this.Submesh_Face_Indice_lastOffset += (Submesh_Face_Indice_Count*0x02);	
			
			Increment_For_Vert_position += (VB_Sizes[submesh]*submesh_vert_count);
		}
		minCoords = new float[] {10000f, 10000f, 10000f};
		maxCoords = new float[] {-10000f, -10000f, -10000f};
		return true;
		
	}
	
	public int getSubMeshCount(){
		return Mesh_Object_Count;
	}
	
	public String getName(){
		return ObjectFullName;
	}
	
	
	
	//-----------------------------
	public int[] getIndices(int submesh){
		int[] buffer = new int[getIndiceCount(submesh)];
		for (int i=0; i<buffer.length; i++){
			buffer[i] = readShort(ChunkBytes, Indice_Positions[submesh]+(i*0x02));
		}
		return buffer;
	}
	
	public float[] getVertexPositions(int submesh){
		float[] buffer = new float[Vertices_Count[submesh]*3];
		if (VB_Data_Offset[submesh][1] == 769){ //FLOAT
			for (int i=0; i<buffer.length; i++){
				buffer[i] = readFloat(ChunkBytes, (int) ((VB_Offset[submesh]+(VB_Sizes[submesh]*Math.floor(i/3))) + ((i%3)*0x04)))*Model_Scale;
			}
		}else{ //HALF-FLOAT
			for (int i=0; i<buffer.length; i++){
				buffer[i] = readHalfFloat(ChunkBytes, (int) ((VB_Offset[submesh]+(VB_Sizes[submesh]*Math.floor(i/3))) + ((i%3)*0x02)))*Model_Scale;
			}
		}
		
		//<--axis aligned bounding box-->
		for (int i=0; i<buffer.length; i++){
			float vertex = buffer[i];
			int mod = i%3;//xyz
			if (vertex > maxCoords[mod]){
				maxCoords[mod] = vertex;
			}
			if (vertex < minCoords[mod]){
				minCoords[mod] = vertex;
			}
		}
		//System.out.println("Min: "+minCoords[0]+", "+minCoords[1]+", "+minCoords[2]+" Max: "+maxCoords[0]+", "+maxCoords[1]+", "+maxCoords[2]);
		return buffer;
	}
	
	public float[] getUVCoords(int submesh) {
		float[] coords = new float[Vertices_Count[submesh]*2];
		for (int i1=0; i1<VB_Data_Offset[submesh].length;i1++){
			if (VB_Data_Offset[submesh][i1]==1569){
				for (int i2=0; i2<coords.length; i2++){
					coords[i2] = readHalfFloat(ChunkBytes, (int) ((VB_Offset[submesh]+(VB_Sizes[submesh]*Math.floor(i2/2))) + ((i2%2)*0x02))+(VB_Data_Offset[submesh][i1+1]));
				}
				return coords;
			}
		}
		return coords;
	}
	
	//-----------------------------
	public String getSubMeshName(int submesh){
		return SubmeshNames[submesh];
	}
	
	public int getIndicesPositions(int submesh){
		return Indice_Positions[submesh];
	}
	
	public int getIndiceCount(int submesh) {
		return Indice_Count[submesh];
	}

	//Inputstream Operations
	public int readShort(byte[] fileArray, int offset){
		return ByteBuffer.wrap(readByte(fileArray, offset, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
	}
	
	public int readInt(byte[] fileArray, int offset){
		return ByteBuffer.wrap(readByte(fileArray, offset, 4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}
	
	public float readHalfFloat(byte[] fileArray, int offset){
		return convertHalfToFloat(ByteBuffer.wrap(readByte(fileArray, offset, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort());
	}
	
	public float readFloat(byte[] fileArray, int offset){
		return ByteBuffer.wrap(readByte(fileArray, offset, 4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	
	public long readLong(byte[] fileArray, int offset){
		return ByteBuffer.wrap(readByte(fileArray, offset, 8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}
	
	public String readString(byte[] fileArray, int offset){
		String tmp = "";
		for (int i=0; i < 1000; i++){
			byte[] b = readByte(fileArray, offset+i, 1);
			if (b[0] != 0x0){
				String str;
				try {
					str = new String(b, "UTF-8");
					tmp += str;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					i = 1500;
				}
			}else{
				i = 1500;
			}
		}
		return tmp;
	}
	
	public byte[] readByte(byte[] fileArray, int offset, int len){
		byte[] buffer = new byte[len];
		for (int i=0; i < len; i++){
			buffer[i] = fileArray[offset+i];
		}
		return buffer;
	}

	public byte[] readFile(String filepath){
		try{
			File file = new File(filepath);
			FileInputStream fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int)file.length()];	
			fin.read(fileContent);
			fin.close();
			return fileContent;
		}catch (Exception e){
			System.err.println("could not read file: "+filepath);
			return null;
		}
	}
	
	public float convertHalfToFloat(short half) {
        switch ((int) half) {
            case 0x0000:
                return 0f;
            case 0x8000:
                return -0f;
            case 0x7c00:
                return Float.POSITIVE_INFINITY;
            case 0xfc00:
                return Float.NEGATIVE_INFINITY;
            default:
                return Float.intBitsToFloat(((half & 0x8000) << 16)
                        | (((half & 0x7c00) + 0x1C000) << 13)
                        | ((half & 0x03FF) << 13));
        }
    }

	public float[] getMinCoords() {
		return minCoords;
	}

	public float[] getMaxCoords() {
		return maxCoords;
	}
	
	
}
