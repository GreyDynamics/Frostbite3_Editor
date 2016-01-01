package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXTextureShaderParameter extends EBXStructureEntry{
	public static enum ParameterName {Diffuse, Normal, SpecSmooth, DetailNormalTexture,
		Specular, DetailNormal, Break_Diffuse, Break_Normal, SpecMask,
		BarkNormal, DiffuseBark, DiffuseLeaves, LeavesNormal};
	
	private String parameterName = null;
	private EBXExternalGUID value = null;
	
	public EBXTextureShaderParameter(EBXStructureEntry parent, EBXComplex complex) {
		super(parent, EntryType.TextureShaderParameter);
		
		for (EBXField field : complex.getFields()) {
			switch (field.getFieldDescritor().getName()) {
			case "ParameterName": /* -------------- ParameterName -------------- */
				this.parameterName = (String) field.getValue();
				break;
			case "Value": /* -------------- Value -------------- */
				this.value = new EBXExternalGUID(field);
				break;
			}
		}
	}

	public String getParameterName() {
		return parameterName;
	}

	public EBXExternalGUID getValue() {
		return value;
	}
	
	
}
