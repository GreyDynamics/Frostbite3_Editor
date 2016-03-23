package tk.greydynamics.Render.Shader.Shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Render.Shader.ShaderProgram;

public class StaticShader extends ShaderProgram{
	
	private int transMatrixID;
	private int projeMatrixID;
	private int viewMatrixID;
	private int highlightedID;
	private int heighlightedColorID;
	private int pickerColorID;
	private int pickerID;

	public StaticShader() {
		super("res/shader/StaticShader.vert", "res/shader/StaticShader.frag");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoord");
	}

	@Override
	protected void getAllUniformLocations() {
		transMatrixID = super.getUniformLocation("transformationMatrix");
		projeMatrixID = super.getUniformLocation("projectionMatrix");
		viewMatrixID = super.getUniformLocation("viewMatrix");
		highlightedID = super.getUniformLocation("isHighlighted");
		heighlightedColorID = super.getUniformLocation("heighlightedColor");
		pickerID = super.getUniformLocation("isPicker");
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
	
	public void loadHighlighted(boolean bool){
		super.loadBoolean(highlightedID, bool);
	}
	
	public void loadHeighlightedColor(Vector3f vec3){
		super.loadVector(heighlightedColorID, vec3);
	}
	
	public void loadPicker(boolean bool){
		super.loadBoolean(pickerID, bool);
	}
	
	public void loadPickerColor(Vector3f vec3){
		super.loadVector(pickerColorID, vec3);
	}
}
