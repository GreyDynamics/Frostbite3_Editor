package tk.greydynamics.Render;

import java.io.File;
import java.util.HashMap;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.Cas.CasDataReader;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ITexture;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ITextureHandler;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ImageConverter;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ImageConverter.ImageType;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class TextureHandler {
	public HashMap<String, Integer> textures;
	
	public TextureHandler(){
		this.textures = new HashMap<String, Integer>();
	}
	public int loadITexture(ResourceLink itextureResLink){
		if (isExisting(itextureResLink.getName())){
			return getTextureID(itextureResLink.getName());
		}
		byte[] itextureHeader = Core.getGame().getResourceHandler().readResourceLink(itextureResLink);
		byte[] ddsBytes = ITextureHandler.getDSS(itextureHeader);
		if (ddsBytes!=null){
			File ddsFile = new File("temp/images/"+itextureResLink.getName().replace('/', '_')+".dds");
			FileHandler.writeFile(ddsFile.getAbsolutePath(), ddsBytes);
			
			File pngFile = null;
			ITexture itexture = new ITexture(itextureHeader, null);
			if (itexture.getPixelFormat()==ITexture.TF_NormalDXN){
				//Convert using Nvidia
				File tga = ImageConverter.convertToTGA(ddsFile);
				pngFile = ImageConverter.convert(tga, ImageType.PNG, true);
			}else{
				//Convert using ImageMagick
				pngFile = ImageConverter.convert(ddsFile, ImageType.PNG, true);
			}
			if (pngFile!=null){
				int txID = Core.getGame().getModelHandler().getLoader().loadTexture(pngFile.getAbsolutePath());
				if (txID!=Core.getGame().getModelHandler().getLoader().getNotFoundID()){
					textures.put(itextureResLink.getName(), txID);
				}
			}
		}
		return Core.getGame().getModelHandler().getLoader().getNotFoundID();
	}
	
	public int getTextureID(String name){
		try{
			return textures.get(name);
		}catch (Exception e){
			return Core.getGame().getModelHandler().getLoader().getNotFoundID();
		}
	}
	
	public boolean isExisting(String name){
		for (String s : textures.keySet()){
			if (s.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public void addTextureID(Integer id, String name){
		textures.put(name, id);
	}

	public HashMap<String, Integer> getTextures() {
		return textures;
	}
	

	
	
}
