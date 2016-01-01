package tk.greydynamics.Resource.Frostbite3.EBX;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;

public class EBXCreator {
	
	private EBXHeader header;
	private ArrayList<Byte> headerBytes;
//	private FileSeeker headerByteSeeker;
	
	private ArrayList<EBXExternalGUID> externalGUIDs;
	private ArrayList<Byte> externalGUIDBytes;
//	private FileSeeker externalGUIDByteSeeker;
	
	private ArrayList<String> names;
	private ArrayList<Byte> nameBytes;
//	private FileSeeker nameByteSeeker;
	
	private ArrayList<EBXFieldDescriptor> fieldDescriptors;
	private ArrayList<Byte> fieldDescriptorBytes;
//	private FileSeeker fieldDescriptorByteSeeker;	
	
	private ArrayList<EBXComplexDescriptor> complexDescriptors;
	private ArrayList<Byte> complexDescriptorBytes;
//	private FileSeeker complexDescriptorByteSeeker;
	
	private ArrayList<EBXInstanceRepeater> instanceRepeaters;
	private ArrayList<Byte> instanceRepeaterBytes;
//	private FileSeeker instanceRepeaterByteSeeker;
	
	private ArrayList<EBXArrayRepeater> arrayRepeaters;
	private ArrayList<Byte> arrayRepeaterBytes;
//	private FileSeeker arrayRepeaterByteSeeker;
	
	private ArrayList<String> strings;
	private ArrayList<Byte> stringBytes;
//	private FileSeeker stringByteSeeker;
	
	private ArrayList<Byte> payloadData;
//	private FileSeeker payloadDataSeeker;
	
	private ArrayList<Byte> arrayPayloadData;
	
	private ArrayList<ArrayList<Byte>> finalDataArrays;
	
	private ArrayList<Byte> filler = new ArrayList<>();
	
	private ArrayList<String> internalGUIDs;
	
	private boolean firstRun = true;
	
