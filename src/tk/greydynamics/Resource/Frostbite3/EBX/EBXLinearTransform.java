package tk.greydynamics.Resource.Frostbite3.EBX;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;
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
		Matrix4f matrix = new Matrix4f();
		//Create Transformation Matrix from Data.
		matrix.m00 = this.tranformations.get(0).getX();
		matrix.m01 = this.tranformations.get(0).getY();
		matrix.m02 = this.tranformations.get(0).getZ();
		
		matrix.m10 = this.tranformations.get(1).getX();
		matrix.m11 = this.tranformations.get(1).getY();
		matrix.m12 = this.tranformations.get(1).getZ();
		
		matrix.m20 = this.tranformations.get(2).getX();
		matrix.m21 = this.tranformations.get(2).getY();
		matrix.m22 = this.tranformations.get(2).getZ();
		
		matrix.m03 = this.tranformations.get(3).getX();
		matrix.m13 = this.tranformations.get(3).getY();
		matrix.m23 = this.tranformations.get(3).getZ();
		
				
		Matrix4f invertedRotationMatrix = (Matrix4f) Matrices.createTransformationMatrix(getTranformation(), getRotation(), new Vector3f(1.0f, 1.0f, 1.0f)).invert();

		Matrix4f scaleMatrix = Matrix4f.mul(matrix, invertedRotationMatrix, null);
//		System.out.println(scaleMatrix);
				
		return new Vector3f(scaleMatrix.m00, scaleMatrix.m11, scaleMatrix.m22);
	}
}
