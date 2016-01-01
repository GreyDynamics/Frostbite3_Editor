package tk.greydynamics.JavaFX;

import javafx.scene.control.TreeItem;

public class TreeViewUtils {
	public static TreeItem<TreeViewEntry> filter(TreeItem<TreeViewEntry> source, String filterStr){
		if (filterStr==null){
			return source;
		}
		TreeItem<TreeViewEntry> filtered = new TreeItem<TreeViewEntry>(source.getValue());
		for (TreeItem<TreeViewEntry> entry : source.getChildren()){
			TreeItem<TreeViewEntry> filteredEntry = filter(entry, filterStr);
			if (filteredEntry!=null){
				filtered.getChildren().add(filteredEntry);
			}
		}
		if (filtered.getChildren().size()>=1||filtered.getValue().getName().contains(filterStr)){
			filtered.setGraphic(source.getGraphic());
			filtered.setExpanded(source.isExpanded());
			return filtered;
		}else{
			return null;
		}
	}
}
