package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Maths.Matrices;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry.EBXObjVec3.Component;

public class EBXBlueprintTransform {
	
	private BlueprintTransformType type;
	private ArrayList<EBXObjVec3> tranformations;
	public static boolean IsDIRECT3D = true;
	
	public static enum BlueprintTransformType{
		LinearTransform, 
	}
	
	//Consturctor
	public EBXBlueprintTransform(BlueprintTransformType type, ArrayList<EBXObjVec3> tranformations){
		this.type = type;
		this.tranformations = tranformations;
	}
	
	public EBXBlueprintTransform(EBXComplex ebxComplex){
		this.tranformations = new ArrayList<>();
		if (ebxComplex.getComplexDescriptor().getName().equals("LinearTransform")){
			for (EBXField field : ebxComplex.getFields()){
				EBXObjVec3 transVec3 = EBXObjVec3.getTransformVec3(field);
				if (transVec3!=null){
					tranformations.add(transVec3);
					//System.out.println(transVec3.getName() +" "+ transVec3.getX()+" "+ transVec3.getY()+" "+ transVec3.getZ());
				}else{
					System.err.println("EBXObjBlueprintTransform constructor has a problem with reading a Transformation (null)!");
				}
			}
			this.type=BlueprintTransformType.LinearTransform;
		}else{
			System.err.println("Can not read in EBXObjBlueprintTransform because type is unknown!");
		}
	}
	
	//Getter and Setter
	public BlueprintTransformType getType() {
		return type;
	}

	public void setType(BlueprintTransformType type) {
		this.type = type;
	}


	public ArrayList<EBXObjVec3> getTranformations() {
		return tranformations;
	};	
	
	public EBXObjVec3 getEBXVector(EBXObjVec3.Component enumComponent){
		for (EBXObjVec3 vec : tranformations){
			if (vec.getName().equalsIgnoreCase(enumComponent.toString())){
				return vec;
			}
		}
		return null;
	}
	
	public Vector3f getRotation(){
		if (type==BlueprintTransformType.LinearTransform){
			return Matrices.getRotationInEulerAngles(getEBXVector(Component.RIGHT).getVector(), getEBXVector(Component.UP).getVector(), getEBXVector(Component.FORWARD).getVector(), IsDIRECT3D);
		}
		//System.err.println("Can't get Rotation from "+type+" in EBXBlueprintTransform's method!");
		return null;
	}
	
	public Vector3f getTranformation(){
		if (type==BlueprintTransformType.LinearTransform){
			return getEBXVector(Component.TRANS).getVector();
		}
		//System.err.println("Can't get Transformation from "+type+" in EBXBlueprintTransform's method!");
		return null;
	}

	public Vector3f getScaling() {
		System.err.println("Can't get Scaling from EBXBlueprintTransform!\n"
							+ "No function for this implemented yet.");
		return new Vector3f(1.0f, 1.0f, 1.0f);
	}
}
