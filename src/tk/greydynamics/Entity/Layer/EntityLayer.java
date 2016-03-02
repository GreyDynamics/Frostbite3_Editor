package tk.greydynamics.Entity.Layer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityPicker;

public class EntityLayer {
	private String name;
	private ArrayList <Entity> entities;

	public EntityLayer(String name) {
		this.name = name;
		this.entities = new ArrayList <Entity>();
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
