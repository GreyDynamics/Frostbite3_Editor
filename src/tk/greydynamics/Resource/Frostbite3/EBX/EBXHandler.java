package tk.greydynamics.Resource.Frostbite3.EBX;

import java.util.ArrayList;
import java.util.HashMap;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.Frostbite3.Cas.Bundle;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundleEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Modify.EBXModifyHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureFile;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.Structure.EBXStructureReader;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;


public class EBXHandler {
	//public String guidTablePath;
	public EBXLoader loader;
	public EBXCreator creator;
	public HashMap<EBXExternalFileReference, EBXFile> ebxFiles /*FileName, File*/;
	public ArrayList<EBXStructureFile> ebxStructureFiles;
	public EBXModifyHandler modifyHandler;
	
	public enum FieldValueType{
		Complex, ArrayComplex, String, Enum, ExternalGuid, Hex8, Unknown,/*Field,*/ Float, Integer, Bool, Short, Byte, UInteger, ChunkGuid, Guid
	}
	
	public static int hasher(byte[] bytes) {
		int hash = 5381;
		for (Byte b : bytes) {
			hash = hash * 33 ^ b;
		}
		return hash;
	}
	
	public EBXHandler(){
		reset();
	}
	
	public void reset(){
		this.loader = new EBXLoader();
		this.creator = new EBXCreator();
		this.ebxFiles = new HashMap<EBXExternalFileReference, EBXFile>();
		this.ebxStructureFiles = new ArrayList<>();
		this.modifyHandler = new EBXModifyHandler();
	}


		
	public EBXFile loadFile(byte[] data) {
		try{
			if (loader.loadEBX(data)){
				EBXFile newFile = new EBXFile(loader.getTrueFilename(), loader.getInstances(), loader.getFileGUID(), loader.getByteOrder(), loader.getExternalGUIDs());
				for (EBXInstance instance: newFile.getInstances()){
					instance.setParentFile(newFile);
				}
				EBXExternalFileReference efr = new EBXExternalFileReference(loader.getFileGUID(), loader.getTrueFilename());
				ebxFiles.put(efr, newFile);
				return newFile;
			}else{
				return null;
			}
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("EBXFile could not be loaded.");
			return null;
		}
	}
	
	public byte[] createEBX(EBXFile ebxFile){
		//ADD TODO
		return creator.createEBX(ebxFile);
	}
	
	public EBXStructureFile getStructureFileByGUID(String fileGUID, boolean tryLoad, boolean loadOriginal){
		for (EBXStructureFile strFile : ebxStructureFiles){
			if (strFile.getEBXGUID().equalsIgnoreCase(fileGUID)){
				return strFile;
			}
		}
		
		EBXFile ebxFile = getEBXFileByGUID(fileGUID, tryLoad, loadOriginal);
		if (ebxFile!=null){
			EBXStructureFile strFile = readEBXStructureFile(ebxFile);
			if (strFile!=null){
				return strFile;
			}else{
				System.err.println("EBXFile for 'StructureFile from GUID' could not get converted.");
				return null;
			}
		}
		System.err.println("EBXStructureFile with GUID "+fileGUID+" was not found or could not get created!");
		return null;
	}
	
	
	public EBXStructureEntry getStructureInstance(EBXExternalGUID externalGUID, boolean tryLoad, boolean loadOriginal){
		if (externalGUID==null){return null;}
		EBXStructureFile targetFile = getStructureFileByGUID(externalGUID.getFileGUID(), tryLoad, loadOriginal);
		if (targetFile!=null){
			for (EBXStructureInstance instance : targetFile.getInstances()){
				if (instance.getGuid().equalsIgnoreCase(externalGUID.getInstanceGUID())){
					return instance;
				}	
			}
			System.err.println("The instance "+externalGUID.getInstanceGUID()+" does not exist inside the StrucutureFile. "+targetFile.getStructureName());
			return null;
		}
		System.err.println("Unable to get StructureInstance from a Structure, that not exist!");
		return null;
	}
	
