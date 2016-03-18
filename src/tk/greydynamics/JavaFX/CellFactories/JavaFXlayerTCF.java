package tk.greydynamics.JavaFX.CellFactories;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import tk.greydynamics.Entity.Entity;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;

public class JavaFXlayerTCF extends TreeCell<Entity> {
	
	EBXStructureEntry tmpEntry = null;
	
	private MenuItem show, hide, remove;
	private ContextMenu contextMenu = new ContextMenu();
	
	
	public JavaFXlayerTCF() {
		
		show = new MenuItem("Show");
		show.setGraphic(new ImageView(JavaFXHandler.ICON_BINOCULAR));
		show.setOnAction(new EventHandler<ActionEvent>() {
	        public void handle(ActionEvent t) {
	            getTreeItem().getValue().setIsVisible(true);
	            updateItem(getTreeItem().getValue(), false);
	        }
	    }
		);
		
		hide = new MenuItem("Hide");
		hide.setGraphic(new ImageView(JavaFXHandler.ICON_BINOCULAR_2));
		hide.setOnAction(new EventHandler<ActionEvent>() {
	        public void handle(ActionEvent t) {
	            getTreeItem().getValue().setIsVisible(false);
	            updateItem(getTreeItem().getValue(), false);
	        }
	    }
		);
		
		remove = new MenuItem("Remove");
		remove.setDisable(true);
		remove.setGraphic(new ImageView(JavaFXHandler.ICON_REMOVE));
		remove.setOnAction(new EventHandler<ActionEvent>() {
	        public void handle(ActionEvent t) {
	            System.err.println("Not implemented yet.");
	        }
	    }
		);
	}
	
	@Override
	public void updateItem(Entity item, boolean empty) {
		super.updateItem(item, empty);
		setStyle(null);
		setEffect(null);
		if (empty) {
			setText(null);
			setGraphic(null);
		}else if (item==null){
			setText("Layers");
		} 
		else {
//			tmpEntry = item.getStructEntry();
			contextMenu.getItems().clear();
//			if (tmpEntry!=null){
//				String[] instanaceGUID = item.getName().split("/");
//				if (instanaceGUID.length==2){
//					setText(item.getStructEntry().getType().toString()+" "+instanaceGUID[1]);
//				}else{
//					setText(item.getStructEntry().getType().toString()+" "+item.getName());
//				}
				if (!item.getIsVisible()){
					contextMenu.getItems().add(show);
					setStyle("-fx-background-color: red");
				}else if(item.getIsVisible()){
					contextMenu.getItems().add(hide);
					setStyle("-fx-background-color: lightgrey");
				}/*else if (item.getTexturedModelNames()!=null){
//					setStyle("-fx-background-color: green");
//				}*/
//			}else{
				setText(item.getName()+" P:"+item.getPosition()+" C:"+item.getPickerColors());
//				setStyle("-fx-background-color: lightgrey");
//			}
			setGraphic(getTreeItem().getGraphic());
//			contextMenu.getItems().add(remove);
			setContextMenu(contextMenu);
		}

	}
}