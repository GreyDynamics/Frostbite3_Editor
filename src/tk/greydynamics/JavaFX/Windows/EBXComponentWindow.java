package tk.greydynamics.JavaFX.Windows;

import java.io.IOException;

import org.lwjgl.opengl.Display;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.TreeViewConverter;
import tk.greydynamics.JavaFX.TreeViewEntry;
import tk.greydynamics.JavaFX.CellFactories.JavaFXebxComponentTCF;
import tk.greydynamics.JavaFX.CellFactories.JavaFXebxTCF;
import tk.greydynamics.JavaFX.Controller.EBXComponentWindowController;
import tk.greydynamics.JavaFX.Controller.EBXWindowController;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Component.EBXComponentComplex;

public class EBXComponentWindow {
	private FXMLLoader ebxComponentWindowLoader = new FXMLLoader(EBXComponentWindow.class.getResource("EBXComponentWindow.fxml"));
	private EBXComponentWindowController controller;
	private Parent parent;
	private Stage stage;
	private Scene scene;
	private EBXComponentComplex ebxComponent;
	private boolean isOriginalFile;

	public EBXComponentWindow(EBXComponentComplex ebxComponent, String resLinkName, boolean isOriginal){
		this.isOriginalFile = isOriginal;
		this.ebxComponent = ebxComponent;
		try {
			controller = new EBXComponentWindowController();
			ebxComponentWindowLoader.setController(controller);
			parent = ebxComponentWindowLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	    scene = new Scene(parent, 475, 700);
	    stage = new Stage();
	    stage.setScene(scene);
	    if (ebxComponent==null){
	    	stage.setTitle("EBX Component Window - NO FILE");
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
				Core.getJavaFXHandler().getMainWindow().destroyEBXComponentWindow(stage);
			}
		});
	    controller.setWindow(this);
	    
	    controller.getEBXComponentExplorer().setEditable(true);
	    controller.getEBXComponentExplorer().setPrefWidth(Display.getDesktopDisplayMode().getWidth());
	    controller.getEBXComponentExplorer().setPrefHeight(Display.getDesktopDisplayMode().getHeight());
	    
	    controller.getEBXComponentExplorer().setCellFactory(new Callback<TreeView<Object>,TreeCell<Object>>(){
	        @Override
	        public TreeCell<Object> call(TreeView<Object> p) {
	            return new JavaFXebxComponentTCF(ebxComponent, isOriginal);
	        }
	    });
	    
	    TreeItem<Object> ebxComponentTreeView = null;
	    if (ebxComponent!=null){
	    	//ebxComponentTreeView = TreeViewConverter.getTreeView(ebxComponent);
	    	System.err.println("TODO CONTENT EBX COMPONENT WINDOW");
	    }
	    controller.getEBXComponentExplorer().setRoot(ebxComponentTreeView);
	}



	public EBXComponentWindowController getController() {
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

	public EBXComponentComplex getEBXComponent() {
		return ebxComponent;
	}
	
	public boolean isOriginalFile() {
		return isOriginalFile;
	}
	
	
	
}
