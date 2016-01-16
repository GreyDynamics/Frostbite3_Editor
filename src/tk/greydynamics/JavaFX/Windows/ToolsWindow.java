package tk.greydynamics.JavaFX.Windows;

import java.io.IOException;

import org.lwjgl.opengl.Display;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.TreeViewEntry;
import tk.greydynamics.JavaFX.TreeViewUtils;
import tk.greydynamics.JavaFX.CellFactories.JavaFXexplorer1TCF;
import tk.greydynamics.JavaFX.CellFactories.JavaFXexplorerTCF;
import tk.greydynamics.JavaFX.CellFactories.JavaFXlayerTCF;
import tk.greydynamics.JavaFX.Controller.ToolsWindowController;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;

public class ToolsWindow {
	private Stage stage;
	private FXMLLoader loader;
	private ToolsWindowController controller;
	private Scene scene;
	private Parent parent;
	
	private TreeItem<TreeViewEntry> unfilteredExplorer1Root = null;
	private TreeItem<TreeViewEntry> filteredExplorer1Root = null;
	
	public ToolsWindow() {
		stage = new Stage();
		try {
			loader = new FXMLLoader(getClass().getResource("ToolsWindow.fxml"));
			parent = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scene = new Scene(parent, 300, 700);
		stage.setX(Display.getDesktopDisplayMode().getWidth()*0.01f);
		stage.setY(Display.getDesktopDisplayMode().getHeight()/2-(scene.getHeight()/2));
		stage.setTitle("Tools / Explorer");
		stage.getIcons().add(JavaFXHandler.ICON_APPLICATION16);
		stage.getIcons().add(JavaFXHandler.ICON_APPLICATION32);
		stage.setScene(scene);
		stage.hide();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				e.consume();
			}
		});
        controller.getExplorer().setCellFactory(new Callback<TreeView<TreeViewEntry>,TreeCell<TreeViewEntry>>(){
            @Override
            public TreeCell<TreeViewEntry> call(TreeView<TreeViewEntry> p) {
                return new JavaFXexplorerTCF();
            }
        });
        controller.getExplorer().setEditable(false);
        controller.getExplorer().setPrefWidth(Display.getDesktopDisplayMode().getWidth());
        controller.getExplorer().setPrefHeight(Display.getDesktopDisplayMode().getHeight()); //Back to top in TCF or what ?

        controller.getExplorer1().setCellFactory(new Callback<TreeView<TreeViewEntry>,TreeCell<TreeViewEntry>>(){
            @Override
            public TreeCell<TreeViewEntry> call(TreeView<TreeViewEntry> p) {
                return new JavaFXexplorer1TCF();
            }
        });
        
        controller.getExplorer1().setEditable(false);
                
        controller.getExplorer1().setPrefWidth(Display.getDesktopDisplayMode().getWidth());
        controller.getExplorer1().setPrefHeight(Display.getDesktopDisplayMode().getHeight());
        
        //controller.getLayer().getItems().addAll("Test","Testsss","Testtssst");
        controller.getLayer().valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.err.println("Old: "+oldValue+" New: "+newValue);
			}
		});
        
        controller.getVariationDatabase().valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				handleVariationDatabaseChange(newValue);
			}
		});
        /*controller.getVariationDatabase().setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				handleVariationDatabase(controller.getVariationDatabase().getValue());				
			}
		});*/
        
        
        controller.getLayerTreeView().setEditable(false);
        controller.getLayerTreeView().setPrefWidth(Display.getDesktopDisplayMode().getWidth());
        controller.getLayerTreeView().setPrefHeight(Display.getDesktopDisplayMode().getHeight()); //Back to top in TCF or what ?

        controller.getLayerTreeView().setCellFactory(new Callback<TreeView<Entity>,TreeCell<Entity>>(){
            @Override
            public TreeCell<Entity> call(TreeView<Entity> p) {
                return new JavaFXlayerTCF();
            }
        });
        
        controller.getConsiderPitchBox().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CheckBox considerBox = controller.getConsiderPitchBox();
				if (considerBox.isSelected()){
					Core.getRender().getCamera().setConsiderPitch(true);
				}else{
					Core.getRender().getCamera().setConsiderPitch(false);
				}
			}
		});
        controller.getMouseSensitivity().valueProperty().addListener(new ChangeListener<Number>() {
        	public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
        		double mouseSens = (double)new_val/500;
        		Core.getRender().getCamera().setMouseSensitivity((float)mouseSens);
        	}
        });
	}
	
	private void handleVariationDatabaseChange(String newValue){
		if (controller.getLayer().getValue()!=""&&controller.getLayer().getValue()!=null){
			String currentLayerName = controller.getLayer().getValue().split(" ")[0];
			String currentDatabaseName = controller.getVariationDatabase().getValue().split(" ")[0];
			Core.getJavaFXHandler().getDialogBuilder().showAsk("Question", "Do you want to assign "+currentDatabaseName+" to the current layer ?", new Runnable() {
				public void run() {
					//Pressed YES
					EntityLayer layer = Core.getGame().getEntityHandler().getEntityLayer(currentLayerName);
					EBXStructureFile variationDB = Core.getGame().getResourceHandler().getMeshVariationDatabaseHandler().getDatabaseByName(currentDatabaseName);
					if (layer==null||variationDB==null){
						Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Something went wrong!", null);
					}else{
						Core.runOnMainThread(new Runnable() {
							
							@Override
							public void run() {
								Core.getGame().getEntityHandler().updateLayer(layer, variationDB);
							}
						});
						
					}
				}
			}, new Runnable() {
				//Pressed NO
				@Override
				public void run() {
					Core.getJavaFXHandler().getDialogBuilder().showAsk("Question", "Do you want to DELETE "+currentDatabaseName+" then ?", new Runnable() {
						@Override
						public void run() {
							//Pressed YES
							Core.getGame().getResourceHandler().getMeshVariationDatabaseHandler().deleteDatabase(currentDatabaseName);
						}
					}, null);
				}
			});
		}
	}
	
	public void setExplorer1(TreeItem<TreeViewEntry> root, String str){
		if (root!=null){
			unfilteredExplorer1Root = root;
			filteredExplorer1Root = TreeViewUtils.filter(unfilteredExplorer1Root, str);
			controller.getExplorer1().setRoot(filteredExplorer1Root);
		}
		if (root==null){
			unfilteredExplorer1Root = null;
			filteredExplorer1Root = null;
			controller.getExplorer1().setRoot(unfilteredExplorer1Root);
		}
	}


	public Stage getStage() {
		return stage;
	}

	public FXMLLoader getLoader() {
		return loader;
	}

	public ToolsWindowController getController() {
		return controller;
	}

	public Scene getScene() {
		return scene;
	}

	public Parent getParent() {
		return parent;
	}
	
	
}
