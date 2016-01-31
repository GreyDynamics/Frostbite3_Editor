package tk.greydynamics.JavaFX.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import tk.greydynamics.JavaFX.Windows.ListSelectWindow;

public class ListSelectWindowController {
	@FXML
	private Button button1;
	@FXML
	private Button button2;
	@FXML
	private Label text;
	@FXML
	private ListView<String> list;
	@FXML
	private BorderPane base;
	
	private ListSelectWindow window;
	
	public ListSelectWindowController(ListSelectWindow window) {
		this.window = window;
	}
	public void button1Click(){
		this.window.setSelectedString(null);
		this.window.getStage().close();
	}
	public void button2Click(){
		this.window.setSelectedString(list.getSelectionModel().getSelectedItem());
		this.window.getStage().close();
	}
	
	public Button getButton1() {
		return button1;
	}
	public Button getButton2() {
		return button2;
	}
	public Label getText() {
		return text;
	}
	public ListView<String> getList() {
		return list;
	}

	public BorderPane getBase() {
		return base;
	}
	
}
