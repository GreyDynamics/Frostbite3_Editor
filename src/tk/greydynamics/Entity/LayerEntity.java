package tk.greydynamics.Entity;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Layer.EntityLayer;

public class LayerEntity extends Entity{

	public LayerEntity(EntityLayer layer, String layerName) {
		super(layer, layerName, Type.Layer, null, null, null, new Vector3f(0.0f, 0.0f, 0.0f));
	}

	@Override
	public void update() {		
	}

}
