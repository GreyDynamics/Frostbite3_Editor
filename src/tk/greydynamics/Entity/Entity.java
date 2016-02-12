package tk.greydynamics.Entity;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Maths.Matrices;
import tk.greydynamics.Model.RawModel;

public abstract class Entity {
	
	public static enum Type{
			Object, Light,
			Layer
	};

	public String name;
	public Type type;
	public Object entityObject;

	public Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
	public Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
	public Vector3f scaling = new Vector3f(1.0f, 1.0f, 1.0f);
	public Vector3f velocity = new Vector3f(0.0f, 0.0f, 0.0f);

	public Boolean isVisible = true;

	public Boolean highlighted = false;
	public Vector3f heighlightedColor = new Vector3f(0.5f, 0.0f, 0.0f);

	public Vector3f minCoords = new Vector3f(0.0f, 0.0f, 0.0f);
	public Vector3f maxCoords = new Vector3f(0.0f, 0.0f, 0.0f);
	public boolean showBoundingBox = false;

	public RawModel[] rawModels;
	
	public ArrayList<Entity> childrens = new ArrayList<>();
	public Entity parent = null;
	
	public Matrix4f absMatrix = null;
	public Matrix4f relMatrix = null;
	public boolean recalculateAbs = true;

	public Entity(String name, Type type, Object entityObject, Entity parent, RawModel[] rawModels) {
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.rawModels = rawModels;
		this.entityObject = entityObject;
		recalculateRelMatrix();
	}

	public Entity(String name, Type type, Object entityObject, Entity parent, RawModel[] rawModels,
			Vector3f minCoords, Vector3f maxCoords) {		
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.rawModels = rawModels;
		this.minCoords = minCoords;
		this.maxCoords = maxCoords;
		this.entityObject = entityObject;
		recalculateRelMatrix();
	}

	public void changePosition(float dx, float dy, float dz) {
		position.x += dx;
		position.y += dy;
		position.z += dz;
		recalculateRelMatrix();
	}

	public void changePosition(Vector3f relPos) {
		position.x += relPos.x;
		position.y += relPos.y;
		position.z += relPos.z;
		recalculateRelMatrix();
	}

	public void changeRotation(float dx, float dy, float dz) {
		rotation.x += dx;
		rotation.y += dy;
		rotation.z += dz;
		recalculateRelMatrix();
	}

	public void changeScaling(float dx, float dy, float dz) {
		scaling.x += dx;
		scaling.y += dy;
		scaling.z += dz;
		recalculateRelMatrix();
	}

	public void changeVelocity(float relX, float relY, float relZ) {
		this.velocity.x += relX;
		this.velocity.y += relY;
		this.velocity.z += relZ;
		recalculateRelMatrix();
	}

	public void changeVelocity(Vector3f relVel) {
		this.velocity.x += relVel.x;
		this.velocity.y += relVel.y;
		this.velocity.z += relVel.z;
		recalculateRelMatrix();
	}

	public ArrayList<Entity> getChildrens() {
		return childrens;
	}

	public Vector3f getHeighlightedColor() {
		return heighlightedColor;
	}

	public Boolean getHighlighted() {
		return highlighted;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public Vector3f getMaxCoords() {
		return maxCoords;
	}

	public Vector3f getMinCoords() {
		return minCoords;
	}

	public String getName() {
		return name;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getScaling() {
		return scaling;
	}



	public Vector3f getVelocity() {
		return velocity;
	}

	public boolean isShowBoundingBox() {
		return showBoundingBox;
	}

	public Vector2f moveBackwards(float distance) {
		Vector2f vec = new Vector2f(distance
				* (float) Math.sin(Math.toRadians(rotation.y)), distance
				* (float) Math.cos(Math.toRadians(rotation.y)));
		position.x -= vec.x;
		position.z += vec.y;
		recalculateRelMatrix();
		return vec;
	}

	public Vector2f moveForward(float distance) {
		Vector2f vec = new Vector2f(distance
				* (float) Math.sin(Math.toRadians(rotation.y)), distance
				* (float) Math.cos(Math.toRadians(rotation.y)));
		position.x += vec.x;
		position.z -= vec.y;
		recalculateRelMatrix();
		return vec;
	}

	public Vector2f moveLeft(float distance) {
		Vector2f vec = new Vector2f(distance
				* (float) Math.sin(Math.toRadians(rotation.y - 90)), distance
				* (float) Math.cos(Math.toRadians(rotation.y - 90)));
		position.x += vec.x;
		position.z -= vec.y;
		recalculateRelMatrix();
		return vec;
	}

	public Vector2f moveRight(float distance) {
		Vector2f vec = new Vector2f(distance
				* (float) Math.sin(Math.toRadians(rotation.y + 90)), distance
				* (float) Math.cos(Math.toRadians(rotation.y + 90)));
		position.x += vec.x;
		position.z -= vec.y;
		recalculateRelMatrix();
		return vec;
	}

	public void setHeighlightedColor(Vector3f heighlightedColor) {
		this.heighlightedColor = heighlightedColor;
	}

	public void setHighlighted(Boolean highlighted) {
		this.highlighted = highlighted;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setMaxCoords(Vector3f maxCoords) {
		this.maxCoords = maxCoords;
	}

	public void setMinCoords(Vector3f minCoords) {
		this.minCoords = minCoords;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		recalculateRelMatrix();
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
		recalculateRelMatrix();
	}

	public void setScaling(Vector3f scaling) {
		this.scaling = scaling;
		recalculateRelMatrix();
	}
	
	public void setShowBoundingBox(boolean showBoundingBox) {
		this.showBoundingBox = showBoundingBox;
	}
	
	public void setVelocity(float velX, float velY, float velZ) {
		this.velocity = new Vector3f(velX, velY, velZ);
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public void toggleHighlighted() {
		if (this.highlighted) {
			this.highlighted = false;
		} else {
			this.highlighted = true;
		}
	}
	
	
	public void toggleVisibility() {
		if (this.isVisible) {
			this.isVisible = false;
		} else {
			this.isVisible = true;
		}
	}
	
	

	public RawModel[] getRawModels() {
		return rawModels;
	}


	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}



	public Object getEntityObject() {
		return entityObject;
	}

	public void setEntityObject(Object entityObject) {
		this.entityObject = entityObject;
	}

	public Type getType() {
		return type;
	}
	
	

	public Matrix4f getAbsMatrix() {
		return absMatrix;
	}

	public boolean isRecalculateAbs() {
		return recalculateAbs;
	}

	public Matrix4f getRelMatrix() {
		return relMatrix;
	}

	public void setRecalculateAbs(boolean recalculateAbs) {
		this.recalculateAbs = recalculateAbs;
	}
	
	public void recalculateRelMatrix(){
		this.relMatrix =  Matrices.createTransformationMatrix(position,
				rotation, scaling);
	}
	
	public void recalculateAbsMatrix(Matrix4f parentMtx){
		this.absMatrix = Matrix4f.mul(parentMtx, relMatrix, null);
	}

	public abstract void update();

}
