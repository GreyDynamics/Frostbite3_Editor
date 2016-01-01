package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader.EntryType;

public class EBXStaticModelEntityData extends EBXStructureEntry{
	
	private EBXObjArray partLinks = null;
	private EBXExternalGUID mesh = null;
	private long boneCount;
	private EBXObjArray basePoseTransforms = null;
	private EBXObjArray physicsPartInfos = null;
	private EBXObjArray networkInfo = null;
	private boolean excludeFromNearbyObjectDestruction = false;
	private boolean animatePhysics = false;
	private boolean terrainShaderNodesEnable = false;
	private boolean visible = true;
	
	public EBXStaticModelEntityData(EBXStructureEntry parent, EBXComplex complex) {
		super(parent, EntryType.StaticModelEntityData);
	
		for (EBXField field : complex.getFields()) {
			switch (field.getFieldDescritor().getName()) {
			case "ExcludeFromNearbyObjectDestruction": /* -------------- ExcludeFromNearbyObjectDestruction -------------- */
				this.excludeFromNearbyObjectDestruction = (boolean) field.getValue();
				break;
			case "AnimatePhysics": /* -------------- AnimatePhysics -------------- */
				this.animatePhysics = (boolean) field.getValue();
				break;
			case "TerrainShaderNodesEnable": /* -------------- TerrainShaderNodesEnable -------------- */
				this.terrainShaderNodesEnable = (boolean) field.getValue();
				break;
			case "Visible": /* -------------- Visible -------------- */
				this.visible = (boolean) field.getValue();
				break;
			case "BoneCount": /* -------------- BoneCount -------------- */
				this.boneCount = (Long) field.getValue();
				break;
			case "Mesh": /* -------------- Mesh -------------- */
				this.mesh = new EBXExternalGUID(field);
				break;
			default:
				System.err.println(field.getFieldDescritor().getName()+" is not handled in EBXStaticModelEntityData's Constructor for readIn!");
				break;
			}
		}
		
	}

	public EBXObjArray getPartLinks() {
		return partLinks;
	}

	public EBXExternalGUID getMesh() {
		return mesh;
	}

	public long getBoneCount() {
		return boneCount;
	}

	public EBXObjArray getBasePoseTransforms() {
		return basePoseTransforms;
	}

	public EBXObjArray getPhysicsPartInfos() {
		return physicsPartInfos;
	}

	public EBXObjArray getNetworkInfo() {
		return networkInfo;
	}

	public boolean isExcludeFromNearbyObjectDestruction() {
		return excludeFromNearbyObjectDestruction;
	}

	public boolean isAnimatePhysics() {
		return animatePhysics;
	}

	public boolean isTerrainShaderNodesEnable() {
		return terrainShaderNodesEnable;
	}

	public boolean isVisible() {
		return visible;
	}
	
	
}
