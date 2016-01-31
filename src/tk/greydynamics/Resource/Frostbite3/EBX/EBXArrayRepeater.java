package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXArrayRepeater{
	int offset;
	int repetitions;
	int complexIndex;
	public EBXArrayRepeater(int offset, int repetitions, int complexIndex) {
		this.offset = offset;
		this.repetitions = repetitions;
		this.complexIndex = complexIndex;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getRepetitions() {
		return repetitions;
	}
	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}
	public int getComplexIndex() {
		return complexIndex;
	}
	public void setComplexIndex(int complexIndex) {
		this.complexIndex = complexIndex;
	}
	public static EBXArrayRepeater clone(EBXArrayRepeater arrayRepeater) {
		return new EBXArrayRepeater(
				Integer.valueOf(arrayRepeater.getOffset()),
				Integer.valueOf(arrayRepeater.getRepetitions()),
				Integer.valueOf(arrayRepeater.getComplexIndex())
			);
	}
	
}