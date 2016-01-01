package tk.greydynamics.Player;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


public class PlayerEntity {
	public float height = 100;
	public float width = 100;
	
	public float posX;
	public float posY;
	public float posZ;
	
	public float rotX;
	public float rotY;
	public float rotZ;
	
	public float velX = 0;
	public float velY = 0;
	public float velZ = 0;
	
	public float movementSpeed = 1.0f;
	public float jumpStrength = 0.05f;
	public float gravity = 0.0f;
	
	public boolean onGround = false;
	
	public PlayerEntity(float posX, float posY, float posZ){
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public float getPosZ() {
		return posZ;
	}

	public float getRotX() {
		return rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public float getVelX() {
		return velX;
	}

	public float getVelY() {
		return velY;
	}

	public float getVelZ() {
		return velZ;
	}

	public float getMovementSpeed() {
		return movementSpeed;
	}

	public float getJumpStrength() {
		return jumpStrength;
	}

	public float getGravity() {
		return gravity;
	}

	public boolean isOnGround() {
		return onGround;
	}
	
	public Vector3f getPos(){
		return new Vector3f(posX, posY, posZ);
	}
	public Vector3f getRot(){
		return new Vector3f(rotX, rotY, rotZ);
	}
	public Vector3f getVel(){
		return new Vector3f(velX, velY, velZ);
	}
	
	public void setPos(Vector3f pos){
		this.posX = pos.x;
		this.posY = pos.y;
		this.posZ = pos.z;
	}
	
	public void setRot(Vector3f rot){
		this.rotX = rot.x;
		this.rotY = rot.y;
		this.rotZ = rot.z;
	}
	
	public void setVel(Vector3f vel){
		this.velX = vel.x;
		this.velY = vel.y;
		this.velZ = vel.z;
	}
	
	public void changePosition(Vector3f relPos){
		this.posX += relPos.x;
		this.posY += relPos.y;
		this.posZ += relPos.z;
	}
	
	public Vector2f moveForward(float distance)
	{
		Vector2f vec = new Vector2f(distance * (float)Math.sin(Math.toRadians(rotY)), distance * (float)Math.cos(Math.toRadians(rotY)));
		posX += vec.x;
		posZ -= vec.y;
		return vec;
	}
	public Vector2f moveBackwards(float distance)
	{
		Vector2f vec = new Vector2f(distance * (float)Math.sin(Math.toRadians(rotY)), distance * (float)Math.cos(Math.toRadians(rotY)));
		posX -= vec.x;
		posZ += vec.y;
		return vec;
	}
	public Vector2f moveLeft(float distance)
	{
		Vector2f vec = new Vector2f(distance * (float)Math.sin(Math.toRadians(rotY-90)), distance * (float)Math.cos(Math.toRadians(rotY-90)));
		posX += vec.x;
		posZ -= vec.y;
		return vec;
	}
	public Vector2f moveRight(float distance)
	{
		Vector2f vec = new Vector2f(distance * (float)Math.sin(Math.toRadians(rotY+90)), distance * (float)Math.cos(Math.toRadians(rotY+90)));
		posX += vec.x;
		posZ -= vec.y;
		return vec;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public void setPosZ(float posZ) {
		this.posZ = posZ;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	public void setVelZ(float velZ) {
		this.velZ = velZ;
	}

	public void setMovementSpeed(float movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	public void setJumpStrength(float jumpStrength) {
		this.jumpStrength = jumpStrength;
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
	
	
}

