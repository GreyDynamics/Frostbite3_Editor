package tk.greydynamics.Resource.Frostbite3.EBX.Event;

import java.util.ArrayList;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;

public class EBXLinkConnection {
	private String sourceGUID;
	private EBXInstance sourceInstance;
	private String targetGUID;
	private EBXInstance targetInstance;
	private int sourceFieldId = -1;
	private EBXField sourceField = null;
	private int targetFieldId = -1;
	private EBXField targetField = null;
	
	public EBXLinkConnection(EBXComplex linkConnectionComplex, EBXFile ebxFile, boolean tryLoad, boolean loadOriginal) {
		if (linkConnectionComplex!=null){
			EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
			for (EBXField field : linkConnectionComplex.getFields()){
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
						case "SourceFieldId":
							if (field.getValue() != null){
								this.sourceFieldId = (int) field.getValue();
								if (this.sourceInstance!=null){
									EBXField sourceFieldQuery = EBXHandler.getEBXField(this.sourceInstance, this.sourceFieldId);
									if (sourceFieldQuery!=null){
										this.sourceField = sourceFieldQuery;
									}
								}
							}
							break;
						case "TargetFieldId":
							if (field.getValue() != null){
								this.targetFieldId = (int) field.getValue();
								if (this.targetInstance!=null){
									EBXField targetFieldQuery = EBXHandler.getEBXField(this.targetInstance, this.targetFieldId);
									if (targetFieldQuery!=null){
										this.targetField = targetFieldQuery;
									}
								}
							}
							break;
						default:
							System.err.println("LinkConnectionFieldName is unknown! "+field.getFieldDescritor().getName());
						}
					}else{
						System.err.println("LinkConnectionFieldDescriptor is null!");
					}
				}else{
					System.err.println("LinkConnectionField is null!");
				}
			}
		}else{
			System.err.println("LinkConnectionComplex is null!");
		}
	}

	public String getSourceGUID() {
		return sourceGUID;
	}

	public String getTargetGUID() {
		return targetGUID;
	}

	public int getSourceFieldId() {
		return sourceFieldId;
	}

	public int getTargetFieldId() {
		return targetFieldId;
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

	public EBXField getSourceField() {
		return sourceField;
	}

	public void setSourceField(EBXField sourceField) {
		this.sourceField = sourceField;
	}

	public EBXField getTargetField() {
		return targetField;
	}

	public void setTargetField(EBXField targetField) {
		this.targetField = targetField;
	}
}
