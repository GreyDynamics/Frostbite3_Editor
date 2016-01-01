package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXInstanceRepeater{
	int complexIndex;
	int repetitions;
	public EBXInstanceRepeater(int complexIndex, int repetitions) {
		this.complexIndex = complexIndex;
		this.repetitions = repetitions;
	}
	public int getComplexIndex() {
		return complexIndex;
	}
	public void setComplexIndex(int complexIndex) {
		this.complexIndex = complexIndex;
	}
	public int getRepetitions() {
		return repetitions;
	}
	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}
}