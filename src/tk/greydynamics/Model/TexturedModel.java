package tk.greydynamics.Model;

public class TexturedModel {
	public RawModel rawModel;
	public int textureID;
	
	public TexturedModel(RawModel rawModel, int textureID) {
		this.rawModel = rawModel;
		this.textureID = textureID;
	}
}
