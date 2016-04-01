package tk.greydynamics.Render.Shader.Shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Render.Shader.ShaderProgram;

public class ObjectShader extends ShaderProgram{
	
	private int transMatrixID;
	private int projeMatrixID;
	private int viewMatrixID;
	private int highlightedID;
	private int highlightedColorID;
	private int lightPositionID;
	private int isNormalID;

	public ObjectShader() {
		super("res/shader/ObjectShader.vert", "res/shader/ObjectShader.frag");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoord");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		transMatrixID = super.getUniformLocation("transformationMatrix");
		projeMatrixID = super.getUniformLocation("projectionMatrix");
		viewMatrixID = super.getUniformLocation("viewMatrix");
		highlightedID = super.getUniformLocation("isHighlighted");
		highlightedColorID = super.getUniformLocation("highlightedColor");
		lightPositionID = super.getUniformLocation("lightPosition");
		isNormalID = super.getUniformLocation("isNormal");
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
	
	public void loadHighlightedColor(Vector3f vec3){
		super.loadVector(highlightedColorID, vec3);
	}
	
	public void loadLightPosition(Vector3f vec3){
		super.loadVector(lightPositionID, vec3);
	}
	
	public void loadIsNormal(boolean b){
		super.loadBoolean(isNormalID, b);
	}
}
