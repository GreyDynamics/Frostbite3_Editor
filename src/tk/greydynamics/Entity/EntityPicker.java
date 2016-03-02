package tk.greydynamics.Entity;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Layer.EntityLayer;

public class EntityPicker {
	public static float FLOAT_PRECISION = 0.01f;
	
	private Entity entityOLD = null;
	private Entity entityPICKED = null;
	
	private void newPickedEntity(Entity newPickedEntity){
		this.entityOLD = this.entityPICKED;
		if (this.entityOLD!=null){
			this.entityOLD.setHighlighted(false);
		}
		this.entityPICKED = newPickedEntity;
	}
	
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
			Entity compareEntity = getPickedEntity(pickingColor, e);
			if (compareEntity!=null){
				newPickedEntity(compareEntity);
				return compareEntity;
			}
		}
		return null;
	}
	
	public Entity getPickedEntity(Vector3f pickingColor, Entity compareEntity){
		if (Math.abs((1-compareEntity.getPickerColors().getX()) - pickingColor.getX()) < FLOAT_PRECISION){
			if (Math.abs((1-compareEntity.getPickerColors().getY()) - pickingColor.getY()) < FLOAT_PRECISION){
				if (Math.abs((1-compareEntity.getPickerColors().getZ()) - pickingColor.getZ()) < FLOAT_PRECISION){
					newPickedEntity(compareEntity);
					return compareEntity;
				}
			}
		}
		return getPickedEntity(pickingColor, compareEntity.getChildrens());
	}

	public Entity getEntityPICKED() {
		return entityPICKED;
	}
	
}
