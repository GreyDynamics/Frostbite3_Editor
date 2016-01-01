package tk.greydynamics.Terrain;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Player.PlayerEntity;
import tk.greydynamics.Player.PlayerHandler;

public class TerrainHandler {
	public ArrayList<Terrain> terrainList = new ArrayList<Terrain>();
	public static float distance = 100f;
	int maxX = 32;
	int maxZ = 32;
	
	
	public void generate(int idX, int idZ) {
		terrainList.add(new Terrain(new Vector3f((float)idX*distance*maxX, 0.0f,(float)idZ*distance*maxZ), maxX, maxZ, distance));
	}
	
	public ArrayList<Terrain> getTerrainList() {
		return terrainList;
	}
	
	public Terrain getCurrentTerrainOn(float x, float z){
		/*for (Terrain terrain : terrainList) {
			if ((terrain.points[0][0].getX() >= x && terrain.points[maxX-1][maxZ-1].getX() <= x)||
					(terrain.points[0][0].getX() <= x && terrain.points[maxX-1][maxZ-1].getX() >= x)){
				if ((terrain.points[0][0].getZ() >= z && terrain.points[maxX-1][maxZ-1].getZ() <= z)||
						(terrain.points[0][0].getZ() <= z && terrain.points[maxX-1][maxZ-1].getZ() >= z)){
					return terrain;
				}
			}
		}*/
		return null;
	}
	
	public int[] getCurrentTerrainID(Terrain current){
		int[] id = new int[2];
		int size = (int) (maxX*distance);
		id[0] = (int) (current.points[0][0].getX()/size);
		id[1] = (int) (current.points[0][0].getZ()/size);
		return id;
	}

	public void collisionUpdate(PlayerHandler plH) {
		PlayerEntity pe = plH.getPlayerEntity();
		Terrain currentTerrain = getCurrentTerrainOn(pe.getPosX(), pe.getPosZ());
		if (currentTerrain != null){
			getCurrentTerrainID(currentTerrain);
		}
	}
	
}
