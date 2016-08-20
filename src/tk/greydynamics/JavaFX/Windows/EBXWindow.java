package tk.greydynamics.JavaFX.Windows;

import java.io.IOException;

import org.lwjgl.opengl.Display;

import javafx.application.Platform;
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
import tk.greydynamics.Entity.Layer.EntityLayer;
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
	
	private String name;
	
	private EntityLayer entityLayer;
	
	public EBXWindow(byte[] originalBytes, EBXFile ebxFile, String resLinkName, boolean isOriginal){
		this.name = resLinkName;
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
	    if (Core.singleEBXTool){
	    	stage.setTitle("EXTERNAL EBX Modification Toolkit");
	    }else if (ebxFile==null){
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
				controller.close();
				e.consume();
			}
		});
	    controller.setWindow(this);
	    
	    controller.getEBXExplorer().setEditable(false);
	    controller.getEBXExplorer().setPrefWidth(Display.getDesktopDisplayMode().getWidth());
	    controller.getEBXExplorer().setPrefHeight(Display.getDesktopDisplayMode().getHeight());
	    
	    setCellFactory(ebxFile, isOriginalFile);
	    
	    controller.update(ebxFile);
	    controller.setOriginalBytes(originalBytes);
	    if (originalBytes==null){
	    	controller.getSaveEBXMenuItem().setDisable(true);
	    }
	    if (Core.singleEBXTool){
	    	controller.getCompileEBXMenuItem().setVisible(false);
	    	controller.getLayerMenu().setVisible(false);
	    	controller.getEventMenu().setVisible(false);
	    }
	}
	public void setCellFactory(EBXFile ebxFile, boolean isOriginalFile){
		controller.getEBXExplorer().setCellFactory(new Callback<TreeView<Object>,TreeCell<Object>>(){
	        @Override
	        public TreeCell<Object> call(TreeView<Object> p) {
	            return new JavaFXebxTCF(controller, ebxFile, isOriginalFile);
	        }
	    });
	}
	public void refresh(){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				//Tested, its working and all is necessary to work!!
				TreeItem<Object> root = controller.getEBXExplorer().getRoot();
				int selectedItem = controller.getEBXExplorer().getSelectionModel().getSelectedIndex();
//				System.out.println(selectedItem);
				
				controller.getEBXExplorer().setRoot(null);
				controller.getEBXExplorer().setRoot(root);
				controller.getEBXExplorer().getSelectionModel().select(selectedItem);
				if (selectedItem>0){
					controller.getEBXExplorer().scrollTo(selectedItem);
				}
			}
		});
	}

	public void setEbxFile(EBXFile ebxFile) {
		this.ebxFile = ebxFile;
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

	public EntityLayer getEntityLayer() {
		return entityLayer;
	}

	public void setEntityLayer(EntityLayer entityLayer) {
		this.entityLayer = entityLayer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
