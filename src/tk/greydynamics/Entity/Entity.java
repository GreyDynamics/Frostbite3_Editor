package tk.greydynamics.Entity;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entities.InstanceEntity;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Game.Core;
import tk.greydynamics.Maths.Matrices;
import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXLinearTransform;

public abstract class Entity {
	
	public static enum Type{
			Object, Light,
			Layer, Instance, Gizmo
	};

	private String name;
	private Type type;
	private Object entityObject;

	private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
	private Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);// stores value as radians (Math.toRadians(degrees));
	private Vector3f scaling = new Vector3f(1.0f, 1.0f, 1.0f);
	private Vector3f velocity = new Vector3f(0.0f, 0.0f, 0.0f);

	private Boolean isVisible = true;

	private Boolean highlighted = false;
	private Vector3f heighlightedColor = new Vector3f(0.5f, 0.0f, 0.0f);
	
	private Vector3f pickerColors = null;

	private Vector3f minCoords = new Vector3f(0.0f, 0.0f, 0.0f);
	private Vector3f maxCoords = new Vector3f(0.0f, 0.0f, 0.0f);
	private boolean showBoundingBox = false;

	private RawModel[] rawModels;
	
	private ArrayList<Entity> childrens = new ArrayList<>();
	private Entity parent = null;
	
	private Matrix4f absMatrix = null;
	private Matrix4f relMatrix = null;
	private boolean recalculateAbs = true;
	
	private int lastPokeTick = 0;
	
	private EntityLayer layer;

	public Entity(EntityLayer layer, String name, Type type, Object entityObject, Entity parent, RawModel[] rawModels, Vector3f parentPickingColors) {
		this.layer = layer;
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.rawModels = rawModels;
		this.entityObject = entityObject;
		recalculateRelMatrix();
		initPickingColors(parentPickingColors);
	}

	public Entity(EntityLayer layer, String name, Type type, Object entityObject, Entity parent, RawModel[] rawModels,
			Vector3f minCoords, Vector3f maxCoords, Vector3f parentPickingColors) {		
		this.layer = layer;
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.rawModels = rawModels;
		this.minCoords = minCoords;
		this.maxCoords = maxCoords;
		this.entityObject = entityObject;
		recalculateRelMatrix();
		initPickingColors(parentPickingColors);
	}

	public void changePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		recalculateRelMatrix();
	}

	public void changePosition(Vector3f relPos) {
		this.position.x += relPos.x;
		this.position.y += relPos.y;
		this.position.z += relPos.z;
		recalculateRelMatrix();
	}

	public void changeRotation(float dx, float dy, float dz) {
		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
		recalculateRelMatrix();
	}

	public void changeScaling(float dx, float dy, float dz) {
		this.scaling.x += dx;
		this.scaling.y += dy;
		this.scaling.z += dz;
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
		this.position.x -= vec.x;
		this.position.z += vec.y;
		recalculateRelMatrix();
		return vec;
	}

	public Vector2f moveForward(float distance) {
		Vector2f vec = new Vector2f(distance
				* (float) Math.sin(Math.toRadians(rotation.y)), distance
				* (float) Math.cos(Math.toRadians(rotation.y)));
		this.position.x += vec.x;
		this.position.z -= vec.y;
		recalculateRelMatrix();
		return vec;
	}

	public Vector2f moveLeft(float distance) {
		Vector2f vec = new Vector2f(distance
				* (float) Math.sin(Math.toRadians(rotation.y - 90)), distance
				* (float) Math.cos(Math.toRadians(rotation.y - 90)));
		this.position.x += vec.x;
		this.position.z -= vec.y;
		recalculateRelMatrix();
		return vec;
	}

	public Vector2f moveRight(float distance) {
		Vector2f vec = new Vector2f(distance
				* (float) Math.sin(Math.toRadians(rotation.y + 90)), distance
				* (float) Math.cos(Math.toRadians(rotation.y + 90)));
		this.position.x += vec.x;
		this.position.z -= vec.y;
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
		this.recalculateAbs = true;
		
		//Apply Transformation Data.
	    if (this instanceof InstanceEntity){
	        System.out.println("Applying Transformation to EBX: "+name);// "from "+layer.getName());
	        InstanceEntity instanceEntity = (InstanceEntity) this;
	        if (instanceEntity.getLinearTransformField()!=null){
//	        	System.out.println("DEBUG Entity Picking: "+instanceEntity.getLinearTransformField().getValueAsComplex().getComplexDescriptor().getName());
	        	EBXLinearTransform.setTransformation(this.getRelMatrix(), instanceEntity.getLinearTransformField().getValueAsComplex(), layer.getEBXWindow(), true/*tmp*/);
	        }
	    }
	}
	
	public void recalculateAbsMatrix(Matrix4f parentMtx){
		this.absMatrix = Matrix4f.mul(parentMtx, relMatrix, null);
	}
	
	public static Vector3f randomizedPickerColors(){
		return new Vector3f(Core.random.nextFloat(), Core.random.nextFloat(), Core.random.nextFloat());
	}

	public Vector3f getPickerColors() {
		return pickerColors;
	}
	public void initPickingColors(Vector3f parentPickingColors){
		if (parentPickingColors!=null){
			this.pickerColors = parentPickingColors;
		}else{
			this.pickerColors = randomizedPickerColors();
		}
	}

	public void setRawModels(RawModel[] rawModels) {
		this.rawModels = rawModels;
	}
	
	public void setPickerColors(Vector3f pickerColors) {
		this.pickerColors = pickerColors;
	}

	public EntityLayer getLayer() {
		return layer;
	}

	public void setLayer(EntityLayer layer) {
		this.layer = layer;
	}

	public void pokeRawModels(int currentTick){
		if (this.lastPokeTick<currentTick){
			//Only do it, every tick.
			this.lastPokeTick = currentTick;
			if (this.rawModels!=null){
				for (RawModel m : this.rawModels){
					m.poke();
				}
			}
		}
	}

	public abstract void update();

}
