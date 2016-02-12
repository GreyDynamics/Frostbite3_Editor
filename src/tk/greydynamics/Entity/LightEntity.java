package tk.greydynamics.Entity;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Model.RawModel;

public class LightEntity extends Entity{

	public LightEntity(String name, Object entityObject, Entity parent, RawModel[] rawModels,
			Vector3f minCoords, Vector3f maxCoords) {
		super(name, Type.Light, entityObject, parent, rawModels, minCoords, maxCoords);
	}
	public LightEntity(String name, Object entityObject, Entity parent, RawModel[] rawModels) {
		super(name, Type.Light, entityObject, parent, rawModels);
	}
	
	@Override
	public void update() {
		
	}

}
