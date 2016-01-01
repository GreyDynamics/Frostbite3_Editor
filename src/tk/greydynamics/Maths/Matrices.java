package tk.greydynamics.Maths;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Matrices {
	public static Matrix4f createProjectionMatrix(Float fieldOfView, int WIDTH, int HEIGHT, float near_plane, float far_plane){
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setIdentity();
		float aspectRatio = (float)WIDTH / (float)HEIGHT;
		 
		float y_scale = (float) (1/Math.tan(Math.toRadians(fieldOfView / 2f)));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;
		 
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scaling){
		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.setIdentity();
		Matrix4f.translate(translation, transformationMatrix, transformationMatrix);
		Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0), transformationMatrix, transformationMatrix);
		Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0), transformationMatrix, transformationMatrix);
		Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1), transformationMatrix, transformationMatrix);
		Matrix4f.scale(scaling, transformationMatrix, transformationMatrix);
		return transformationMatrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Vector3f position, Vector3f rotation){
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0,0,1), viewMatrix, viewMatrix);
		Matrix4f.translate(position, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Vector3f getRotationInEulerAngles(Vector3f right, Vector3f up, Vector3f forward){
		//http://nghiaho.com/?page_id=846
		//<-Checked working->
		Vector3f angles = new Vector3f();
        // calculate the euler angles
		angles.x = (float) Math.atan2(forward.y, forward.z); // range: (-pi, pi)
		angles.y = (float) Math.atan2(-forward.x, Math.sqrt((forward.y * forward.y) + (forward.z * forward.z))); // range: (-(pi/2), pi/2)
		angles.z = (float) Math.atan2(up.x, right.x); // range: (-pi, pi)
        return angles;
    }
	
	public static Vector3f getRotationInDegrees(Vector3f right, Vector3f up, Vector3f forward){
		//http://nghiaho.com/?page_id=846
		Vector3f degrees = new Vector3f();
        // calculate the euler angles
        Vector3f euler = getRotationInEulerAngles(right, up, forward);
        // calculate euler angles to degrees
        degrees.x = (float) ((euler.x * 180) / Math.PI);
        degrees.y = (float) ((euler.y * 180) / Math.PI);
        degrees.z = (float) ((euler.z * 180) / Math.PI);
        
        
        /*!!!NOT TESTED YET!!!*/
        System.err.println("getRotationInDegrees not tested yet!!");
        return degrees;
    }
}
