package tk.greydynamics.Resource.Frostbite3.EBX.Event;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;

public class EventGraphParser {
	public static boolean parseConnections(EBXFile ebxFile, ArrayList<EBXPropertyConnection> targetPropertyConnections, ArrayList<EBXLinkConnection> targetLinkConnections, ArrayList<EBXEventConnection> targetEventConnections, HashMap<EBXInstance, AnchorPane> nodes, boolean tryLoad, boolean loadOriginal){
		if (ebxFile!=null){
			ArrayList<EBXField> propertyConnectionArrayFields = EBXHandler.getEBXField(ebxFile.getInstances().get(0), null, "PropertyConnections", FieldValueType.ArrayComplex);
			ArrayList<EBXField> linkConnectionArrayFields = EBXHandler.getEBXField(ebxFile.getInstances().get(0), null, "LinkConnections", FieldValueType.ArrayComplex);
			ArrayList<EBXField> eventConnectionArrayFields = EBXHandler.getEBXField(ebxFile.getInstances().get(0), null, "EventConnections", FieldValueType.ArrayComplex);
			
			
			if (propertyConnectionArrayFields.size()>0){
				EBXField propertyConnectionArray = propertyConnectionArrayFields.get(0);
				for (EBXField propertyConnectionMember : propertyConnectionArray.getValueAsComplex().getFields()){
					try{
						targetPropertyConnections.add(new EBXPropertyConnection(propertyConnectionMember.getValueAsComplex(), ebxFile, tryLoad, loadOriginal));
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			
			if (linkConnectionArrayFields.size()>0){
				EBXField linkConnectionArray = linkConnectionArrayFields.get(0);
				for (EBXField linkConnectionMember : linkConnectionArray.getValueAsComplex().getFields()){
					try{
						targetLinkConnections.add(new EBXLinkConnection(linkConnectionMember.getValueAsComplex(), ebxFile, tryLoad, loadOriginal));
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			
			if (eventConnectionArrayFields.size()>0){
				EBXField eventConnectionArray = eventConnectionArrayFields.get(0);
				for (EBXField eventConnectionMember : eventConnectionArray.getValueAsComplex().getFields()){
					try{
						targetEventConnections.add(new EBXEventConnection(eventConnectionMember.getValueAsComplex(), ebxFile, tryLoad, loadOriginal));
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			
			AnchorPane propColorTemplate = new AnchorPane();
			propColorTemplate.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(10), null)));
			
			AnchorPane linkColorTemplate = new AnchorPane();
			linkColorTemplate.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(10), null)));
			
			AnchorPane evntColorTemplate = new AnchorPane();
			evntColorTemplate.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(10), null)));
			
			
			
			for (EBXPropertyConnection propConnection : targetPropertyConnections){
				nodes.put(propConnection.getSourceInstance(), propColorTemplate);
				nodes.put(propConnection.getTargetInstance(), propColorTemplate);
			}
			for (EBXLinkConnection linkConnection : targetLinkConnections){
				nodes.put(linkConnection.getSourceInstance(), linkColorTemplate);
				nodes.put(linkConnection.getTargetInstance(), linkColorTemplate);
			}
			for (EBXEventConnection evntConnection : targetEventConnections){
				nodes.put(evntConnection.getSourceInstance(), evntColorTemplate);
				nodes.put(evntConnection.getTargetInstance(), evntColorTemplate);
			}
			
			
			return true;
		}
		System.err.println("EventGraphParser was not able to parse connections!");
		return false;
	}
}
