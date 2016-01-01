package tk.greydynamics.Shader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class StaticShader extends ShaderProgram{
	
	public int transMatrixID;
	public int projeMatrixID;
	public int viewMatrixID;
	public int highlightedID;
	public int heighlightedColorID;

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
}
