package tk.greydynamics.JavaFX.CellFactories;

import java.io.File;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.TreeViewConverter;
import tk.greydynamics.JavaFX.TreeViewEntry;
import tk.greydynamics.JavaFX.Windows.MainWindow.EntryType;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.Data.ResourceFinder;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutFile;
import tk.greydynamics.Resource.Frostbite3.Toc.ConvertedTocFile;
import tk.greydynamics.Resource.Frostbite3.Toc.TocConverter;
import tk.greydynamics.Resource.Frostbite3.Toc.TocEntry;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager;

public class JavaFXexplorerTCF extends TreeCell<TreeViewEntry> {
	public static String backString = "GO BACK";
	public JavaFXexplorerTCF() {
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (getTreeItem() != null){
					if (getTreeItem().getChildren().isEmpty() && getTreeItem().getValue().getValue() instanceof TocEntry){
						TocEntry entry = ((TocEntry)getTreeItem().getValue().getValue());
						//TOC MODE
						if (entry.getType() == LinkBundleType.BUNDLES){
							cleanExplorer1();
							
							if (Core.getGame().getCurrentToc().isCas()){
								//CAS-> Frostbite 3
								LayoutFile bundleLayout = entry.getLayout();
								CasBundle casBundle = TocConverter.convertCASBundle(bundleLayout, true);
																
//								//EBX-Export
//								for (ResourceLink ebx : casBundle.getEbx()){
//									ResourceLink.exportResourceLink(ebx, ResourceHandler.getOrigin(Core.getGame().getCurrentFile()), Core.EDITOR_PATH_GAMEDATA+Core.gameName+"/ResourceLink/EBX/", ".erl");//EBX RESOURCE LINK == ERL
//								}
//								//RES-Export
//								for (ResourceLink res : casBundle.getRes()){
//									ResourceLink.exportResourceLink(res, ResourceHandler.getOrigin(Core.getGame().getCurrentFile()), Core.EDITOR_PATH_GAMEDATA+Core.gameName+"/ResourceLink/RES/", ".rrl");//RES RESOURCE LINK == ERL
//								}
								
								Core.getGame().setCurrentBundle(casBundle);
								TreeItem<TreeViewEntry> tree = TreeViewConverter.getTreeView(casBundle);
								Core.getJavaFXHandler().getMainWindow().setPackageExplorer1(tree, null);
							}else{
								//NON-CAS -> Common Chunks, DLC's and on Servers!
								int size = entry.getSize();
								if (size==-1){
									size = (int) entry.getSizeLong();
								}
								if (entry.isDelta()){
									//Base does exist. (Patched)
									System.err.println("PATCHED!!");
									File base = ResourceFinder.findUnpatchedXPackData(entry.getBundlePath());//.sb for XPACKS
									if (!base.exists()){
										//It's not a XPack, its has to be inside Data
										base = ResourceFinder.findUnpatchedData(entry.getBundlePath());//.sb for Data
									}
									if (base.exists()){										
										//now open the file and get the correct bundle (with the same name as the delta bundle) 
										LayoutFile baseLayout = TocManager.readToc(base.getAbsolutePath().replace(".sb", ""));
										ConvertedTocFile convBase = TocConverter.convertTocFile(baseLayout);
										if (convBase!=null){
											TocEntry baseEntry = null;
											for (TocEntry bundleEntry : convBase.getBundles()){
												if (bundleEntry.getID().equalsIgnoreCase(entry.getID())){
													baseEntry = bundleEntry;
												}
											}
											if (baseEntry!=null){
												int baseSize = baseEntry.getSize();
												if (baseSize==-1){
													baseSize = (int) baseEntry.getSizeLong();
												}
												byte[] baseBundleBytes = FileHandler.readFile(baseEntry.getBundlePath(), baseEntry.getOffset(), baseSize);
												FileHandler.writeFile("temp/baseBundle_nonCas", baseBundleBytes);
												byte[] deltaBundleBytes = FileHandler.readFile(entry.getBundlePath(), entry.getOffset(), size);
												if (baseBundleBytes!=null&&deltaBundleBytes!=null){
													//System.out.println("BASE FOUND! "+base.getAbsolutePath());
													NonCasBundle nonCas = new NonCasBundle(base.getAbsolutePath(), entry.getBundlePath(), entry.getID(), (int) baseEntry.getOffset(), (int) entry.getOffset(), baseBundleBytes, deltaBundleBytes);
													System.out.println("PATCHED NON-CAS TEST!");
													Core.getGame().setCurrentBundle(nonCas);
													TreeItem<TreeViewEntry> tree = TreeViewConverter.getTreeView(nonCas);
													if (tree!=null){
														Core.getJavaFXHandler().getMainWindow().setPackageExplorer1(tree, null);
													}
												}else{
													Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Offset+Size is out of bounds.", null);
												}
											}else{
												Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Bundle was not found inside Base. ("+entry.getID()+")", null);
											}
										}
									}else{
										Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Base does not exist!", null);
									}
								}else if (entry.isBase()){
									//inside Patched but reads Base
									File base = ResourceFinder.findUnpatchedXPackData(entry.getBundlePath());//Update/XPack0/Random.sb
									if (base==null){//!base.exists()
										//Its not a XPack, so its has to be inside Data.
										base = ResourceFinder.findUnpatchedData(entry.getBundlePath());
									}
									if (base!=null){//base.exists()
										byte[] bundleBytes = FileHandler.readFile(base.getAbsolutePath(), entry.getOffset(), size);
										if (bundleBytes!=null){
											NonCasBundle nonCas = new NonCasBundle(base.getAbsolutePath(), null, entry.getID(), (int) entry.getOffset(), -1, bundleBytes, null);
											Core.getGame().setCurrentBundle(nonCas);
											TreeItem<TreeViewEntry> tree = TreeViewConverter.getTreeView(nonCas);
											if (tree!=null){
												Core.getJavaFXHandler().getMainWindow().setPackageExplorer1(tree, null);
											}
										}
									}else{
										Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Base not found for patched, you should find the Base manually!", null);
									}
								}else{
									//Base (Unpatched)
									byte[] bundleBytes = FileHandler.readFile(entry.getBundlePath(), entry.getOffset(), size);
									if (bundleBytes!=null){
										NonCasBundle nonCas = new NonCasBundle(entry.getBundlePath(), null, entry.getID(), (int) entry.getOffset(), -1, bundleBytes, null);
										//TEST
//										FileHandler.writeFile("temp/debug/test_nonCasBundle_unpatched/original_bundle", bundleBytes);
//										NonCasBundleCompiler.compileUnpatchedBundle(nonCas, ByteOrder.BIG_ENDIAN, "temp/debug/test_nonCasBundle_unpatched/");
										
										Core.getGame().setCurrentBundle(nonCas);
										TreeItem<TreeViewEntry> tree = TreeViewConverter.getTreeView(nonCas);
										if (tree!=null){
											Core.getJavaFXHandler().getMainWindow().setPackageExplorer1(tree, null);
										}
									}
								}
							}
							
							/*Core.runOnMainThread(new Runnable() {
								@Override
								public void run() {
									Core.getGame().getModelHandler().getLoader().cleanUp();//Clean VAO, VBO, Textures!
								}
							});*/
						}else{
							System.err.println(((TocEntry)getTreeItem().getValue().getValue()).getType()+" are not supported yet.");
						}
					}else if (getTreeItem().getChildren().isEmpty() && getTreeItem().getValue().getValue() instanceof File){
						//EXPLORER MODE
						Core.getGame().setCurrentFile(FileHandler.normalizePath(((File)getTreeItem().getValue().getValue()).getAbsolutePath().replace(".sb", "")));
						LayoutFile toc = TocManager.readToc(Core.getGame().getCurrentFile());
						ConvertedTocFile convToc = TocConverter.convertTocFile(toc);
						if (convToc.getName()==null){
							//is null if patched! (update folder files are noname's)
							convToc.setName(Core.getGame().getCurrentFile().replace(FileHandler.normalizePath(Core.gamePath), ""));
						}
						if (!convToc.isCas()&&Core.getGame().getCurrentFile().contains(Core.PATH_UPDATE.replace("/", ""))){
							Core.getJavaFXHandler().getDialogBuilder().showWarning("WARNING", ">>NON-CAS IS VIEWING ONLY!<<\n\n", null);
						
							Core.getJavaFXHandler().getMainWindow().setPackageExplorerBackground(Color.PEACHPUFF);
						}
						Core.getGame().setCurrentToc(convToc);
						TreeItem<TreeViewEntry> masterTree = new TreeItem<TreeViewEntry>(new TreeViewEntry("BACK (Click)", new ImageView(JavaFXHandler.ICON_ARROW_LEFT), backString, EntryType.LIST));
						TreeItem<TreeViewEntry> convTocTree = TreeViewConverter.getTreeView(convToc);
						
						if (toc.getSBPath().contains(Core.PATH_UPDATE)){
							if (toc.getSBPath().contains(Core.PATH_PATCH)){
								//patch
								convTocTree.getValue().setGraphic(new ImageView(JavaFXHandler.ICON_UPDATE));
								if (convToc.isCas()){
									Core.getJavaFXHandler().getMainWindow().setPackageExplorerBackground(Color.LIGHTBLUE);
								}
							}else{
								//xpack
								convTocTree.getValue().setGraphic(new ImageView(JavaFXHandler.ICON_PLUS));
								if (convToc.isCas()){
									Core.getJavaFXHandler().getMainWindow().setPackageExplorerBackground(Color.LIGHTGREEN);
								}
							}
						}
						convTocTree.setExpanded(true);
						masterTree.getChildren().add(convTocTree);
						masterTree.setExpanded(true);
						//Core.getJavaFXHandler().setTreeViewStructureLeft(masterTree);
						Core.getJavaFXHandler().getMainWindow().setPackageExplorer(masterTree);					
					}else if (getTreeItem().getValue().getValue() instanceof String){
						if (((String)getTreeItem().getValue().getValue()).equals(backString)){
							//BACK
							Core.getGame().buildExplorerTree();
							cleanExplorer1();
						}
					}
				}
			}
		});
	}
	@Override
	public void updateItem(TreeViewEntry item, boolean empty) {
	    super.updateItem(item, empty);
	    setBackground(null);
	    if (empty) {
		    setText(null);
		    setGraphic(null);
	    } else {
	    	if (item.getType() == EntryType.STRING){
	    		if (item.getValue() != null && !(item.getValue() instanceof TocEntry)){
	    			setText(item.getName()+":"+(String)item.getValue());
	    		}else{
	    			setText(item.getName());
	    		}
	    	}else if (item.getType() == EntryType.LIST){
	    		setText(item.getName());
	    	}else{
	    		setText(item.getName()+" with undefined type for explorerTCF: "+item.getType());
	    	}
	    	if (item.getTooltip()!=null){
	    		setTooltip(new Tooltip(item.getTooltip()));
	    	}
	    	if (item.getBackgroundColor()!=null){
	    		setBackground(new Background(new BackgroundFill(item.getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
	    	}
		    setGraphic(item.getGraphic());
	    }
	}
	
	public void cleanExplorer1(){
		Core.getGame().getResourceHandler().resetEBXRelated();//clean all ebxFiles in DB
		Core.getGame().getEntityHandler().clear();
		Core.getJavaFXHandler().getMainWindow().setPackageExplorerBackground(Color.WHITE);
		Core.getJavaFXHandler().getMainWindow().setPackageExplorer1(null, null);
	}
}