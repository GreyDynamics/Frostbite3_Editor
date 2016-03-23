package tk.greydynamics.Entity.Entities;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;


public class InstanceEntity extends Entity{
	
	private EBXField linearTransformField;

	public InstanceEntity(EntityLayer layer, String name, Object entityObject, Entity parent, Vector3f parentPickingColors, EBXField linearTransformField) {
		super(layer, name, Type.Instance, entityObject, parent, null, parentPickingColors);
		this.linearTransformField = linearTransformField;
	}
	@Override
	public void update() {
	}
	public EBXField getLinearTransformField() {
		return linearTransformField;
	}
	public void setLinearTransformField(EBXField linearTransformField) {
		this.linearTransformField = linearTransformField;
	}
	
}
