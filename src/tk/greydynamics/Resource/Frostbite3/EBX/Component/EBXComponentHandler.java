package tk.greydynamics.Resource.Frostbite3.EBX.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import tk.greydynamics.Game.Core;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;

public class EBXComponentHandler {
	private ArrayList<EBXComponentComplex> knownComponents;
	private String knownComponentsPath;
	public static String knownComponentsFileType = ".txt";
	
	public EBXComponentHandler(String knownComponentsPath){
		reset(knownComponentsPath);
	}
	
	//***********************************//
	public boolean saveKnownComponents(){
		ArrayList<String> stringList = new ArrayList<>();
		for (EBXComponentComplex knwnComp : knownComponents){
			if (knwnComp.isNew()){
				//Only save new ones, we haven't known before this session!
				stringList.clear();
				stringList.add(knwnComp.getName() +"::"+ knwnComp.isOccurredAsInstance() +"::"+ knwnComp.getTotalSize() +"::"+ knwnComp.getAlignment());
				if (knwnComp.getName().equals("array")){
					if (!knwnComp.getEntries().isEmpty()){
						EBXComponentEntry entry = knwnComp.getEntries().get(0);
						stringList.add(entry.getName() +"::"+ entry.getType() +"::"+ entry.getTotalSize() +"::"+ entry.getAlignment());
						if (!FileHandler.writeLine(stringList, new File(knownComponentsPath+knwnComp.getName()+"/"+entry.getType()+".txt"))){
							System.err.println("EBXComponent"+ knwnComp.getName()+" unable to save.");
						}
					}
				}else{
					for (EBXComponentEntry entry : knwnComp.getEntries()){
						stringList.add(entry.getName() +"::"+ entry.getType() +"::"+ entry.getTotalSize() +"::"+ entry.getAlignment());
					}
					
					File temp = new File(Core.EDITOR_PATH_TEMP+UUID.randomUUID());
					if (FileHandler.writeLine(stringList, temp)){
						String hash = FileHandler.checkSumSHA1(temp);
						if (hash!=null){
							File target = new File(knownComponentsPath+knwnComp.getName()+"/"+hash+knownComponentsFileType);
							if (target.exists()){
								//ALREADY EXISTS, SKIP
							}else{
								FileHandler.copy(temp, target, false);
							}
						}
						temp.delete();
					}
				}
			}
		}
		return false;
	}
	
	public boolean readKnownComponents(){
		reset(knownComponentsPath);
		for (File file : FileHandler.listf(knownComponentsPath, knownComponentsFileType)){
			knownComponents.add(EBXComponentReader.readKnownComponents(file));
		}
		return false;
	}
	//***********************************//
	
	public void addKnownComponent(EBXComponentComplex c){
		if (!isKnownComponent(c)){
			knownComponents.add(c);
		}
	}
	public void addKnownComponent(EBXFile e){
		EBXComponentReader.analyzeEBX(e, this);
//		ArrayList<EBXComponent> components =
//		if (components!=null){
//			for (EBXComponent c : components){
//				it got already added to knownComponents List
//				i just like debugging :)
//			}
//		}
	}
	
	public boolean isKnownComponent(EBXComponentComplex c){
		for (EBXComponentComplex component : knownComponents){
			if (component.getName().equals(c.getName())){
				if (component.getTotalSize()==c.getTotalSize()){
					if (component.getEntries().size()==c.getEntries().size()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
		
	public String getKnownComponentsPath() {
		return knownComponentsPath;
	}

	public ArrayList<EBXComponentComplex> getKnownComponents() {
		return knownComponents;
	}

	public void reset(String knownComponentsPath){
		this.knownComponents = new ArrayList<EBXComponentComplex>();
		this.knownComponentsPath = knownComponentsPath;
	}
}
