package tk.greydynamics.JavaFX.Controller;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.TreeViewConverter;
import tk.greydynamics.JavaFX.TreeViewEntry;
import tk.greydynamics.JavaFX.Windows.EBXWindow;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.Modify.ChangeFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;

public class EBXWindowController {
	@FXML
	private TreeView<TreeViewEntry> ebxExplorer;
	
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
		Core.runOnMainThread(new Runnable() {
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
						}*/
						
					}else{
						Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "MeshVariationDatabase FAILED!!", null);
					}	
				}else{
					Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Not a valid MeshVariationDatabase!", null);
				}	
			}
		});
	}
	
	public void close(){
		Core.getJavaFXHandler().getMainWindow().destroyEBXWindow(stage);
	}
	
	public void saveEBX(){
		if (ebxExplorer.getRoot() != null){
			if (Core.getGame().getCurrentMod()!=null&&!Core.isDEBUG){
				EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
				
				Core.getJavaFXHandler().getDialogBuilder().showAsk("WARNING!",
						"After this process is completed successfully,\nthe EBXWindow will close and the Package will reload.\nThis can take some time!\n\nDo you really want to continue?",
							new Runnable() {
								@Override
								public void run() {
									EBXFile ebxFile = window.getEBXFile();
									if (ebxFile!=null){
										ChangeFile cFile = ebxHandler.getModifyHandler().getChangeFileByEBXGuid(ebxFile.getGuid());
										if (cFile!=null){
											if (cFile.applyChanges(true)){
												Core.getJavaFXHandler().getDialogBuilder().showInfo("SUCESSFULL!", "Changes successfully appied to Mod!\nReloading...");
												Core.getGame().getResourceHandler().resetEBXRelated();
												Core.getJavaFXHandler().getMainWindow().destroyEBXWindow(stage);
											}else{
												Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "Error, while applying changes!", null);
											}
										}else{
											Core.getJavaFXHandler().getDialogBuilder().showError("ERROR", "No changes found, unable to save.", null);
										}
									}
								}
							}, null);
				/*
				String resPath = ebxExplorer.getRoot().getValue().getName()+".ebx";		
				
				String test = Core.getGame().getCurrentToc().getName();
				Package pack = Core.getModTools().getPackage(test);
				Core.getModTools().extendPackage(
						LinkBundleType.BUNDLES,
						Core.getGame().getCurrentSB().getPath(), 
						ResourceType.EBX,
						resPath,
						pack
				);
				
				//EBXFile ebxFile = TreeViewConverter.getEBXFile(ebxExplorer.getRoot());
				//byte[] ebxBytes = EBXConverter.createEBX(ebxFile);
				FileHandler.writeFile(Core.getGame().getCurrentMod().getPath()+ModTools.RESOURCEFOLDER+resPath, new byte[] {0x00}/*ebxBytes goes here!); //TODO
				
				
				//This will be moved over into main save.
				Core.getModTools().writePackages();
				*/
				
				
			}else{
				//DEBUG--
				EBXFile ebxFile = TreeViewConverter.getEBXFile(ebxExplorer.getRoot());
				System.err.println("TODO");
				byte[] ebxBytes = Core.getGame().getResourceHandler().getEBXHandler().createEBX(ebxFile);
				FileHandler.writeFile("output/DEBUG.ebx", ebxBytes);
				
				//TEST 2
				EBXFile orig = Core.getGame().getResourceHandler().getEBXHandler().loadFile(FileHandler.readFile("mods/SampleMod/resources/levels/mp/mp_playground/content/layer2_buildings.bak--IGNORE"));
				byte[] origBytes = Core.getGame().getResourceHandler().getEBXHandler().createEBX(orig);
				FileHandler.writeFile("output/ORIG_DEBUG.ebx", origBytes);
				
			}
		}
	}
	
	public TreeView<TreeViewEntry> getEBXExplorer() {
		return ebxExplorer;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}


	public void setWindow(EBXWindow window) {
		this.window = window;
	}
	
	

	
	
	
	
}