	public void init(){
		firstRun = false;
		
		finalDataArrays = new ArrayList<>();
		
		headerBytes = new ArrayList<>();
		externalGUIDBytes = new ArrayList<>();
		nameBytes = new ArrayList<>();
		fieldDescriptorBytes = new ArrayList<>();
		complexDescriptorBytes = new ArrayList<>();
		instanceRepeaterBytes = new ArrayList<>();
		arrayRepeaterBytes = new ArrayList<>();
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
		if (firstRun){
			init();
		}
		
		//TODO
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
		
		
		//NOTE - TRUEFILENAME does actually not exist, it uses the String value of the first 'Name' field in the primaryInstance
		boolean isPrimaryInstance = true;
		for (EBXInstance instance : ebxFile.getInstances()){
			proccInternalGUID(instance.getGuid());//register guid's first!
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
		while (stringOffset%16!=0){//TODO temp testing
			arrayRepeaterBytes.add((byte) 0x00);//line padding
			stringOffset = calcStringOffset();
		}
		header.setAbsStringOffset(stringOffset);
		writeStrings();
		
		payloadData.add((byte) 0x00);//add one line at least ?
		while (payloadData.size()%16!=0){
			payloadData.add((byte) 0x00);
		}
		
		while (arrayPayloadData.size()%16!=0){
			arrayPayloadData.add((byte) 0x00);
		}
		
		
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
		finalDataArrays.add(stringBytes);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(payloadData);
			finalDataArrays.add(filler);//TEST
		finalDataArrays.add(arrayPayloadData);
		
		byte[] data = FileHandler.toBytes(finalDataArrays);
		init(); //as cleanUp!
		return data;
	}
	
	private int calcStringOffset(){
		return /*headerBytes.size()*/ 64 + externalGUIDBytes.size() + nameBytes.size() + fieldDescriptorBytes.size() +
				complexDescriptorBytes.size() + instanceRepeaterBytes.size() + arrayRepeaterBytes.size();
	}

	public boolean proccInstance(EBXInstance ebxInstance, boolean isPrimaryInstance){
		while(payloadData.size()%16!=0){//TODO its aligned in original one, how does the size get effected ?
			payloadData.add((byte) 0x00);
		}
		String guid = ebxInstance.getGuid();
		if (guid.length()>15){
			FileHandler.addBytes(FileHandler.hexStringToByteArray(ebxInstance.getGuid()), payloadData);
			header.setNumGUIDRepeater(header.getNumGUIDRepeater()+1);
		}
		if (!isPrimaryInstance){
			for (int i=0; i<8;i++){
				payloadData.add((byte) 0x00);
			}
		}
		//obfuscationShift. shift by 8 ?? #alignment 4 instances require subtracting 8 for all field offsets and the complex size
		short index = proccComplex(ebxInstance.getComplex(), false/*isArrayMember*/, true/*proccFieldDesc*/, false/*hasNoPayload*/);
		
		EBXInstanceRepeater repeater = new EBXInstanceRepeater(index, 0);
		/*sow, id clud be ´tha we cud makke a repeatzer 4 eavery one yoooo, in the original one as it always 0. we should be fine with that.*/
		instanceRepeaters.add(repeater);
		return true;
	}
	
	public short proccComplex(EBXComplex ebxComplex, boolean isArrayMember, boolean proccDescriptor, boolean hasNoPayload){
		//return index of complex
		//TODO
		boolean isAligned = false;
		while(payloadData.size()%16>=12){//TODO complex gets aligned, but why ?
			payloadData.add((byte) 0x00);
			
		}
		if (payloadData.size()%16==0){
			isAligned = true;
		}
		int startOffset = payloadData.size();
		
		int totalSize = 0; //its acc. a short but wanna have a use for secondary size ^__^
		for (EBXField field : ebxComplex.getFields()){
			int fieldSize = proccField(field, isArrayMember, proccDescriptor, hasNoPayload);
			if(fieldSize>=0){
				totalSize += fieldSize;
			}else{
				System.err.println("Couldn't processing EBXComplex's field! Name: "+field.getFieldDescritor().getName());
			}
		}
		int payloadTotalSize = payloadData.size()-startOffset;
		//while(payloadData.size()%16>=12&&payloadTotalSize>=12&&payloadTotalSize<16){<-Worked really well
		//while((payloadData.size()%16>=12&&payloadTotalSize>=12&&payloadTotalSize<16)||(payloadTotalSize>32&&payloadData.size()%16>=12)){
		while((payloadData.size()%16>=12&&payloadTotalSize>=12&&payloadTotalSize<16)||(payloadTotalSize>32&&payloadData.size()%16>=12)){//TODO fields after complex gets SOMETIMES aligned too, but why ?
			payloadData.add((byte) 0x00);
			payloadTotalSize++;
		}
		return proccComplexDescriptor(ebxComplex.getComplexDescriptor(), ebxComplex.getFields().length, totalSize);
	}
	
	public short proccComplexDescriptor(EBXComplexDescriptor desc, Integer numFields, int totalSize){
		//return index
		
		//TODO
		desc.setFieldStartIndex(fieldDescriptors.size()-numFields);
		
		desc.setSize((short) totalSize);
		desc.setSecondarySize((short) (totalSize>>16 & 0xFFFF)); //?? maybe ??
		
		desc.setNumField((char) (numFields & 0xFF));
		
		
		//PAYLOAD ??
		
		complexDescriptors.add(desc);
		
		return (short) (complexDescriptors.size()-1);
	}
	
	public boolean proccFieldDescriptor(EBXFieldDescriptor desc){
		//TODO desc.setOffset(offset);  obfuscationShift ??
		
		//PAYLOAD ??
		
		fieldDescriptors.add(desc);
		
		return true;
	}
	
	public int proccField(EBXField ebxField, boolean isArrayMember, boolean proccDescriptor, boolean hasNoPayload){
		ArrayList<Byte> targetList = null;
		if (isArrayMember){
			targetList = arrayPayloadData;
		}else{
			targetList = payloadData;
		}
		EBXFieldDescriptor desc = ebxField.getFieldDescritor();
		byte[] data = null;
		if (ebxField.getType()!=null){//if newly added with TreeView, it does not have a type as SHORT) 
			switch (ebxField.getType()) {
				case ArrayComplex:
					desc.setType((short) 0x0041);
					break;
				case Bool:
					desc.setType((short) 0xc0ad);
					break;
				case Byte:
					desc.setType((short) 0xc0cd);
					break;
				case ChunkGuid:
					desc.setType((short) 0xC15D);
					break;
				case Complex:
					desc.setType((short) 0x0029);//OR WHATEVER ??
					break;
				case Enum:
					desc.setType((short) 0x0089);
					break;
				case ExternalGuid:
					desc.setType((short) 0x0035);
					break;
				case Float:
					desc.setType((short) 0xC13D);
					break;
				case Guid:
					desc.setType((short) 0x0035); //same as external ID!
					break;
				case Hex8:
					desc.setType((short) 0x417D);
					break;
				case Integer:
					desc.setType((short) 0xc0fd);
					break;
				case Short:
					desc.setType((short) 0xc0ed);
					break;
				case String:
					desc.setType((short) 0x407D);//OR WHATEVER ??
					break;
				case UInteger:
					desc.setType((short) 0xc10d);
					break;
				case Unknown:
					desc.setType((short) 0xFFFF);//ERROR
					break;
				}
		}
		short h = ebxField.getFieldDescritor().getType();
		if (h==0xFFFF){
			System.err.println("Unknown type!");
			//DEFUQ ?
		}else if(h==(short)0x407D||h==(short)0x409D){ //_________________________________________________________________________________STRING
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
				data = FileHandler.toBytes((float) ebxField.getValue(), ByteOrder.BIG_ENDIAN);
				FileHandler.addBytes(data, targetList);
			}
		}else if(h==(short)0x0029||h==(short)0xd029||h==(short)0x8029){//_______________________________________________________________________COMPLEX
			
			//data = new byte[] {0x00, 0x00, 0x00, 0x00};
			//FileHandler.addBytes(data, payloadData);//?? what is the value/size ?? //TODO
			
			short index = proccComplex(ebxField.getValueAsComplex(), isArrayMember, proccDescriptor, hasNoPayload);
			if (index==-1){
				return -1;
			}
			desc.setRef(index);
		}else if (h==(short)0xC089||h==(short)0x0089){//_________________________________________________________________________________ENUM //TODO NEEDS WORK IN TCF, SELECTED INDEX FAIL.
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
							proccInternalGUID(val);
						index++;//because 1 is acc. 0
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
			int startOffset = arrayPayloadData.size();

			if (ebxField.getValue() instanceof String){
				data = new byte[] {0x00, 0x00, 0x00, 0x00};
			}else{
				EBXComplex arrayComplex = ebxField.getValueAsComplex();
				arrayComplex.getComplexDescriptor().setName("array");//->try this because of treeview converter creates a complex of a complex.
				short type = 0;
				if (arrayComplex.getFields().length>0){
					type = arrayComplex.getField(0).getFieldDescritor().getType();
				}

				int fieldStartIndex = 0;
				if (type==0){//type is signed so we would have to cast it to unsigned but...nope
					System.err.println("TODO: ARRAYCOMPLEX UNDEFINED TYPE");
				}else if (type==(short)0x29){//Type of Complex| //TODO are the more type's there using diffrent descriptors for each Member ?
					fieldStartIndex = fieldDescriptors.size();
					//data = FileHandler.toBytes(arrayComplex.getFields().length, ByteOrder.LITTLE_ENDIAN);//test
					for (EBXField field : arrayComplex.getFields()){
						int refIndex = complexDescriptors.size();
			/*//TODO*/	//procfield gets procc by proccComplex too!
						proccField(field, true /*isArrayMember*/, true /*proccFieldDescriptor*/, true /*hasNoPayload*/);//complex does not have payload, only structure!
						EBXFieldDescriptor memberFieldDesc = new EBXFieldDescriptor("member", type, (short) refIndex, 0, 0);
						fieldDescriptors.add(memberFieldDesc);
					}
				}else{	
					fieldStartIndex = fieldDescriptors.size();
					EBXFieldDescriptor memberMasterFieldDesc = new EBXFieldDescriptor("member", type, (short) 0, 0, 0);
					fieldDescriptors.add(memberMasterFieldDesc);
		/*//TODO *///for (EBXField field : arrayComplex.getFields()){ <- This gets done later in the proccComplex!
					//	proccField(field, true /*isArrayMember*/, false /*proccFieldDescriptor*/, hasNoPayload);//only write to arrayPayloadSection. Do NOT proccFieldDescriptor!
					//}
				}
				arrayComplex.getComplexDescriptor().setAlignment((char)0x4); //TODO test alignment
				arrayComplex.getComplexDescriptor().setType((short)0x41);
				if (type==(short)0x29){
					arrayComplex.getComplexDescriptor().setNumField((char)arrayComplex.getFields().length);
				}else{
					arrayComplex.getComplexDescriptor().setNumField((char) 0);//TODO TEST setNumFields in arrayComplex
				}

				arrayComplex.getComplexDescriptor().setSize((short) 0);//TODO arraycomplex calc size

				arrayComplex.getComplexDescriptor().setFieldStartIndex(fieldStartIndex);

	/*//TODO*/	short arrayComplexIndex = proccComplex(arrayComplex, true/*isArrayMember*/, false/*proccFieldDesc*/, hasNoPayload);

				if (type==(short)0x29){//Array of Complex's
					data = new byte[] {0x00, 0x00, 0x00, 0x00};
				}else{
					EBXArrayRepeater repeater = new EBXArrayRepeater(startOffset, arrayComplex.getFields().length, arrayComplexIndex /*complexIndex - same as ref*/);
					arrayRepeaters.add(repeater);

					data = FileHandler.toBytes(arrayRepeaters.size(), ByteOrder.LITTLE_ENDIAN);
				}
			}
			//data = new byte[] {0x41, 0x72, 0x72, 0x79};
			FileHandler.addBytes(data, targetList);
		}else if(h==(short)0xc0ed){//____________________________________________________________________________________________SHORT
			if (ebxField.getValue()!=null){
				short val = (short) ebxField.getValue();
				if (isArrayMember){
					data = FileHandler.toBytes(val, ByteOrder.BIG_ENDIAN);
				}else{//normalPayload short is 4 bytes. defuq...
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
			if (ebxField.getValue()!=null){
				byte[] typebyte = FileHandler.toBytes(h,ByteOrder.LITTLE_ENDIAN);
				System.err.println("Type not found: 0x"+FileHandler.bytesToHex(typebyte));
			}
		}
		
		
		if (proccDescriptor){
			if (!proccFieldDescriptor(desc)){
				return -1;
			}
		}
		
		if (desc.getName().equals("member")&&fieldDescriptors.size()>1000){
			System.err.println("MEMBER!!");
		}
		
		
		
		Integer length = 0;
		if (data!=null){
			length = data.length;
		}

		return length;
	}
	
	
	public int proccName(String name){
		//returns FNV_1 hash
		for (String entryValue : names){
			if (name.equals(entryValue)){
				//System.out.println(entryValue+" "+EBXHandler.hasher(name.getBytes()));
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
		//HashMap<Integer, String> sortedMap = new HashMap<Integer, String>(names);
		for (String s : names){
			FileHandler.addBytes(s.getBytes(), nameBytes);
			nameBytes.add((byte) 0x00);//may not needed, for cancel out.
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
		while (stringBytes.size()%16!=0){
			stringBytes.add((byte) 0x00);//TODO line padding, add unnecessary extra line
		}
		header.setLenString(stringBytes.size());
	}
	
}
