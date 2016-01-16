package tk.greydynamics.Resource.Frostbite3.EBX;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;

public class EBXCreator {
	
	private EBXHeader header;
	private ArrayList<Byte> headerBytes;
	
	private ArrayList<EBXExternalGUID> externalGUIDs;
	private ArrayList<Byte> externalGUIDBytes;
	
	private ArrayList<String> names;
	private ArrayList<Byte> nameBytes;
	
	private ArrayList<EBXFieldDescriptor> fieldDescriptors;
	private ArrayList<Byte> fieldDescriptorBytes;	
	
	private ArrayList<EBXComplexDescriptor> complexDescriptors;
	private ArrayList<Byte> complexDescriptorBytes;
	
	private ArrayList<EBXInstanceRepeater> instanceRepeaters;
	private ArrayList<Byte> instanceRepeaterBytes;
	
	private ArrayList<EBXArrayRepeater> arrayRepeaters;
	private ArrayList<Byte> arrayRepeaterBytes;
	
	private ArrayList<Byte> stringAlignment;
	
	private ArrayList<String> strings;
	private ArrayList<Byte> stringBytes;
	
	private ArrayList<Byte> payloadData;
	
	private ArrayList<Byte> arrayPayloadData;
	
	private ArrayList<ArrayList<Byte>> finalDataArrays;
	
	private ArrayList<Byte> filler = new ArrayList<>();
	
	private ArrayList<String> internalGUIDs;
	
	
	public void init(){
		finalDataArrays = new ArrayList<>();
		
		headerBytes = new ArrayList<>();
		externalGUIDBytes = new ArrayList<>();
		nameBytes = new ArrayList<>();
		fieldDescriptorBytes = new ArrayList<>();
		complexDescriptorBytes = new ArrayList<>();
		instanceRepeaterBytes = new ArrayList<>();
		arrayRepeaterBytes = new ArrayList<>();
		stringAlignment = new ArrayList<>();
		stringBytes = new ArrayList<>();
		payloadData = new ArrayList<>();
		arrayPayloadData = new ArrayList<>();
		
		header = new EBXHeader();
		names = new ArrayList<>();
		externalGUIDs = new ArrayList<>();
		fieldDescriptors = new ArrayList<>();
		complexDescriptors = new ArrayList<>();
		instanceRepeaters = new ArrayList<>();
		arrayRepeaters = new ArrayList<>();
		strings = new ArrayList<>();
		
		internalGUIDs = new ArrayList<>();
	}
	
