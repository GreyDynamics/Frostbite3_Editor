package tk.greydynamics.Render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityPicker;
import tk.greydynamics.Entity.Entities.GizmoEntity;
import tk.greydynamics.Entity.Entities.ObjectEntity;
import tk.greydynamics.Entity.Picker.GizmoEntityPicker;
import tk.greydynamics.Model.ModelHandler;
import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Render.GizmoHandler.GizmoType;

public class GizmoHandler {
	public static enum GizmoType {GIZMO_MOVE, GIZMO_ROTATE, GIZMO_SCALE};
	public static final String GIZMOROOTNAME = "GIZMO_ROOT";
	private RawModel moveRawModel = null;
	private RawModel rotateRawModel = null;
	private RawModel scaleRawModel = null;
	private RawModel sphereRawModel = null;
	
	private GizmoType currentGizmoType = GizmoType.GIZMO_MOVE;
	
	private Entity gizmoEntity = new ObjectEntity(null, GIZMOROOTNAME, null, null, null, null, null);
	
	private GizmoEntityPicker gizmoPicker = new GizmoEntityPicker();
		
	public Entity pick(Vector3f color){
		return this.gizmoPicker.getPickedEntity(color, gizmoEntity, true);
	}
	public void init(ModelHandler modelHandler){
		this.moveRawModel = modelHandler.getLoader().loadOBJSimple("res/model/gizmo/move.obj", GL11.GL_TRIANGLES);
		this.moveRawModel.setLifeTicks(RawModel.LIFETIME_INFINITE);
		
		this.rotateRawModel = modelHandler.getLoader().loadOBJSimple("res/model/gizmo/rotate.obj", GL11.GL_TRIANGLES);
		this.rotateRawModel.setLifeTicks(RawModel.LIFETIME_INFINITE);
		
		this.scaleRawModel = modelHandler.getLoader().loadOBJSimple("res/model/gizmo/scale.obj", GL11.GL_TRIANGLES);
		this.scaleRawModel.setLifeTicks(RawModel.LIFETIME_INFINITE);
		
		this.sphereRawModel = modelHandler.getLoader().loadOBJSimple("res/model/gizmo/sphere.obj", GL11.GL_TRIANGLES);
		this.sphereRawModel.setLifeTicks(RawModel.LIFETIME_INFINITE);
		
		gizmoEntity.getChildrens().add(buildMoveEntity(this.moveRawModel));
		gizmoEntity.getChildrens().add(buildRotateEntity(this.rotateRawModel, this.sphereRawModel));
		gizmoEntity.getChildrens().add(buildScaleEntity(this.scaleRawModel));
	}
	
	private Entity buildMoveEntity(RawModel moveRawModel){
		Entity moveEntity = new GizmoEntity(GizmoType.GIZMO_MOVE.toString(), gizmoEntity, null, null, 1.0f);
		
		Entity moveX = new GizmoEntity(GizmoType.GIZMO_MOVE.toString()+"_X", moveEntity, new RawModel[] {moveRawModel}, null, 1.0f);
			moveX.setRotation(new Vector3f(0.0f, 0.0f, (float) Math.toRadians(-90.0f)), true);
		moveEntity.getChildrens().add(moveX);
			
		Entity moveY = new GizmoEntity(GizmoType.GIZMO_MOVE.toString()+"_Y", moveEntity, new RawModel[] {moveRawModel}, null, 1.0f);
			moveY.setRotation(new Vector3f(0.0f, 0.0f, 0.0f), true);
		moveEntity.getChildrens().add(moveY);
		
		Entity moveZ = new GizmoEntity(GizmoType.GIZMO_MOVE.toString()+"_Z", moveEntity, new RawModel[] {moveRawModel}, null, 1.0f);
			moveZ.setRotation(new Vector3f((float) Math.toRadians(90.0f), 0.0f, 0.0f), true);
		moveEntity.getChildrens().add(moveZ);
		
		return moveEntity;
	}
	
	private Entity buildRotateEntity(RawModel rotateRawModel, RawModel sphereRawModel){
		Entity rotateEntity = new GizmoEntity(GizmoType.GIZMO_ROTATE.toString(), gizmoEntity, null, null, 1.0f);
		
		Entity sphere = new GizmoEntity(GizmoType.GIZMO_ROTATE.toString()+"_Sphere", rotateEntity, new RawModel[] {sphereRawModel}, null, 0.2f);
		sphere.setScaleing(new Vector3f(0.9f, 0.9f, 0.9f), true);
		rotateEntity.getChildrens().add(sphere);
		
		Entity rotateX = new GizmoEntity(GizmoType.GIZMO_ROTATE.toString()+"_X", rotateEntity, new RawModel[] {rotateRawModel}, null, 1.0f);
		rotateX.setRotation(new Vector3f(0.0f, (float) Math.toRadians(90.0f), 0.0f), true);
			rotateX.setScaleing(new Vector3f(1.02f, 1.02f, 1.02f), true);
			rotateEntity.getChildrens().add(rotateX);
			
		Entity rotateY = new GizmoEntity(GizmoType.GIZMO_ROTATE.toString()+"_Y", rotateEntity, new RawModel[] {rotateRawModel}, null, 1.0f);
			rotateY.setRotation(new Vector3f((float) Math.toRadians(90.0f), 0.0f, 0.0f), true);
			rotateY.setScaleing(new Vector3f(1.01f, 1.01f, 1.01f), true);
			rotateEntity.getChildrens().add(rotateY);
		
		Entity rotateZ = new GizmoEntity(GizmoType.GIZMO_ROTATE.toString()+"_Z", rotateEntity, new RawModel[] {rotateRawModel}, null, 1.0f);
			rotateZ.setRotation(new Vector3f(0.0f, 0.0f, 0.0f), true);
			rotateEntity.getChildrens().add(rotateZ);
		
		
		return rotateEntity;
	}
	
	private Entity buildScaleEntity(RawModel scaleRawModel){
		Entity scaleEntity = new GizmoEntity(GizmoType.GIZMO_SCALE.toString(), gizmoEntity, null, null, 1.0f);
		
		Entity scaleX = new GizmoEntity(GizmoType.GIZMO_SCALE.toString()+"_X", scaleEntity, new RawModel[] {scaleRawModel}, null, 1.0f);
			scaleX.setRotation(new Vector3f(0.0f, 0.0f, (float) Math.toRadians(90.0f)), true);
			scaleEntity.getChildrens().add(scaleX);
			
		Entity scaleY = new GizmoEntity(GizmoType.GIZMO_SCALE.toString()+"_Y", scaleEntity, new RawModel[] {scaleRawModel}, null, 1.0f);
			scaleY.setRotation(new Vector3f(0.0f, 0.0f, 0.0f), true);
			scaleEntity.getChildrens().add(scaleY);
		
		Entity scaleZ = new GizmoEntity(GizmoType.GIZMO_SCALE.toString()+"_Z", scaleEntity, new RawModel[] {scaleRawModel}, null, 1.0f);
			scaleZ.setRotation(new Vector3f((float) Math.toRadians(90.0f), 0.0f, 0.0f), true);
			scaleEntity.getChildrens().add(scaleZ);
		
		return scaleEntity;
	}

	public Entity getGizmoEntity() {
		return gizmoEntity;
	}

	public GizmoEntityPicker getGizmoPicker() {
		return gizmoPicker;
	}
	public GizmoType getCurrentGizmoType() {
		return currentGizmoType;
	}
	public void setCurrentGizmoType(GizmoType currentGizmoType) {
		this.currentGizmoType = currentGizmoType;
	}
}
