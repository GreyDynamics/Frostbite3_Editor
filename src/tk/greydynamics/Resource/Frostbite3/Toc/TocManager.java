package tk.greydynamics.Resource.Frostbite3.Toc;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.FileSeeker;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutEntry;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutField;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutFile;

public class TocManager {
	public static enum LayoutFieldType {
		STRING, BOOL, INTEGER, LONG, GUID, SHA1, LIST, RAW, RAW2, 
		ENTRY // ENTRY IS ONLY FOR RECREATION!
	};
	
	public static enum LayoutEntryType {
		ORDINARY, UNKNOWN
	};

	public static enum LayoutFileType {
		XORSig, Sig, None,CASBundle
	};
	
	/*ONLY NEEDED IF RAW FILE SHOULD SHOW UP IN EXPLORER. (DEBUG)
	public static ConvertedTocFile getConvertedToc(byte[] toc){
		return TocConverter.convertTocFile(readToc(toc));
	}
	*/
	public static LayoutFile readToc(String path){
		byte[] rawData = FileHandler.readFile(path+".toc");
		if (rawData!=null){
			return readToc(rawData, path+".sb");
		}
		return null;
	}

	public static LayoutFile readToc(byte[] fileArray, String sbPath) {
		FileSeeker seeker = new FileSeeker();
		ArrayList<LayoutEntry> entries = new ArrayList<LayoutEntry>();
		byte[] data = null;
		int header = FileHandler.readInt(fileArray, seeker, ByteOrder.BIG_ENDIAN);
		LayoutFileType fileType = null;
		if (header == 0x00D1CE00 || header == 0x00D1CE01) { //#the file is XOR encrypted and has a signature
			fileType = LayoutFileType.XORSig;
			data = null;
			System.out.println("TODO: XOR Decryption with TOC Files!"); //TODO
			/*
			 *  seek(296) #skip the signature
		     *  key=[ord(f.read(1))^123 for i in xrange(260)] #bytes 257 258 259 are not used; XOR the key with 123 right away
		     *  encryptedData=f.read()
		     *  data="".join([chr(key[i%257]^ord(encryptedData[i])) for i in xrange(len(encryptedData))]) #go through the data applying one key byte on one data 
			 */
		} else if (header == 0x00D1CE03) {
			fileType = LayoutFileType.Sig;
			data = new byte[fileArray.length-556];
			for (int i = 0; i < data.length; i++){
				data[i] = fileArray[i+556]; //seek(556) #skip signature + skip empty key + skip header
			}
		} else { //#the file is not encrypted; no key + no signature
			fileType = LayoutFileType.None;
			data = fileArray;
		}
		seeker = new FileSeeker();
		if (data!=null){
			while (seeker.getOffset() < data.length){ //READ ENTRIES
				LayoutEntry entry = readEntry(data, seeker);
				if (entry != null){
					entries.add(entry);
				}
			}//EOF
			LayoutFile file = new LayoutFile(fileType, sbPath);
			file.getEntries().addAll(entries);
			return file;
		}else{
			return null;
		}
	}
	
	public static LayoutFile readCASBundleLayout(byte[] bundle) { // instead of reading the complete file, only read parts
		FileSeeker seeker = new FileSeeker();
		ArrayList<LayoutEntry> entries = new ArrayList<LayoutEntry>();
		while (seeker.getOffset() < bundle.length){ //READ ENTRIES
			LayoutEntry entry = readEntry(bundle, seeker);
			if (entry != null){
				entries.add(entry);
			}
		}//EOF
		LayoutFile file = new LayoutFile(LayoutFileType.CASBundle);
		file.getEntries().addAll(entries);
		return file;
	}
	
