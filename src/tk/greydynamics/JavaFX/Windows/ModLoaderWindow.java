package tk.greydynamics.JavaFX.Windows;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.CellFactories.ModLoaderListFactory;
import tk.greydynamics.JavaFX.Controller.ModLoaderController;
import tk.greydynamics.Mod.Mod;

public class ModLoaderWindow {
	private Stage stage;
	private Scene scene;
	private FXMLLoader loader;
	private Parent parent;
	private ModLoaderController controller;
	
	
	
	public ModLoaderWindow() {
		try { 
			loader = new FXMLLoader(getClass().getResource("ModLoaderWindow.fxml")); //not static to access controller class
			parent = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stage = new Stage();
        Scene sceneModLoader = new Scene(parent, 800, 600);
        stage.setTitle("FrostBite 3 Tools "+Core.buildVersion);
        stage.setScene(sceneModLoader);
        stage.getIcons().add(JavaFXHandler.ICON_APPLICATION16);
        stage.getIcons().add(JavaFXHandler.ICON_APPLICATION32);
        stage.hide();
        stage.setResizable(false);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Core.keepAlive(false);
			}
		});
        
        controller.getList().setCellFactory(new Callback<ListView<Mod>, ListCell<Mod>>() {
	        @Override 
	        public ListCell<Mod> call(ListView<Mod> list) {
	            return new ModLoaderListFactory();
	        }
        });
        controller.getRunEditor().setDisable(true);
	}



	public Stage getStage() {
		return stage;
	}



	public Scene getScene() {
		return scene;
	}



	public FXMLLoader getLoader() {
		return loader;
	}



	public Parent getParent() {
		return parent;
	}



	public ModLoaderController getController() {
		return controller;
	}
	
	
}
