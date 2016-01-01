package tk.greydynamics.JavaFX;



import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import tk.greydynamics.JavaFX.Windows.MainWindow.EntryType;

public class TreeViewEntry implements Cloneable{

	private String name;
	private String tooltip;
	private ImageView graphic;
	private Object value;
	private EntryType type;
	private short ebxType;
	
	private Color backgroundColor;
	
	private int offset;
	
	
	public TreeViewEntry(String name, ImageView graphic, Object value, EntryType type) {
		this.name = name;
		this.graphic = graphic;
		this.value = value;
		this.type = type;
		this.ebxType = 0;
		this.tooltip = null;
		this.offset = -1;
		this.backgroundColor = null;
	}
	
	
	
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}




	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}




	public short getEbxType() {
		return ebxType;
	}




	public String getTooltip() {
		return tooltip;
	}



	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ImageView getGraphic() {
		return graphic;
	}
	public void setGraphic(ImageView graphic) {
		this.graphic = graphic;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public EntryType getType() {
		return type;
	}
	public void setType(EntryType type) {
		this.type = type;
	}
	
	public short getEBXType() {
		return ebxType;
	}

	public void setEBXType(short ebxType) {
		this.ebxType = ebxType;
	}
	

	public int getOffset() {
		return offset;
	}



	public void setOffset(int offset) {
		this.offset = offset;
	}



	public TreeViewEntry clone(){
		try {
			return (TreeViewEntry) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
