package tk.greydynamics.Resource.Frostbite3.EBX;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;

public class EBXLoader {
	public byte[] ebxFileBytes;
	public String guidTablePath;
	//public HashMap<Integer, Integer> numDict = new HashMap<Integer, Integer>();
	public HashMap<Integer, String> keywordDict;
	

	public static byte[] littleHeader = new byte[] { (byte) 0xCE, (byte) 0xD1,
			(byte) 0xB2, (byte) 0x0F };
	public byte[] bigHeader = new byte[] { (byte) 0x0F, (byte) 0xB2,
			(byte) 0xD1, (byte) 0xCE };

	private ByteOrder order;
	public EBXHeader header;

	private int arraySectionstart;
	private String trueFilename;
	private String fileGUID;
	private EBXExternalGUID[] externalGUIDs;
	private ArrayList<String> internalGUIDs;
	private EBXFieldDescriptor[] fieldDescriptors;
	private EBXComplexDescriptor[] complexDescriptors;
	private EBXInstanceRepeater[] instanceRepeaters;
	private EBXArrayRepeater[] arrayRepeaters;
	private ArrayList<EBXInstance> instances;
	private ArrayList<EBXField> fields;
	
	private ArrayList<Integer> internalGUIDFieldIndexs;
		
	private FileSeeker seeker;
	
	private boolean isPrimaryInstance;
	private String filePath;
	
	public ArrayList<EBXInstance> getInstances(){
		return instances;
	}
	
	public String getTrueFilename(){
		return trueFilename;
	}
	
	public static String getGUID(byte[] ebxFile){
		if (ebxFile==null){return null;}
		//4Bytes Magic + 36Bytes Header == FileGUID Offset at 40. Byte (0x28)
		return FileHandler.bytesToHex(FileHandler.readByte(ebxFile, 0x28, 16)).toUpperCase();
	}

