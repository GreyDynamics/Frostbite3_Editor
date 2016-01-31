package tk.greydynamics.Mod;

public class Mod {
	String name;
	String author;
	String path;
	String tempFolder;
	String desc;
	String game;
	String gameVersion;
	String folderName;
	boolean isCompiled = false;
	boolean isInstalled = false;

	// String destFolderPath;

	public Mod(String name, String author, String game, String gameVersion, String path, String folderName, boolean isCompiled, boolean isInstalled) {
		this.name = name;
		this.author = author;
		this.path = path;
		this.game = game;
		this.tempFolder = "";
		this.desc = "";
		this.folderName = folderName;
		// this.destFolderPath = null;
		this.gameVersion = gameVersion;
		this.isInstalled = isInstalled;
		this.isCompiled = isCompiled;
	}

	public Mod() {
		/* USING NULLCONSTRUCTOR */
	}

	// public String getDestFolderPath() {
	// return destFolderPath;
	// }
	//
	// public void setDestFolderPath(String destFolderPath) {
	// this.destFolderPath = destFolderPath;
	// }

	public String getFolderName() {
		return folderName;
	}

	public boolean isCompiled() {
		return isCompiled;
	}

	public void setCompiled(boolean isCompiled) {
		this.isCompiled = isCompiled;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}
	

}
