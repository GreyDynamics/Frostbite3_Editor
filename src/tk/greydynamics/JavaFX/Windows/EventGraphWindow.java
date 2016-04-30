package tk.greydynamics.JavaFX.Windows;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.Event.EBXEventConnection;
import tk.greydynamics.Resource.Frostbite3.EBX.Event.EBXLinkConnection;
import tk.greydynamics.Resource.Frostbite3.EBX.Event.EBXPropertyConnection;
import tk.greydynamics.Resource.Frostbite3.EBX.Event.EventGraphParser;

public class EventGraphWindow {
	private boolean survivable = false;
	
	private Stage stage = new Stage();
	private Scene scene;
	private EBXFile ebxFile;
	
	private ArrayList<EBXPropertyConnection> propertyConnections = new ArrayList<>();
	private ArrayList<EBXLinkConnection> linkConnections = new ArrayList<>();
	private ArrayList<EBXEventConnection> eventConnections = new ArrayList<>();
	
//	private ArrayList<EBXInstance> usedInstances = new ArrayList<>();
//	private ArrayList<AnchorPane> nodes = new ArrayList<>();
	
	private HashMap<EBXInstance, AnchorPane> nodes = new HashMap<>();
	
	
	private ArrayList<Line> lines = new ArrayList<>();
	
	private double orgSceneX, orgSceneY;
	
