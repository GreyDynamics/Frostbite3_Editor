package tk.greydynamics.Mod;

import java.util.ArrayList;

public class Package {
	public String name;
	public ArrayList<PackageEntry> entries;
		
	//Getter and Setter.
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<PackageEntry> getEntries() {
		return entries;
	}
	
	
	
	//Const.
	
	public Package(String name){
		this.name = name;
		this.entries = new ArrayList<>();
	}	
}
