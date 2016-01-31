package tk.greydynamics.Resource.Frostbite3.EBX;

import java.util.ArrayList;

public class EBXEnumHelper {
	private String enumName = null;
	private int selectedIndex;
	private EBXComplexDescriptor enumComplexDesc;
	private ArrayList<EBXFieldDescriptor> entries = new ArrayList<>();
	
	public EBXEnumHelper(EBXComplexDescriptor enumComplexDesc, int selectedIndex) {
		this.enumComplexDesc = enumComplexDesc;
		this.selectedIndex = selectedIndex;
	}

	public EBXComplexDescriptor getEnumComplexDesc() {
		return enumComplexDesc;
	}

	public void setEnumComplexDesc(EBXComplexDescriptor enumComplexDesc) {
		this.enumComplexDesc = enumComplexDesc;
	}

	public ArrayList<EBXFieldDescriptor> getEntries() {
		return entries;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	/**
	 * This method updates the EnumName and adds the Entry to the list.
	 */
	public void addEntry(EBXFieldDescriptor entry){
		if (enumName==null){
			this.enumName = entry.getName().replace("_", " ").split(" ")[0];
		}
		entries.add(entry);
	}

	public String getEnumName() {
		return this.enumName;
	}

	public static EBXEnumHelper clone(EBXEnumHelper enumHelper) {
		EBXEnumHelper newHelper = new EBXEnumHelper(EBXComplexDescriptor.clone(enumHelper.getEnumComplexDesc()), Integer.valueOf(enumHelper.getSelectedIndex()));
		for (EBXFieldDescriptor entry : enumHelper.getEntries()){
			newHelper.addEntry(EBXFieldDescriptor.clone(entry));
		}
		return newHelper;
	}
}