	public byte[] createEBX(EBXFile ebxFile){
		init();
		
		//just for debugging, makes it easier to see sections.
		if (false&&filler.isEmpty()){
			filler.add((byte) 0xAA);
			filler.add((byte) 0xAA);
			filler.add((byte) 0xAA);
			filler.add((byte) 0xAA);
			filler.add((byte) 0xBB);
			filler.add((byte) 0xBB);
			filler.add((byte) 0xBB);
			filler.add((byte) 0xBB);
			filler.add((byte) 0xAA);
			filler.add((byte) 0xAA);
			filler.add((byte) 0xAA);
			filler.add((byte) 0xAA);
			filler.add((byte) 0xBB);
			filler.add((byte) 0xBB);
			filler.add((byte) 0xBB);
			filler.add((byte) 0xBB);
		}
		
		
		header.setNumGUIDRepeater(0);
		
		
		//NOTE - TRUEFILENAME does actually not exist, it uses the String value of the first 'Name' field in the primaryInstance (its mostly the first string in stringSection)
		boolean isPrimaryInstance = true;
		for (EBXInstance instance : ebxFile.getInstances()){
			proccInternalGUID(instance.getGuid());//register guid's first, make sure to only store them once.
		}
		
		for (EBXInstance instance : ebxFile.getInstances()){
			if (!proccInstance(instance, isPrimaryInstance)){
				System.err.println("Couldn't processing EBXInstance. GUID: "+instance.getGuid());
			}
			isPrimaryInstance = false;
		}
		
		
		writeFieldDescriptors();
		writeComplexDescriptors();
		writeInstanceRepeaters();
		/*DEBUG*///FileHandler.writeFile("output/ebxInstanceRepeater_part", FileHandler.toByteArray(instanceRepeaterBytes));
		writeArrayRepeaters();
		writeExternalGUIDs();
		writeNames();
		/*DEBUG*///FileHandler.writeFile("output/ebxNames_part", FileHandler.toByteArray(nameBytes));
		
		
		int stringOffset = calcStringOffset();
		while (stringOffset%16!=0){
			stringAlignment.add((byte) 0x0);
			stringOffset++;
		}
		header.setAbsStringOffset(stringOffset);
		writeStrings();
				
		int fileSize = stringOffset + stringBytes.size() + payloadData.size() + arrayPayloadData.size();
		header.setLenStringToEOF(fileSize - stringOffset);
		header.setLenPayload(payloadData.size());
		
		
		writeHeader();//needs extend by payload! 4bytes fourCC + 36bytes
		FileHandler.addBytes(FileHandler.hexStringToByteArray(ebxFile.getGuid()), headerBytes); //+16Bytes GUID
		for (int i=0; i<8;i++){
			headerBytes.add((byte) 0x00);//+8Bytes padding
		}/*TOTAL HEADER SIZE = 64Bytes*/
		
		
		finalDataArrays.add(headerBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(externalGUIDBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(nameBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(fieldDescriptorBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(complexDescriptorBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(instanceRepeaterBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(arrayRepeaterBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(stringAlignment);
		finalDataArrays.add(stringBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(payloadData);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(arrayPayloadData);
		
		byte[] data = FileHandler.toBytes(finalDataArrays);
		return data;
	}
	
	private int calcStringOffset(){
		return /*headerBytes.size()*/ 0x40 + externalGUIDBytes.size() + nameBytes.size() + fieldDescriptorBytes.size() +
				complexDescriptorBytes.size() + instanceRepeaterBytes.size() + arrayRepeaterBytes.size();
	}

	
	
	
	
/****PROCC LOGIC********PROCC LOGIC********PROCC LOGIC********PROCC LOGIC********PROCC LOGIC********PROCC LOGIC********PROCC LOGIC********PROCC LOGIC********PROCC LOGIC****/
	public boolean proccInstance(EBXInstance ebxInstance, boolean isPrimaryInstance){
		/*each instance is aligned to 16. We start after the string section, so
		 its automaticly aligned. Just make sure... to use padding at the end of the instances
		 payload block to align for the next one.*/
		
		String guid = ebxInstance.getGuid();
		if (guid.length()>15){
			FileHandler.addBytes(FileHandler.hexStringToByteArray(ebxInstance.getGuid()), payloadData);
			header.setNumGUIDRepeater(header.getNumGUIDRepeater()+1);
		}
//		wrong
//		if (!isPrimaryInstance){
//			for (int i=0; i<8;i++){
//				payloadData.add((byte) 0x00);
//			}
//		}
		//obfuscationShift. shift by 8 ?? #alignment 4 instances require subtracting 8 for all field offsets and the complex size
		int index = proccComplex(ebxInstance.getComplex(), true /*isInstanceComplex*/, false/*isArrayMember*/, true/*proccFieldDesc*/, false/*hasNoPayload*/, false /*isNoNameParent*/);
		
		EBXInstanceRepeater repeater = new EBXInstanceRepeater(index, 1);
		instanceRepeaters.add(repeater);
		return true;
	}
	
	public int proccComplex(EBXComplex ebxComplex, boolean isInstanceComplex, boolean isArrayMember, boolean proccDescriptor, boolean hasNoPayload, boolean isNoNameParent){
		//return index of complex
		
		//TODO proccDescriptor (boolean)
		//TODO hasNoPayload (boolean)
		//TODO alignment
		
		ArrayList<Byte> targetList = null;
		if (isArrayMember){
			targetList = this.arrayPayloadData;
		}else{
			targetList = this.payloadData;
		}
		
		//The complexOffset is needed to calculate the relative field offset.
		int complexOffset = targetList.size();
		//TODO complexOffset -1 ?
		
		
		int obfuscationShift = 0;
		if (isInstanceComplex&&ebxComplex.getComplexDescriptor().getAlignment()==4){
			obfuscationShift = -8;
		}else if (isInstanceComplex){
			for (int i=0;i<8;i++){
				targetList.add((byte) 0x00);
			}
		}
		int noNameShift = 0;
		if (isNoNameParent){
			noNameShift = -8;
		}
		
		ArrayList<EBXFieldDescriptor> complexFieldDescriptors = new ArrayList<>();
		for (EBXField field : ebxComplex.getFields()){
			EBXFieldDescriptor complexFieldDescriptor = proccField(field, isArrayMember, proccDescriptor, hasNoPayload);
			if (complexFieldDescriptor!=null){
				complexFieldDescriptors.add(complexFieldDescriptor);
			}else{
				System.err.println("EBXCreator: proccField failed!");
			}
		}
		boolean foundMatchingGroup = false;
//		int fieldsStartIndex = findMatchingFieldDescGroup(complexFieldDescriptors);
		int fieldsStartIndex = -1;
		if (fieldsStartIndex>=0){
			//group does already exist, lets use it.
			foundMatchingGroup = true;
		}else{
			fieldsStartIndex = this.fieldDescriptors.size();
			//group does NOT exist, make sure to register fieldDescriptors later on.
		}
		int complexPadding = 0;
		while (targetList.size()<(complexOffset+ebxComplex.getComplexDescriptor().getSize()+obfuscationShift+noNameShift)){
			targetList.add((byte) 0x00);
			complexPadding++;
		}
		int instancePadding = 0;
		if (isInstanceComplex){
			while (targetList.size()%16!=0){
				targetList.add((byte) 0x00);
				instancePadding++;
			}
		}
		
		//proccComplexDescriptor(complexDescritptor, numberOfFields, totalSize)
		int complexIndex = proccComplexDescriptor(ebxComplex.getComplexDescriptor(), fieldsStartIndex);
		
		if (!foundMatchingGroup){
			//fields does NOT exist, lets register them.
			for (EBXFieldDescriptor fieldDescriptor : complexFieldDescriptors){
				proccFieldDescriptor(fieldDescriptor);
			}
		}
		return complexIndex;
	}
	
	public short proccComplexDescriptor(EBXComplexDescriptor desc, int fieldStartIndex){
		//return index
		
		desc.setFieldStartIndex(fieldStartIndex);
		
		proccName(desc.getName()/*MAY NEED TAILING NULL*/);
		
		this.complexDescriptors.add(desc);
		
		return (short) (this.complexDescriptors.size()-1);
	}
	
	public int proccFieldDescriptor(EBXFieldDescriptor desc){

		proccName(desc.getName()/*MAY NEED TAILING NULL*/);
		
		this.fieldDescriptors.add(desc);
		
		return this.fieldDescriptors.size()-1;
	}
	
	public EBXFieldDescriptor proccField(EBXField ebxField, boolean isArrayMember, boolean proccDescriptor, boolean hasNoPayload){
		ArrayList<Byte> targetList = null;
		if (isArrayMember){
			targetList = arrayPayloadData;
		}else{
			targetList = payloadData;
		}
		EBXFieldDescriptor desc = ebxField.getFieldDescritor();
		//desc.setOffset((targetList.size()-complexOffset)+obfuscationShift);//TODO is the complexOffset/relFieldOffset working in arrays?
		byte[] data = null;
		//to make this happen, we have to have a fully working descriptor.
		short h = ebxField.getFieldDescritor().getType();
//		if (h==0x0){
//			if (ebxField.getValue() instanceof EBXComplex){
//				System.err.println("EBXCreator test -> fieldtype 0 to valuetype.");
//				h=ebxField.getValueAsComplex().getComplexDescriptor().getType();
//			}else{
//				System.err.println("EBXCreator does not know field: "+ebxField.getValue().getClass().toString());
//			}
//		}else 
		if (h==0xFFFF){
			System.err.println("Unknown type!");
		}else if(h==(short)0x407D||h==(short)0x409D){ //_________________________________________________________________________________STRING
			//TODO whats the difference (type)?
			if (ebxField.getValue()!=null){
				String val = (String) ebxField.getValue();
				if (val.contains("*nullString*")){
					data = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
				}else{
					int relOffset = 0;
					for (String s : strings){
						relOffset += s.length()+1;
					}
					strings.add(val);
					data = FileHandler.toBytes((int) relOffset, ByteOrder.LITTLE_ENDIAN);
				}
				FileHandler.addBytes(data, targetList);
			}
		}else if(h==(short)0xC13D){//_____________________________________________________________________________________________FLOAT
			if (ebxField.getValue()!=null){
				data = FileHandler.toBytes((float) ebxField.getValue());
				FileHandler.addBytes(data, targetList);
			}
		}else if(h==(short)0x0029||h==(short)0xd029||h==(short)0x8029||h==(short)0x0000){//_______________________________________________________________________COMPLEX
			//TODO whats the difference (type)?
			//data = new byte[] {0x00, 0x00, 0x00, 0x00};
			//FileHandler.addBytes(data, payloadData);//?? what is the value/size ?? //TODO
			
			short index = (short) (proccComplex(ebxField.getValueAsComplex(), false, isArrayMember, proccDescriptor, hasNoPayload, ebxField.getFieldDescritor().getName().equals("$")));
			//TODO does it need work ?
			desc.setRef(index);
		}else if (h==(short)0xC089||h==(short)0x0089){//_________________________________________________________________________________ENUM //TODO NEEDS WORK IN TCF, SELECTED INDEX FAIL.
			//TODO whats the difference (type)?
			if (ebxField.getValue() instanceof String){
				System.err.println("NULL ENUM (STRING)");
				EBXComplexDescriptor enumComplexDesc = new EBXComplexDescriptor(
						"$", //name
						0,
						(char) (0),//numFields <-0 == nullENUM
						(char) 4,//TODO alignment
						(short) 0x0,//type
						(short) 0x0,//size
						(short)0x0//secondarySize
						);
				complexDescriptors.add(enumComplexDesc);//add directly because proccComplexDescr. contains size methods
				desc.setRef((short) (complexDescriptors.size()-1));//TODO -1 ??

				data = FileHandler.toBytes(0, ByteOrder.LITTLE_ENDIAN);
				if (ebxField.getValue()!=null){//for hasEmtyPayload
					FileHandler.addBytes(data, targetList);
				}
			}else if(ebxField.getValue() instanceof HashMap<?, ?>){//EBXFieldDescriptor, Boolean
				HashMap<EBXFieldDescriptor, Boolean> enumList = (HashMap<EBXFieldDescriptor, Boolean>) ebxField.getValue();
				int selectedIndex = -1;
				boolean treeSet = false;
				for (EBXFieldDescriptor fieldDesc : enumList.keySet()){
					String fieldName = fieldDesc.getName();
					if (fieldName.contains("_")&&!treeSet){
						String treeName = fieldName.replace("_", " ").split(" ")[0];
						proccName(treeName);
						treeSet = true;
					}
					//fieldDesc.setOffset(current);//offset does represent the relative index from fieldStartIndex, is already set in loader
					fieldDescriptors.add(fieldDesc);
					Boolean selected = enumList.get(fieldDesc);
					if (selected){
						selectedIndex = fieldDesc.getOffset();
					}
				}
				EBXComplexDescriptor enumComplexDesc = new EBXComplexDescriptor(
						"$", //name
						fieldDescriptors.size()-1-enumList.size(),//start index //TODO -1 ??
						(char) (enumList.size()&0xFF),//numFields
						(char) 4,//TODO alignment
						(short) 0x0,//type
						(short) 0x0,//size
						(short)0x0//secondarySize
						);
				complexDescriptors.add(enumComplexDesc);//add directly because proccComplexDescr. contains size methods
				desc.setRef((short) (complexDescriptors.size()-1));

				data = FileHandler.toBytes(selectedIndex, ByteOrder.LITTLE_ENDIAN);
				if (selectedIndex>=0){//for hasNoPayloadData
					FileHandler.addBytes(data, targetList);
				}
			}else{
				System.err.println("ENUM ERROR");
			}
		}else if(h==(short)0x0035){//___________________________________________________________________________________________GUID
			if (ebxField.getValue()!=null){
				String val = (String) ebxField.getValue();
				if (val.contains("null")){
					data = new byte[] {0x00, 0x00, 0x00, 0x00};
				}else if (val.contains(" ")){//External GUID
					String[] split = val.split(" ");
					EBXExternalGUID extGUID = new EBXExternalGUID(split[0], split[1]);
					int index = proccExternalGUID(extGUID);
					data = FileHandler.toBytes(index+0x80000000/*first bit toggled ;)*/, ByteOrder.LITTLE_ENDIAN);
				}else{//Internal GUID
					byte[] internal = FileHandler.hexStringToByteArray(val);//
					if (internal.length==4){
						data = new byte[] {
								internal[3],
								internal[2],
								internal[1],
								internal[0]//Should be LITTLE_ENDIAN :)
						};
						Integer index = FileHandler.readInt(internal, new FileSeeker());
						index++;
						data = FileHandler.toBytes(index, ByteOrder.LITTLE_ENDIAN);
						//System.err.println("GUID");//i changed the loader so it uses the index as guid :)
					}else if (internal.length==16){
						internal = null;
						Integer index = proccInternalGUID(val);
						data = FileHandler.toBytes(index, ByteOrder.LITTLE_ENDIAN);
						//data for now :)
						System.err.println("TEST->internal guid with 16 bytes");//TODO TEST internal guid
					}else{
						System.err.println("Invalid Internal GUID - check length!");
					}				
				}
				//data = new byte[] {0x47, 0x55, 0x49, 0x44};
				FileHandler.addBytes(data, targetList);
			}
		}else if(h==(short)0x0041){//___________________________________________________________________________________________ARRAYCOMPLEX
			//TODO ARRAYCOMPLEX
			
			int startOffset = this.arrayPayloadData.size();
			/*NOTE:
			  arraySectionstart will get calculated like this:
			  header.getAbsStringOffset()+header.getLenString()+header.getLenPayload(); 
			  the startOffset will be added to obtain the arrays total offset in ebx file.#
			*/
			EBXArrayRepeater arrayRepeater = null;
			if (ebxField.getValue() instanceof EBXArrayRepeater){
				arrayRepeater = (EBXArrayRepeater) ebxField.getValue();
				//TODO emty array
				System.err.println("EBXCreator - Emty Array may cause an error.");
			}else{
				EBXComplex arrayComplex = ebxField.getValueAsComplex();
				int fieldStartIndex = -1;
				int arrayComplexIndex = -1;
				short type = -1;
				if (arrayComplex.getFields().length>0){
					type = arrayComplex.getField(0).getFieldDescritor().getType();
				}
				if (type==-1){
					System.err.println("TODO: ARRAYCOMPLEX UNDEFINED TYPE");
				}else if (type==(short)0x29){//Type of Complex
					ArrayList<EBXFieldDescriptor> arrayFieldDescs = new ArrayList<>();
					for (EBXField field : arrayComplex.getFields()){
						arrayFieldDescs.add(proccField(field, true, proccDescriptor, false));						
					}
					fieldStartIndex = this.fieldDescriptors.size();
					for (EBXFieldDescriptor fieldDesc : arrayFieldDescs){
						proccFieldDescriptor(fieldDesc);
					}
					
					arrayComplexIndex = proccComplexDescriptor(arrayComplex.getComplexDescriptor(), fieldStartIndex);
					arrayRepeater = new EBXArrayRepeater(startOffset, 0, arrayComplexIndex);
				}else{//Array, shares the same descriptor for each field.
					
					//Write each field to array payload section. The fields share the same fieldDescriptor, so we only have to store it once.
					EBXFieldDescriptor arraySharedFieldDesc = null;
					for (EBXField field : arrayComplex.getFields()){
						arraySharedFieldDesc = proccField(field, true, proccDescriptor, hasNoPayload);
					}
					//Register shared descriptor.
					fieldStartIndex = proccFieldDescriptor(arraySharedFieldDesc);
					arrayComplexIndex = proccComplexDescriptor(arrayComplex.getComplexDescriptor(), fieldStartIndex);
					arrayRepeater = new EBXArrayRepeater(startOffset, arrayComplex.getFields().length, arrayComplexIndex);	
				}
				desc.setRef((short) arrayRepeater.getComplexIndex());
				arrayRepeaters.add(arrayRepeater);
				data = FileHandler.toBytes(arrayRepeaters.size()-1, ByteOrder.LITTLE_ENDIAN);
			}
			//data = new byte[] {0x41, 0x72, 0x72, 0x79};
			FileHandler.addBytes(data, targetList);
		}else if(h==(short)0xc0ed){//____________________________________________________________________________________________SHORT
			if (ebxField.getValue()!=null){
				short val = (short) ebxField.getValue();
				if (isArrayMember){
					data = FileHandler.toBytes(val, ByteOrder.BIG_ENDIAN);
				}else{//normalPayload short is 4 bytes. defuq... //TODO are you bananas ? ;) its an casted short.
					data = FileHandler.toBytes((int) val, ByteOrder.LITTLE_ENDIAN);
				}
				FileHandler.addBytes(data, targetList);
			}
		}else if(h==(short)0xc10d){//____________________________________________________________________________________________UNSIGNED INTEGER (TREEVIEW VALUE AS LONG)
			if (ebxField.getValue()!=null){
				long val = (Long)ebxField.getValue();
				data = FileHandler.toBytes(FileHandler.longToInt(val), ByteOrder.LITTLE_ENDIAN); //TODO TEST
				FileHandler.addBytes(data, targetList);
			}
		}else if(h==(short)0xc0fd){//____________________________________________________________________________________________SIGNED INTEGER
			if (ebxField.getValue()!=null){
				data = FileHandler.toBytes((Integer) ebxField.getValue(), ByteOrder.LITTLE_ENDIAN);
				FileHandler.addBytes(data, targetList);
			}
		}else if (h==(short)0xc0ad){//____________________________________________________________________________________________BOOL
			if (ebxField.getValue()!=null){
				Boolean value = (Boolean) ebxField.getValue();
				if (value){
					data = new byte[]{0x01};
				}else{
					data = new byte[]{0x00};
				}
				FileHandler.addBytes(data, targetList);
			}
		}else if(h==(short)0xc0cd){//_____________________________________________________________________________________________BYTE
			if (ebxField.getValue()!=null){
				data = new byte[]{(byte) ebxField.getValue()};
				FileHandler.addBytes(data, targetList);
			}
		}else if (h==(short)0xC15D){//____________________________________________________________________________________________CHUNK GUID
			if (ebxField.getValue()!=null){
				data = FileHandler.hexStringToByteArray((String)ebxField.getValue());
				FileHandler.addBytes(data, targetList);
			}
		}else if (h==(short)0x417D){//____________________________________________________________________________________________8HEX
			if (ebxField.getValue()!=null){
				data = FileHandler.hexStringToByteArray((String)ebxField.getValue());
				FileHandler.addBytes(data, targetList);
			}
		}else{
			byte[] typebyte = FileHandler.toBytes(h,ByteOrder.LITTLE_ENDIAN);
			System.err.println("Type not found: 0x"+FileHandler.bytesToHex(typebyte));
		}		
		return desc;
	}
	
	
	public int proccName(String name){
		//returns FNV_1 hash
		for (String entryValue : names){
			if (name.equals(entryValue)){
				return EBXHandler.hasher(name.getBytes());
			}
		}
		names.add(name);
		return EBXHandler.hasher(name.getBytes());
	}
	
	public int proccExternalGUID(EBXExternalGUID guid){
		int index = 0;
		for (EBXExternalGUID entry : externalGUIDs){
			if (guid.getFileGUID().equals(entry.getFileGUID()) && guid.getInstanceGUID().equals(entry.getInstanceGUID())){
				return index;
			}
			index++;
		}
		externalGUIDs.add(guid);
		return externalGUIDs.size()-1;
	}
	
	public int proccInternalGUID(String internalGUID){
		int index = 1;/*we need the index whois starting at 1*/;
		for (String s : internalGUIDs){
			if (s.equalsIgnoreCase(internalGUID)){
				return index;
			}
			index++;
		}
		internalGUIDs.add(internalGUID);
		return internalGUIDs.size()/*return the index whois starting at 1, so don't sub -1*/;
	}
/****END OF PROCC LOGIC********END OF PROCC LOGIC********END OF PROCC LOGIC********END OF PROCC LOGIC********END OF PROCC LOGIC********END OF PROCC LOGIC********END OF PROCC LOGIC****/
		
	
	
	
/****WRITE LOGIC********WRITE LOGIC********WRITE LOGIC********WRITE LOGIC********WRITE LOGIC********WRITE LOGIC********WRITE LOGIC********WRITE LOGIC********WRITE LOGIC********WRITE LOGIC****/
	public void writeHeader(){
		FileHandler.addBytes(new byte[]{(byte) 0xCE, (byte) 0xD1, (byte) 0xB2, (byte) 0x0F}, headerBytes); //FourCC _little
		FileHandler.addBytes(FileHandler.toBytes(header.getAbsStringOffset(), ByteOrder.LITTLE_ENDIAN), headerBytes); //AbsString offset section start
		FileHandler.addBytes(FileHandler.toBytes(header.getLenStringToEOF(), ByteOrder.LITTLE_ENDIAN), headerBytes); //len from string section to EOF.
		FileHandler.addBytes(FileHandler.toBytes(header.getNumGUID(), ByteOrder.LITTLE_ENDIAN), headerBytes); //num of external GUIDs (FileGUID|InstanceGUID)
		FileHandler.addBytes(FileHandler.toBytes((short) header.getNumInstanceRepeater(), ByteOrder.LITTLE_ENDIAN), headerBytes); //num of Instance Repeaters
		FileHandler.addBytes(FileHandler.toBytes((short) header.getNumGUIDRepeater(), ByteOrder.LITTLE_ENDIAN), headerBytes); //num of InstanceRepeaters with GUID aka. GUIDRepeater
		FileHandler.addBytes(FileHandler.toBytes((short) header.getUnknown()/*TODO*/, ByteOrder.LITTLE_ENDIAN), headerBytes);
		FileHandler.addBytes(FileHandler.toBytes((short) header.getNumComplex(), ByteOrder.LITTLE_ENDIAN), headerBytes); //total number of complex entries
		FileHandler.addBytes(FileHandler.toBytes((short) header.getNumField(), ByteOrder.LITTLE_ENDIAN), headerBytes); //total number of field entries
		FileHandler.addBytes(FileHandler.toBytes((short) header.getLenName(), ByteOrder.LITTLE_ENDIAN), headerBytes); //len of name
		FileHandler.addBytes(FileHandler.toBytes(header.getLenString(), ByteOrder.LITTLE_ENDIAN), headerBytes); //len of string
		FileHandler.addBytes(FileHandler.toBytes(header.getNumArrayRepeater(), ByteOrder.LITTLE_ENDIAN), headerBytes); //total number of array repeater
		FileHandler.addBytes(FileHandler.toBytes(header.getLenPayload(), ByteOrder.LITTLE_ENDIAN), headerBytes); //len of normal payload - the start of the ARRAY payload section is absStringOffset+lenString+lenPayload		
	}
	
	public void writeExternalGUIDs(){
		for (EBXExternalGUID guid : externalGUIDs){
			FileHandler.addBytes(FileHandler.hexStringToByteArray(guid.getFileGUID()), externalGUIDBytes);//file guid 16 bytes
			FileHandler.addBytes(FileHandler.hexStringToByteArray(guid.getInstanceGUID()), externalGUIDBytes);//instance guid 16 bytes
		}
		header.setNumGUID(externalGUIDs.size());
	}
	
	public void writeNames(){
		//its not sorted by the hash value, so maybe inside (non)logic ??		
		for (String s : names){
			FileHandler.addBytes(s.getBytes(), nameBytes);
			nameBytes.add((byte) 0x00);
		}
		while (nameBytes.size()%16!=0){
			nameBytes.add((byte) 0x00);//line padding.
		}
		header.setLenName(nameBytes.size());
	}
	
	public void writeFieldDescriptors(){
		for (EBXFieldDescriptor fdsc : fieldDescriptors){
			Integer name = proccName(fdsc.getName()/*MAY NEED TAILING NULL*/);
			
			FileHandler.addBytes(FileHandler.toBytes(name, ByteOrder.LITTLE_ENDIAN), fieldDescriptorBytes); //Hashed name as FNV_1 hash with modi. base and modf.
			FileHandler.addBytes(FileHandler.toBytes((short)fdsc.getType(), ByteOrder.LITTLE_ENDIAN), fieldDescriptorBytes); //type as short
			FileHandler.addBytes(FileHandler.toBytes((short)fdsc.getRef(), ByteOrder.LITTLE_ENDIAN), fieldDescriptorBytes); //ref as short
			FileHandler.addBytes(FileHandler.toBytes(fdsc.getOffset(), ByteOrder.LITTLE_ENDIAN), fieldDescriptorBytes); //offset (unsigned) in payload section; replative to the complex containing it.
			FileHandler.addBytes(FileHandler.toBytes(fdsc.getSecondaryOffset(), ByteOrder.LITTLE_ENDIAN), fieldDescriptorBytes); //2nd'ary offset (unsigned)
		}
		header.setNumField(fieldDescriptors.size());
	}
	
	public void writeComplexDescriptors(){		
		for (EBXComplexDescriptor cdsc : complexDescriptors){
			Integer name = proccName(cdsc.getName()/*MAY NEED TAILING NULL*/);
			
			FileHandler.addBytes(FileHandler.toBytes(name, ByteOrder.LITTLE_ENDIAN), complexDescriptorBytes); //Hashed name as FNV_1 hash with modi. base and modf.
			FileHandler.addBytes(FileHandler.toBytes(cdsc.getFieldStartIndex(), ByteOrder.LITTLE_ENDIAN), complexDescriptorBytes); //the index of the first field belonging to the complex
			complexDescriptorBytes.add((byte) cdsc.getNumField()); //total number of fields belonging to the complex
			complexDescriptorBytes.add((byte) cdsc.getAlignment()); //alignment
			FileHandler.addBytes(FileHandler.toBytes((short)cdsc.getType(), ByteOrder.LITTLE_ENDIAN), complexDescriptorBytes); // type as short
			FileHandler.addBytes(FileHandler.toBytes((short)cdsc.getSize(), ByteOrder.LITTLE_ENDIAN), complexDescriptorBytes); //total length of the complex in the payload section.
			FileHandler.addBytes(FileHandler.toBytes((short)cdsc.getSecondarySize(), ByteOrder.LITTLE_ENDIAN), complexDescriptorBytes); //seems deprecated or may for padding.
		}
		header.setNumComplex(complexDescriptors.size());
	}
	
	public void writeInstanceRepeaters(){
		for (EBXInstanceRepeater rep : instanceRepeaters){
			FileHandler.addBytes(FileHandler.toBytes((short)rep.getComplexIndex(), ByteOrder.LITTLE_ENDIAN), instanceRepeaterBytes); //represents the complex
			FileHandler.addBytes(FileHandler.toBytes((short)rep.getRepetitions(), ByteOrder.LITTLE_ENDIAN), instanceRepeaterBytes); //total number of repetitions in the complex.
		}
		header.setNumInstanceRepeater(instanceRepeaters.size());
		//instanceRepeater creates data that is not a multiple of 16, so there is some padding.
		while (this.instanceRepeaterBytes.size()%16!=0){
			this.instanceRepeaterBytes.add((byte) 0x0);
		}
		
	}
	
	public void writeArrayRepeaters(){		
		for (EBXArrayRepeater rep : arrayRepeaters){
			FileHandler.addBytes(FileHandler.toBytes(rep.getOffset(), ByteOrder.LITTLE_ENDIAN), arrayRepeaterBytes);//offset in array payload section
			FileHandler.addBytes(FileHandler.toBytes(rep.getRepetitions(), ByteOrder.LITTLE_ENDIAN), arrayRepeaterBytes);//number of array repetitions
/*TODO*/	FileHandler.addBytes(FileHandler.toBytes(rep.getComplexIndex(), ByteOrder.LITTLE_ENDIAN), arrayRepeaterBytes);//the complex belonging to the array - not necessary for extraction.
		}
		header.setNumArrayRepeater(arrayRepeaters.size());
	}
	
	
	public void writeStrings(){
		for (String s : strings){
			FileHandler.addBytes(s.getBytes(), stringBytes);
			stringBytes.add((byte) 0x00);
		}

		while(stringBytes.size()%16!=0){
			stringBytes.add((byte) 0x00);
		}
		//we don't have to worry about emty strings, they are ignored in the loader.
		header.setLenString(stringBytes.size());
	}
/****END OF WRITE LOGIC********END OF WRITE LOGIC********END OF WRITE LOGIC********END OF WRITE LOGIC********END OF WRITE LOGIC********END OF WRITE LOGIC********END OF WRITE LOGIC****/
	
	
	
/****COMPARE LOGIC********COMPARE LOGIC********COMPARE LOGIC********COMPARE LOGIC********COMPARE LOGIC********COMPARE LOGIC********COMPARE LOGIC********COMPARE LOGIC********COMPARE LOGIC****/
	private int findMatchingFieldDescGroup(ArrayList<EBXFieldDescriptor> fds){
		if (fds.size()>0){
			ArrayList<Integer> foundGroups = findMatchingFieldDescriptors(fds.get(0));
			if (foundGroups!=null){
				for (Integer firstIndex : foundGroups){
					boolean goNext = false;
					
					if (fds.size()>1){
						//loop trough the remaining ones, if at least 2 field exists.
						for (int i=1;i<fds.size();i++){
							if (!isMatchingFieldDescriptor(this.fieldDescriptors.get(firstIndex+i), fds.get(i))){
								//is not fitting, break out - check next group.
								goNext = true;
								break;
							}
						}	
					}
					
					if (!goNext){
						//all desc of group fit from the list.
						return firstIndex;
					}
				}
			}
		}
		return -1;
	}
	private ArrayList<Integer> findMatchingFieldDescriptors(EBXFieldDescriptor fieldDesc){
		ArrayList<Integer> found = new ArrayList<>();
		for (int i=0;i<this.fieldDescriptors.size();i++){
			EBXFieldDescriptor registredDesc = this.fieldDescriptors.get(i);
			if (isMatchingFieldDescriptor(registredDesc, fieldDesc)){
				found.add(i);
			}
		}
		if (found.size()>0){
			return found;
		}
		return null;
	}
	private boolean isMatchingFieldDescriptor(EBXFieldDescriptor fieldDesc1, EBXFieldDescriptor fieldDesc2){
		if (fieldDesc2.getType()==fieldDesc1.getType()){
			//if (fieldDesc2.getOffset()==fieldDesc1.getOffset()){
			//TODO index -1 ?? and is offset important ?
				if (fieldDesc2.getSize()==fieldDesc1.getSize()){
					if (fieldDesc2.getRef()==fieldDesc1.getRef()){
						if (fieldDesc2.getName().equals(fieldDesc1.getName())){
							return true;
						}	
					}
				}
			//}
		}
		return false;
	}
	
	private ArrayList<Integer> findMatchingComplexDescriptors(EBXComplexDescriptor complexDesc){
		ArrayList<Integer> found = new ArrayList<>();
		for (int i=0;i<this.complexDescriptors.size();i++){
			EBXComplexDescriptor registredDesc = this.complexDescriptors.get(i);
			if (registredDesc.getNumField()==complexDesc.getNumField()){
				if (registredDesc.getSize()==complexDesc.getSize()){
					if (registredDesc.getAlignment()==complexDesc.getAlignment()){
						if (registredDesc.getName().equals(complexDesc.getName())){
							
							if (registredDesc.getType()==complexDesc.getType()){
								found.add(i);
							}
							
						}
					}
				}
			}
		}
		if (found.size()>0){
			return found;
		}
		return null;
	}
/****END OF COMPARE LOGIC********END OF COMPARE LOGIC********END OF COMPARE LOGIC********END OF COMPARE LOGIC********END OF COMPARE LOGIC********END OF COMPARE LOGIC********END OF COMPARE LOGIC****/
	
}
