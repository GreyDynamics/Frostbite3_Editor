package tk.greydynamics.Entity;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Layer.EntityLayer;

public abstract class EntityPicker {
	public static float FLOAT_PRECISION = 0.01f;
	
	private Entity entityOLD = null;
	private Entity entityPICKED = null;
	
	public abstract void newPickedEntity(Entity newPickedEntity);
	
	
	public Entity getPickedEntityFromLayers(Vector3f pickingColor, ArrayList<EntityLayer> layers){
		for (EntityLayer layer : layers){
			Entity en = getPickedEntity(pickingColor, layer.getEntities());
			if (en!=null){
				newPickedEntity(en);
				return en;
			}
		}
		newPickedEntity(null);
		return null;
	}
	
	public Entity getPickedEntity(Vector3f pickingColor, EntityLayer layer){
		return getPickedEntity(pickingColor, layer.getEntities());
	}
	
	public Entity getPickedEntity(Vector3f pickingColor, ArrayList<Entity> entites){
		for (Entity e : entites){
			Entity compareEntity = getPickedEntity(pickingColor, e, true);
			if (compareEntity!=null){
				newPickedEntity(compareEntity);
				return compareEntity;
			}
		}
		return null;
	}
	
	public Entity getPickedEntity(Vector3f pickingColor, Entity compareEntity, boolean checkChildrens){
//		System.out.println("Compare "+pickingColor+" with "+compareEntity.getPickerColors());
		if (Math.abs((compareEntity.getPickerColors().getX()) - pickingColor.getX()) < FLOAT_PRECISION){
			if (Math.abs((compareEntity.getPickerColors().getY()) - pickingColor.getY()) < FLOAT_PRECISION){
				if (Math.abs((compareEntity.getPickerColors().getZ()) - pickingColor.getZ()) < FLOAT_PRECISION){
					newPickedEntity(compareEntity);
					return compareEntity;
				}
			}
		}
		if (checkChildrens){
			return getPickedEntity(pickingColor, compareEntity.getChildrens());
		}
		return null;
	}

	public Entity getEntityPICKED() {
		return entityPICKED;
	}


	public Entity getEntityOLD() {
		return entityOLD;
	}


	public void setEntityOLD(Entity entityOLD) {
		this.entityOLD = entityOLD;
	}


	public void setEntityPICKED(Entity entityPICKED) {
		this.entityPICKED = entityPICKED;
	}
	
}
