package tk.greydynamics.Render.Gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Maths.Matrices;
import tk.greydynamics.Model.Loader;
import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Render.Shader.GuiShader;

public class GuiRenderer {
	private final RawModel quad;
	private GuiShader shader;
	
	public GuiRenderer(Loader loader){
		this.shader = Core.getGame().getShaderHandler().getGuiShader();
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadVAO("quad", GL11.GL_TRIANGLE_STRIP, positions);
	}
	
	public void update(ArrayList<GuiTexture> guis){
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (GuiTexture gui : guis){
			Vector2f scaleVec2 = new Vector2f(gui.getScale().x, gui.getScale().y);
			scaleVec2.x /= (float) Core.DISPLAY_WIDTH/(float) Core.DISPLAY_HEIGHT;
			shader.loadTransformation(Matrices.createTransformationMatrix(gui.getPosition(), scaleVec2));
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			GL11.glDrawArrays(quad.getDrawMethod(), 0, quad.getVertexCount());
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}	
}
