package tk.greydynamics.Entity.Entities;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityTextureData;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Model.RawModel;

public class GizmoEntity extends Entity{
	private float alphaColor = 1.0f;
	
	public GizmoEntity(String name, Entity parent, RawModel[] rawModels, Vector3f parentPickingColors, float alphaColor) {
		super(null, name, Type.Gizmo, null, parent, rawModels, parentPickingColors);
		this.alphaColor = alphaColor;
	}

	@Override
	public void update() {		
	}

	public float getAlphaColor() {
		return alphaColor;
	}

	public void setAlphaColor(float alpha) {
		this.alphaColor = alpha;
	}

}