	public boolean loadEBX(byte[] ebxFileBytes){
		if (ebxFileBytes==null){
			return false;
		}
		this.seeker = new FileSeeker();
		this.ebxFileBytes = ebxFileBytes;
		this.trueFilename = "";
		
		if (Arrays.equals(readMagic(ebxFileBytes), littleHeader)){
			order = ByteOrder.LITTLE_ENDIAN;
		}else if (Arrays.equals(readMagic(ebxFileBytes), bigHeader)){
			order = ByteOrder.BIG_ENDIAN;
		}else{
			System.err.println("EBX File does not match Header's: "+filePath);
		}
		
		//Decrypt Header 36 Bytes long 3I6H3I
		seeker.setOffset(0x04);
		header = new EBXHeader(
				FileHandler.readInt(ebxFileBytes, seeker, order), //absolute offset for string section start 0x04 4bytes
				FileHandler.readInt(ebxFileBytes, seeker, order), //length from string section start to EOF 0x08 4bytes
				FileHandler.readInt(ebxFileBytes, seeker, order), //number of external GUIDs 0x0C 4bytes
				FileHandler.readShort(ebxFileBytes, seeker, order), //total number of instance repeaters 0x10 2byte
				FileHandler.readShort(ebxFileBytes, seeker, order), //instance repeaters with GUID 0x12 2bytes
				FileHandler.readShort(ebxFileBytes, seeker, order), //0x14 2bytes ??!
				FileHandler.readShort(ebxFileBytes, seeker, order), //number of complex entries 0x16 2bytes
				FileHandler.readShort(ebxFileBytes, seeker, order), //number of field entries 0x18 2bytes
				FileHandler.readShort(ebxFileBytes, seeker, order), //length of name section including padding 0x1A 2bytes
				FileHandler.readInt(ebxFileBytes, seeker, order), //length of string section including padding 0x1C 4bytes
				FileHandler.readInt(ebxFileBytes, seeker, order), //num array repeater 0x20 4bytes
				FileHandler.readInt(ebxFileBytes, seeker, order)); //length of normal payload section; the start of the array payload section is absStringOffset+lenString+lenPayload 0x24 4bytes	
		if (seeker.hasError()){
			return false;
		}
		//Calculate ArraySection Start Offset
		arraySectionstart=header.getAbsStringOffset()+header.getLenString()+header.getLenPayload();
		if (seeker.hasError()){
			return false;
		}
		//Read out file GUID as HEXSTRING!
		fileGUID=FileHandler.bytesToHex(FileHandler.readByte(ebxFileBytes, seeker, 16)).toUpperCase();
		if (seeker.hasError()){
			return false;
		}
		//Calc of externalGUIDs
		while (seeker.getOffset()%16!=0){ seeker.seek(1); } //#padding
		externalGUIDs = new EBXExternalGUID[header.getNumGUID()];
		for (int i=0;i<externalGUIDs.length;i++){
			externalGUIDs[i] = new EBXExternalGUID(
					(FileHandler.bytesToHex(FileHandler.readByte(ebxFileBytes, seeker, 16)).toUpperCase()),
					(FileHandler.bytesToHex(FileHandler.readByte(ebxFileBytes, seeker, 16)).toUpperCase())
			);
			if (seeker.hasError()){
				return false;
			}
		}
		//Get Keywords and Calculate Hashes
		this.keywordDict = new HashMap<Integer, String>();
		int startKeyOffset = 0x40+((header.getNumGUID())*0x20);
		int keyOffset = 0;
		while (startKeyOffset+keyOffset < startKeyOffset+header.getLenName()){
			String s = readString(ebxFileBytes, startKeyOffset+keyOffset);
			keywordDict.put(EBXHandler.hasher(s.getBytes()), s);
			keyOffset += s.length()+1;
			if (seeker.hasError()){
				return false;
			}
		}
		//fieldDescriptors
		seeker.setOffset(startKeyOffset+header.getLenName());
		this.fieldDescriptors = new EBXFieldDescriptor[header.getNumField()];
		for (int i=0;i<fieldDescriptors.length;i++){
			int nameHash = FileHandler.readInt(ebxFileBytes, seeker);
			fieldDescriptors[i] = new EBXFieldDescriptor(keywordDict.get(nameHash),
					FileHandler.readShort(ebxFileBytes, seeker, order),
					FileHandler.readShort(ebxFileBytes, seeker, order),
					FileHandler.readInt(ebxFileBytes, seeker, order),
					FileHandler.readInt(ebxFileBytes, seeker, order));
			fieldDescriptors[i].setNameHash(nameHash);
//			if (fieldDescriptors[i].getName().equalsIgnoreCase("right")){
//				System.out.println("EBX-Padding!");
//			}
			if (seeker.hasError()){
				return false;
			}
		}		
		//ComplexDescriptor
		complexDescriptors = new EBXComplexDescriptor[header.getNumComplex()];
		for (int i=0;i<complexDescriptors.length;i++){
			complexDescriptors[i] = new EBXComplexDescriptor(keywordDict.get(FileHandler.readInt(ebxFileBytes, seeker, order)),
					FileHandler.readInt(ebxFileBytes, seeker, order), 
					(char) FileHandler.readByte(ebxFileBytes, seeker), 
					(char) FileHandler.readByte(ebxFileBytes, seeker),
					FileHandler.readShort(ebxFileBytes, seeker, order),
					FileHandler.readShort(ebxFileBytes, seeker, order),
					FileHandler.readShort(ebxFileBytes, seeker, order));
			if (seeker.hasError()){
				return false;
			}
//			if (complexDescriptors[i].getName().equalsIgnoreCase("Vec3")){
//				System.out.println("EBX-Complex-Padding!");
//			}			
		}
		//instanceRepeaters
		instanceRepeaters = new EBXInstanceRepeater[header.getNumInstanceRepeater()];
		for (int i=0;i<instanceRepeaters.length;i++){
			instanceRepeaters[i] = new EBXInstanceRepeater(
					FileHandler.readShort(ebxFileBytes, seeker, order), 
					FileHandler.readShort(ebxFileBytes, seeker, order));
			if (seeker.hasError()){
				return false;
			}
		}
		while (seeker.getOffset()%16!=0){ seeker.seek(1);} // #padding
		
		//arrayRepeaters	
		arrayRepeaters = new EBXArrayRepeater[header.getNumArrayRepeater()];
		for (int i=0; i<arrayRepeaters.length;i++){
			arrayRepeaters[i] = new EBXArrayRepeater(
					FileHandler.readInt(ebxFileBytes, seeker, order),
					FileHandler.readInt(ebxFileBytes, seeker, order),
					FileHandler.readInt(ebxFileBytes, seeker, order));
			if (seeker.hasError()){
				return false;
			}
		}
		
		
		//PayLoad
		internalGUIDs = new ArrayList<String>(); 
		fields = new ArrayList<EBXField>(); 
		instances = new ArrayList<EBXInstance>(); 
		internalGUIDFieldIndexs = new ArrayList<Integer>();
		seeker.setOffset(header.absStringOffset+header.lenString);
		//int nonGUIDindex = 0;
		String tempGUID = "";
		isPrimaryInstance = true;
		for (int repeater=0; repeater<instanceRepeaters.length;repeater++){
			EBXInstanceRepeater ir = instanceRepeaters[repeater];
			for (int repetition=0; repetition<ir.getRepetitions();repetition++){
				while (seeker.getOffset()%complexDescriptors[ir.complexIndex].alignment!=0){ seeker.seek(1); } //#obey alignment of the instance; peek into the complex for that		
				if (repeater<header.getNumGUIDRepeater()){
					tempGUID = FileHandler.bytesToHex(FileHandler.readByte(ebxFileBytes, seeker, 16));
				}else{
					tempGUID = FileHandler.bytesToHex(ByteBuffer.allocate(4).putInt(internalGUIDs.size()).array());//lets use the index as guid
					//tempGUID = FileHandler.bytesToHex(ByteBuffer.allocate(4).putInt(nonGUIDindex).array());
					//nonGUIDindex++;
				}
				internalGUIDs.add(tempGUID);
				instances.add(new EBXInstance(null, tempGUID, readComplex(ir.getComplexIndex(), true, false)));
				isPrimaryInstance = false;
				if (seeker.hasError()){
					return false;
				}
			}
			if (seeker.hasError()){
				return false;
			}
		}
		//Internal GUIDs are defined by their index.
		//So we have to proc. the instancec first and set
		//the real value after...
		for (Integer i : internalGUIDFieldIndexs){
			EBXField field = fields.get(i);
			Integer tempValue = (Integer) field.getValue();
			
			String intGuid = internalGUIDs.get(tempValue-1);
			
			field.setValue(intGuid, FieldValueType.Guid); //set guid from temp value!
		}
		
		ebxFileBytes = null;
		return true;
	}
	
