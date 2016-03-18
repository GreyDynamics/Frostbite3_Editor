package tk.greydynamics.Entity.Layer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityPicker;
import tk.greydynamics.JavaFX.Windows.EBXWindow;

public class EntityLayer {
	private String name;
	private ArrayList <Entity> entities;
	private EBXWindow ebxWindow;

	public EntityLayer(String name, EBXWindow ebxWindow) {
		this.name = name;
		this.entities = new ArrayList <Entity>();
		this.ebxWindow = ebxWindow;
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

	public EBXWindow getEBXWindow() {
		return ebxWindow;
	}

	public void setEBXWindow(EBXWindow ebxWindow) {
		this.ebxWindow = ebxWindow;
	}
}