	public EventGraphWindow(EBXFile ebxFile, boolean isOriginal, boolean tryLoad, boolean loadOriginal) {
		this.ebxFile = ebxFile;
		boolean success = EventGraphParser.parseConnections(ebxFile, propertyConnections, linkConnections, eventConnections, nodes, tryLoad, loadOriginal);
		
		stage.setTitle("EventGraph of "+ebxFile.getTruePath()+" - "+propertyConnections.size()+" PropertyConnections, "+linkConnections.size()+" LinkConnections, "+eventConnections.size()+" EventConnections, "+nodes.size()+" Nodes");
		if (success){
		
//			Group root = new Group();
			AnchorPane root = new AnchorPane();
			root.setOnMouseDragged(canvasDraggedEventHandler);
			root.setOnMousePressed(canvasPressedEventHandler);
			scene = new Scene(root, 500, 500);
			
			
//			AnchorPane node1 = createNodePane("master1", 100, 100, 50, 60, Color.ALICEBLUE);
//			AnchorPane node2 = createNodePane("master2", 100, 200, 50, 60, Color.RED);
//			root.getChildren().addAll(node1, node2);
//			connect(node1, node2);
			
			//Create a Node of each Instance
			HashMap<EBXInstance, AnchorPane> newNodes = new HashMap<>();
			for (EBXInstance instance : nodes.keySet()){
				if (instance!=null){
					AnchorPane nodePane = createNodePane(instance.getComplex().getComplexDescriptor().getName()+"\n"+instance.getGuid(),100, 200, 50, 60, nodes.get(instance).getBackground());
					newNodes.put(instance, nodePane);
				}
//				connect(node1, nodePane);
			}
			nodes = newNodes;
			
			
			//Connect the nodes
			for (EBXPropertyConnection propConnection : propertyConnections){
				try{
					AnchorPane sourcePane = nodes.get(propConnection.getSourceInstance());
					String sourceText = "";
					if (propConnection.getSourceField()!=null){
						sourceText += propConnection.getSourceField().getFieldDescritor().getName()+" ("+propConnection.getSourceFieldId()+")";
					}else{
						sourceText += propConnection.getSourceFieldId();
					}
					int transYsource = createNodeIO(sourcePane, sourceText, false);
					AnchorPane targetPane = nodes.get(propConnection.getTargetInstance());
					String targetText = "";
					if (propConnection.getTargetField()!=null){
						targetText += propConnection.getTargetField().getFieldDescritor().getName()+" ("+propConnection.getTargetFieldId()+")";
					}else{
						targetText += propConnection.getTargetFieldId();
					}
					int transYtarget = createNodeIO(targetPane, targetText, true);
					connect(sourcePane, targetPane, transYsource, transYtarget, Color.BLUE);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			for (EBXLinkConnection linkConnection : linkConnections){
				try{
					AnchorPane sourcePane = nodes.get(linkConnection.getSourceInstance());
					String sourceText = "";
					if (linkConnection.getSourceField()!=null){
						sourceText += linkConnection.getSourceField().getFieldDescritor().getName()+" ("+linkConnection.getSourceFieldId()+")";
					}else{
						sourceText += linkConnection.getSourceFieldId();
					}
					int transYsource = createNodeIO(sourcePane, sourceText, true);
					AnchorPane targetPane = nodes.get(linkConnection.getTargetInstance());
					String targetText = "";
					if (linkConnection.getTargetField()!=null){
						targetText += linkConnection.getTargetField().getFieldDescritor().getName()+" ("+linkConnection.getTargetFieldId()+")";
					}else{
						targetText += linkConnection.getTargetFieldId();
					}
					int transYtarget = createNodeIO(targetPane, targetText, false);
					connect(targetPane, sourcePane, transYtarget, transYsource, Color.DARKGREEN);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			for (EBXEventConnection evntConnection : eventConnections){
				try{
					AnchorPane sourcePane = nodes.get(evntConnection.getSourceInstance());
					int transYsource = createNodeIO(sourcePane, String.valueOf(evntConnection.getSourceEventID()), false);
					AnchorPane targetPane = nodes.get(evntConnection.getTargetInstance());
					int transYtarget = createNodeIO(targetPane, String.valueOf(evntConnection.getTargetEventID()), true);
					connect(sourcePane, targetPane, transYsource, transYtarget, Color.ORANGERED);
				}catch (Exception e){
					e.printStackTrace();
				}
				
			}

			
			//Put all together
			root.getChildren().addAll(lines);
			for (Node n : nodes.values()){
				root.getChildren().add(n);
			}
			
			stage.setScene(scene);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent e) {
					Core.getJavaFXHandler().getMainWindow().destroyEventGraphWindow(stage);
					e.consume();
				}
			});
			stage.getIcons().add(JavaFXHandler.ICON_APPLICATION16);
		    stage.getIcons().add(JavaFXHandler.ICON_APPLICATION32);
			stage.show();
			survivable = true;
		}else{
			System.err.println("EventGraphWindow could not be created!");
			survivable = false;
		}
	}	
    private EventHandler<MouseEvent> nodePressedEventHandler = (t) ->
    {
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();    
        Node c = (Node) (t.getSource());      
        c.toFront();
    };
    private EventHandler<MouseEvent> nodeDraggedEventHandler = (t) ->
    {
        double offsetX = t.getSceneX() - orgSceneX;
        double offsetY = t.getSceneY() - orgSceneY;
 
        Node c = (Node) (t.getSource());
 
        c.setTranslateX(c.getTranslateX() + offsetX);
        c.setTranslateY(c.getTranslateY() + offsetY);
 
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();
    };
    
    private EventHandler<MouseEvent> canvasPressedEventHandler = (t) ->
    {
    	orgSceneX = t.getSceneX();
    	orgSceneY = t.getSceneY();    
//        Node c = (Node) (t.getSource());      
//        c.toFront();
    };
    private EventHandler<MouseEvent> canvasDraggedEventHandler = (t) ->
    {
    	
        double offsetX = t.getSceneX() - orgSceneX;
        double offsetY = t.getSceneY() - orgSceneY;
        
        AnchorPane c = (AnchorPane) (t.getSource());
 
        for (Node child : c.getChildren()){
        	if (child instanceof Line){
        		continue;
        	}
        	child.setTranslateX(child.getTranslateX() + offsetX);
        	child.setTranslateY(child.getTranslateY() + offsetY);
        }
 
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();
    };
    
    private AnchorPane createNodePane(String name, double x, double y, double height, double width, Background background)
    {
    	AnchorPane anchorPane = new AnchorPane();
    	anchorPane.setTranslateX(Core.random.nextInt(800));
    	anchorPane.setTranslateY(Core.random.nextInt(800));
//    	anchorPane.setPrefWidth(width);
//    	anchorPane.setPrefHeight(height);
    	anchorPane.setBackground(background);
 
    	anchorPane.setCursor(Cursor.CROSSHAIR);
 
    	anchorPane.setOnMousePressed(nodePressedEventHandler);
    	anchorPane.setOnMouseDragged(nodeDraggedEventHandler);

    	
    	BorderPane borderPane = new BorderPane();
    	Label nameLabel = new Label(name);
    	nameLabel.setTextAlignment(TextAlignment.CENTER);
    	nameLabel.setAlignment(Pos.TOP_CENTER);
    	nameLabel.setPadding(new Insets(5));
    	borderPane.setTop(nameLabel);
    	
    	VBox leftVBox = new VBox();
    	VBox rightVBox = new VBox();
    	borderPane.setLeft(leftVBox);
    	borderPane.setRight(rightVBox);
    	
    	
    	
    	anchorPane.getChildren().add(borderPane);
 
        return anchorPane;
    }
    
	private int createNodeIO(AnchorPane nodePane, String fieldID, boolean isInput){
		int transY = 55;
		BorderPane borderPane = (BorderPane) nodePane.getChildren().get(0);
		RadioButton radioButton = new RadioButton();
		radioButton.setSelected(true);
		if (isInput){
			VBox leftBox = (VBox) borderPane.getLeft();
			radioButton.setText(fieldID);
			transY += leftBox.getChildren().size()*17;
			leftBox.getChildren().add(radioButton);
		}else{
			VBox rightBox = (VBox) borderPane.getRight();
			HBox hBox = new HBox();
			hBox.setAlignment(Pos.TOP_RIGHT);
			Label label = new Label(fieldID);
			label.setPadding(new Insets(0, 5, 0, 5));
			hBox.getChildren().add(label);
			radioButton.setText("");
			transY += rightBox.getChildren().size()*17;
			hBox.getChildren().add(radioButton);
			rightBox.getChildren().add(hBox);
		}
		return transY;
	}
 
    private Line connect(AnchorPane c1, AnchorPane c2, int transYsource, int transYtarget, Color color)
    {
        Line line = new Line();
        
        line.startXProperty().bind(c1.translateXProperty().add(c1.widthProperty()));
        line.startYProperty().bind(c1.translateYProperty().add(transYsource));
 
        line.endXProperty().bind(c2.translateXProperty().add(0));
        line.endYProperty().bind(c2.translateYProperty().add(transYtarget));
        
//        line.startXProperty().bind(c1.translateXProperty().add(c1.prefWidthProperty().divide(2)));
//        line.startYProperty().bind(c1.translateYProperty().add(c1.prefHeightProperty().divide(2)));
// 
//        line.endXProperty().bind(c2.translateXProperty().add(c2.prefWidthProperty().divide(2)));
//        line.endYProperty().bind(c2.translateYProperty().add(c2.prefHeightProperty().divide(2)));
//        
 
        line.setStrokeWidth(3);
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        line.setStroke(color);
        line.getStrokeDashArray().setAll(1.0, 4.0);
        
        this.lines.add(line);
        return line;
    }
    
	public ArrayList<EBXPropertyConnection> getPropertyConnections() {
		return propertyConnections;
	}

	public ArrayList<EBXLinkConnection> getLinkConnections() {
		return linkConnections;
	}

	public ArrayList<EBXEventConnection> getEventConnections() {
		return eventConnections;
	}

	public Stage getStage() {
		return stage;
	}

	public Scene getScene() {
		return scene;
	}

	public EBXFile getEbxFile() {
		return ebxFile;
	}

	public boolean isSurvivable() {
		return survivable;
	}
	
	
}