	public EBXStructureFile readEBXStructureFile(EBXFile ebxFile){
		EBXStructureFile existingFile = getEBXStructureFileByGUID(ebxFile.getGuid(), false, false, true/*Look Only*/);
		if (existingFile!=null){
			return existingFile;
		}
		EBXStructureFile file = EBXStructureReader.readStructure(ebxFile);
		if (file!=null){
			if (ebxStructureFiles!=null){
				ebxStructureFiles.add(file);
			}
			return file;
		}
		return null;
	}
	public EBXStructureFile getEBXStructureFileByGUID(String fileGUID, boolean tryLoad, boolean loadOriginal, boolean lookOnly){
		for (EBXStructureFile file : ebxStructureFiles){
			if (file.getEBXGUID().equalsIgnoreCase(fileGUID)){
				return file;
			}
		}
		if (!lookOnly){
			EBXFile ebxFile = getEBXFileByGUID(fileGUID, tryLoad, loadOriginal);
			if (ebxFile!=null){
				return readEBXStructureFile(ebxFile);
			}
		}
		return null;
	}
	
	public EBXFile getEBXFileByGUID(String fileGUID, boolean tryLoad, boolean loadOriginal){
		if (fileGUID==null){return null;}
		for (EBXExternalFileReference efr : ebxFiles.keySet()){
			if (efr.getGuid().equalsIgnoreCase(fileGUID)){
				return ebxFiles.get(efr);
			}
		}
		if (tryLoad){	
			byte[] data = getEBXFileBytesByGUID(fileGUID, loadOriginal);
			if (data==null){
				return null;
			}
			EBXFile ebxFile = loadFile(data);
			if (ebxFile!=null){
				return ebxFile;
			}else{
				System.err.println("EBXFile's data was found, but could not be converted.");
			}
		}
		//System.err.println("EBXFile could not be found.");
		return null;
		
	}
	
	public byte[] getEBXFileBytesByGUID(String fileGUID, boolean loadOriginal){
		if (Core.getGame().getCurrentBundle().getType()==Bundle.BundleType.CAS){
			ResourceLink targetLink = null;
			CasBundle casBundle = (CasBundle) Core.getGame().getCurrentBundle();
			for (ResourceLink ebxLink : casBundle.getEbx()){
				if (ebxLink.getEbxFileGUID()!=null){
					if (ebxLink.getEbxFileGUID().equalsIgnoreCase(fileGUID)){
						targetLink = ebxLink;
						break;
					}
				}else{
					//System.err.println(ebxLink.getName()+" could not be read!");
				}
			}
			if (targetLink==null){
				System.err.println("EBXFile not found. No ResourceLink with FileGUID "+fileGUID+" found.");
				return null;
			}
			byte[] data = Core.getGame().getResourceHandler().readResourceLink(targetLink, loadOriginal);
			return data;
		}else{
			NonCasBundleEntry ebxTarget = null;
			NonCasBundle nonCasBundle = (NonCasBundle) Core.getGame().getCurrentBundle();
			for (NonCasBundleEntry ebxEntry : nonCasBundle.getEbx()){
				if (ebxEntry.getEbxFileGUID()!=null){
					if (ebxEntry.getEbxFileGUID().equalsIgnoreCase(fileGUID)){
						ebxTarget = ebxEntry;
						break;
					}
				}else{
					//System.err.println(ebxEntry.getName()+" could not be read!");
				}
			}
			if (ebxTarget==null){
				System.err.println("EBXFile not found. No NonCasBundleEntry with FileGUID "+fileGUID+" found.");
				return null;
			}
			byte[] data = Core.getGame().getResourceHandler().readNonCasBundleEntry(ebxTarget);
			return data;
		}
	}
	
	public EBXFile getEBXFileByTrueFileName(String trueFileName){
		for (EBXExternalFileReference efr : ebxFiles.keySet()){
			if (efr.getTrueFileName().equalsIgnoreCase(trueFileName)){
				return ebxFiles.get(efr);
			}
		}
		System.err.println("No EBXFile with trueFileName "+trueFileName+" was found.");
		return null;
	}
	
