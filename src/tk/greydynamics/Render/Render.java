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
import java.util.ConcurrentModificationException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import tk.greydynamics.Camera.FPCameraController;
import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.EntityTextureData;
import tk.greydynamics.Entity.Entities.GizmoEntity;
import tk.greydynamics.Entity.Entities.InstanceEntity;
import tk.greydynamics.Entity.Entities.ObjectEntity;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Entity.Picker.ObjectEntityPicker;
import tk.greydynamics.Game.Core;
import tk.greydynamics.Game.Game;
import tk.greydynamics.Game.Point;
import tk.greydynamics.Maths.Matrices;
import tk.greydynamics.Maths.VectorMath;
import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Player.PlayerEntity;
import tk.greydynamics.Render.FrameBuffer.FrameBufferHandler;
import tk.greydynamics.Render.GizmoHandler.GizmoType;
import tk.greydynamics.Render.Gui.GuiRenderer;
import tk.greydynamics.Render.Shader.ShaderProgram;
import tk.greydynamics.Render.Shader.Shaders.GizmoShader;
import tk.greydynamics.Render.Shader.Shaders.ObjectShader;
import tk.greydynamics.Render.Shader.Shaders.ObjectPickerShader;
import tk.greydynamics.Render.Shader.Shaders.TerrainShader;
import tk.greydynamics.Terrain.Terrain;

public class Render {

	private Game game;
	

	private FPCameraController camera;
	private PlayerEntity pe;
	public Matrix4f viewMatrix;
	public Matrix4f projectionMatrix;
	public Matrix4f transformationMatrix;
	private GuiRenderer guiRenderer;
	private int renderCalls = 0;	
	
	private FrameBufferHandler frameBufferHandler = new FrameBufferHandler();
	
	public static Matrix4f identityMatrix = new Matrix4f(); 

