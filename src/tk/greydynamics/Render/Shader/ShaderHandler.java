package tk.greydynamics.Render.Shader;

import java.util.ArrayList;

import tk.greydynamics.Render.Shader.Shaders.GizmoShader;
import tk.greydynamics.Render.Shader.Shaders.StaticShader;
import tk.greydynamics.Render.Shader.Shaders.TerrainShader;

public class ShaderHandler {
	public ArrayList<ShaderProgram> shaderPrograms = null;
	
	private StaticShader staticShader = null;
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

	public StaticShader getStaticShader() {
		return staticShader;
	}	
	
	public TerrainShader getTerrainShader() {
		return terrainShader;
	}

	public GizmoShader getGizmoShader() {
		return gizmoShader;
	}

	public void init(){
		this.shaderPrograms = new ArrayList<>();
		
		staticShader = new StaticShader();
		shaderPrograms.add(staticShader);
		
		guiShader = new GuiShader();
		shaderPrograms.add(guiShader);
		
		terrainShader = new TerrainShader();
		shaderPrograms.add(terrainShader);
		
		gizmoShader = new GizmoShader();
		shaderPrograms.add(gizmoShader);
		
	}
	
}
