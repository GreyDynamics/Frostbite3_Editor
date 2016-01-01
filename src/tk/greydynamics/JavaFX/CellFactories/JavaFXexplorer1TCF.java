package tk.greydynamics.JavaFX.CellFactories;

import java.io.File;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import tk.greydynamics.Game.Core;
import tk.greydynamics.Game.Game;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.TreeViewConverter;
import tk.greydynamics.JavaFX.TreeViewEntry;
import tk.greydynamics.JavaFX.Windows.MainWindow.EntryType;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler;
import tk.greydynamics.Resource.Frostbite3.Cas.CasDataReader;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundleEntry;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasDataReader;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Component.EBXComponent;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ITexture;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ITextureHandler;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ImageConverter;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ImageConverter.ImageType;
import tk.greydynamics.Resource.Frostbite3.MESH.MeshConverter;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter.ResourceBundleType;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;

public class JavaFXexplorer1TCF extends TreeCell<TreeViewEntry> {
	private ContextMenu contextMenu = new ContextMenu();
    private MenuItem restore, remove, rename;
    
	public JavaFXexplorer1TCF() {
		
		restore = new MenuItem("Restore Orignal File");
		restore.setGraphic(new ImageView(JavaFXHandler.ICON_PASTE));
		restore.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	byte[] data = null;
            	ResourceLink link = (ResourceLink) getTreeItem().getValue().getValue();
            	if (link!=null){
            		data = CasDataReader.readCas(link.getBaseSha1(), link.getDeltaSha1(), link.getSha1(), link.getCasPatchType());
            		if (data!=null){
            			File target = new File(Core.getGame().getCurrentMod().getPath()+ModTools.RESOURCEFOLDER+link.getName()+"."+link.getBundleType().toString().toLowerCase());
            			if (target.exists()){
            				File backup = new File(target.getAbsoluteFile()+".bak");
            				if (backup.exists()){
            					backup.delete();
            				}
            				target.renameTo(backup);
            			}
            			if (!FileHandler.writeFile(target.getAbsolutePath(), data)){
            				System.err.println("Could not write data to file. Check permissions!");
            			}
            		}else{
            			System.err.println("Could not fetch original data.");
            		}
            	}
            }
        });
		
		remove = new MenuItem("Remove");
		remove.setGraphic(new ImageView(JavaFXHandler.ICON_REMOVE));
		remove.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	System.err.println("TODO REMOVE");
            }
        });
		remove.setDisable(true);
		
		rename = new MenuItem("Rename");
		rename.setGraphic(new ImageView(JavaFXHandler.ICON_TEXT));
		rename.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	System.err.println("TODO RENAME");
            }
        });
		rename.setDisable(true);
		
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				TreeItem<TreeViewEntry> i = getTreeItem();
				if (i != null){
					if (i.getParent()!=null){
						if (i.getValue().getValue() != null){
							if (event.getButton() == MouseButton.PRIMARY){
								
								boolean loadOriginal = false;
								
								Game game = Core.getGame();
								ResourceHandler rs = game.getResourceHandler();
								
								ResourceBundleType bundleType = null;
								ResourceType resType = null;
								String name = null;
								byte[] data = null;
								if (i.getValue().getValue() instanceof ResourceLink){
									ResourceLink link = (ResourceLink) i.getValue().getValue();
									data = rs.readResourceLink(link, loadOriginal);
									bundleType = link.getBundleType();
									resType = link.getType();
									name = link.getName();
								}else if (i.getValue().getValue() instanceof NonCasBundleEntry){
									Core.getJavaFXHandler().getDialogBuilder().showWarning("WARNING", "NON CAS not fully implemented yet!", null);
									NonCasBundleEntry entry = (NonCasBundleEntry) i.getValue().getValue();
									NonCasBundle nonCasBundle = (NonCasBundle) Core.getGame().getCurrentBundle();
									data = NonCasDataReader.readNonCasBundleData(nonCasBundle, entry);
									name = nonCasBundle.getName();
									bundleType = entry.getBundleType();
									resType = entry.getResourceType();
								}else{
									System.err.println("NO ResLink or NonCasBundleEntry!");
								}
								
								if (Core.isDEBUG&&data!=null){
									FileHandler.writeFile("output/debug/targetData_TCF1", data);
								}
								if (data!=null&&bundleType!=null){
									if (bundleType == ResourceBundleType.EBX){
										EBXFile ebxFile = game.getResourceHandler().getEBXHandler().loadFile(data);
										if (ebxFile!=null){
											
//											System.out.println("DEBUG: COMPONENT VALIDATION TEST!");
											Core.getGame().getResourceHandler().getEBXComponentHandler().reset(Core.getGame().getResourceHandler().getEBXComponentHandler().getKnownComponentsPath());
//											
											Core.getGame().getResourceHandler().getEBXComponentHandler().addKnownComponent(ebxFile);
											ArrayList<EBXComponent> comp = Core.getGame().getResourceHandler().getEBXComponentHandler().getKnownComponents();
											Core.getGame().getResourceHandler().getEBXComponentHandler().saveKnownComponents();
//											Core.getGame().getResourceHandler().getEBXComponentHandler().readKnownComponents();
//											System.out.println("END OF DEBUG!");
											
											
											TreeItem<TreeViewEntry> ebx = TreeViewConverter.getTreeView(ebxFile);

											Core.getJavaFXHandler().getMainWindow().createEBXWindow(ebxFile, name, loadOriginal);
										}
										
									}else if (bundleType == ResourceBundleType.RES){
										if (resType == ResourceType.ITEXTURE){
											byte[] ddsBytes = ITextureHandler.getDSS(data);
											if (ddsBytes!=null){
												File ddsFile = new File("temp/images/"+name.replace('/', '_')+".dds");
												FileHandler.writeFile(ddsFile.getAbsolutePath(), ddsBytes);

												File pngFile = null;
												ITexture itexture = new ITexture(data, null);
												if (itexture.getPixelFormat()==ITexture.TF_NormalDXN){
													//Convert using Nvidia
													File tga = ImageConverter.convertToTGA(ddsFile);
													pngFile = ImageConverter.convert(tga, ImageType.PNG, true);
												}else{
													//Convert using ImageMagick
													pngFile = ImageConverter.convert(ddsFile, ImageType.PNG, true);
												}
												if (pngFile!=null){
													Core.getJavaFXHandler().getMainWindow().createImagePreviewWindow(pngFile, ddsFile, i.getValue().getValue(), name);
												}
											}
										}else if (resType == ResourceType.MESH){
											byte[] obj = MeshConverter.getAsOBJ(data, game.getCurrentBundle());
											FileHandler.writeFile("output/"+name.replace('/', '_')+".obj", obj);
										}else{
											System.err.println("Type not supported yet.");
											FileHandler.writeFile("output/"+name.replace('/', '_')+"."+resType, data);
										}
									}else if (i.getParent().getValue().getType() == EntryType.LIST){
										System.out.println(((ResourceLink)i.getValue().getValue()).getBundleType()+" is currently not supported.");
									}
								}
							}
						}
					}
				}
			}
		});
	}
	
	@Override
	public void updateItem(TreeViewEntry item, boolean empty) {
	    super.updateItem(item, empty);
	    contextMenu.getItems().clear();
//	    setBackground(null);
	    if (empty) {
		    setText(null);
		    setGraphic(null);
		    setUnderline(false);
	    }else if (item.getType() == EntryType.LIST){
    		setText(item.getName());
    		setGraphic(item.getGraphic());
    		setUnderline(false);
    		setContextMenu(contextMenu);
    	}else {
    		setUnderline(false);
	    	setText(item.getName());
		    setGraphic(item.getGraphic());
		    String tooltip = item.getTooltip();
		    if (tooltip != null){
		    	 setTooltip(new Tooltip(tooltip));
		    }
		    setContextMenu(contextMenu);
		    if (item.getValue()!=null&&item.getValue() instanceof ResourceLink){
		    	ResourceLink link = (ResourceLink) item.getValue();
		    	if (link.isHasModFile()){
		    		setUnderline(true);
		    		contextMenu.getItems().addAll(restore);
		    	}else{
		    		contextMenu.getItems().addAll(rename, remove);
		    	}
		    }
	    }
//	    if (!empty&&item.getBackgroundColor()!=null){
//	    	setBackground(new Background(new BackgroundFill(item.getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
//	    }
	}
}