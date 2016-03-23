package tk.greydynamics.Render.Shader.Shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import tk.greydynamics.Render.Shader.ShaderProgram;

public class GizmoShader extends ShaderProgram{
	private int transMatrixID;
	private int projeMatrixID;
	private int viewMatrixID;
	private int colorID;

	public GizmoShader() {
		super("res/shader/GizmoShader.vert", "res/shader/GizmoShader.frag");
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
		colorID = super.getUniformLocation("color");
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
		
	public void loadColor(Vector4f vec4){
		super.loadVector(colorID, vec4);
	}	
}
