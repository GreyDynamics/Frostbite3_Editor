package tk.greydynamics.Entity.Picker;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityPicker;

public class GizmoEntityPicker extends EntityPicker{
	@Override
	public void newPickedEntity(Entity newPickedEntity){
		this.setEntityOLD(this.getEntityPICKED());
		if (this.getEntityOLD()!=null){
			//Undo stuff
			this.getEntityOLD().setHighlighted(false);
		}
		this.setEntityPICKED(newPickedEntity);
	}
}
