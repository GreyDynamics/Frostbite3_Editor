package tk.greydynamics.Entity;

import org.lwjgl.util.vector.Vector3f;

public class LayerEntity extends Entity{

	public LayerEntity(String layerName) {
		super(layerName, Type.Layer, null, null, null, new Vector3f(0.0f, 0.0f, 0.0f));
	}

	@Override
	public void update() {		
	}

}
