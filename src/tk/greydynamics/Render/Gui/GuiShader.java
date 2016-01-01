package tk.greydynamics.Render.Gui;

import org.lwjgl.util.vector.Matrix4f;

import tk.greydynamics.Shader.ShaderProgram;

public class GuiShader extends ShaderProgram{
	
    private int location_transformationMatrix;
 
    public GuiShader() {
        super("res/shader/GuiShader.vert", "res/shader/GuiShader.frag");
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
