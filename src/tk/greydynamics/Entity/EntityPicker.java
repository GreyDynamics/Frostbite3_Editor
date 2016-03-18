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
			//Undo stuff
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
			Entity compareEntity = getPickedEntity(pickingColor, e, true);
			if (compareEntity!=null){
				newPickedEntity(compareEntity);
				return compareEntity;
			}
		}
		return null;
	}
	
	public Entity getPickedEntity(Vector3f pickingColor, Entity compareEntity, boolean checkChildrens){
		if (Math.abs((1-compareEntity.getPickerColors().getX()) - pickingColor.getX()) < FLOAT_PRECISION){
			if (Math.abs((1-compareEntity.getPickerColors().getY()) - pickingColor.getY()) < FLOAT_PRECISION){
				if (Math.abs((1-compareEntity.getPickerColors().getZ()) - pickingColor.getZ()) < FLOAT_PRECISION){
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
	
//	public Entity getMasterParent(Entity child){
//		if (child.getParent()!=null){
//			//Check if the parent is from same kind.
//			if (getPickedEntity(child.getPickerColors(), child.getParent(), false)!=null){
//				System.out.println("DEBUG: "+child.getParent().getName()+" "+child.getParent().getPosition());
//				return getMasterParent(child.getParent());
//			}else{
//				//the child we working with, is already the master parent we want!
//				System.out.println("Child is already masterparent!");
//				return child;
//			}
//		}
//		return child;
//	}

	public Entity getEntityPICKED() {
		return entityPICKED;
	}
	
}
