package tk.greydynamics.JavaFX.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.TreeViewConverter;
import tk.greydynamics.JavaFX.Windows.EBXWindow;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Mod.Package;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;

public class EBXWindowController {
	@FXML
	private TreeView<Object> ebxExplorer;
	@FXML
	private MenuItem saveEBXMenuItem;
	
	private EBXWindow window;
	private Stage stage;
	private byte[] originalBytes;
	
	
	public void createLayer(){
		Core.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				if (window.getEntityLayer()!=null){
					System.err.println("EntityLayer does already exists!");
				}else{
					window.setEntityLayer(new EntityLayer("dummy", window));//bypass, that the user can't close the window while in progress.
					window.setEntityLayer(Core.getGame().getEntityHandler().createEntityLayer(window.getEBXFile(), window));
					System.err.println("--------------Layer creation done!!------------------");
				}
			}
		});
	}
	public void openEventGraph(){
		Core.getJavaFXHandler().getMainWindow().createEventGraphWindow(window.getEBXFile(), window.isOriginalFile(), true, false);
	}
	
	public void createMeshVariationDatabase(){
		/*Core.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				if (window.getStage().getTitle().contains("variation")){
					EBXStructureFile strcFile = Core.getGame().getResourceHandler().getEBXHandler().readEBXStructureFile(window.getEBXFile());
					if (strcFile!=null){
						Core.getGame().getResourceHandler().getMeshVariationDatabaseHandler().addDatabase(strcFile);
						Core.getJavaFXHandler().getDialogBuilder().showInfo("SUCCESSFUL", "MeshVariationDatabase added SUCCESSFUL!!", null, null);
						
						

						/*DEBUG
						ArrayList<EntityLayer> layers = Core.getGame().getEntityHandler().getLayers();
						if (!layers.isEmpty()){
							Core.getGame().getEntityHandler().updateLayer(layers.get(0), strcFile);
						}
						
					}else{
						Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "MeshVariationDatabase FAILED!!", null);
					}	
				}else{
					Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Not a valid MeshVariationDatabase!", null);
				}	
			}
		});*/
	}
	
	public void close(){
		Core.getJavaFXHandler().getMainWindow().destroyEBXWindow(stage);
	}
	public boolean compileEBX(){
		System.err.println("(EXPERIMENTAL)");
		if (ebxExplorer.getRoot() != null){
			if (Core.getGame().getCurrentMod()!=null&&!Core.isDEBUG){
				String resLinkName = window.getName();
				if (window.getEBXFile()!=null){
					EBXFile ebxFile = window.getEBXFile();
					byte[] ebxBytes = Core.getGame().getResourceHandler().getEBXHandler().createEBX(ebxFile);
					FileHandler.writeFile("output/DEBUG.ebx", ebxBytes);
					
					
					EBXFile test = Core.getGame().getResourceHandler().getEBXHandler().loadFile(ebxBytes);
					Core.getJavaFXHandler().getMainWindow().createEBXWindow(null, test, "recreated ebx test", false);
					if (test==null){						
						Core.getJavaFXHandler().getDialogBuilder().showAsk("ERROR",
								"The compiler was unable to compile a new EBXFile.\n"
							  + "Do you want to save the changed data instead ? \n\n(only considered: Float, Int, UInt, Short, UShort, Byte, Bool)", new Runnable() {	
									@Override
									public void run() {
										saveEBX();
									}
								}, null);
						return false;
					}else{
						Core.getJavaFXHandler().getDialogBuilder().showInfo("INFORMATION", "If the test was successful, the game maybe reject the file!");
					}
					return saveData(resLinkName, ebxBytes, false);
				}
			}
		}
		return false;
	}
	
	private boolean saveData(String resLinkName, byte[] ebxBytes, boolean isDEBUG){
		String resPath = resLinkName+".ebx";
		
		if (isDEBUG){
			System.err.println("saveData - DEBUG!");
			EBXFile test = Core.getGame().getResourceHandler().getEBXHandler().loadFile(ebxBytes);
			Core.getJavaFXHandler().getMainWindow().createEBXWindow(null, test, "changed ebx test", false);
			System.out.println("data would be saved to "+Core.getGame().getCurrentMod().getPath()+ModTools.FOLDER_RESOURCE+resPath);
			FileHandler.writeFile("temp/debug/saveDataEBX", ebxBytes);
			
		}else{
			String currentToc = FileHandler.normalizePath(Core.getGame().getCurrentFile()).replace(Core.gamePath, "");
			Package pack = Core.getModTools().getPackage(currentToc);
			System.out.println("Extend Current Bundle: "+Core.getGame().getCurrentBundle().getName());
			Core.getModTools().extendPackage(
					LinkBundleType.BUNDLES,
					Core.getGame().getCurrentBundle().getName(),
					ResourceType.EBX,
					resPath,
					null,
					pack
			);
			
			FileHandler.writeFile(Core.getGame().getCurrentMod().getPath()+ModTools.FOLDER_RESOURCE+resPath, ebxBytes);
			Core.getModTools().writePackages();
		}
		
		Core.getJavaFXHandler().getMainWindow().destroyEBXWindow(stage);
		Core.getGame().getResourceHandler().resetEBXRelated();
		return true;
	}
	
	public void saveEBX(){
		if (ebxExplorer.getRoot() != null){
			if (Core.getGame().getCurrentMod()!=null&&!Core.isDEBUG){
				String resLinkName = window.getName();
				if (window.getEBXFile()!=null){
					if (originalBytes!=null){
						saveData(resLinkName, originalBytes, false/*debug*/);
					}
				}
			}
		}else{
			System.err.println("No data to save :(");
		}
	}
	public void update(EBXFile ebxFile){
		TreeItem<Object> ebxTreeView = null;
	    if (ebxFile!=null){
	    	ebxTreeView = TreeViewConverter.getTreeView(ebxFile);
	    	if (!ebxTreeView.getChildren().isEmpty()){
		    	ebxTreeView.setExpanded(true);
		    }
	    }
	    ebxExplorer.setRoot(ebxTreeView);
	}

	
	public TreeView<Object> getEBXExplorer() {
		return ebxExplorer;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}


	public void setWindow(EBXWindow window) {
		this.window = window;
	}

	public byte[] getOriginalBytes() {
		return originalBytes;
	}

	public void setOriginalBytes(byte[] originalBytes) {
		this.originalBytes = originalBytes;
	}

	public MenuItem getSaveEBXMenuItem() {
		return saveEBXMenuItem;
	}
	
	

	
	
	
	
}
