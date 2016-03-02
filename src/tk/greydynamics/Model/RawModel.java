package tk.greydynamics.Model;

import java.util.ArrayList;

import tk.greydynamics.Game.Core;

public class RawModel {
	private String name;
	private int vaoID;
	private int vertexCount;
	private int drawMethod;
	
	private int lifeTicks = 0;
	public static int LIFETIME = Core.TICK_RATE*10;
	
	public RawModel(String name, int vaoID, int vertexCount, int drawMethod) {
		this.name = name;
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.drawMethod = drawMethod;
	}
	
	public RawModel cleanUP(Loader loader){
		loader.cleanVAO(this.vaoID, true);
		this.vaoID = -1;
		this.vertexCount = -1;
		return this;
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

	public int getLifeTicks() {
		return this.lifeTicks;
	}
	
	public void addLifeTick(){
		this.lifeTicks += 1;
	}
	
	public void poke(){
		this.lifeTicks = 0;
	}

	public void setLifeTicks(int lifeTicks) {
		this.lifeTicks = lifeTicks;
	}
}
