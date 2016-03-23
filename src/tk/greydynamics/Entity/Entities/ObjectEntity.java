package tk.greydynamics.Entity.Entities;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityTextureData;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Model.RawModel;


public class ObjectEntity extends Entity{
	
	private EntityTextureData textureData;

	public ObjectEntity(EntityLayer layer, String name, Object entityObject, Entity parent, RawModel[] rawModels, EntityTextureData textureData, Vector3f parentPickingColors) {
		super(layer, name, Type.Object, entityObject, parent, rawModels, parentPickingColors);
		this.textureData = textureData;
	}
	public ObjectEntity(EntityLayer layer, String name, Object entityObject, Entity parent, RawModel[] rawModels, EntityTextureData textureData,
			Vector3f minCoords, Vector3f maxCoords, Vector3f parentPickingColors) {
		super(layer, name, Type.Object, entityObject, parent, rawModels, minCoords, maxCoords, parentPickingColors);
		this.textureData = textureData;
	}
	@Override
	public void update() {
	}
	public EntityTextureData getTextureData() {
		return textureData;
	}
}
