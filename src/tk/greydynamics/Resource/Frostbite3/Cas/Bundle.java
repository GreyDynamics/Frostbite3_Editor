package tk.greydynamics.Resource.Frostbite3.Cas;

public abstract class Bundle {
	private String basePath = null;
	private String deltaPath = null;
	private String name = null;
	private int baseOffset = -1;
	private int deltaOffset = -1;
	private BundleType type = null;
	
	public static enum BundleType {CAS, NONCAS_PATCHED, NONCAS_UNPATCHED, NONCAS_BASE, UNDEFINED};
	
	public Bundle(BundleType bundleType, String basePath, String deltaPath, String name, int baseOffset, int deltaOffset) {
		this.type = bundleType;
		this.baseOffset = baseOffset;
		this.deltaOffset = deltaOffset;
		this.name = name;
		this.basePath = basePath;
		this.deltaPath = deltaPath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BundleType getType() {
		return type;
	}
	public void setType(BundleType type) {
		this.type = type;
	}
	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	public String getDeltaPath() {
		return deltaPath;
	}
	public void setDeltaPath(String deltaPath) {
		this.deltaPath = deltaPath;
	}
	public int getBaseOffset() {
		return baseOffset;
	}
	public void setBaseOffset(int baseOffset) {
		this.baseOffset = baseOffset;
	}
	public int getDeltaOffset() {
		return deltaOffset;
	}
	public void setDeltaOffset(int deltaOffset) {
		this.deltaOffset = deltaOffset;
	}
	
	
	
	
}
