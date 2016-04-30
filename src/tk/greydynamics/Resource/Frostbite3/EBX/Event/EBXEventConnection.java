package tk.greydynamics.Resource.Frostbite3.EBX.Event;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplexDescriptor;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXEnumHelper;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;

public class EBXEventConnection {
	private String sourceGUID;
	private EBXInstance sourceInstance;
	private String targetGUID;
	private EBXInstance targetInstance;
	private int sourceEventID = -1;
	private EBXField sourceEventField = null;//TODO
	private int targetEventID = -1;
	private EBXField targetEventField = null;//TODO
	private String targetType;
	
	public EBXEventConnection(EBXComplex eventConnectionComplex, EBXFile ebxFile, boolean tryLoad, boolean loadOriginal) {
		if (eventConnectionComplex!=null){
			EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
			for (EBXField field : eventConnectionComplex.getFields()){
				if (field!=null){
					if (field.getFieldDescritor()!=null){
						switch (field.getFieldDescritor().getName()){
						case "Source":
							if (field.getValue() instanceof EBXExternalGUID){
								EBXExternalGUID sourceExternalGUID = (EBXExternalGUID) field.getValue();
								this.sourceGUID = sourceExternalGUID.getBothGUIDsWithSpace();
								this.sourceInstance = ebxHandler.getEBXInstance(sourceExternalGUID, tryLoad, loadOriginal);
							}else{
								this.sourceGUID = (String) field.getValue();
								this.sourceInstance = EBXHandler.getEBXInstance(ebxFile, this.sourceGUID);
							}
							break;
						case "Target":
							if (field.getValue() instanceof EBXExternalGUID){
								EBXExternalGUID targetExternalGUID = (EBXExternalGUID) field.getValue();
								this.targetGUID = targetExternalGUID.getBothGUIDsWithSpace();
								this.targetInstance = ebxHandler.getEBXInstance(targetExternalGUID, tryLoad, loadOriginal);
							}else{
								this.targetGUID = (String) field.getValue();
								this.targetInstance = EBXHandler.getEBXInstance(ebxFile, this.targetGUID);
							}
							break;
						case "SourceEvent":
							if (field.getValue() instanceof EBXComplex){
								EBXComplex sourceEventSpec = field.getValueAsComplex();
								this.sourceEventID = (int) sourceEventSpec.getField(0).getValue();
							}else{
								System.err.println("EventConnectionsSourceEvent is unknown type!");
								this.sourceEventID = 0;
							}
							break;
						case "TargetEvent":
							if (field.getValue() instanceof EBXComplex){
								EBXComplex targetEventSpec = field.getValueAsComplex();
								this.targetEventID = (int) targetEventSpec.getField(0).getValue();
							}else{
								System.err.println("EventConnectionsTargetEvent is unknown type!");
								this.targetEventID = 0;
							}
							break;
						case "TargetType":
							if (field.getValue() instanceof EBXComplexDescriptor){
								this.targetType = null;
							}else if (field.getValue() instanceof EBXEnumHelper){
								EBXEnumHelper enumHelper = (EBXEnumHelper) field.getValue();
								this.targetType = enumHelper.getEntries().get(enumHelper.getSelectedIndex()).getName();
							}else{
								System.err.println("EventConnectionTargetType is unknown type!");
								this.targetType = null;
							}
							break;
						default:
							System.err.println("EventConnectionFieldName is unknown! "+field.getFieldDescritor().getName());
						}
					}else{
						System.err.println("EventConnectionFieldDescriptor is null!");
					}
				}else{
					System.err.println("EventConnectionField is null!");
				}
			}
		}else{
			System.err.println("EventConnectionComplex is null!");
		}
	}

	public String getSourceGUID() {
		return sourceGUID;
	}

	public String getTargetGUID() {
		return targetGUID;
	}

	public int getSourceEventID() {
		return sourceEventID;
	}

	public int getTargetEventID() {
		return targetEventID;
	}

	public String getTargetType() {
		return targetType;
	}

	public EBXInstance getSourceInstance() {
		return sourceInstance;
	}

	public void setSourceInstance(EBXInstance sourceInstance) {
		this.sourceInstance = sourceInstance;
	}

	public EBXInstance getTargetInstance() {
		return targetInstance;
	}

	public void setTargetInstance(EBXInstance targetInstance) {
		this.targetInstance = targetInstance;
	}	
	
	
}
