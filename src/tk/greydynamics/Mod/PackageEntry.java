package tk.greydynamics.Mod;

import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;

public class PackageEntry {	
	LinkBundleType bundleType; //bundles - chunks
	String subPackage;//levels/mp/mp_siege/rush
	ResourceType resType;//EBX, ITEXTURE, MESH
	String resourcePath;//mods/mod.../res.../levels/mp/mp_siege/rush/rush.ebx
	String targetPath;//Additional, /levels/mp/mp_siege/rush/rush.ebx -> /levels/mp/mp_.../rush_2.ebx

	public PackageEntry(){
		bundleType = null;
		subPackage = null;
		resType = null;
		resourcePath = null;
		targetPath = null;
	}

	public PackageEntry(LinkBundleType bundleType, String subPackage,
			ResourceType resType, String resourcePath) {
		this.bundleType = bundleType;
		this.subPackage = subPackage;
		this.resType = resType;
		this.resourcePath = resourcePath;
		this.targetPath = null;
	}

	public LinkBundleType getBundleType() {
		return bundleType;
	}

	public void setBundleType(LinkBundleType bundleType) {
		this.bundleType = bundleType;
	}

	public String getSubPackage() {
		return subPackage;
	}

	public void setSubPackage(String subPackage) {
		this.subPackage = subPackage;
	}

	public ResourceType getResType() {
		return resType;
	}

	public void setResType(ResourceType resType) {
		this.resType = resType;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	
	
}
