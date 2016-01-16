package tk.greydynamics.JavaFX.CellFactories;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import tk.greydynamics.Resource.Frostbite3.EBX.Component.EBXComponentComplex;

public class JavaFXebxComponentTCF extends TreeCell<Object> {
	private TextField textField;

	private boolean isOriginal;
	public JavaFXebxComponentTCF(EBXComponentComplex ebxComponent, boolean isOriginal) {
		this.isOriginal = isOriginal;
	}

	@Override
	public void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				setText(null);
				setGraphic(textField);
			} else {
				if (item != null){
					setText("test");
					//setText(item.getName()+":"+item.getType().toString());
					//setGraphic(getTreeItem().getValue().getGraphic());
				}
			}
		}
	}
}
