package tk.greydynamics.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ImageConverter;
import tk.greydynamics.Resource.Frostbite3.ITEXTURE.ImageConverter.ImageType;

public class Loader {
	public ArrayList<Integer> vaos = new ArrayList<Integer>();
	public ArrayList<Integer> vbos = new ArrayList<Integer>();
	public HashMap<String, Integer> textures = new HashMap<String, Integer>();
	public int notFoundID;
	public int crosshairID;
	
	public RawModel loadVAO(String name, int drawMethod, float[] positions, float[] uvs, int[] indices){
		int vaoID = createVAO();
		bindIndiciesBuffer(indices);
		storeDataAsAttr(0, 3, positions);
		storeDataAsAttr(1, 2, uvs);
		unbindVAO();
		return new RawModel(name, vaoID, indices.length, drawMethod);
	}
	
	public RawModel loadVAO(String name, int drawMethod, float[] positions){
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		unbindVAO();
		return new RawModel(name, vaoID, positions.length/2, drawMethod);
	}
	
	public int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	public void cleanUp(){
		for(int vao: vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo: vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture: textures.values()){
			GL11.glDeleteTextures(texture);
		}
	}
	
	public Loader(){
		notFoundID = 0;
		crosshairID = 0;
	}
	public void init(){
		notFoundID = loadTexture("res/notFound/notFound.dds");
		crosshairID = loadTexture("res/interface/crosshair-vector.png");
	}
	
	public int getNotFoundID() {
		return notFoundID;
	}
	

	public int getCrosshairID() {
		return crosshairID;
	}

	public int loadTexture(String path){
		Texture texture;
		if (textures.containsKey(path)){
			return textures.get(path);
		}else{
			try {
				if (path.endsWith(".dds")){
					File tga = ImageConverter.convert(new File(path), ImageType.TGA, true);
					texture = TextureLoader.getTexture("TGA", new FileInputStream(tga.getAbsolutePath()));
				}else{
					texture = TextureLoader.getTexture("PNG", new FileInputStream(path));
				}
				textures.put(path, texture.getTextureID());
				return texture.getTextureID();
			} catch (IOException e) {
				System.err.println("Unable to load Texture - FILE NOT FOUND! "+path);
				return notFoundID;
			}	
		}
	}
	
	
	
	private void storeDataAsAttr(int index, int dimensions, float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = getFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, dimensions, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
	}
	
	private void storeDataInAttributeList(int attributeNumber, int vectorSize, float[] data){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, vectorSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	public void bindIndiciesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = getIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	
	public IntBuffer getIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public FloatBuffer getFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
}
