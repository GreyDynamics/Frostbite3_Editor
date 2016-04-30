package tk.greydynamics.JavaFX.Windows;

import java.io.File;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.TreeViewConverter;
import tk.greydynamics.JavaFX.TreeViewEntry;
import tk.greydynamics.JavaFX.CellFactories.ModLoaderListFactory;
import tk.greydynamics.Mod.Mod;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Component.EBXComponentComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;


public class MainWindow extends Application{
	
	public static enum EntryType{
		//General
		STRING, INTEGER, LONG, BOOL, FLOAT, DOUBLE, UINTEGER,
		LIST, BYTE, NULL, SHORT,
		
		//Frostite specific.
		ARRAY, SHA1, GUID, ENUM, HEX8, RAW, CHUNKGUID, RAW2,
		
		//Layer Specific.
		EntityLayer, ObjectEntity, LightEntity,	
	};
	
	public static enum WorkDropType { DROP_INTO, REORDER };

	private ArrayList<EBXWindow> ebxWindows;
	private ArrayList<EBXComponentWindow> ebxComponentWindows;
	private ArrayList<ImagePreviewWindow> imagePreviewWindows;
	private ArrayList<EventGraphWindow> eventGraphWindows;
	private ModLoaderWindow modLoaderWindow = null;
	private ToolsWindow toolsWindow;