	static LayoutEntry readEntry(byte[] data, FileSeeker seeker){
		//System.out.println("Reading ENTRY at "+seeker.getOffset());
		LayoutEntry entry;
		int entryType = (FileHandler.readByte(data, seeker) & 0xFF); //byte needs to be casted to unsigned.
		if (entryType == 0x82){
			entry = new LayoutEntry(LayoutEntryType.ORDINARY);
			int entrySize = FileHandler.readLEB128(data, seeker); 
			int entryOffset = seeker.getOffset();
			
			while (seeker.getOffset() < entryOffset+entrySize){ //READ FIELDS
				LayoutField field = readField(data, seeker);
				if (field != null){
					entry.getFields().add(field);
				}
			}
		}else{
			if (entryType != 0){
				entry = new LayoutEntry(LayoutEntryType.UNKNOWN);
				System.err.println("Unknown Type in TocManger detected: "+entryType+" at "+seeker.getOffset());
			}else{
				entry = null;
			}
			//TODO
		}
		return entry;
	}
	
	static LayoutField readField(byte[] data, FileSeeker seeker){
		LayoutField field = null;
		int fieldType = (FileHandler.readByte(data, seeker) & 0xFF); //byte needs to be casted to unsigned.
		String name = "";
		if (fieldType != 0x00){
			name = FileHandler.readString(data, seeker);
		}
		/* Really stupid stuff happend here, i have forgotten to replace the original path with the new one and so i currupted the original ones :/
		 * if (name.equalsIgnoreCase("sto")){
		 *	System.err.println("DEBUG");
		}*/
		if (fieldType == 0x01){ //#list type, containing ENTRIES (MULTIPLE ONE) 
			ArrayList<LayoutEntry> list = new ArrayList<LayoutEntry>();
			int listSize = FileHandler.readLEB128(data, seeker); 
			int listOffset = seeker.getOffset();
			while (seeker.getOffset() < listOffset+listSize){ 
				list.add(readEntry(data, seeker));
			}
			if (list.isEmpty()){list = null;}
			field = new LayoutField(list, LayoutFieldType.LIST, name);
		}else if (fieldType == 0x0F){ //ID 16 stored as HEXSTRING
			field = new LayoutField(FileHandler.bytesToHex(FileHandler.readByte(data, seeker, 16)), LayoutFieldType.GUID, name);
		}else if (fieldType == 0x09){ //LONG
			field = new LayoutField(FileHandler.readLong(data, seeker), LayoutFieldType.LONG, name);

		}else if (fieldType == 0x08){ //INTEGER
			field = new LayoutField(FileHandler.readInt(data, seeker), LayoutFieldType.INTEGER, name);
		}else if (fieldType == 0x06){ //BOOL
			boolean bool = false;
			if (FileHandler.readByte(data, seeker) == 0x01){
				bool = true;
			}
			field = new LayoutField(bool, LayoutFieldType.BOOL, name);
		}else if (fieldType == 0x02){ 
			int length = FileHandler.readLEB128(data, seeker);
			field = new LayoutField(FileHandler.readByte(data, seeker, length), LayoutFieldType.RAW, name);
		}else if (fieldType == 0x13){ 
			int length = FileHandler.readLEB128(data, seeker);
			field = new LayoutField(FileHandler.readByte(data, seeker, length), LayoutFieldType.RAW2, name);
			//sbTocFile -> RES -> ENTRY: idata 0x13 //TODO What is the diffrence ?
		}else if (fieldType == 0x10){ //SHA1 stored as HEXSTRING
			field = new LayoutField(FileHandler.bytesToHex(FileHandler.readByte(data, seeker, 20)), LayoutFieldType.SHA1, name);
		}else if (fieldType == 0x07){ // #string, length (including trailing null) prefixed as 7bit int
			FileHandler.readLEB128(data, seeker); //SKIP LENGTH
			field = new LayoutField(FileHandler.readString(data, seeker), LayoutFieldType.STRING, name);
		}else if (fieldType == 0x00){
			return null;
		}else{
			System.err.println("Unknown FieldType: "+fieldType+" in TocManager detected at "+seeker.getOffset());
		}
		return field;
	}
}
