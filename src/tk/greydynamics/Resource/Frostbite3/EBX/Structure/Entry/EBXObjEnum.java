package tk.greydynamics.Resource.Frostbite3.EBX.Structure.Entry;

import java.util.HashMap;

import tk.greydynamics.Resource.Frostbite3.EBX.EBXFieldDescriptor;

public class EBXObjEnum {
	public HashMap<String, Boolean> values;

	//Constructor
	@SuppressWarnings("unchecked")
	public EBXObjEnum(HashMap<?, ?> hashMap, boolean areFieldDescriptors) {
		if (areFieldDescriptors){
			values = new HashMap<>();
			HashMap<EBXFieldDescriptor, Boolean> fieldHashMap = (HashMap<EBXFieldDescriptor, Boolean>) hashMap;
			for (EBXFieldDescriptor fieldDescriptor : fieldHashMap.keySet()){
				Boolean bool = fieldHashMap.get(fieldDescriptor);
				values.put(fieldDescriptor.getName(), bool);
			}
		}else{
			this.values = (HashMap<String, Boolean>) hashMap;
		}
	}
	
	//Getter and Setter
	public HashMap<String, Boolean> getValues() {
		return values;
	}	
}
