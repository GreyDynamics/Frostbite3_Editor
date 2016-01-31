package tk.greydynamics.JavaFX.Windows;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.Controller.ListSelectWindowController;

public class ListSelectWindow {
	private Stage stage;
	private FXMLLoader loader;
	private Parent parent;
	private Scene scene;
	private ListSelectWindowController controller;
	private String selectedString = null;
	
	public ListSelectWindow(String title, String text, ArrayList<String> items, Integer width, Integer height) {
		
		try{ 
			loader = new FXMLLoader(getClass().getResource("ListSelectWindow.fxml"));
			controller = new ListSelectWindowController(this);
			loader.setController(controller);
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stage = new Stage();
	    scene = new Scene(parent, width, height);//controller.getBase().getWidth(), controller.getBase().getHeight()
	    stage.setTitle(title);
	    stage.setScene(scene);
	    stage.getIcons().add(JavaFXHandler.ICON_APPLICATION16);
	    stage.getIcons().add(JavaFXHandler.ICON_APPLICATION32);
	    stage.setResizable(false);
	    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				controller.button1Click();
			}
		});
	    controller.getButton1().setText("Cancel");
	    controller.getButton2().setText("Select");
	    controller.getText().setText(text);
	    for (String s : items){
	    	controller.getList().getItems().add(s);
	    }
	    stage.showAndWait();
	}

	public Stage getStage() {
		return stage;
	}

	public FXMLLoader getLoader() {
		return loader;
	}

	public Parent getParent() {
		return parent;
	}

	public Scene getScene() {
		return scene;
	}

	public ListSelectWindowController getController() {
		return controller;
	}

	public String getSelectedString() {
		return selectedString;
	}

	public void setSelectedString(String selectedString) {
		this.selectedString = selectedString;
	}



	
	
	
    
}
