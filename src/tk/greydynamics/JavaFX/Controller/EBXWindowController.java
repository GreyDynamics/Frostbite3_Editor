package tk.greydynamics.JavaFX.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
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
	
	private EBXWindow window;
	private Stage stage;
	
	
	public void createLayer(){
		Core.runOnMainThread(new Runnable() {
			@Override
			public void run() {
				Core.getGame().getEntityHandler().createEntityLayer(window.getEBXFile());
				System.err.println("--------------Layer creation done!!------------------");
			}
		});
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
				String resLinkName = window.getStage().getTitle();
				if (window.getEBXFile()!=null){
					EBXFile ebxFile = window.getEBXFile();
					byte[] ebxBytes = Core.getGame().getResourceHandler().getEBXHandler().createEBX(ebxFile);
					FileHandler.writeFile("output/DEBUG.ebx", ebxBytes);
					
					
					EBXFile test = Core.getGame().getResourceHandler().getEBXHandler().loadFile(ebxBytes);
					Core.getJavaFXHandler().getMainWindow().createEBXWindow(test, "recreated ebx test", false);
					if (test==null){
						System.err.println("unable to compile a valid ebx.");
						return false;
					}
					
					String resPath = resLinkName+".ebx";
					
					String currentToc = FileHandler.normalizePath(Core.getGame().getCurrentFile()).replace(Core.gamePath, "");
					Package pack = Core.getModTools().getPackage(currentToc);
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
					Core.getJavaFXHandler().getMainWindow().destroyEBXWindow(stage);
					Core.getGame().getResourceHandler().resetEBXRelated();
					return true;
				}
			}
		}
		return false;
	}
	
	public void saveEBX(){
		if (ebxExplorer.getRoot() != null){
			if (Core.getGame().getCurrentMod()!=null&&!Core.isDEBUG){
				String resLinkName = window.getStage().getTitle();
				if (window.getEBXFile()!=null){
//					Replaced with compile
					
					
					
					
//					String resPath = resLinkName+".ebx";
//					
//					String test = Core.getGame().getCurrentToc().getName();
//					Package pack = Core.getModTools().getPackage(test);
//					Core.getModTools().extendPackage(
//							LinkBundleType.BUNDLES,
//							Core.getGame().getCurrentBundle().getBasePath(),//TODO is this basePath ?ß 
//							ResourceType.EBX,
//							resPath,
//							pack
//					);
//					
//					FileHandler.writeFile(Core.getGame().getCurrentMod().getPath()+ModTools.FOLDER_RESOURCE+resPath, new byte[] {0x00});
//					Core.getModTools().writePackages();
//					Core.getGame().getResourceHandler().resetEBXRelated();
//					Core.getJavaFXHandler().getMainWindow().destroyEBXWindow(stage);
				}
			}
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
	
	

	
	
	
	
}
