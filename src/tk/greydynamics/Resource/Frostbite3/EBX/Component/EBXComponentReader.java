package tk.greydynamics.Resource.Frostbite3.EBX.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;

public class EBXComponentReader {
	public static ArrayList<EBXComponent> analyzeEBX(EBXFile ebxFile, EBXComponentHandler handler){
		if (ebxFile==null)return null;
		ArrayList<EBXComponent> components = new ArrayList<>();
		
		for (EBXInstance instance : ebxFile.getInstances()){
			EBXComponent compexComponent = analyzeEBXComplex(instance.getComplex(), true, handler);
			if (compexComponent!=null){
				components.add(compexComponent);
			}else{
				//ERROR
			}
		}
		//Each Instance got analyzed and is part of the known list already.
		System.out.println("EBXFile ("+ebxFile.getTruePath()+") got analyzed and was added to the known Componentslist!");
		return components;
	}
	
	public static EBXComponent analyzeEBXComplex(EBXComplex ebxComplex, boolean isInstanceComplex, EBXComponentHandler handler){
		if (ebxComplex==null)return null;
		
		ArrayList<EBXComponentEntry> componentEntries = new ArrayList<>();
		for (EBXField field : ebxComplex.getFields()){
			EBXComponentEntry entry = analyzeEBXField(field, handler);
			if (entry!=null){
				componentEntries.add(entry);
			}else{
				//ERROR
			}
		}
		EBXComponent complexComponent = new EBXComponent(isInstanceComplex, ebxComplex.getComplexDescriptor().getName(), ebxComplex.getComplexDescriptor().getSize(), ebxComplex.getComplexDescriptor().getAlignment(), true, componentEntries);
		handler.addKnownComponent(complexComponent);//get the name, but keep it independent.
		return complexComponent;
	}
	
	public static EBXComponentEntry analyzeEBXField(EBXField ebxField, EBXComponentHandler handler){
		if (ebxField==null)return null;
		
		int totalSize = 0;
		String type = null;
		
		if (ebxField.getType()==FieldValueType.ArrayComplex||ebxField.getType()==FieldValueType.Complex){
			EBXComplex fieldValue = ebxField.getValueAsComplex();
			if (fieldValue instanceof EBXComplex){
				EBXComponent complexComponent = analyzeEBXComplex(fieldValue, false, handler);
				if (complexComponent!=null){
					type = complexComponent.getName();
					if (ebxField.getType()==FieldValueType.Complex){
						totalSize = complexComponent.getTotalSize();
					}else{
						//keep size 0 if array..
					}
				}else{
					//ERROR
					return null;
				}
			}else{
				//ERROR
				System.err.println("UNKNOWN EBXComponent Complex Type!");
			}
		}else{
			switch (ebxField.getType()) {
				case ArrayComplex:
					//handled above
					break;
				case Bool:
					totalSize = 1;
					break;
				case Byte:
					totalSize = 1;
					break;
				case ChunkGuid:
					totalSize = 16;
					break;
				case Complex:
					//handled above
					break;
				case Enum:
					totalSize = 4;
					break;
				case ExternalGuid:
					break;
				case Float:
					totalSize = 4;
					break;
				case Guid:
					totalSize = 4;
					break;
				case Hex8:
					totalSize = 8;
					break;
				case Integer:
					totalSize = 4;
					break;
				case Short:
					totalSize = 2;
					break;
				case String:
					totalSize = 4;
					break;
				case UInteger:
					totalSize = 4;
					break;
				case Unknown:
					//ERROR
					break;
			}
			type = "_"+ebxField.getType().toString().toUpperCase(); // _STRING, _INTEGER, _GUID
		}
		return new EBXComponentEntry(ebxField.getFieldDescritor().getName(), type, totalSize, 0);
	}
	
	public static EBXComponent readKnownComponents(File file){
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			EBXComponent component = new EBXComponent(false, null, -1, -1, false, null);
			String[] desc = br.readLine().split("::");
			component.setName(desc[0]);
			component.setOccurredAsInstance(Boolean.valueOf(desc[1]));
			component.setTotalSize(Integer.valueOf(desc[2]));
			component.setTotalSize(Integer.valueOf(desc[3]));
		    String line = "";
		    while ((line = br.readLine()) != null){
		    	String[] entryParts = line.split("::");
		    	if (entryParts.length>1){
		    		component.getEntries().add(new EBXComponentEntry(entryParts[0], entryParts[1], Integer.valueOf(entryParts[2]), Integer.valueOf(entryParts[3])));
		    	}else{
		    		break;
		    	}
		    }		     
		    br.close();
		    fr.close();
		    return component; 
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("Could not read EBXComponent "+file.getAbsolutePath());
		}
		return null;
	}
}