	private EBXComplex readComplex(int complexIndex, boolean isInstance, boolean hasEmtyPayload) {
		EBXComplexDescriptor complexDesc = complexDescriptors[complexIndex];
		EBXComplex cmplx = new EBXComplex(complexDesc);
		cmplx.setOffset(seeker.getOffset());
		
		
		EBXField[] fields = new EBXField[complexDesc.getNumField()];
		//#alignment 4 instances require subtracting 8 for all field offsets and the complex size 
		int obfuscationShift=0;
		if (isInstance && cmplx.getComplexDescriptor().getAlignment()==4){
			obfuscationShift = 8;
		}
//		if (cmplx.getComplexDescriptor().getAlignment()!=4){
//			System.out.println(cmplx.getComplexDescriptor().getName()+" "+cmplx.getComplexDescriptor().getAlignment()+" "+cmplx.getComplexDescriptor().getSize());
//		}
		for (int fieldIndex=0; fieldIndex<complexDesc.getNumField(); fieldIndex++){
			int noNameShift = 0;
			if (fieldDescriptors[complexDesc.getFieldStartIndex()+fieldIndex].getName().equals("$")){
				noNameShift = -8;
			}
			seeker.setOffset(cmplx.getOffset()+fieldDescriptors[complexDesc.getFieldStartIndex()+fieldIndex].getOffset()+noNameShift-obfuscationShift);
	    	fields[fieldIndex] = readField(complexDesc.getFieldStartIndex()+fieldIndex, hasEmtyPayload);
	    	if (seeker.hasError()){
				return null;
			}
		}
		cmplx.setFields(fields);
		seeker.setOffset(cmplx.getOffset()+complexDesc.getSize()-obfuscationShift);
		return cmplx;
	}

