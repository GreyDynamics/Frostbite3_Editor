package tk.greydynamics.Resource.Frostbite3.EBX;

public class EBXHeader{
    public int absStringOffset;
    public int lenStringToEOF;
    public int numGUID;
    public int numInstanceRepeater;
    public int numGUIDRepeater;
    public int unknown;
    public int numComplex;
    public int numField;
    public int lenName;
    public int lenString;
    public int numArrayRepeater;
    public int lenPayload;
    
	public EBXHeader(int absStringOffset, int lenStringToEOF, int numGUID,
			int numInstanceRepeater, int numGUIDRepeater, int unknown,
			int numComplex, int numField, int lenName, int lenString,
			int numArrayRepeater, int lenPayload) {
		this.absStringOffset = absStringOffset;
		this.lenStringToEOF = lenStringToEOF;
		this.numGUID = numGUID;
		this.numInstanceRepeater = numInstanceRepeater;
		this.numGUIDRepeater = numGUIDRepeater;
		this.unknown = unknown;
		this.numComplex = numComplex;
		this.numField = numField;
		this.lenName = lenName;
		this.lenString = lenString;
		this.numArrayRepeater = numArrayRepeater;
		this.lenPayload = lenPayload;
	}

	public EBXHeader() {
		// TODO Auto-generated constructor stub
	}

	public int getAbsStringOffset() {
		return absStringOffset;
	}

	public void setAbsStringOffset(int absStringOffset) {
		this.absStringOffset = absStringOffset;
	}

	public int getLenStringToEOF() {
		return lenStringToEOF;
	}

	public void setLenStringToEOF(int lenStringToEOF) {
		this.lenStringToEOF = lenStringToEOF;
	}

	public int getNumGUID() {
		return numGUID;
	}

	public void setNumGUID(int numGUID) {
		this.numGUID = numGUID;
	}

	public int getNumInstanceRepeater() {
		return numInstanceRepeater;
	}

	public void setNumInstanceRepeater(int numInstanceRepeater) {
		this.numInstanceRepeater = numInstanceRepeater;
	}

	public int getNumGUIDRepeater() {
		return numGUIDRepeater;
	}

	public void setNumGUIDRepeater(int numGUIDRepeater) {
		this.numGUIDRepeater = numGUIDRepeater;
	}

	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}

	public int getNumComplex() {
		return numComplex;
	}

	public void setNumComplex(int numComplex) {
		this.numComplex = numComplex;
	}

	public int getNumField() {
		return numField;
	}

	public void setNumField(int numField) {
		this.numField = numField;
	}

	public int getLenName() {
		return lenName;
	}

	public void setLenName(int lenName) {
		this.lenName = lenName;
	}

	public int getLenString() {
		return lenString;
	}

	public void setLenString(int lenString) {
		this.lenString = lenString;
	}

	public int getNumArrayRepeater() {
		return numArrayRepeater;
	}

	public void setNumArrayRepeater(int numArrayRepeater) {
		this.numArrayRepeater = numArrayRepeater;
	}

	public int getLenPayload() {
		return lenPayload;
	}

	public void setLenPayload(int lenPayload) {
		this.lenPayload = lenPayload;
	}
}