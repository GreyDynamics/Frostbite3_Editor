package tk.greydynamics.Render.Shader;

import java.util.ArrayList;

import tk.greydynamics.Render.Shader.Shaders.GizmoShader;
import tk.greydynamics.Render.Shader.Shaders.ObjectShader;
import tk.greydynamics.Render.Shader.Shaders.ObjectPickerShader;
import tk.greydynamics.Render.Shader.Shaders.TerrainShader;

public class ShaderHandler {
	public ArrayList<ShaderProgram> shaderPrograms = null;
	
	private ObjectPickerShader objectPickerShader = null;
	private ObjectShader objectShader = null;
	private GuiShader guiShader = null;
	private TerrainShader terrainShader = null;
	private GizmoShader gizmoShader = null;
		
	public void cleanUpAll(){
		for (ShaderProgram shader : shaderPrograms){
			shader.cleanUp();
		}
	}

	public GuiShader getGuiShader() {
		return guiShader;
	}

	public ArrayList<ShaderProgram> getShaderPrograms() {
		return shaderPrograms;
	}

	public ObjectPickerShader getObjectPickerShader() {
		return objectPickerShader;
	}	
	
	public TerrainShader getTerrainShader() {
		return terrainShader;
	}

	public GizmoShader getGizmoShader() {
		return gizmoShader;
	}

	public ObjectShader getObjectShader() {
		return objectShader;
	}

	public void init(){
		this.shaderPrograms = new ArrayList<>();
		
		objectPickerShader = new ObjectPickerShader();
		shaderPrograms.add(objectPickerShader);
		
		objectShader = new ObjectShader();
		shaderPrograms.add(objectShader);
		
		guiShader = new GuiShader();
		shaderPrograms.add(guiShader);
		
		terrainShader = new TerrainShader();
		shaderPrograms.add(terrainShader);
		
		gizmoShader = new GizmoShader();
		shaderPrograms.add(gizmoShader);
		
	}
	
}