	public EBXField readField(int fieldIndex, boolean hasEmtyPayload){
		EBXFieldDescriptor fieldDesc = fieldDescriptors[fieldIndex];
		EBXField field = new EBXField(fieldDesc, seeker.getOffset());
		field.indexDEBUG = fieldIndex;
		field.setFieldID(fieldDesc.getNameHash()+0);//TODO FieldID!!
		fields.add(field);
				
		/*<DECODE>*/
		if (fieldDesc.getType() == (short) 0x0029|| fieldDesc.getType() == (short) 0xd029 || fieldDesc.getType() == (short) 0x0000 || fieldDesc.getType() == (short) 0x8029){ //COMPLEX
			field.setValue(readComplex(fieldDesc.getRef(), false, hasEmtyPayload), EBXHandler.FieldValueType.Complex);
		}else if (fieldDesc.getType() == 0x0041){ //ARRAYCOMPLEX
			int repeaterIndex = FileHandler.readInt(ebxFileBytes, seeker);
			if (repeaterIndex>arrayRepeaters.length){
				System.err.println("Out of bounds!");
			}
			EBXArrayRepeater arrayRepeater = arrayRepeaters[repeaterIndex];
			EBXComplexDescriptor arrayComplexDesc = complexDescriptors[fieldDesc.getRef()];//fields.getRef is the same as arrayRepeater.getIndex
			seeker.setOffset(arraySectionstart+arrayRepeater.offset);
			EBXComplex arrayComplex = new EBXComplex(arrayComplexDesc);
			EBXField[] fields = null;
//			EBXComplexDescriptor test = complexDescriptors[arrayRepeater.getComplexIndex()];
			if (arrayRepeater.getRepetitions()>0){//Guids,Strings,Integer... with payload
				fields = new EBXField[arrayRepeater.getRepetitions()];
				for (int i=0; i<fields.length;i++){
					fields[i] = readField(arrayComplexDesc.getFieldStartIndex(), false);
				}
			}else if (arrayComplexDesc.getNumField()>0){//Field is prob, Complex|| fields does not have any payload!
				arrayComplex.setEmtyPayload(true);
//TODO is this needed, removed 16-jan-2016	int index = FileHandler.readInt(ebxFileBytes, new FileSeeker(seeker.getOffset()));//array section contains a integer for it ?
				//System.out.println("ArrayComplex-Integer-Debug: "+index);
				fields = new EBXField[arrayComplexDesc.getNumField()];
				for (int i=0; i<fields.length;i++){
					fields[i] = readField(arrayComplexDesc.getFieldStartIndex()+i, true);//has no payload data!
				}
			}
			if (fields!=null){
				arrayComplex.setFields(fields);
				field.setValue(arrayComplex, FieldValueType.ArrayComplex);
			}else{
				field.setValue(arrayRepeater, FieldValueType.ArrayComplex);
			}
		}else if (fieldDesc.getType() == (short) 0x407D || fieldDesc.getType() == (short) 0x409D){//STRING
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.String);
			}else{
				int stringOffset = FileHandler.readInt(ebxFileBytes, seeker);
				if (stringOffset<=-1){
					field.setValue("*nullString*", FieldValueType.String);
				}else{
					field.setValue(readString(ebxFileBytes, header.getAbsStringOffset()+stringOffset),FieldValueType.String);
				}
				//TRUEFILENAME
				if (isPrimaryInstance && fieldDesc.getName().equals("Name") && trueFilename.equals("")){
					trueFilename = (String) field.getValue();
				}
			}
		}else if (fieldDesc.getType() == (short) 0x0089 || fieldDesc.getType() == (short) 0xC089){
			int selectedIndex = -1;
			if (!hasEmtyPayload){
				selectedIndex = FileHandler.readInt(ebxFileBytes, seeker);//starts always at 0 and counts up.
			}
			EBXComplexDescriptor enumComplex = complexDescriptors[fieldDesc.getRef()];
			if (enumComplex.getNumField()==0){
				field.setValue(enumComplex, FieldValueType.Enum);//null Enum
			}else{
				EBXEnumHelper enumHelper = new EBXEnumHelper(enumComplex, selectedIndex);
				int numFields = enumComplex.getNumField();
				if (numFields>>8==0xFF){
					numFields=numFields&0xFF;
				}
				for (int index=0; index<numFields; index++){
					int absIndex = enumComplex.getFieldStartIndex()+index;
					if (absIndex<fieldDescriptors.length){
						enumHelper.addEntry(fieldDescriptors[absIndex]);
					}else{
						System.err.println("EBXLoader has a problem with a ENUM!");
						return null;
					}
				}
				field.setValue(enumHelper, FieldValueType.Enum);//Enum with one entry selected
			}
		}else if (fieldDesc.getType()== (short) 0xC15D){ //GUID CHUNK
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.ChunkGuid);
			}else{
				field.setValue(FileHandler.bytesToHex(FileHandler.readByte(ebxFileBytes, seeker,16)), FieldValueType.ChunkGuid);
			}
		}else if (fieldDesc.getType()== (short) 0x417D){ //8HEX
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.Hex8);
			}else{
				field.setValue(FileHandler.bytesToHex(FileHandler.readByte(ebxFileBytes,seeker, 8)), FieldValueType.Hex8);
			}
		}else if (fieldDesc.getType()==(short) 0xC13D){//FLOAT
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.Float);
			}else{
				field.setValue(FileHandler.readFloat(ebxFileBytes, seeker), FieldValueType.Float);
			}
		}else if (fieldDesc.getType()==(short) 0xc10d){//uint
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.UInteger);
			}else{
				field.setValue((FileHandler.readInt(ebxFileBytes, seeker, order) & 0xffffffffL), FieldValueType.UInteger);
			}
		}else if(fieldDesc.getType() == (short) 0xc0fd){ //signed int
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.Integer);
			}else{
				field.setValue(FileHandler.readInt(ebxFileBytes, seeker), FieldValueType.Integer);
			}
		}else if (fieldDesc.getType() == (short) 0xc0ad){//BOOL
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.Bool);
			}else{
				byte b = FileHandler.readByte(ebxFileBytes, seeker);
				if (b==0x00){
					field.setValue(false, FieldValueType.Bool);
				}else if (b==0x01){
					field.setValue(true, FieldValueType.Bool);
				}else{
					field.setValue(null, FieldValueType.Bool);
					System.err.println("Field is not a boolean! at "+seeker.getOffset());
				}
			}
		}else if (fieldDesc.getType() == (short) 0xc0ed){//short
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.Short);		
			}else{
				field.setValue(FileHandler.readShort(ebxFileBytes, seeker, order), FieldValueType.Short);			
			}
		}else if (fieldDesc.getType() == (short) 0xc0cd){//BYTE
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.Byte);
			}else{
				field.setValue(FileHandler.readByte(ebxFileBytes, seeker), FieldValueType.Byte);
			}
		}else if(fieldDesc.getType() == (short) 0x0035){ // #guid
			if (hasEmtyPayload){
				field.setValue(null, FieldValueType.Guid);
			}else{
//				if (seeker.getOffset()>=33043){
//					System.err.println("ASdasdf");
//				}
				int tempValue = FileHandler.readInt(ebxFileBytes, seeker);
				//field.setValue(String.valueOf(tempValue)+"test", FieldValueType.Guid);
				//return field;
				//	INTERNAL STARTS AT 0x1 (values needs to be substracted by 1 to optain the index)
				// 	EXTERNAL STARTS AT 0x80 00 00 00 -> substract base value to optain index. (starts at 0)	
				if(((tempValue>>31)) == -1){
					EBXExternalGUID guid = externalGUIDs[(tempValue & 0x7fffffff)];//same as (tempValue - 0x800000) ^__^
					
					/*String fileGUIDName = null; -> Lets do that in the EBX TreeViewCellFactory
					try{
						if (Main.getGame().getEBXFileGUIDs()!=null){//DEBUG-
							fileGUIDName = Main.getGame().getEBXFileGUIDs().get(guid.getFileGUID().toUpperCase());
						}
					}catch(NullPointerException e){
						System.err.println("EBXFileGUID Database does not exist!");
					}
					if (fileGUIDName != null){
						field.setValue(fileGUIDName+" "+guid.getInstanceGUID(), FieldValueType.ExternalGuid); //We need to convert the String later back to a FileGUID
					}else{//}*/
	
					field.setValue(guid, FieldValueType.ExternalGuid);
					
				}else if (tempValue == 0x0){
					field.setValue("*nullGUID*", FieldValueType.Guid);
				}else{
					internalGUIDFieldIndexs.add(fields.size()-1);//current field index ;)
					
					
					//String test = FileHandler.bytesToHex(FileHandler.toBytes(tempValue, ByteOrder.BIG_ENDIAN));
					//String intGuid = internalGUIDs.get(tempValue-1);			
					
					field.setValue(tempValue, FieldValueType.Guid); //set tempValue temp as value.
					//the real value gets set after the instance loop.
					//order seems to be different but not wrong.
				}
			}
		}else{
			System.err.println("(EBXLoader) Unknown field type: "+Integer.toHexString(fieldDesc.getType())+" File name: "+filePath);
			field.setValue("*unknown field type*", FieldValueType.Unknown);
			/*
			 try:
                (typ,length)=numDict[fieldDesc.type]
                num=self.unpack(typ,f.read(length))[0]
                field.value=num
            except:
                print "Unknown field type: "+str(fieldDesc.type)+" File name: "+self.relPath
                field.value="*unknown field type*"
			 */
		}
		/*<END OF DECODE>*/
		
		if (seeker.hasError()){
			return null;
		}
		return field;
		
	}

	

	public String readString(byte[] fileArray, int offset) {
		String tmp = "";
		for (int i = 0; i < 1000; i++) {
			byte[] b = readByte(fileArray, offset + i, 1);
			if (b[0] != 0x0) {
				String str;
				try {
					str = new String(b, "UTF-8");
					tmp += str;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					i = 1500;
				}
			} else {
				break;
			}
		}
		return tmp;
	}

	public byte[] readByte(byte[] fileArray, int offset, int len) {
		byte[] buffer = new byte[len];
		for (int i = 0; i < len; i++) {
			try{
				buffer[i] = fileArray[offset + i];
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("Error, END OF FILE REACHED in EBXLoader!!");
				buffer[i] = 0;
				return null;
			}
		}
		return buffer;
	}


	public byte[] readMagic(byte[] fileArray) {
		return readByte(fileArray, 0, 4);
	}
	
	

	public String getFileGUID() {
		return fileGUID;
	}

	// Constructor
	public EBXLoader() {
		/*
		numDict.put(0xC12D, 8);
		numDict.put(0xc0cd, 1);
		numDict.put(0x0035, 4);
		numDict.put(0xc10d, 4);
		numDict.put(0xc14d, 8);
		numDict.put(0xc0ad, 1);
		numDict.put(0xc0fd, 4);
		numDict.put(0xc0bd, 1);
		numDict.put(0xc0ed, 2);
		numDict.put(0xc0dd, 2);
		numDict.put(0xc13d, 4);
		*/
	}
	public ByteOrder getByteOrder() {
		return order;
	}
	
	public ArrayList<EBXExternalGUID> getExternalGUIDs(){
		ArrayList<EBXExternalGUID> externalGUIDsList = new ArrayList<>();
		for (EBXExternalGUID guid : externalGUIDs){
			externalGUIDsList.add(guid);
		}
		return externalGUIDsList;
	}
	
}
