package tk.greydynamics.JavaFX.CellFactories;

import java.io.File;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.Controller.ModLoaderController;
import tk.greydynamics.Mod.Mod;
import tk.greydynamics.Resource.FileHandler;

public class ModLoaderListFactory extends ListCell<Mod>{

	public ModLoaderListFactory(){
		setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				Mod mod = getItem();
				ModLoaderController ctrlr = Core.getJavaFXHandler().getMainWindow().getModLoaderWindow().getController();
				if (mod != null){
					Core.getGame().setCurrentMod(mod);
					Core.getModTools().getPackages().clear();
					Core.getModTools().fetchPackages();
					
					ctrlr.getModName().setText(mod.getName());
					ctrlr.getAuthorName().setText(mod.getAuthor());
					ctrlr.getGameName().setText(mod.getGame());
						
					ctrlr.getDesc().setWrapText(true);
					ctrlr.getDesc().setText(mod.getDesc());
					File image = new File(mod.getPath()+"/logo.png");
					if (image.exists()){
						ctrlr.getLogo().setImage(new Image(FileHandler.getStream(image.getAbsolutePath())));
					}else{
						ctrlr.getLogo().setImage(null);
					}
					ctrlr.getRunEditor().setDisable(false);
					ctrlr.getPlayButton().setDisable(false);
					
					File destFolder = new File(mod.getDestFolderPath());
					if (destFolder.isDirectory()){
						ctrlr.getCheckBox().setVisible(true);
						ctrlr.getCheckBox().setDisable(false);
					}else{
						ctrlr.getCheckBox().setVisible(false);
						ctrlr.getCheckBox().setDisable(true);
					}
					
				}else{
					ctrlr.getRunEditor().setDisable(true);
					Core.getGame().setCurrentMod(null);
					ctrlr.getModName().setText("No mod currently selected!");
					ctrlr.getAuthorName().setText("");
					ctrlr.getGameName().setText("");
						
					ctrlr.getDesc().setWrapText(true);
					ctrlr.getDesc().setText("");
					ctrlr.getLogo().setImage(null);
					ctrlr.getRunEditor().setDisable(true);
					ctrlr.getPlayButton().setDisable(true);
					
					ctrlr.getCheckBox().setVisible(false);
					ctrlr.getCheckBox().setDisable(true);
				}
			}
		});
	}
	
	@Override
	protected void updateItem(Mod item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty){
			String name = item.getFolderName();
			File destFolder = new File(item.getDestFolderPath());
			if (destFolder.isDirectory()){
				name+=" (Compiled)";
			}
			setText(name);
		}else{
			setText(null);
		}
	}
	
	

}
