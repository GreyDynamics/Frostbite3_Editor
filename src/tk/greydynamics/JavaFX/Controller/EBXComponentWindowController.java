package tk.greydynamics.JavaFX.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import tk.greydynamics.JavaFX.Windows.EBXComponentWindow;

public class EBXComponentWindowController {
	@FXML
	private TreeView<Object> ebxComponentExplorer;
	
	private EBXComponentWindow window;
	private Stage stage;
	
	public EBXComponentWindow getWindow() {
		return window;
	}
	public void setWindow(EBXComponentWindow window) {
		this.window = window;
	}
	public Stage getStage() {
		return stage;
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public TreeView<Object> getEBXComponentExplorer() {
		return ebxComponentExplorer;
	}
	

}
