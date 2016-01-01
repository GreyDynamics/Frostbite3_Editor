package tk.greydynamics.Maths;

import org.lwjgl.util.vector.Vector3f;

public class RayCasting {
	public static void getRayPosition(Vector3f ray, Vector3f direction, float distance){
		ray.x += distance * (float)Math.sin(Math.toRadians(direction.getY()));//yaw
		ray.y -= distance * (float)Math.tan(Math.toRadians(direction.getX()));//pitch
		ray.z -= distance * (float)Math.cos(Math.toRadians(direction.getY()));//yaw
	}
	public static Vector3f getRayPosition(Vector3f position, Vector3f direction, float distance, Vector3f target){
		Vector3f ray = new Vector3f(position.x, position.y, position.z);
		ray.x += distance * (float)Math.sin(Math.toRadians(direction.getY()));//yaw
		ray.y -= distance * (float)Math.tan(Math.toRadians(direction.getX()));//pitch
		ray.z -= distance * (float)Math.cos(Math.toRadians(direction.getY()));//yaw
		if (target!=null){
			target = ray;
		}
		return ray;
	}
}
