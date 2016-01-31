package tk.greydynamics.JavaFX.CellFactories;

import java.io.File;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.Controller.ModLoaderController;
import tk.greydynamics.Mod.Mod;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Resource.FileHandler;

public class ModLoaderListFactory extends ListCell<Mod>{

	public ModLoaderListFactory(){
		setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				Mod mod = getItem();
				updateModInfo(mod);
			}
		});
	}
	public void updateModInfo(Mod mod){
		ModLoaderController ctrlr = Core.getJavaFXHandler().getMainWindow().getModLoaderWindow().getController();
		
		if (mod != null && Core.gameVersion != null){
			if (isSameGameVersion(mod.getGameVersion())){
				ctrlr.getModInfo().setDisable(false);
				ctrlr.getDesc().setText(mod.getDesc());
				ctrlr.getRunEditor().setDisable(false);
			}else{
				ctrlr.getModInfo().setDisable(true);
				ctrlr.getDesc().setText("This mod is not compatible with Game Version "+Core.gameVersion+".\n"
						+ "Please select a right game with the same version, as the mod you want to run.\n\n"
						+ "Mod was created with Game Version "+mod.getGameVersion()+".");
				ctrlr.getRunEditor().setDisable(true);
			}
			Core.getGame().setCurrentMod(mod);
			Core.getModTools().getPackages().clear();
			Core.getModTools().fetchPackages();
			
			ctrlr.getModName().setText(mod.getName());
			ctrlr.getAuthorName().setText(mod.getAuthor());
			ctrlr.getGameName().setText(mod.getGame());
				
			ctrlr.getDesc().setWrapText(true);
			File image = new File(mod.getPath()+"/logo.png");
			if (image.exists()){
				ctrlr.getLogo().setImage(new Image(FileHandler.getStream(image.getAbsolutePath())));
			}else{
				ctrlr.getLogo().setImage(null);
			}
			ctrlr.getInstallButton().setDisable(false);
			String installedMod = ModTools.getInstalledMod(Core.gamePath);
			if (installedMod == null){
				mod.setInstalled(false);
			}
			if (mod.isInstalled()){
				ctrlr.getInstallButton().setText("Uninstall");
				ctrlr.getInstallButton().setUnderline(true);
				ctrlr.getCompileButton().setDisable(true);
				ctrlr.getRunEditor().setDisable(true);
			}else if (installedMod==null&&mod.isCompiled()){
				ctrlr.getInstallButton().setText("Install");
				ctrlr.getCompileButton().setText("Recompile");
				ctrlr.getInstallButton().setUnderline(false);
				ctrlr.getCompileButton().setDisable(false);
				ctrlr.getRunEditor().setDisable(false);
			}else{
				ctrlr.getInstallButton().setDisable(true);
				ctrlr.getInstallButton().setUnderline(false);
				ctrlr.getInstallButton().setText("Install");
				ctrlr.getCompileButton().setText("Compile");
				ctrlr.getCompileButton().setDisable(false);
				ctrlr.getRunEditor().setDisable(false);
			}
		}else{
			ctrlr.getModInfo().setDisable(true);
			ctrlr.getRunEditor().setDisable(true);
			Core.getGame().setCurrentMod(null);
			ctrlr.getModName().setText("No mod currently selected!");
			ctrlr.getAuthorName().setText("");
			ctrlr.getGameName().setText("");
				
			ctrlr.getDesc().setWrapText(true);
			ctrlr.getDesc().setText("");
			ctrlr.getLogo().setImage(null);
			ctrlr.getRunEditor().setDisable(true);
			ctrlr.getInstallButton().setDisable(true);
			ctrlr.getCompileButton().setDisable(true);
			ctrlr.getInstallButton().setText("Install");
			ctrlr.getInstallButton().setUnderline(false);
			ctrlr.getCompileButton().setText("Compile");
		}
	}
	
	@Override
	protected void updateItem(Mod item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty){
			String name = item.getFolderName();
//			File destFolder = new File(item.getDestFolderPath());
//			if (destFolder.isDirectory()){
//				name+=" (Compiled)";
//			}
			setText(name);
			if (isSameGameVersion(item.getGameVersion())){
				setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
			}else{
				setBackground(new Background(new BackgroundFill(Color.ORANGERED, CornerRadii.EMPTY, Insets.EMPTY)));
			}
		}else{
			setText(null);
			setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		}
	}
	
	private static boolean isSameGameVersion(String modVersion){
		if (Core.gameVersion!=null){
			if (modVersion.equalsIgnoreCase(Core.gameVersion)){
				return true;
			}
		}
		return false;
	}
	
	

}