	public Render(Game game) {
		this.game = game;
		this.guiRenderer = new GuiRenderer(game.getModelHandler().getLoader());
		this.pe = game.getPlayerHandler().getPlayerEntity();
		this.camera = new FPCameraController(pe);
		
		updateProjectionMatrix(Core.FOV, Core.DISPLAY_WIDTH,
				Core.DISPLAY_HEIGHT, Core.zNear, Core.zFar);

		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);

	}

	public void drawBoundingBoxLines(Vector3f minCoords,
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
	public void drawBoundingBoxFaces(Vector3f minCoords,
			Vector3f maxCoords) {
		glColor3f(0.7f, 0.0f, 0.0f);
		
		// Bottom
		glBegin(GL11.GL_QUADS);
		glVertex3f(minCoords.x, minCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, minCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, minCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, minCoords.y, maxCoords.z);
		glEnd();
		
		// Top
		glBegin(GL11.GL_QUADS);
		glVertex3f(minCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, maxCoords.z);
		glEnd();
		
		// Left
		glBegin(GL11.GL_QUADS);
		glVertex3f(minCoords.x, minCoords.y, minCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, minCoords.y, maxCoords.z);
		glEnd();
		
		// Right
		glBegin(GL11.GL_QUADS);
		glVertex3f(maxCoords.x, minCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, maxCoords.z);
		glVertex3f(maxCoords.x, minCoords.y, maxCoords.z);
		glEnd();
		
		// Back
		glBegin(GL11.GL_QUADS);
		glVertex3f(minCoords.x, minCoords.y, maxCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, maxCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, maxCoords.z);
		glVertex3f(maxCoords.x, minCoords.y, maxCoords.z);
		glEnd();
		
		// Front
		glBegin(GL11.GL_QUADS);
		glVertex3f(minCoords.x, minCoords.y, minCoords.z);
		glVertex3f(minCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, maxCoords.y, minCoords.z);
		glVertex3f(maxCoords.x, minCoords.y, minCoords.z);
		glEnd();
		
	}

	public FPCameraController getCamera() {
		return camera;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void update() {
//		GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
//		GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
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

		
		//Render Objects to Screen
		ObjectShader objShader = game.getShaderHandler().getObjectShader();
		objShader.start();
		objShader.loadProjectionMatrix(projectionMatrix);
		objShader.loadViewMatrix(viewMatrix);
		objShader.loadLightPosition(game.getPlayerHandler().getPlayerEntity().getPos());
		objShader.loadIsNormal(false);//Render Normals as Color ?
				
		RenderEntityLayers(game.getEntityHandler().getLayers(), identityMatrix, (ShaderProgram) objShader, false, false, false);
		objShader.stop();
		
		
		//Render Objects to framebuffer for picking
		if (Core.currentTick%(Core.TICK_RATE/5)==0){
			ObjectPickerShader objPickerShader = game.getShaderHandler().getObjectPickerShader();
			objPickerShader.start();
			objPickerShader.loadProjectionMatrix(projectionMatrix);
			objPickerShader.loadViewMatrix(viewMatrix);
			frameBufferHandler.bindPickingFrameBuffer();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			RenderEntityLayers(game.getEntityHandler().getLayers(), identityMatrix, (ShaderProgram) objPickerShader, true, false, false);
			
			if (!Mouse.isGrabbed()&&Mouse.isButtonDown(0)&&game.getEntityHandler().getGizmoHandler().getGizmoPicker().getEntityPICKED()==null){//
				GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_FLOAT, frameBufferHandler.getRGBAPickingBuffer());
				Vector3f pickingColor = new Vector3f(frameBufferHandler.getRGBAPickingBuffer().get(0), frameBufferHandler.getRGBAPickingBuffer().get(1), frameBufferHandler.getRGBAPickingBuffer().get(2));
				Entity picked = game.getEntityHandler().pickEntity(pickingColor);
				if (picked!=null){
					String additional = "";
					if (picked instanceof InstanceEntity){
						InstanceEntity en = (InstanceEntity) picked;
						additional += " from "+en.getLayer().getName()+" ";
					}
					Display.setTitle("["+picked.getName()+additional+"]");
					picked.setHighlighted(true);
				}else{
					Display.setTitle("[NO SELECTION]");
				}
			}
			frameBufferHandler.unbindCurrentFrameBuffer();
			objPickerShader.stop();
		}
		
		
		/*Terrains*/
		RenderTerrains(game.getShaderHandler().getTerrainShader());
				
		/*Gizmo*/
		Entity entityPicked = null;
		if (false){//debug
			entityPicked = new ObjectEntity(null, "DEBUG_Gizmo", null, null, null, null, null);
		}else{
			entityPicked = game.getEntityHandler().getObjectEntityPicker().getEntityPICKED();
		}
		if (entityPicked!=null){
			GizmoHandler gizmoHandler = game.getEntityHandler().getGizmoHandler();
			
			//Calculate Gizmo Scale by distance from Camera to Gizmo.
			float scale = VectorMath.getDistance(entityPicked.getPosition(), game.getPlayerHandler().getPlayerEntity().getPos())/2f;
			if (scale>10f){
				scale = 10;
			}
			Vector3f scaleVector = new Vector3f(scale, scale, scale);
			float mouseSpeed = (scale/100f)*Core.getInputHandler().speedMultipShift;
			
			//Render Gizmo
			RenderGizmo(game.getShaderHandler().getGizmoShader(), entityPicked.getPosition(), scaleVector, false, gizmoHandler.getGizmoEntity(), gizmoHandler.getCurrentGizmoType());
			if (Core.currentTick%(Core.TICK_RATE/5)==0){
				frameBufferHandler.bindPickingFrameBuffer();
				RenderGizmo(game.getShaderHandler().getGizmoShader(), entityPicked.getPosition(), scaleVector, false, gizmoHandler.getGizmoEntity(), gizmoHandler.getCurrentGizmoType());
				if (!Mouse.isGrabbed()){
					if (!Mouse.isButtonDown(0)){
						GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_FLOAT, frameBufferHandler.getRGBAPickingBuffer());
						Vector3f pickingColor = new Vector3f(frameBufferHandler.getRGBAPickingBuffer().get(0), frameBufferHandler.getRGBAPickingBuffer().get(1), frameBufferHandler.getRGBAPickingBuffer().get(2));
						Entity picked = gizmoHandler.pick(pickingColor);
						if (picked!=null){
//							System.out.println("Gizmo -> "+picked.getName());
							picked.setPickerColors(Entity.randomizedPickerColors());
							picked.setHighlighted(true);
						}else{
							gizmoHandler.getGizmoPicker().newPickedEntity(null);
	//						System.out.println("Gizmo -> NONE");
						}
					}else{
						if (gizmoHandler.getGizmoPicker().getEntityPICKED()!=null){
							Entity picked = gizmoHandler.getGizmoPicker().getEntityPICKED();
							
							//TODO Move to own function! NOT HERE!
							if (picked.getName().startsWith(GizmoType.GIZMO_MOVE.toString())){
								if (picked.getName().equals(GizmoType.GIZMO_MOVE.toString()+"_X")){
									entityPicked.changePosition(Mouse.getDX() * mouseSpeed, 0f, 0f, false);
								}else if (picked.getName().equals(GizmoType.GIZMO_MOVE.toString()+"_Y")){
									entityPicked.changePosition(0f, Mouse.getDY() * mouseSpeed, 0f, false);
								}else if (picked.getName().equals(GizmoType.GIZMO_MOVE.toString()+"_Z")){
									entityPicked.changePosition(0f, 0f, Mouse.getDX() * mouseSpeed, false);
								}
								System.out.println("[GIZMO] Change Position to "+entityPicked.getPosition());
							}else if (picked.getName().startsWith(GizmoType.GIZMO_ROTATE.toString())){	
								if (picked.getName().equals(GizmoType.GIZMO_ROTATE.toString()+"_X")){
									entityPicked.changeRotation(Mouse.getDY() * mouseSpeed, 0f, 0f, false);
								}else if (picked.getName().equals(GizmoType.GIZMO_ROTATE.toString()+"_Y")){
									entityPicked.changeRotation(0f, Mouse.getDX() * mouseSpeed, 0f, false);
								}else if (picked.getName().equals(GizmoType.GIZMO_ROTATE.toString()+"_Z")){
									entityPicked.changeRotation(0f, 0f, Mouse.getDY() * mouseSpeed, false);
								} 
								System.out.println("[GIZMO] Change Rotation to "+entityPicked.getRotation());
							}else if (picked.getName().startsWith(GizmoType.GIZMO_SCALE.toString())){	
								if (picked.getName().equals(GizmoType.GIZMO_SCALE.toString()+"_X")){
									entityPicked.changeScaleing(Mouse.getDX() * mouseSpeed, 0f, 0f, false);
								}else if (picked.getName().equals(GizmoType.GIZMO_SCALE.toString()+"_Y")){
									entityPicked.changeScaleing(0f, Mouse.getDY() * mouseSpeed, 0f, false);
								}else if (picked.getName().equals(GizmoType.GIZMO_SCALE.toString()+"_Z")){
									entityPicked.changeScaleing(0f, 0f, Mouse.getDX() * mouseSpeed, false);
								}
								System.out.println("[GIZMO] Change Scale to "+entityPicked.getScaleing());
							}
						}
					}
				}
				frameBufferHandler.unbindCurrentFrameBuffer();
			}
		}
		
		
		guiRenderer.update(game.getGuis());
		
		Display.update();
		if (Display.wasResized()) {
			updateProjectionMatrix(Core.FOV, Display.getWidth(),
					Display.getHeight(), Core.zNear, Core.zFar);

		}
		Display.sync(Core.DISPLAY_RATE);
		//Display.setVSyncEnabled(true);
	}
	public void RenderEntityLayers(ArrayList<EntityLayer> layers, Matrix4f identityMatrix, ShaderProgram shader, boolean isPickingShader, boolean renderBoxOnly, boolean isHighlighted){
		try{
			for (EntityLayer layer : layers){
				RenderEntities(true, layer.getEntities(), identityMatrix, shader, false, isPickingShader, renderBoxOnly, isHighlighted);
			}
		}catch(ConcurrentModificationException e){
		}
	}
	
	public void RenderEntity(boolean visible, Entity e, Matrix4f parentMtx, ShaderProgram shader, boolean recalcChilds, boolean isPickingShader, boolean renderBoxOnly, boolean isHighlighted){
		e.pokeRawModels(Core.currentTick);
			Matrix4f stackMtx = null;
			if (e.isRecalculateAbs() || recalcChilds || e.getAbsMatrix()==null){
				e.recalculateAbsMatrix(parentMtx);
				e.setRecalculateAbs(false);
				recalcChilds = true;
			}
			stackMtx = e.getAbsMatrix();
			
			if (isPickingShader){
				((ObjectPickerShader) shader).loadTransformationMatrix(stackMtx);
				((ObjectPickerShader) shader).loadPickerColor(e.getPickerColors());
			}else{
				((ObjectShader) shader).loadTransformationMatrix(stackMtx);
				if ((e.getHighlighted()||isHighlighted)) {
					((ObjectShader) shader).loadHighlighted(true);
					((ObjectShader) shader).loadHighlightedColor(e.getPickerColors());
				}else{
					((ObjectShader) shader).loadHighlighted(false);
				}
			}
			
			if (e.isShowBoundingBox()||renderBoxOnly) {
				if (renderBoxOnly){
					drawBoundingBoxFaces(e.getMinCoords(), e.getMaxCoords());
				}else{
					drawBoundingBoxLines(e.getMinCoords(), e.getMaxCoords());
				}
			}
			
			RawModel[] rawModels = e.getRawModels();
			if (rawModels!=null){
				int[] diffuseTextures = null;
				if (e.getType()==Type.Object){
					ObjectEntity objEntity = (ObjectEntity) e;
					EntityTextureData etd = objEntity.getTextureData();
					if (etd!=null){
						if (etd.getDiffuseIDs()!=null){
							diffuseTextures = etd.getDiffuseIDs();
						}
					}
				}
				if (!renderBoxOnly&&e.getIsVisible()&&visible){
					for (int i = 0; i < rawModels.length; i++) {
						RawModel raw = rawModels[i];
						glColor3f(0.25f, 0.25f, 0.25f);
						GL30.glBindVertexArray(raw.getVaoID());
						GL20.glEnableVertexAttribArray(0);
						if (!isPickingShader){
							GL20.glEnableVertexAttribArray(1);
							GL20.glEnableVertexAttribArray(2);
						}
						GL13.glActiveTexture(GL13.GL_TEXTURE0);
						if (diffuseTextures!=null&&(i<diffuseTextures.length)){
							GL11.glBindTexture(GL11.GL_TEXTURE_2D, diffuseTextures[i]);
						}else{
							GL11.glBindTexture(GL11.GL_TEXTURE_2D, game.getModelHandler().getLoader().getNotFoundID());
						}
						GL11.glDrawElements(raw.getDrawMethod(), raw.getVertexCount(),
								GL11.GL_UNSIGNED_INT, 0);
						GL20.glDisableVertexAttribArray(0);
						if (!isPickingShader){
							GL20.glDisableVertexAttribArray(1);
							GL20.glDisableVertexAttribArray(2);
						}
						GL30.glBindVertexArray(0);
					}
				}
			}
			renderCalls++;
			RenderEntities(e.getIsVisible()&&visible, e.getChildrens(), stackMtx, shader, recalcChilds, isPickingShader, renderBoxOnly, (isHighlighted||e.getHighlighted()));
//		}
	}
	
	public void RenderEntities(boolean visible, ArrayList<Entity> entities, Matrix4f parentMtx, ShaderProgram shader, boolean recalcChilds, boolean isPickingShader, boolean renderBoxOnly, boolean isHighlighted){
		for (Entity e : entities) {
			RenderEntity(visible, e, parentMtx, shader, recalcChilds, isPickingShader, renderBoxOnly, isHighlighted);
		}
	}
	
	public void RenderGizmo(GizmoShader gizmoShader, Vector3f pos, Vector3f scale, boolean recalcChilds, Entity gizmoEntity, GizmoHandler.GizmoType axisType){
		gizmoShader.start();
		gizmoShader.loadProjectionMatrix(projectionMatrix);
		gizmoShader.loadViewMatrix(viewMatrix);
		GL11.glDisable(GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
//		if (gizmoEntity.isRecalculateAbs()||gizmoEntity.getAbsMatrix()==null){
//			gizmoEntity.recalculateAbsMatrix(new Matrix4f());
//			gizmoEntity.setRecalculateAbs(false);
//		}
		
		for (Entity type : gizmoEntity.getChildrens()){
			if (type.getName().equals(axisType.toString())){
				//pick the right type
				for (Entity part : type.getChildrens()){
					//render each component
					part.recalculateAbsMatrix(new Matrix4f().translate(pos).scale(scale));
					gizmoShader.loadTransformationMatrix(part.getAbsMatrix());
					gizmoShader.loadColor(new Vector4f(part.getPickerColors().x, part.getPickerColors().y, part.getPickerColors().z, ((GizmoEntity) part).getAlphaColor()));
					if (((GizmoEntity) part).getAlphaColor()<1.0f){
						glEnable(GL11.GL_BLEND);	
//						continue;
					}
					for (RawModel m : part.getRawModels()){
						GL30.glBindVertexArray(m.getVaoID());
						GL20.glEnableVertexAttribArray(0);
						GL11.glDrawElements(m.getDrawMethod(), m.getVertexCount(),
								GL11.GL_UNSIGNED_INT, 0);
						GL20.glDisableVertexAttribArray(0);
						GL30.glBindVertexArray(0);
					}
					if (((GizmoEntity) part).getAlphaColor()<1.0f){
						GL11.glDisable(GL11.GL_BLEND);
					}
				}
				break;
			}
		}
		GL11.glEnable(GL_DEPTH_TEST);
		gizmoShader.stop();
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
		GL11.glViewport(0, 0, Core.DISPLAY_WIDTH, Core.DISPLAY_HEIGHT);
	}

	public GuiRenderer getGuiRenderer() {
		return guiRenderer;
	}

	public FrameBufferHandler getFrameBufferHandler() {
		return frameBufferHandler;
	}
	
}
