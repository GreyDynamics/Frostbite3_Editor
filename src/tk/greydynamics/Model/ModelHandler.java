package tk.greydynamics.Model;

import java.util.ArrayList;
import java.util.HashMap;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Game.Core;

public class ModelHandler {
	private HashMap<String, RawModel> rawModels = new HashMap<String, RawModel>();
	//public HashMap<String, TexturedModel> texturedModels = new HashMap<String, TexturedModel>();
	
	private Loader loader = new Loader();
	
	public RawModel addRawModel(int drawMethod, String name, float[] positions, float[] uvs, int[] indices){
		RawModel existingRawModel = rawModels.get(name);
		if (existingRawModel!=null){
			return existingRawModel;
		}
		RawModel model = loader.loadVAO(name, drawMethod, positions, uvs, indices);
		if (model!=null){
			model.setLifeTicks(0-(Core.TICK_RATE*30));
			rawModels.put(name, model);
			return model;
		}
		return null;
	}
	
	/*public String addTexturedModel(RawModel rawModel, int textureID){
		String name = rawModel.getName()+"_TX_"+textureID;
		texturedModels.put(name, new TexturedModel(rawModel, textureID));
		return name;
	}*/
	
	public void addLifeTick(){
		for (RawModel m : rawModels.values()){
			m.addLifeTick();
		}
	}
	
	public int cleanUnused(){
		ArrayList<RawModel> unusedModels = new ArrayList<>();
		for (RawModel model : rawModels.values()){
			if (model.getLifeTicks()>RawModel.LIFETIME){
				model.cleanUP(loader);
				unusedModels.add(model);
			}
		}
		for (RawModel m : unusedModels){
			rawModels.remove(m.getName());
			System.out.println("- CLEANING FROM GPU MEMORY: "+m.getName()+" -");
		}
		return unusedModels.size();
	}
	
	public HashMap<String, RawModel> getRawModels(){
		return rawModels;
	}
	
	/*public HashMap<String, TexturedModel> getTexturedModels(){
		return texturedModels;
	}*/
	
	public Loader getLoader(){
		return loader;
	}
}
