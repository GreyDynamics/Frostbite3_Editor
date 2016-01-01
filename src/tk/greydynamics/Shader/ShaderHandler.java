package tk.greydynamics.Shader;

import java.util.ArrayList;

import tk.greydynamics.Render.Gui.GuiShader;
import tk.greydynamics.Terrain.TerrainShader;

public class ShaderHandler {
	public ArrayList<ShaderProgram> shaderPrograms = null;
	
	public StaticShader staticShader = null;
	public GuiShader guiShader = null;
	public TerrainShader terrainShader = null;
		
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

	public void init(){
		this.shaderPrograms = new ArrayList<>();
		
		staticShader = new StaticShader();
		shaderPrograms.add(staticShader);
		
		guiShader = new GuiShader();
		shaderPrograms.add(guiShader);
		
		terrainShader = new TerrainShader();
		shaderPrograms.add(terrainShader);
		
	}
	
}
