package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;

public class EBXObjVec3 {
	
	public enum Component {
		RIGHT, UP, FORWARD, TRANS
	};
	
	private float x = 0f, y = 0f, z = 0f;
	private int offset = -1;
	private String name;
	
	
	//Constructors
	
	public static EBXObjVec3 getTransformVec3(EBXField complexField){
		EBXObjVec3 transVec3 = new EBXObjVec3(complexField.getFieldDescritor().getName());
		
		for (EBXField axis : complexField.getValueAsComplex().getFields()){
			switch (axis.getFieldDescritor().getName()){
				case "x":
					transVec3.setX((float) axis.getValue());
					transVec3.setOffset(axis.getOffset());
					break;
				case "y":
					transVec3.setY((float) axis.getValue());
					break;
				case "z":
					transVec3.setZ((float) axis.getValue());
					break;
			}
		}
		return transVec3;
	}
	
	
	public EBXObjVec3(String name) {
		this.name = name;
		this.x = 0f;
		this.y = 0f;
		this.z = 0f;
	}


	public EBXObjVec3(String name, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
	}
	
	
	//Getter and Setter
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public int getOffset() {
		return offset;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}


	public Vector3f getVector(){
		return new Vector3f(x, y, z);
	}
	public Vector3f getVector(float mulX, float mulY, float mulZ){
		return new Vector3f(x*mulX, y*mulY, z*mulZ);
	}
	
	
}
