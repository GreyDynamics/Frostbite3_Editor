package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import java.util.HashMap;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXReferencedObjectData extends EBXStructureEntry {

	private EBXExternalGUID blueprint = null;
	private EBXBlueprintTransform blueprintTransform = null;
	private EBXExternalGUID objectVariation = null;
	private EBXObjEnum streamRealm = null;
	private EBXObjEnum radiosityTypeOverride = null;
	private boolean castSunShadowEnable = false;
	private boolean castReflectionEnable = false;
	private boolean excluded = false;

	public EBXReferencedObjectData(EBXStructureEntry parent, EBXComplex complex) {
		super(parent, EntryType.ReferenceObjectData);
		for (EBXField field : complex.getFields()) {
			switch (field.getFieldDescritor().getName()) {
			case "Blueprint": /* -------------- Blueprint -------------- */
				this.blueprint = new EBXExternalGUID(field);
				break;
			case "BlueprintTransform": /* -------------- BlueprintTransform -------------- */
				this.blueprintTransform = new EBXBlueprintTransform(field.getValueAsComplex());
				break;
			case "ObjectVariation": /* -------------- BlueprintTransform -------------- */
				this.objectVariation = new EBXExternalGUID(field);
				break;
			case "StreamRealm": /* -------------- StreamRealm -------------- */
				this.streamRealm = new EBXObjEnum((HashMap<?, ?>) field.getValue(), true);
				break;
			case "RadiosityTypeOverride": /* -------------- RadiosityTypeOverride -------------- */
				this.radiosityTypeOverride = new EBXObjEnum((HashMap<?, ?>) field.getValue(), true);
				break;
			case "CastSunShadowEnable": /* -------------- CastSunShadowEnable -------------- */
				this.castSunShadowEnable = (boolean) field.getValue();
				break;
			case "CastReflectionEnable": /* -------------- CastReflectionEnable -------------- */
				this.castReflectionEnable = (boolean) field.getValue();
				break;
			case "Excluded": /* -------------- Excluded -------------- */
				this.excluded = (boolean) field.getValue();
				break;
			}
		}
	}

	public EBXExternalGUID getBlueprint() {
		return blueprint;
	}

	public void setBlueprint(EBXExternalGUID blueprint) {
		this.blueprint = blueprint;
	}

	public EBXExternalGUID getObjectVariation() {
		return objectVariation;
	}

	public void setObjectVariation(EBXExternalGUID objectVariation) {
		this.objectVariation = objectVariation;
	}

	public EBXBlueprintTransform getBlueprintTransform() {
		return blueprintTransform;
	}

	public void setBlueprintTransform(
			EBXBlueprintTransform blueprintTransform) {
		this.blueprintTransform = blueprintTransform;
	}

	public EBXObjEnum getStreamRealm() {
		return streamRealm;
	}

	public void setStreamRealm(EBXObjEnum streamRealm) {
		this.streamRealm = streamRealm;
	}

	public EBXObjEnum getRadiosityTypeOverride() {
		return radiosityTypeOverride;
	}

	public void setRadiosityTypeOverride(EBXObjEnum radiosityTypeOverride) {
		this.radiosityTypeOverride = radiosityTypeOverride;
	}

	public boolean isCastSunShadowEnable() {
		return castSunShadowEnable;
	}

	public void setCastSunShadowEnable(boolean castSunShadowEnable) {
		this.castSunShadowEnable = castSunShadowEnable;
	}

	public boolean isCastReflectionEnable() {
		return castReflectionEnable;
	}

	public void setCastReflectionEnable(boolean castReflectionEnable) {
		this.castReflectionEnable = castReflectionEnable;
	}

	public boolean isExcluded() {
		return excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}

}