	/*---------START--------------*/
	public void runApplication(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				launchApplication();
			}
		}).start();
	}
		
	void launchApplication(){
		launch();
		System.err.println("JavaFX Application closed.");
	}

	@Override
	public void start(Stage stageLeft) {
		ebxWindows = new ArrayList<>();
		ebxComponentWindows = new ArrayList<>();
		imagePreviewWindows = new ArrayList<>();
		eventGraphWindows = new ArrayList<>();
		modLoaderWindow = new ModLoaderWindow();
		toolsWindow = new ToolsWindow();
		Core.getJavaFXHandler().setMainWindow(this);
	}
	
	public boolean createEBXWindow(byte[] originalBytes, EBXFile ebxFile, String resName, boolean isOriginal){
		try{
			Platform.runLater(new Runnable() {
				public void run() {
					EBXWindow window = new EBXWindow(originalBytes, ebxFile, resName, isOriginal);
					ebxWindows.add(window);
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("EBXWindow creation failed!");
			return false;
		}
		return true;
	}
	
	public boolean destroyEBXWindow(Stage stage){
		try{Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				for (EBXWindow window : ebxWindows){
					if (window.getStage()==stage){
						if (window.getEntityLayer()!=null){
							Core.getGame().getEntityHandler().destroyEntityLayer(window.getEntityLayer());
						}
						stage.close();
						ebxWindows.remove(window);
						break;
					}
				}
			}
		});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("EBXWindow could not get destroyed!");
			return false;
		}
		return true;
	}
	
	public void destroyEBXWindows(){
		for (EBXWindow window : ebxWindows){
			destroyEBXWindow(window.getStage());
		}
	}
	public boolean createEBXComponentWindow(EBXComponentComplex ebxComponent, String resName, boolean isOriginal){
		try{
			Platform.runLater(new Runnable() {
				public void run() {
					EBXComponentWindow window = new EBXComponentWindow(ebxComponent, resName, isOriginal);
					ebxComponentWindows.add(window);
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("EBXComponentWindow creation failed!");
			return false;
		}
		return true;
	}
	public boolean destroyEBXComponentWindow(Stage stage){
		try{Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				for (EBXComponentWindow window : ebxComponentWindows){
					if (window.getStage()==stage){
						stage.close();
						ebxComponentWindows.remove(window);
						break;
					}
				}
			}
		});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("EBXComponentWindow could not get destroyed!");
			return false;
		}
		return true;
	}
	public void destroyEBXComponentWindows(){
		for (EBXComponentWindow ebxComponentWindow : ebxComponentWindows){
			destroyEBXComponentWindow(ebxComponentWindow.getStage());
		}
	}
	
	public void destroyEventGraphWindows(){
		for (EventGraphWindow eventGraphWindow : eventGraphWindows){
			destroyEventGraphWindow(eventGraphWindow.getStage());
		}
	}
	
	public boolean createEventGraphWindow(EBXFile ebxFile, boolean isOriginal, boolean tryLoad, boolean loadOriginal){
		try{
			Platform.runLater(new Runnable() {
				public void run() {
					EventGraphWindow window = new EventGraphWindow(ebxFile, isOriginal, tryLoad, loadOriginal);
					if (window.isSurvivable()){
						eventGraphWindows.add(window);
					}else{
						if (window.getStage()!=null){
							window.getStage().close();
						}
					}
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("EventGraphWindow creation failed!");
			return false;
		}
		return true;
	}
	public boolean destroyEventGraphWindow(Stage stage){
		try{Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				for (EventGraphWindow window : eventGraphWindows){
					if (window.getStage()==stage){
						stage.close();
						eventGraphWindows.remove(window);
						break;
					}
				}
			}
		});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("EventGraphWindow could not get destroyed!");
			return false;
		}
		return true;
	}
	
	public boolean createImagePreviewWindow(File file, File ddsFile, Object object, String title){
		try{
			Platform.runLater(new Runnable() {
				public void run() {
					ImagePreviewWindow ipw = new ImagePreviewWindow(file, ddsFile, object, title);
					ipw.getController().setParentStage(ipw.getStage());
					imagePreviewWindows.add(ipw);
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("EBX Window could not get created!");
			return false;
		}
		return true;
	}
	
	public boolean destroyImagePreviewWindow(Stage stage){
		try{Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (ImagePreviewWindow window : imagePreviewWindows){
					if (window.getStage()==stage){
						stage.close();
						imagePreviewWindows.remove(window);
						break;
					}
				}
				System.err.println("ImagePreviewWindow's stage not found!");
			}
		});
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("ImagePreviewWindow could not get destroyed!");
			return false;
		}
		return true;
	}
	
	

	/*UPDATE METHODS*/	
	public void setPackageExplorer(TreeItem<TreeViewEntry> treeview){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				toolsWindow.getController().getExplorer().setRoot(treeview);
				toolsWindow.getController().getExplorer().scrollTo(0);
				if (toolsWindow.getController().getExplorer().getRoot() != null){
					toolsWindow.getController().getExplorer().getRoot().setExpanded(true);
				}
			}
		});	
	}
	
	public void setPackageExplorerBackground(Color color){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (color!=null){
					toolsWindow.getController().getExplorer().setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
				}else{
					toolsWindow.getController().getExplorer().setBackground(null);
				}
			}
		});	
	}
	public void setPackageExplorer1Background(Color color){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.err.println("Background for Explorer 1 Disabled!");
//				if (color!=null){
//					toolsWindow.getController().getExplorer1().setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
//				}else{
//					toolsWindow.getController().getExplorer1().setBackground(null);
//				}
			}
		});	
	}
	
	public void updateModsList(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ObservableList<Mod> mods = modLoaderWindow.getController().getList().getItems();
				mods.clear();
				mods.addAll(Core.getModTools().getMods());
				ModLoaderListFactory cellFactory = modLoaderWindow.getListfactory();
				if (cellFactory!=null){
					cellFactory.updateModInfo(Core.getGame().getCurrentMod());
				}
			}
		});	
	}
	
	public void setPackageExplorer1(TreeItem<TreeViewEntry> treeview, String filterStr){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				toolsWindow.setExplorer1(treeview, filterStr);
				/*
				toolsWindow.getController().getExplorer1().setRoot(treeview);
				if (toolsWindow.getController().getExplorer1().getRoot() != null){
					toolsWindow.getController().getExplorer1().getRoot().setExpanded(true);
				}*/
			}
		});	
	}
	
	public void setLayerTreeView(TreeItem<Entity> treeview){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				toolsWindow.getController().getLayerTreeView().setRoot(treeview);
				if (treeview!=null){
					if (treeview.getChildren().size()>=1){
						treeview.setExpanded(true);
					}
				}
			}
		});	
	}
		
	public void toggleLeftVisibility(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Stage tools = toolsWindow.getStage();
				if (tools.isShowing()){
					tools.hide();
				}else{
					tools.show();
				}
			}
		});	
	}
		
	public void toggleModLoaderVisibility(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Stage modLoaderStage = modLoaderWindow.getStage();
				if (modLoaderStage.isShowing()){
					modLoaderStage.hide();
				}else{
					modLoaderStage.show();
				}
			}
		});	
	}
	/*END OF UPDATE METHODS*/
	
	public void selectGamePath(){
		try{
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					final DirectoryChooser directoryChooser = new DirectoryChooser();
					directoryChooser.setTitle("Select a game root directory!");
					final File selectedDirectory = directoryChooser.showDialog(new Stage());
					if (selectedDirectory != null) {
						String path = FileHandler.normalizePath(selectedDirectory.getAbsolutePath());
						System.out.println("Selected '"+path+"' as gamepath.");
						boolean succ = Core.setGamePath(path);
						if (!succ){
							Core.keepAlive = false;
							System.err.println("The gamepath is not valid!");
						}
					}else{
						Core.gamePath = null;
						Core.keepAlive = false;
					}
				}
			});
		}catch(IllegalStateException e){
			System.out.println("Waiting for Toolkit...");
			//JavaFX is threaded and may take a while to work.
			//java.lang.IllegalStateException: Toolkit not initialized
			selectGamePath();
		}
	}
	
	public void updateLayers(ArrayList<EntityLayer> layers){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
//				ObservableList<String> list = toolsWindow.getController().getLayer().getItems();
//				list.clear();
//				for (EntityLayer layer : layers){
//					list.add(layer.getName());
//				}
//				toolsWindow.getController().getDestroyLayerButton().setDisable(layers.isEmpty());
//				
//				
				
				
				
				/*TreeViewLayers*/
				TreeItem<Entity> layerTree = TreeViewConverter.getTreeView(layers);
				setLayerTreeView(layerTree);
				System.out.println("TreeView for Layers updated!");
			}
		});		
	}
	
	public void updateMeshvariationDatabaseComboBox(ArrayList<EBXStructureFile> databases){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ObservableList<String> list = toolsWindow.getController().getVariationDatabase().getItems();
				list.clear();
				list.add("NULL");
				for (EBXStructureFile db : databases){
					list.add(db.getStructureName()+" "+db.getEBXGUID());
				}				
			}
		});		
	}


	public ModLoaderWindow getModLoaderWindow() {
		return modLoaderWindow;
	}



	public ArrayList<ImagePreviewWindow> getImagePreviewWindows() {
		return imagePreviewWindows;
	}
	

	public ArrayList<EBXWindow> getEBXWindows() {
		return ebxWindows;
	}

	public ToolsWindow getToolsWindow() {
		return toolsWindow;
	}

	public ArrayList<EventGraphWindow> getEventGraphWindows() {
		return eventGraphWindows;
	}
	
	
	
	
}
