package tk.greydynamics.Maths;

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
	
	
	/*Multiply Vector3f*/
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
	
}
