package tk.greydynamics.JavaFX;

import java.util.concurrent.CountDownLatch;

import antonsmirnov.javafx.dialog.Dialog.Builder;
import javafx.application.Platform;
import javafx.scene.image.Image;
import tk.greydynamics.JavaFX.Windows.MainWindow;
import tk.greydynamics.Resource.FileHandler;


public class JavaFXHandler {
	
	MainWindow main;
		

	public static final String imageFolder = "res/images/";
	
	public static final Image ICON_APPLICATION16 = new Image(FileHandler.getStream("res/icon/16.png"));
	public static final Image ICON_APPLICATION32 = new Image(FileHandler.getStream("res/icon/32.png"));
	
	public static final Image ICON_TEXT = new Image(FileHandler.getStream(imageFolder+"edit-small-caps.png"));
	public static final Image ICON_BYTE = new Image(FileHandler.getStream(imageFolder+"document-attribute-b.png"));
	public static final Image ICON_BOOL = new Image(FileHandler.getStream(imageFolder+"document-attribute-bool.png"));
	public static final Image ICON_DOUBLE = new Image(FileHandler.getStream(imageFolder+"document-attribute-d.png"));
	public static final Image ICON_FLOAT = new Image(FileHandler.getStream(imageFolder+"document-attribute-f.png"));
	public static final Image ICON_INTEGER = new Image(FileHandler.getStream(imageFolder+"document-attribute-i.png"));
	public static final Image ICON_INTEGER_UNSIGNED = new Image(FileHandler.getStream(imageFolder+"document-attribute-i_unsigned.png"));
	public static final Image ICON_LONG = new Image(FileHandler.getStream(imageFolder+"document-attribute-l.png"));
	public static final Image ICON_SHORT = new Image(FileHandler.getStream(imageFolder+"document-attribute-s.png"));
	public static final Image ICON_SHORT_UNSIGNED = new Image(FileHandler.getStream(imageFolder+"document-attribute-s_unsigned.png"));
	public static final Image ICON_ARRAY = new Image(FileHandler.getStream(imageFolder+"edit-code.png"));
	public static final Image ICON_INSTANCE = new Image(FileHandler.getStream(imageFolder+"box.png"));
	public static final Image ICON_LIST = new Image(FileHandler.getStream(imageFolder+"wooden-box.png"));
	public static final Image ICON_HASH = new Image(FileHandler.getStream(imageFolder+"hash.png"));
	public static final Image ICON_RAW = new Image(FileHandler.getStream(imageFolder+"block.png"));
	public static final Image ICON_STRUCTURE = new Image(FileHandler.getStream(imageFolder+"structure.png"));
	public static final Image ICON_INTERNAL = new Image(FileHandler.getStream(imageFolder+"internal.png"));
	public static final Image ICON_RESOURCE = new Image(FileHandler.getStream(imageFolder+"resource.png"));
	public static final Image ICON_QUESTION = new Image(FileHandler.getStream(imageFolder+"question-frame.png"));
	public static final Image ICON_IMAGE = new Image(FileHandler.getStream(imageFolder+"image.png"));
	public static final Image ICON_GEOMETRY = new Image(FileHandler.getStream(imageFolder+"xyz.png"));
	public static final Image ICON_GEOMETRY_2 = new Image(FileHandler.getStream(imageFolder+"xyz2.png"));
	public static final Image ICON_LUA = new Image(FileHandler.getStream(imageFolder+"lua.png"));
	public static final Image ICON_ENUM = new Image(FileHandler.getStream(imageFolder+"enum.png"));
	public static final Image ICON_UPDATE = new Image(FileHandler.getStream(imageFolder+"update-icon.png"));
	public static final Image ICON_PLUS = new Image(FileHandler.getStream(imageFolder+"plus.png"));
	public static final Image ICON_AMP = new Image(FileHandler.getStream(imageFolder+"amp.png"));
	public static final Image ICON_EDIT_LIST = new Image(FileHandler.getStream(imageFolder+"edit-list.png"));
	public static final Image ICON_DISK = new Image(FileHandler.getStream(imageFolder+"disk.png"));
	 
	public static final Image ICON_PENCIL = new Image(FileHandler.getStream(imageFolder+"pencil.png"));
	public static final Image ICON_REMOVE = new Image(FileHandler.getStream(imageFolder+"cross.png"));
	public static final Image ICON_DOCUMENT = new Image(FileHandler.getStream(imageFolder+"folder-open-document.png"));
	public static final Image ICON_ARROW_LEFT = new Image(FileHandler.getStream(imageFolder+"arrow-000.png"));
	public static final Image ICON_ARROR_RIGHT = new Image(FileHandler.getStream(imageFolder+"arrow-180.png"));
	public static final Image ICON_PASTE = new Image(FileHandler.getStream(imageFolder+"clipboard-paste.png"));
	public static final Image ICON_ASTTERISK_YELLOW = new Image(FileHandler.getStream(imageFolder+"asterisk-yellow.png"));
	public static final Image ICON_BINOCULAR = new Image(FileHandler.getStream(imageFolder+"binocular.png"));
	public static final Image ICON_BINOCULAR_2 = new Image(FileHandler.getStream(imageFolder+"binocular--arrow.png"));
	
	public static final Image ICON_3_ORANGE = new Image(FileHandler.getStream(imageFolder+"orange/003.png"));
	public static final Image ICON_2_BLUE = new Image(FileHandler.getStream(imageFolder+"blue/002.png"));
	
	private Builder dialogBuilder;
	
	public JavaFXHandler(){
		main = new MainWindow();
		main.runApplication();
		dialogBuilder = new Builder();
	}
	

	public boolean runAndWait(Runnable action) {
	    if (action == null){
	        System.err.println("No action given to run on JavaFX Thread.");
	        return false;
	    }

	    if (Platform.isFxApplicationThread()) {
	        action.run();
	        return true;
	    }
	
	    final CountDownLatch doneLatch = new CountDownLatch(1);
	    Platform.runLater(() -> {
	        try {
	            action.run();
	        } finally {
	            doneLatch.countDown();
	        }
	    });
	    try {
	        doneLatch.await();
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	    return true;
	}
	
	public MainWindow getMainWindow() {
		return main;
	}

	public void setMainWindow(MainWindow main){
		this.main = main;
	}

	public Builder getDialogBuilder() {
		return dialogBuilder;
	}

}