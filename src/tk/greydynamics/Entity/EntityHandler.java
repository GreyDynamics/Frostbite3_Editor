package tk.greydynamics.Entity;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entities.LightEntity;
import tk.greydynamics.Entity.Entities.ObjectEntity;
import tk.greydynamics.Entity.Entity.Type;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Entity.Layer.EntityLayerConverter;
import tk.greydynamics.Entity.Picker.ObjectEntityPicker;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.Windows.EBXWindow;
import tk.greydynamics.Maths.RayCasting;
import tk.greydynamics.Maths.VectorMath;
import tk.greydynamics.Model.ModelHandler;
import tk.greydynamics.Model.RawModel;
import tk.greydynamics.Render.GizmoHandler;
import tk.greydynamics.Resource.ResourceHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;
import tk.greydynamics.Resource.Frostbite3.MESH.MeshChunkLoader;

public class EntityHandler {
		
	ArrayList <EntityLayer> layers = new ArrayList <>();
//	Vector3f ray = null;
//	Entity focussedEntity = null;
	
	public int MAX_TEXTURES = 1000;
	public int MAX_RAY_CHECKS = 10000;
	public float RAY_CHECK_DISTANCE = 1f;
	
	public ModelHandler modelHandler;
	public ResourceHandler resourceHandler;
	private ObjectEntityPicker objectEntityPicker;
	private GizmoHandler gizmoHandler;
	
	public EntityHandler(ModelHandler modelHandler, ResourceHandler resourceHandler) {
		this.modelHandler = modelHandler;
		this.resourceHandler = resourceHandler;
		this.objectEntityPicker = new ObjectEntityPicker();
		
		this.gizmoHandler = new GizmoHandler();
	}
	public Entity pickEntity(Vector3f pickingColor){
		return objectEntityPicker.getPickedEntityFromLayers(pickingColor, layers);	}
	
	public EntityLayer createEntityLayer(EBXFile ebxFile, EBXWindow ebxWindow){
		EntityLayer layer = EntityLayerConverter.getEntityLayer(ebxFile, ebxWindow);
		if (layer!=null){
			layers.add(layer);
			Core.getJavaFXHandler().getMainWindow().updateLayers(Core.getGame().getEntityHandler().getLayers());
			return layer;
		}
		return null;
	}
	
	
	public EntityLayer getEntityLayer(String name){
		name=name.toLowerCase();
		for (EntityLayer layer : layers){
			if (layer.getName().toLowerCase().startsWith(name)){
				return layer;
			}
		}
		return null;
	}
	
	public void destroyEntityLayer(String name){
		EntityLayer layer = getEntityLayer(name);
		if (layer!=null){
			for (Entity e : layer.getEntities()){
				destroyEntity(e);
			}
			layers.remove(layer);
			Core.getJavaFXHandler().getMainWindow().updateLayers(Core.getGame().getEntityHandler().getLayers());
		}
	}
	
	public void destroyEntityLayer(EntityLayer layer){
		if (layer!=null){
			for (Entity e : layer.getEntities()){
				destroyEntity(e);
			}
			layers.remove(layer);
			Core.getJavaFXHandler().getMainWindow().updateLayers(Core.getGame().getEntityHandler().getLayers());
		}
	}
	
	public void destroyEntity(Entity e){
		System.err.println("Destroy Entity and clean resources if not needed anymore, not done!"); 
	}
	
