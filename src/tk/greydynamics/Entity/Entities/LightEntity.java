package tk.greydynamics.Entity.Entities;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Model.RawModel;

public class LightEntity extends Entity{

	public LightEntity(EntityLayer layer, String name, Object entityObject, Entity parent, RawModel[] rawModels,
			Vector3f minCoords, Vector3f maxCoords, Vector3f parentPickingColors) {
		super(layer, name, Type.Light, entityObject, parent, rawModels, minCoords, maxCoords, parentPickingColors);
	}
	public LightEntity(EntityLayer layer, String name, Object entityObject, Entity parent, RawModel[] rawModels, Vector3f parentPickingColors) {
		super(layer, name, Type.Light, entityObject, parent, rawModels, parentPickingColors);
	}
	
	@Override
	public void update() {
		
	}

}
