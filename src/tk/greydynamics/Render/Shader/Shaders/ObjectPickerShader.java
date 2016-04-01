package tk.greydynamics.Render.Shader.Shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Render.Shader.ShaderProgram;

public class ObjectPickerShader extends ShaderProgram{
	
	private int transMatrixID;
	private int projeMatrixID;
	private int viewMatrixID;
	private int pickerColorID;
	private int pickerID;

	public ObjectPickerShader() {
		super("res/shader/ObjectPickerShader.vert", "res/shader/ObjectPickerShader.frag");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		transMatrixID = super.getUniformLocation("transformationMatrix");
		projeMatrixID = super.getUniformLocation("projectionMatrix");
		viewMatrixID = super.getUniformLocation("viewMatrix");
		pickerColorID = super.getUniformLocation("pickerColor");
	}
	
	public void loadTransformationMatrix(Matrix4f mtx){
		super.loadMatrix(transMatrixID, mtx);
	}
	
	public void loadProjectionMatrix(Matrix4f mtx){
		super.loadMatrix(projeMatrixID, mtx);
	}
	
	public void loadViewMatrix(Matrix4f mtx){
		super.loadMatrix(viewMatrixID, mtx);
	}
	
	public void loadPickerColor(Vector3f vec3){
		super.loadVector(pickerColorID, vec3);
	}
}
