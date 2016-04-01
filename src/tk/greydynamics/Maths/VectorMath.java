package tk.greydynamics.Maths;

import java.util.Vector;

import org.lwjgl.util.vector.Vector3f;


public class VectorMath {
	
	/*Direction A from Location A-B*/
	public static Vector3f getDirection(Vector3f posA, Vector3f posB, Vector3f dest){
		/*
		Vector3f normVec = Vector3f.sub(posA, posB, null).normalise(null);
		float rotx = (float) -Math.atan2(normVec.y, normVec.z);
		float roty = (float) Math.atan2(normVec.x, Math.sqrt(normVec.y*normVec.y + normVec.z*normVec.z));
		Vector3f vectorDegrees = new Vector3f((float) Math.toDegrees(rotx), (float) Math.toDegrees(roty), 0);
		//System.out.println(vectorDegrees.x);
		if (dest!=null){
			dest = vectorDegrees;
		}
		return vectorDegrees;*/
		System.err.println("TODO! getDirection Vector3f");
		return null;
	}
	
	
	/*Multiply direct Vector3f*/
	public static Vector3f multiply(Vector3f left, Vector3f right, Vector3f dest){
		Vector3f vec = new Vector3f(left.x, left.y, left.z);
		vec.x *= right.x;
		vec.y *= right.y;
		vec.z *= right.z;
		if (dest!=null){
			dest = vec;
		}
		return vec;
	}
	public static Vector3f multiply(Vector3f left, float f, Vector3f dest){
		Vector3f vec = new Vector3f(left.x, left.y, left.z);
		vec.x *= f;
		vec.y *= f;
		vec.z *= f;
		if (dest!=null){
			dest = vec;
		}
		return vec;
	}
	
	public static float getDistance(Vector3f pos1, Vector3f pos2){
		//http://freespace.virgin.net/hugo.elias/routines/r_dist.htm
		float xd = pos2.x-pos1.x;
		float yd = pos2.y-pos1.y;
		float zd = pos2.z-pos1.z;
		return (float) Math.sqrt(xd*xd + yd*yd + zd*zd);
	}
	
	public static Vector3f triangulateSurfaceNormal(Vector3f a, Vector3f b, Vector3f c){
		//TODO: triangulateSurfaceNormal: working - blender almost hit the point (very close). is asin required ?
		
		/*
		 http://stackoverflow.com/questions/18519586/calculate-normal-per-vertex-opengl
		 triangles only!
		 The Surface Normal is aligned by default to the most positive delta to 0,0,0 of a, b and c
		 what could cause inverted normals.
		*/
		
		Vector3f subBA = Vector3f.sub(b, a, null);
		Vector3f subCA = Vector3f.sub(c, a, null);
		
		Vector3f crossProduct = Vector3f.cross(subBA, subCA, null);
		float sin_alpha = crossProduct.length() / (subBA.length() * subCA.length());
		float asin = (float) Math.asin(sin_alpha);
		
		return multiply(crossProduct.normalise(null), asin, null);
	}
	
//	public static float[] triangulateVertexNormals(float[] positions, int[] indices){
//		/*
//		 http://stackoverflow.com/questions/18519586/calculate-normal-per-vertex-opengl
//		 triangles only!
//		*/
//		
//		int[][] faces = new int[indices.length/3][];
//		Vector3f[] surfaceNormals = new Vector3f[faces.length];
//		for (int faceIndex = 0; faceIndex<faces.length; faceIndex++){
//			//fill faces with vertex index
//			int indiceIndex = faceIndex*3;
//			faces[faceIndex] = new int[3];
//			faces[faceIndex][0] = indices[indiceIndex  ];
//			faces[faceIndex][1] = indices[indiceIndex+1];
//			faces[faceIndex][2] = indices[indiceIndex+2];
//			
//			//triangulate surface normal
//			surfaceNormals[faceIndex] = triangulateSurfaceNormal(
//				new Vector3f(positions[indices[indiceIndex]  ], positions[indices[indiceIndex] + 1], positions[indices[indiceIndex] + 2]),
//				new Vector3f(positions[indices[indiceIndex+1]], positions[indices[indiceIndex+1]+1], positions[indices[indiceIndex+1]+2]),
//				new Vector3f(positions[indices[indiceIndex+2]], positions[indices[indiceIndex+2]+1], positions[indices[indiceIndex+2]+2])
//			);
//		}
//		
//		
//		float[] normals = new float[positions.length];
//		for (int vertexIndex = 0; vertexIndex<positions.length/3; vertexIndex++){
//			//triangulate Normal for each Vertex
//			Vector3f N = new Vector3f(0f, 0f, 0f);
//			for (int faceIndex = 0; faceIndex < faces.length; faceIndex++) {
//				//take the the surface normal from each face, that uses the current vertex 
//				if (faces[faceIndex][0]==vertexIndex||faces[faceIndex][1]==vertexIndex||faces[faceIndex][2]==vertexIndex){
//					N = Vector3f.add(N, surfaceNormals[faceIndex], null);
////					break;
//				}
//			}
//			N.normalise(N);
//			
//			//store normal in array.
//			normals[vertexIndex*3  ] = N.x;
//			normals[vertexIndex*3+1] = N.y;
//			normals[vertexIndex*3+2] = N.z;
//		}
//		return normals;
//	}
}
