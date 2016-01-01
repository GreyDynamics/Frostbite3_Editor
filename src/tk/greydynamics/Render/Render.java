package tk.greydynamics.Render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Camera.FPCameraController;
import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityTextureData;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.ObjectEntity;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Game.Core;
import tk.greydynamics.Game.Game;
import tk.greydynamics.Game.Point;
import tk.greydynamics.Maths.Matrices;
import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Model.TexturedModel;
import tk.greydynamics.Player.PlayerEntity;
import tk.greydynamics.Render.Gui.GuiRenderer;
import tk.greydynamics.Shader.StaticShader;
import tk.greydynamics.Terrain.Terrain;
import tk.greydynamics.Terrain.TerrainShader;

public class Render {

	public Game game;

	FPCameraController camera;
	PlayerEntity pe;
	public Matrix4f viewMatrix;
	public Matrix4f projectionMatrix;
	public Matrix4f transformationMatrix;
	GuiRenderer guiRenderer;
	int renderCalls = 0;
	
	public static Matrix4f identityMatrix = new Matrix4f(); 

	public Render(Game game) {
		this.game = game;
		guiRenderer = new GuiRenderer(game.getModelHandler().getLoader());
		pe = game.getPlayerHandler().getPlayerEntity();
		camera = new FPCameraController(pe);

		updateProjectionMatrix(Core.FOV, Core.DISPLAY_WIDTH,
				Core.DISPLAY_HEIGHT, Core.zNear, Core.zFar);

		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);

	}

	public void drawAxisAlignedBoundingBox(Vector3f minCoords,
			Vector3f maxCoords) {
		glColor3f(0.7f, 0.0f, 0.0f);
		// Bottom
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(minCoords.x, minCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, minCoords.y, minCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(maxCoords.x, minCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, minCoords.y, maxCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(maxCoords.x, minCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, minCoords.y, maxCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(minCoords.x, minCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, minCoords.y, minCoords.z);
		glEnd();
		// Top
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(minCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, minCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(maxCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, maxCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(maxCoords.x, maxCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, maxCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(minCoords.x, maxCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, minCoords.z);
		glEnd();
		// Connection Lines
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(minCoords.x, minCoords.y, minCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, minCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(maxCoords.x, minCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, minCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(maxCoords.x, minCoords.y, maxCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, maxCoords.z);
		glEnd();
		glBegin(GL11.GL_LINE_STRIP);
		glVertex3f(minCoords.x, minCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, maxCoords.z);
		glEnd();
	}

	public FPCameraController getCamera() {
		return camera;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void update() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		glLoadIdentity();

		camera.lookThrough();
		if (Display.isActive() && Mouse.isGrabbed()) {
			camera.dx = Mouse.getDX();
			camera.dy = Mouse.getDY();
		}

		camera.yaw(camera.dx * camera.mouseSensitivity);
		camera.pitch(-camera.dy * camera.mouseSensitivity);
		
		viewMatrix = Matrices.createViewMatrix(camera.getPosition(),
				new Vector3f(camera.getPitch(), camera.getYaw(), 0.0f));

		StaticShader shader = game.getShaderHandler().getStaticShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadViewMatrix(viewMatrix);
		renderCalls = 0;
		RenderEntityLayers(game.getEntityHandler().getLayers(), identityMatrix, shader);
		//System.out.println("RenderCalls: "+renderCalls);
		shader.stop();
		
		RenderTerrains(game.getShaderHandler().getTerrainShader());
		guiRenderer.update(game.getGuis());
		
		Display.update();
		if (Display.wasResized()) {
			updateProjectionMatrix(Core.FOV, Display.getWidth(),
					Display.getHeight(), Core.zNear, Core.zFar);

		}
		Display.sync(Core.DISPLAY_RATE);
		//Display.setVSyncEnabled(true);
	}
	public void RenderEntityLayers(ArrayList<EntityLayer> layers, Matrix4f identityMatrix, StaticShader shader){
		for (EntityLayer layer : layers){
			RenderEntities(layer.getEntities(), identityMatrix, shader, false);
		}
	}
	
	public void RenderEntity(Entity e, Matrix4f parentMtx, StaticShader shader, boolean recalcChilds){
		if (e.isVisible) {
			Matrix4f stackMtx = null;
			if (e.isRecalculateAbs() || recalcChilds || e.getAbsMatrix()==null){
				e.recalculateAbsMatrix(parentMtx);
				e.setRecalculateAbs(false);
				recalcChilds = true;
			}
			stackMtx = e.getAbsMatrix();
			
			if (e.isShowBoundingBox()) {
				shader.loadTransformationMatrix(Matrices
					.createTransformationMatrix(
							e.getPosition(),
							new Vector3f(0f, 0f, 0f),
							e.getScaling())
				);
				drawAxisAlignedBoundingBox(e.getMinCoords(), e.getMaxCoords());
			}
			if (e.getHighlighted()) {
				shader.loadHighlighted(true);
			}
			shader.loadHeighlightedColor(e.getHeighlightedColor());
			RawModel[] rawModels = e.getRawModels();
			shader.loadTransformationMatrix(stackMtx);
			if (rawModels!=null){
				int[] diffuseTextures = null;
				if (e.getType()==Type.Object){
					ObjectEntity objEntity = (ObjectEntity) e;
					EntityTextureData etd = objEntity.getTextureData();
					if (etd!=null){
						if (etd.getMeshGUID().getInstanceGUID().equalsIgnoreCase("003c1de501eed2fab93fc29b19850e89")){
							//System.out.println("Y");
						}
						if (etd.getDiffuseIDs()!=null){
							diffuseTextures = etd.getDiffuseIDs();
						}
					}
				}
				for (int i = 0; i < rawModels.length; i++) {
					RawModel raw = rawModels[i];
					glColor3f(0.25f, 0.25f, 0.25f);
					GL30.glBindVertexArray(raw.vaoID);
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(1);
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					if (diffuseTextures!=null&&(i<diffuseTextures.length)){
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, diffuseTextures[i]);
					}else{
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, game.getModelHandler().getLoader().getNotFoundID());
					}
					GL11.glDrawElements(raw.drawMethod, raw.vertexCount,
							GL11.GL_UNSIGNED_INT, 0);
					GL20.glDisableVertexAttribArray(0);
					GL20.glDisableVertexAttribArray(1);
					GL30.glBindVertexArray(0);
				}
			}
			renderCalls++;
			RenderEntities(e.getChildrens(), stackMtx, shader, recalcChilds);
			shader.loadHighlighted(false);
		}
	}
	
	public void RenderEntities(ArrayList<Entity> entities, Matrix4f parentMtx, StaticShader shader, boolean recalcChilds){
		for (Entity e : entities) {
			RenderEntity(e, parentMtx, shader, recalcChilds);
		}
	}
	
	public void RenderTerrains(TerrainShader terrainShader){
		terrainShader.start();
		terrainShader.loadProjectionMatrix(projectionMatrix);
		terrainShader.loadViewMatrix(viewMatrix);
		Matrix4f TerrainransformationMatrix = Matrices
			.createTransformationMatrix(
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(1.0f, 1.0f, 1.0f)
		);


		for (Terrain curTerr : game.getTerrainHandler().getTerrainList()) {
			Point[][] points = curTerr.getPoints();
			terrainShader.loadTransformationMatrix(TerrainransformationMatrix);

			for (int i1 = 0; i1 < points.length - 1; i1++) {
				glBegin(GL_LINE_STRIP);
				for (int i2 = 0; i2 < points[i1].length - 1; i2++) {
					glVertex3f(points[i1 + 1][i2].x, points[i1 + 1][i2].y,
							points[i1 + 1][i2].z);
					glVertex3f(points[i1][i2 + 1].x, points[i1][i2 + 1].y,
							points[i1][i2 + 1].z);
				}
				glEnd();
			}
		}
		terrainShader.stop();
	}

	public void updateProjectionMatrix(float FOV, int DISPLAY_WIDTH,
			int DISPLAY_HEIGHT, float zNear, float zFar) {
		System.out.println("Projection Matrix gets an update!");
		Core.FOV = FOV;
		Core.DISPLAY_WIDTH = DISPLAY_WIDTH;
		Core.DISPLAY_HEIGHT = DISPLAY_HEIGHT;
		Core.zNear = zNear;
		Core.zFar = zFar;
		this.projectionMatrix = Matrices.createProjectionMatrix(Core.FOV,
				Core.DISPLAY_WIDTH, Core.DISPLAY_HEIGHT, Core.zNear, Core.zFar);
	}

	public GuiRenderer getGuiRenderer() {
		return guiRenderer;
	}
	
	
}
