package tk.greydynamics.Model;

public class RawModel {
	public String name;
	public int vaoID;
	public int vertexCount;
	public int drawMethod;

	public RawModel(String name, int vaoID, int vertexCount, int drawMethod) {
		this.name = name;
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.drawMethod = drawMethod;
	}

	public String getName() {
		return name;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int getDrawMethod() {
		return drawMethod;
	}

}
