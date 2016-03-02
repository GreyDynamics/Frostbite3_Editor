package tk.greydynamics.JavaFX.Windows;

import java.io.IOException;

import org.lwjgl.opengl.Display;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.CellFactories.JavaFXebxTCF;
import tk.greydynamics.JavaFX.Controller.EBXWindowController;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;

public class EBXWindow {
	private FXMLLoader ebxWindowLoader = new FXMLLoader(EBXWindow.class.getResource("EBXWindow.fxml"));
	private EBXWindowController controller;
	private Parent parent;
	private Stage stage;
	private Scene scene;
	private EBXFile ebxFile;
	private boolean isOriginalFile;
	
	public EBXWindow(byte[] originalBytes, EBXFile ebxFile, String resLinkName, boolean isOriginal){
		this.isOriginalFile = isOriginal;
		this.ebxFile = ebxFile;
		try {
			controller = new EBXWindowController();
			ebxWindowLoader.setController(controller);
			parent = ebxWindowLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	    scene = new Scene(parent, 475, 700);
	    stage = new Stage();
	    stage.setScene(scene);
	    if (ebxFile==null){
	    	stage.setTitle("ERROR.");
	    }else{
	    	stage.setTitle(resLinkName);
	    }
	    /*stage.setX(Display.getDesktopDisplayMode().getWidth()*0.985f-scene.getWidth());
	    stage.setY(Display.getDesktopDisplayMode().getHeight()/2-(scene.getHeight()/2));*/
	    controller.setStage(stage);
	    
	    stage.getIcons().add(JavaFXHandler.ICON_APPLICATION16);
	    stage.getIcons().add(JavaFXHandler.ICON_APPLICATION32);
	    stage.show();
	    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Core.getJavaFXHandler().getMainWindow().destroyEBXWindow(stage);
			}
		});
	    controller.setWindow(this);
	    
	    controller.getEBXExplorer().setEditable(false);
	    controller.getEBXExplorer().setPrefWidth(Display.getDesktopDisplayMode().getWidth());
	    controller.getEBXExplorer().setPrefHeight(Display.getDesktopDisplayMode().getHeight());
	    
	    controller.getEBXExplorer().setCellFactory(new Callback<TreeView<Object>,TreeCell<Object>>(){
	        @Override
	        public TreeCell<Object> call(TreeView<Object> p) {
	            return new JavaFXebxTCF(controller, ebxFile, isOriginal);
	        }
	    });
	    
	    controller.update(ebxFile);
	    controller.setOriginalBytes(originalBytes);
	    if (originalBytes==null){
	    	controller.getSaveEBXMenuItem().setDisable(true);
	    }
	}

	public FXMLLoader getEbxWindowLoader() {
		return ebxWindowLoader;
	}

	public EBXWindowController getController() {
		return controller;
	}

	public Parent getParent() {
		return parent;
	}

	public Stage getStage() {
		return stage;
	}

	public Scene getScene() {
		return scene;
	}

	public EBXFile getEBXFile() {
		return ebxFile;
	}

	public boolean isOriginalFile() {
		return isOriginalFile;
	}
	
	
}
