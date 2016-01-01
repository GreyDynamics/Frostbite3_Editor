package tk.greydynamics.Entity;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;


public class ObjectEntity extends Entity{
	
	private EntityTextureData textureData;

	public ObjectEntity(String name, EBXStructureEntry structEntry, Entity parent, RawModel[] rawModels, EntityTextureData textureData) {
		super(name, Type.Object, structEntry, parent, rawModels);
		this.textureData = textureData;
	}
	public ObjectEntity(String name, EBXStructureEntry structEntry, Entity parent, RawModel[] rawModels, EntityTextureData textureData,
			Vector3f minCoords, Vector3f maxCoords) {
		super(name, Type.Object, structEntry, parent, rawModels, minCoords, maxCoords);
		this.textureData = textureData;
	}
	@Override
	public void update() {
	}
	public EntityTextureData getTextureData() {
		return textureData;
	}
}
