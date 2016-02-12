package tk.greydynamics.Resource.Frostbite3.EBX;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Maths.Matrices;

public class EBXLinearTransform {
	
	private ArrayList<Vector3f> tranformations;
		
	//Consturctor
	public EBXLinearTransform(ArrayList<Vector3f> tranformations){
		this.tranformations = tranformations;
	}
	
	public EBXLinearTransform(EBXComplex ebxComplex){
		this.tranformations = new ArrayList<>();
		if (ebxComplex.getComplexDescriptor().getName().equals("LinearTransform")){
			for (EBXField field : ebxComplex.getFields()){
				for (EBXField part : ebxComplex.getFields()){
					Vector3f vec3 = new Vector3f();
					EBXComplex partComplex = part.getValueAsComplex();
					for (EBXField v : partComplex.getFields()){
						switch (v.getFieldDescritor().getName()){
							case "x":
								if (v.getValue()==null){
									vec3.setX(-1f);
									break;
								}
								vec3.setX((float) v.getValue());
								break;
							case "y":
								if (v.getValue()==null){
									vec3.setY(-1f);
									break;
								}
								vec3.setY((float) v.getValue());
								break;
							case "z":
								if (v.getValue()==null){
									vec3.setZ(-1f);
									break;
								}
								vec3.setZ((float) v.getValue());
								break;
						}
					}
					this.tranformations.add(vec3);
				}
			}
			if (this.tranformations.get(0).getX()==-1.0f&&this.tranformations.get(0).getY()==-1.0f&&this.tranformations.get(0).getZ()==-1.0f){
				//LinearTransform without data. Fix this.
				this.tranformations.set(0, new Vector3f(1.0f, 0.0f, 0.0f));
				this.tranformations.set(1, new Vector3f(0.0f, 1.0f, 0.0f));
				this.tranformations.set(2, new Vector3f(0.0f, 0.0f, 1.0f));
				this.tranformations.set(4, new Vector3f(0.0f, 0.0f, 0.0f));
			}
		}
	}
	
	//Getter and Setter
	
	public Vector3f getRotation(){
		return Matrices.getRotationInEulerAngles(this.tranformations.get(0), this.tranformations.get(1), this.tranformations.get(2));
	}
	
	public Vector3f getTranformation(){
		return this.tranformations.get(3);
	}

	public Vector3f getScaling() {
		System.err.println("Can't get Scaling from EBXLinearTransform!\n"
							+ "No function for this implemented yet.");
		return new Vector3f(this.tranformations.get(0).length(), this.tranformations.get(1).length(), this.tranformations.get(2).length());
	}
}
