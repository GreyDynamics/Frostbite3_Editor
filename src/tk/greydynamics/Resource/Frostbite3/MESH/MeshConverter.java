package tk.greydynamics.Resource.Frostbite3.MESH;

import java.util.ArrayList;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.Cas.Bundle;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;

public class MeshConverter {
		
	public static byte[] getAsOBJ(byte[] mesh, Bundle bundle){
		MeshChunkLoader msl = Core.getGame().getResourceHandler().getMeshChunkLoader();
		msl.loadFile(mesh, bundle);
		String[] subMeshNames = new String[msl.getSubMeshCount()];
		float[][] verts = new float[msl.getSubMeshCount()][];
		float[][] uvcords = new float[msl.getSubMeshCount()][];
		int[][] indices = new int[msl.getSubMeshCount()][];
		for (int i=0; i<msl.getSubMeshCount(); i++){
			subMeshNames[i] = msl.getSubMeshName(i);
			verts[i] = msl.getVertexPositions(i);
			uvcords[i] = msl.getUVCoords(i);
			indices[i] = msl.getIndices(i);
		}
		return convertToOBJ(msl.getName(), subMeshNames, verts, uvcords, indices);
	}
	
	public static byte[] convertToOBJ(String modelName, String[] subMeshName, float[][] verts, float[][] uvcords, int[][] indices){
		ArrayList<Byte> objFile = new ArrayList<>();
		for (byte b: ((String) "o "+modelName+"\ns off\n").getBytes()){objFile.add(b);}//object name and disable smoothing!
		
		int currentVertexCount = 1;//index starts not at 0 ;)
		for (int i=0; i<subMeshName.length; i++){//for each Submesh
			float[] subVert = verts[i];
			float[] subUVs = uvcords[i];
			int[] subIndices = indices[i];
			
			//subMeshName as group
			for (byte b: ((String) "g "+subMeshName[i]+"\n").getBytes()){objFile.add(b);}
			
			//vert - "v 1.000000 -1.000000 -1.000000"
			for (int fi=0; fi<subVert.length;fi++){
				if (fi%3==0){
					for (byte b: ((String) "v").getBytes()){objFile.add(b);}
				}
				for (byte b: ((String) " "+String.valueOf(subVert[fi])).getBytes()){objFile.add(b);}
				if (fi%3==2){
					for (byte b: ((String) "\n").getBytes()){objFile.add(b);}
				}
			}
			//uvs - "vt 0.500000 0.500000"
			for (int fi=0; fi<subUVs.length;fi++){
				if (fi%2==0){
					for (byte b: ((String) "vt").getBytes()){objFile.add(b);}
				}
				for (byte b: ((String) " "+String.valueOf(subUVs[fi])).getBytes()){objFile.add(b);}
				if (fi%2==1){
					for (byte b: ((String) "\n").getBytes()){objFile.add(b);}
				}
			}
			
			//indices
			for (int fi=0; fi<subIndices.length;fi++){
				if (fi%3==0){
					for (byte b: ((String) "f").getBytes()){objFile.add(b);}
				}
				String s = String.valueOf(currentVertexCount+subIndices[fi]);
				for (byte b: ((String) " "+s+"/"+s).getBytes()){objFile.add(b);}
				if (fi%3==2){
					for (byte b: ((String) "\n").getBytes()){objFile.add(b);}
				}
			}
			//Frostbite's mesh starts each submesh at index 0, but Wavefront_OBJ does use the total index!
			currentVertexCount += subVert.length/3;
		}
		return FileHandler.toByteArray(objFile);
	}
	
}
