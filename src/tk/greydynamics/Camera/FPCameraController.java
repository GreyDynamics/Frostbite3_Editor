package tk.greydynamics.Camera;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Player.PlayerEntity;

public class FPCameraController
{
    //3d vector to store the camera's position in
    public Vector3f    position    = null;
    //the rotation around the Y axis of the camera
    public float       yaw         = 0.0f;
    //the rotation around the X axis of the camera
    public float       pitch       = 0.0f;
    
    public boolean considerPitch = false;
    
    
    public float dx = 0.0f;
    public float dy = 0.0f;
    
    public float mouseSensitivity = 0.05f;
    
    public PlayerEntity pe;
    
    //Constructor that takes the starting x, y, z location of the camera
    public FPCameraController(PlayerEntity pe)
    {
    	this.pe = pe;
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(-pe.posX, -pe.posX, -pe.posX);
        
    }
    //increment the camera's current yaw rotation
    public void yaw(float amount)
    {
        //increment the yaw by the amount param
        yaw += amount;
    }
     
    //increment the camera's current yaw rotation
    public void pitch(float amount)
    {
        //increment the pitch by the amount param
    	pitch += amount;
    	
    	if (pitch>=90f){
    		pitch = 90;
    	}else if (pitch<-90f){
    		pitch = -90f;
    	}
    }
    //moves the camera forward relative to its current rotation (yaw|pitch)
    public void walkForward(float distance)
    {
        position.x += distance * (float)Math.sin(Math.toRadians(yaw));
        if (considerPitch){
        	position.y -= distance * (float)Math.tan(Math.toRadians(pitch));
        }
        position.z -= distance * (float)Math.cos(Math.toRadians(yaw));
    }
     
    //moves the camera backward relative to its current rotation (yaw|pitch)
    public void walkBackwards(float distance)
    {
        position.x -= distance * (float)Math.sin(Math.toRadians(yaw));
        if (considerPitch){
        	position.y += distance * (float)Math.tan(Math.toRadians(pitch));
        }
        position.z += distance * (float)Math.cos(Math.toRadians(yaw));
    }
     
    //strafes the camera left relitive to its current rotation (yaw)
    public void strafeLeft(float distance)
    {
        position.x += distance * (float)Math.sin(Math.toRadians(yaw-90));
        position.z -= distance * (float)Math.cos(Math.toRadians(yaw-90));
    }
     
    //strafes the camera right relitive to its current rotation (yaw)
    public void strafeRight(float distance)
    {
        position.x += distance * (float)Math.sin(Math.toRadians(yaw+90));
        position.z -= distance * (float)Math.cos(Math.toRadians(yaw+90));
    }
  //translates and rotate the matrix so that it looks through the camera
    //this dose basic what gluLookAt() does
    public void lookThrough()
    {
    	position.x = -pe.posX;
    	position.y = -pe.posY;
    	position.z = -pe.posZ;
    	
    	if (pe.velZ < 0){
	    	walkForward(pe.movementSpeed*pe.velZ);
	    }else{
	    	walkBackwards(-pe.movementSpeed*pe.velZ);
	    }
	    
	    if (pe.velX > 0){
	    	strafeLeft(pe.movementSpeed*pe.velX);
	    }else{
	    	strafeRight(-pe.movementSpeed*pe.velX);
	    }
	    position.y -= pe.velY;
	    
	    if (!pe.onGround){
	    	position.y += pe.gravity;
		}    
	    
	    pe.velX *= 0.86;
	    pe.velY *= 0.86;
	    pe.velZ *= 0.86;
	    
	    //Apply Camera data to Player.
	    pe.rotX = pitch;
	    pe.rotY = yaw;
	    
	    pe.posX = -position.x;
	    pe.posY = -position.y;
	    pe.posZ = -position.z;
	    /*
        //roatate the pitch around the X axis
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        GL11.glTranslatef(position.x, position.y, position.z);*/
    }
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f pos){
		this.position = pos;
	}
	public float getYaw() {
		return yaw;
	}
	public float getPitch() {
		return pitch;
	}
	public boolean isConsiderPitch() {
		return considerPitch;
	}
	public void setConsiderPitch(boolean considerPitch) {
		this.considerPitch = considerPitch;
	}
	
	public void setRotation(Vector3f rot){
		this.pitch = rot.x;
		this.yaw = rot.y;
	}
	
	public void changePosition(Vector3f relPos){
		this.position.x += relPos.x;
		this.position.z += relPos.y;
		this.position.y += relPos.z;
	}
	public float getMouseSensitivity() {
		return mouseSensitivity;
	}
	public void setMouseSensitivity(float mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;
	}
	
	
	
}