	public void clear(){
		/*Clean's all entities with their resources!*/
		for (EntityLayer layer : layers){
			destroyEntityLayer(layer);
		}
		
		
	}

	
//	public Entity getFocussedEntity(Vector3f position, Vector3f direction){
//		return getFocussedEntity(position, direction, MAX_RAY_CHECKS, RAY_CHECK_DISTANCE);
//	}
	
//	public Entity getFocussedEntity(Vector3f position, Vector3f direction, int maxChecks, float checkDistance){
//		System.err.println("Raycasting has a big, A REALLY BIG performance problem! We have to thing about something else!"); 
//		this.ray = new Vector3f(position.x, position.y, position.z);
//		Vector3f origin = new Vector3f(position.x, position.y, position.z);
//		Vector3f absMinCoords = null;
//		Vector3f absMaxCoords = null;
//		for (int check=1; check<=maxChecks; check++){
//			this.ray = RayCasting.getRayPosition(ray, direction, checkDistance, null);
//			try{
//				for (EntityLayer layer : layers){
//					for (Entity e : layer.getEntities()){
//						e.setShowBoundingBox(false);
//						absMinCoords = Vector3f.add(e.getPosition(), VectorMath.multiply(e.getMinCoords(), e.getScaling(), null), null);//pos+(minCoords*Scaling)
//						absMaxCoords = Vector3f.add(e.getPosition(), VectorMath.multiply(e.getMaxCoords(), e.getScaling(), null), null);
//						if (ray.x >= absMinCoords.x && ray.x <= absMaxCoords.x && ray.y >= absMinCoords.y
//								&& ray.y <= absMaxCoords.y&& ray.z >= absMinCoords.z && ray.z <= absMaxCoords.z){ //Entity covers area of RayPoint
//							
//							
//							if (origin.x >= absMinCoords.x && origin.x <= absMaxCoords.x &&
//									origin.y >= absMinCoords.y && origin.y <= absMaxCoords.y&& origin.z >= absMinCoords.z && origin.z <= absMaxCoords.z){
//								//Origin point isn't allowed to be inside of entity!
//								continue;
//							}
//							
//							e.setShowBoundingBox(true);
//							focussedEntity = e;
//							return e;//entity found!
//						}
//					}
//				}
//			}catch(ConcurrentModificationException e){
//				//null
//			}
//		}
//		focussedEntity = null;
//		return null;//nothing found!
//	}
	
	
	public Entity createEntity(EntityLayer layer, Vector3f pickerColors, byte[] mesh, Type type, Object entityData, EBXExternalGUID meshInstanceGUID, Entity parent, String loaderErrorDesc){
		try{
			
			
			MeshChunkLoader msl = resourceHandler.getMeshChunkLoader();
			msl.loadFile(mesh, Core.getGame().getCurrentBundle());
			RawModel[] rawModels = new RawModel[msl.getSubMeshCount()];
			//ArrayList<String> materials = resourceHandler.getMeshVariationDatabaseHandler().getMaterials(msl.getName(), 0); //VARIATION ID ??!
			for (int submesh=0; submesh<msl.getSubMeshCount();submesh++){
				RawModel model = modelHandler.addRawModel(GL11.GL_TRIANGLES, msl.getName()+submesh, msl.getVertexPositions(submesh), msl.getUVCoords(submesh), msl.getIndices(submesh));
				
				int textureID = modelHandler.getLoader().getNotFoundID();
				/*if (materials!=null){
					if (!materials.isEmpty()){
						try{
							if (resourceHandler.getTextureHandler().isExisting(materials.get(submesh))){
								textureID = resourceHandler.getTextureHandler().getTextureID(materials.get(submesh));
							}else{
								if (resourceHandler.getTextureHandler().getTextures().size()<MAX_TEXTURES && !materials.get(submesh).equals("")){
									textureID = modelHandler.getLoader().loadTexture(textureRoot+"res/"+materials.get(submesh)+".jpg"); 
									resourceHandler.getTextureHandler().addTextureID(textureID, materials.get(submesh));
								}
							}
						}catch(Exception e){
							System.err.println("Problem while loding Texture of Submesh: "+submesh);
						}
					}
				}*/
				rawModels[submesh] = model;
			}
			Entity en = null;
			switch (type){
				case Object:
					en = new ObjectEntity(layer, msl.getName(), entityData, parent, rawModels, new EntityTextureData(meshInstanceGUID, null), pickerColors);
					break;
				case Light:
					en = new LightEntity(layer, msl.getName(), entityData, parent, rawModels,  pickerColors);
					break;
			}
			
			//axis aligned bounding box
			float[] maxCoords = msl.getMaxCoords();
			float[] minCoords = msl.getMinCoords();
			en.setMaxCoords(new Vector3f(maxCoords[0], maxCoords[1], maxCoords[2]));
			en.setMinCoords(new Vector3f(minCoords[0], minCoords[1], minCoords[2]));
			return en;
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Could not create entitiy: "+loaderErrorDesc); 
			return null;
		}
	}
	
	public void updateLayer(EntityLayer layer, EBXStructureFile meshVariationDatabase){
		updateEntities(layer.getEntities(), meshVariationDatabase);
		Core.getJavaFXHandler().getDialogBuilder().showInfo("UPDATED", "The layer got updated by the Meshvariation Database!");  
	}
	
	private void updateEntity(Entity e, EBXStructureFile meshVariationDatabase){
		if (e.getType()==Type.Object){
			ObjectEntity objEn = (ObjectEntity) e;
			EntityTextureData etd = objEn.getTextureData();
			if (etd!=null){
				etd.updateTextures(meshVariationDatabase);
			}
		}
		updateEntities(e.getChildrens(), meshVariationDatabase);
	}
	
	private void updateEntities(ArrayList<Entity> entities, EBXStructureFile meshVariationDatabase){
		for (Entity e : entities){
			updateEntity(e, meshVariationDatabase);
		}
	}


//	public Vector3f getRay() {
//		return ray;
//	}


//	public Entity getFocussedEntity() {
//		return focussedEntity;
//	}
	
	public ArrayList<EntityLayer> getLayers() {
		return layers;
	}

	public ObjectEntityPicker getObjectEntityPicker() {
		return objectEntityPicker;
	}
	public GizmoHandler getGizmoHandler() {
		return gizmoHandler;
	}
	
	
	
	
	
}
