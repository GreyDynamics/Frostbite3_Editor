package tk.greydynamics.Render.Shader.Shaders;

import org.lwjgl.util.vector.Matrix4f;

import tk.greydynamics.Render.Shader.ShaderProgram;

public class TerrainShader extends ShaderProgram{
	private int transMatrixID;
	private int projeMatrixID;
	private int viewMatrixID;

	public TerrainShader() {
		super("res/shader/TerrainShader.vert", "res/shader/TerrainShader.frag");
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
}