	public EBXFile getEBXFileByResourceName(String resourceName, boolean tryLoad, boolean loadOrignal){
		if (Core.getGame().getCurrentBundle().getType()==Bundle.BundleType.CAS){
			CasBundle casBundle = (CasBundle) Core.getGame().getCurrentBundle();
			for (ResourceLink link : casBundle.getEbx()){
				if (link.getName().equalsIgnoreCase(resourceName)){
					return getEBXFileByGUID(link.getEbxFileGUID(), tryLoad, loadOrignal);
				}
			}
			System.err.println("No ResourceLink was found for "+resourceName);
			return null;
		}else{
			return null;
		}
	}
	
	public String getEBXGUIDByResourceName(String resLinksName){
		if (Core.getGame().getCurrentBundle().getType()==Bundle.BundleType.CAS){
			CasBundle casBundle = (CasBundle) Core.getGame().getCurrentBundle();
			for (ResourceLink resLink : casBundle.getEbx()){
				if (resLink.getName().equalsIgnoreCase(resLinksName)){
					return resLink.getEbxFileGUID();
				}
			}
		}
		return null;
	}
	
	public static ArrayList<EBXField> getEBXField(EBXInstance primaryInstance, ArrayList<EBXField> targetList, String filterName, FieldValueType filterType){
		if (targetList==null){
			targetList = new ArrayList<>();
		}
		return getEBXField(primaryInstance.getComplex(), targetList, filterName, filterType);
	}
	
	public static ArrayList<EBXField>  getEBXField(EBXComplex complex, ArrayList<EBXField> targetList, String filterName, FieldValueType filterType){
		for (EBXField field : complex.getFields()){
			if (field.getFieldDescritor().getName().equalsIgnoreCase(filterName)){
				if (filterType==null){
					targetList.add(field);
				}else if (filterType==field.getType()){
					targetList.add(field);
				}
			}else{
				if (field.getValue() instanceof EBXComplex){
					getEBXField(field.getValueAsComplex(), targetList, filterName, filterType);
				}
			}
		}
		return targetList;
	}
	public static EBXField getEBXField(EBXInstance primaryInstance, int fieldID){
		return getEBXField(primaryInstance.getComplex(), fieldID);
	}
	
	public static EBXField getEBXField(EBXComplex complex, int fieldID){
		for (EBXField field : complex.getFields()){
			if (field.getFieldID()==fieldID){
				return field;
			}else{
				if (field.getValue() instanceof EBXComplex){
					getEBXField(field.getValueAsComplex(), fieldID);
				}
			}
		}
		return null;
	}
	
	public static EBXInstance getEBXInstance(EBXFile ebxFile, String instanceGUID){
		for (EBXInstance instance : ebxFile.getInstances()){
			if (instance.getGuid().equalsIgnoreCase(instanceGUID)){
				return instance;
			}
		}
		return null;
	}
	
	public EBXInstance getEBXInstance(EBXExternalGUID externalGUID, boolean tryLoad, boolean loadOriginal){
		EBXFile ebxFile = getEBXFileByGUID(externalGUID.getFileGUID(), tryLoad, loadOriginal);
		if (ebxFile!=null){
			for (EBXInstance instance : ebxFile.getInstances()){
				if (instance.getGuid().equalsIgnoreCase(externalGUID.getInstanceGUID())){
					return instance;
				}
			}
			System.err.println("GetEBXInstance(ExternalGUID) was unable to find the requested instance inside the ebx file.");
		}
		System.err.println("GetEBXInstance(ExternalGUID) was unable to load the target ebx file.");
		return null; 
	}
	
	
	/*GETTER AND SETTER*/

	public ArrayList<EBXStructureFile> getEBXStructureFiles() {
		return ebxStructureFiles;
	}	

	public EBXLoader getLoader() {
		return loader;
	}

	public EBXCreator getCreator() {
		return creator;
	}
	
	public HashMap<EBXExternalFileReference, EBXFile> getEBXFiles() {
		return ebxFiles;
	}

	public EBXModifyHandler getModifyHandler() {
		return modifyHandler;
	}
	
	
}
