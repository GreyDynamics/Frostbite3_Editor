package tk.greydynamics.JavaFX;

import java.io.File;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.LayerEntity;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.Windows.MainWindow.EntryType;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Mod.Package;
import tk.greydynamics.Mod.PackageEntry;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;
import tk.greydynamics.Resource.Frostbite3.Cas.CasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundle;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundleChunk;
import tk.greydynamics.Resource.Frostbite3.Cas.NonCasBundleEntry;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplexDescriptor;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFieldDescriptor;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutEntry;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutField;
import tk.greydynamics.Resource.Frostbite3.Layout.LayoutFile;
import tk.greydynamics.Resource.Frostbite3.Toc.ConvertedTocFile;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;
import tk.greydynamics.Resource.Frostbite3.Toc.TocEntry;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutEntryType;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutFieldType;
import tk.greydynamics.Resource.Frostbite3.Toc.TocManager.LayoutFileType;

public class TreeViewConverter {
	
	/*FROM TOC*/
	public static TreeItem<TreeViewEntry> getTreeView(LayoutFile tocFile){
		TreeItem<TreeViewEntry> root = new TreeItem<TreeViewEntry>(new TreeViewEntry("TocFile", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (LayoutEntry e : tocFile.getEntries()){
			TreeItem<TreeViewEntry> entry = readEntry(e);
			if (entry != null){
				root.getChildren().add(entry);
			}
		}
		return root;
	}
	static TreeItem<TreeViewEntry> readEntry(LayoutEntry tocEntry){
		if (tocEntry != null){
			TreeItem<TreeViewEntry> entry = new TreeItem<TreeViewEntry>(new TreeViewEntry("TocEntry", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
			for (LayoutField f : tocEntry.getFields()){
				entry.getChildren().add(readField(f));
			}
			return entry;
		}else{
			return null;
		}
	}
	@SuppressWarnings({ "unchecked", "incomplete-switch" })
	static TreeItem<TreeViewEntry> readField(LayoutField tocField){
		TreeViewEntry entry = null;
		switch(tocField.getType()){
			case BOOL:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_BOOL), tocField.getObj(), EntryType.BOOL);
				break;
			case GUID:
				entry = new TreeViewEntry(tocField.getName(), null, tocField.getObj(), EntryType.GUID);
				break;
			case INTEGER:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_INTEGER), tocField.getObj(), EntryType.INTEGER);
				break;
			case LIST:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST);
				TreeItem<TreeViewEntry> field = new TreeItem<TreeViewEntry>(entry);
				for (LayoutEntry tocE : (ArrayList<LayoutEntry>) tocField.getObj()){
					field.getChildren().add(readEntry(tocE));
				}
				return field;
			case LONG:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_LONG), tocField.getObj(), EntryType.LONG);
				break;
			case SHA1:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_HASH), tocField.getObj(), EntryType.SHA1);
				break;
			case STRING:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_TEXT), tocField.getObj(), EntryType.STRING);
				break;
			case RAW:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_RAW), tocField.getObj(), EntryType.RAW);
				break;
			case RAW2:
				entry = new TreeViewEntry(tocField.getName(), new ImageView(JavaFXHandler.ICON_RAW), tocField.getObj(), EntryType.RAW2);
				break;
		}
		TreeItem<TreeViewEntry> field = new TreeItem<TreeViewEntry>(entry);
		return field;
	}
	/*END OF TOC*/
	
	
	
	/*FROM EBX*/
	public static TreeItem<TreeViewEntry> getTreeView(EBXFile ebx){
		TreeItem<TreeViewEntry> root = new TreeItem<TreeViewEntry>(new TreeViewEntry(ebx.getTruePath(), new ImageView(JavaFXHandler.ICON_DOCUMENT), ebx.getGuid(), EntryType.LIST));
		for (EBXInstance instance : ebx.getInstances()){
			root.getChildren().add(readInstance(instance));
		}
		return root;
	}
	static TreeItem<TreeViewEntry> readInstance(EBXInstance instance){
		TreeItem<TreeViewEntry> iList = new TreeItem<TreeViewEntry>(new TreeViewEntry(instance.getComplex().getComplexDescriptor().getName()+" "+instance.getGuid().toUpperCase(), new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for(EBXField field : instance.getComplex().getFields()){
			iList.getChildren().add(readField(field));
		}
		return iList;
	}
	static TreeItem<TreeViewEntry> readField(EBXField ebxField){
		TreeViewEntry entry = null;
		if (ebxField != null){
			try{
				switch(ebxField.getType()){
					case ArrayComplex:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_ARRAY), null, EntryType.ARRAY);
						entry.setEBXType((short) (ebxField.getFieldDescritor().getType()&0xFFFF));
						if (ebxField.getValue() instanceof String){
							entry.setValue("*nullArray*");
							TreeItem<TreeViewEntry> field = new TreeItem<TreeViewEntry>(entry);
							return field;
						}else{
							TreeItem<TreeViewEntry> fieldArray = new TreeItem<TreeViewEntry>(entry);
							for (EBXField eF : ebxField.getValueAsComplex().getFields()){
								fieldArray.getChildren().add(readField(eF));
							}
							return fieldArray;
						}
						//array in array does not work right ?
					case Bool:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_BOOL), (Boolean)ebxField.getValue(), EntryType.BOOL); //0x01 == TRUE
						break;
					case Byte:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_BYTE), (Byte)ebxField.getValue(), EntryType.BYTE);
						break;
					case Complex:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName(), new ImageView(JavaFXHandler.ICON_INSTANCE), null, EntryType.LIST);
						entry.setEBXType((short) (ebxField.getFieldDescritor().getType()&0xFFFF));
						TreeItem<TreeViewEntry> complexFields = new TreeItem<TreeViewEntry>(entry);
						for (EBXField eF : ebxField.getValueAsComplex().getFields()){
							complexFields.getChildren().add(readField(eF));
						}
						return complexFields;
					case Enum:
//						if (ebxField.getValue() instanceof EBXFieldDescriptor){
//							entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.enumIcon), ((EBXFieldDescriptor)ebxField.getValue()).getName(), EntryType.ENUM);
//						}else{
//							entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.enumIcon), (String)ebxField.getValue(), EntryType.ENUM);
//						}
						if (ebxField.getValue() instanceof HashMap<?,?>){
							entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_ENUM), (HashMap<EBXFieldDescriptor, Boolean>)ebxField.getValue(), EntryType.ENUM);	
						}else{
							entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_ENUM), (String)ebxField.getValue(), EntryType.ENUM);
						}
						break;
					case Float:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_FLOAT), (Float)ebxField.getValue(), EntryType.FLOAT);
						break;
					case ChunkGuid:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), null, (String)ebxField.getValue(), EntryType.CHUNKGUID);
						break;
					case Guid:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_INTERNAL), (String)ebxField.getValue(), EntryType.GUID);
						break;
					case ExternalGuid:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_STRUCTURE), (String)ebxField.getValue(), EntryType.GUID);
						break;
					case Hex8:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), null, (String)ebxField.getValue(), EntryType.HEX8);
						break;
					case Integer:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_INTEGER), (Integer)ebxField.getValue(), EntryType.INTEGER);
						break;
					case Short:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_SHORT), (Short)ebxField.getValue(), EntryType.SHORT);
						break;
					case String:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_TEXT), (String)ebxField.getValue(), EntryType.STRING);
						break;
					case UInteger:
						entry = new TreeViewEntry(ebxField.getFieldDescritor().getName(), new ImageView(JavaFXHandler.ICON_INTEGER_UNSIGNED), (Long)ebxField.getValue(), EntryType.UINTEGER);
						break;
					case Unknown:
						break;
					default:
						break;
				}
				if (entry != null){
					entry.setEBXType((short) (ebxField.getFieldDescritor().getType()&0xFFFF));
					entry.setOffset(ebxField.getOffset());
				}
				TreeItem<TreeViewEntry> field = new TreeItem<TreeViewEntry>(entry);
				return field;
			}catch (Exception e){
				if (ebxField.getType() == null && ebxField.getFieldDescritor() != null){
					System.out.println(ebxField.getFieldDescritor().getName());
					System.err.println("EBXFile to TreeView: Field does not have a type defined.");
				}else{
					e.printStackTrace();
					System.err.println("Error while converting EBXFile to TreeView.");
				}
				return null;
			}
		}else{
			return null;
		}
	}
	/*END OF EBX*/
	
	/*ENCODE TO EBX*/
	public static EBXFile getEBXFile(TreeItem<TreeViewEntry> rootEntry){
		try{
			ArrayList<EBXInstance> instances = new ArrayList<>();
			for (TreeItem<TreeViewEntry> instance : rootEntry.getChildren()){
				EBXInstance ebxInstance = getEBXInstance(instance);
				if (ebxInstance != null){
					instances.add(ebxInstance);
				}
			}
			EBXFile file = new EBXFile(rootEntry.getValue().getName(), instances, (String) rootEntry.getValue().getValue(), null);
			return file;
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("Error while converting TreeViewStructure to EBXFile.");
		}
		return null;
	}
	
	static EBXInstance getEBXInstance(TreeItem<TreeViewEntry> childEntry){
		try{
			EBXComplex complex = getEBXComplex(childEntry);
			EBXInstance ebxInstance = new EBXInstance(childEntry.getValue().getName().split(" ")[1], complex);
			return ebxInstance;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Error while converting TreeViewStructure to EBXInstance.");
			return null;
		}
		
	}
	
	static EBXComplex getEBXComplex(TreeItem<TreeViewEntry> complexEntry){
		try{
			EBXComplexDescriptor complexDescriptor = null;
			
			String name = complexEntry.getValue().getName();
			String[] split = name.split("::");
			String[] splitInstanceComplex = complexEntry.getValue().getName().split(" ");
			if (split.length==2 && splitInstanceComplex.length==1){ //if it has ::
				complexDescriptor = new EBXComplexDescriptor(split[1]);//GET RIGHT PART AS COMPLEX NAME
			}else if (splitInstanceComplex.length == 2 && split.length == 1){//if it is a instanceComplex
				complexDescriptor = new EBXComplexDescriptor(splitInstanceComplex[0]); //First part is -> ReferencedObjectData //Second is Guid -> 00000001
			}else{
				complexDescriptor = new EBXComplexDescriptor(split[0]);//GET LEFT PART AS COMPLEX NAME
				
			}
			complexDescriptor.setType(complexEntry.getValue().getEBXType());
			
			
			//complexDescriptor.setType(type);
			EBXComplex complex = new EBXComplex(complexDescriptor);
			complex.setFields(new EBXField[complexEntry.getChildren().size()]);
			EBXField[] fields = complex.getFields();
			int index = 0;
			for (TreeItem<TreeViewEntry> twField : complexEntry.getChildren()){
				EBXField ebxField = getEBXField(twField);
				if (ebxField!=null){
					fields[index] = ebxField;
				}else{
					System.err.println("Error while converting TreeViewStructure to EBXComplex. 'null'.");
				}
				index++;
			}
			return complex;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Error while converting TreeViewStructure to EBXComplex.");
			return null;
		}
	}
	
	static EBXField getEBXField(TreeItem<TreeViewEntry> fieldEntry){
		TreeViewEntry entry = fieldEntry.getValue();
		EBXFieldDescriptor desc = new EBXFieldDescriptor(entry.getName(), (short) 0, (short) 0, 0, 0);
		EBXField field = new EBXField(desc, 0);
		switch (fieldEntry.getValue().getType()) {
			case ARRAY:
				if (fieldEntry.getValue().getValue() instanceof String){
					field.setValue("*nullArray*", FieldValueType.ArrayComplex);
					desc.setType(entry.getEBXType());
					break;
				}else{
					EBXComplex complex0 = getEBXComplex(fieldEntry);
					field.setValue(complex0, FieldValueType.ArrayComplex);
					desc.setType(entry.getEBXType());
					break;
				}
			case BOOL:
				field.setValue(entry.getValue(), FieldValueType.Bool);
				desc.setType(entry.getEBXType());
				break;
			case BYTE:
				field.setValue(entry.getValue(), FieldValueType.Byte);
				desc.setType(entry.getEBXType());
				break;
			case CHUNKGUID:
				field.setValue(entry.getValue(), FieldValueType.ChunkGuid);
				desc.setType(entry.getEBXType());
				break;
			case ENUM:
				field.setValue(entry.getValue(), FieldValueType.Enum);//value is string(if null) or hashmap.
				desc.setType(entry.getEBXType());
				break;
			case FLOAT:
				field.setValue(entry.getValue(), FieldValueType.Float);
				desc.setType(entry.getEBXType());
				break;
			case GUID:
				field.setValue(entry.getValue(), FieldValueType.Guid);
				desc.setType(entry.getEBXType());
				break;
			case HEX8:
				field.setValue(entry.getValue(), FieldValueType.Hex8);
				desc.setType(entry.getEBXType());
				break;
			case INTEGER:
				field.setValue(entry.getValue(), FieldValueType.Integer);
				desc.setType(entry.getEBXType());
				break;
			case LIST:
				EBXComplex complex1 = getEBXComplex(fieldEntry);
				
				/*IF FIELD AS PARENT: CHANGE DESCRIPTOR NAME!*/
				desc.setName(desc.getName().split("::")[0]);
				
				field.setValue(complex1, FieldValueType.Complex);
				desc.setType(entry.getEBXType());
				break;
			case SHORT:
				field.setValue(entry.getValue(), FieldValueType.Short);
				desc.setType(entry.getEBXType());
				break;
			case STRING:
				field.setValue(entry.getValue(), FieldValueType.String);
				desc.setType(entry.getEBXType());
				return field;
			case UINTEGER:
				field.setValue(entry.getValue(), FieldValueType.UInteger);
				desc.setType(entry.getEBXType());
				return field;
			default:
				System.err.println("Error while converting TreeViewStructure to EBXField.");
				break;
			}
		return field;
	}
	/*END OF ENCODE TO EBX*/
	
	/*ENCODE TO TOC*/
	public static LayoutFile getTocFile(TreeItem<TreeViewEntry> rootEntry, LayoutFileType type){
		LayoutFile file = new LayoutFile(type);
		ArrayList<LayoutEntry> entries = new ArrayList<LayoutEntry>();
		for (TreeItem<TreeViewEntry> child : rootEntry.getChildren()){
			entries.add(getTocEntry(child, LayoutEntryType.ORDINARY));
		}
		file.getEntries().addAll(entries);
		return file;
	}
	
	static LayoutEntry getTocEntry(TreeItem<TreeViewEntry> entry, LayoutEntryType type){
		LayoutEntry tocEntry = new LayoutEntry(type);
		ArrayList<LayoutField> fields = new ArrayList<LayoutField>();
		for (TreeItem<TreeViewEntry> child : entry.getChildren()){
			fields.add(getTocField(child));
		}
		tocEntry.getFields().addAll(fields);
		return tocEntry;
	}
	@SuppressWarnings("incomplete-switch") //Toc files only handle these fields.
	static LayoutField getTocField(TreeItem<TreeViewEntry> field){
		LayoutField tf = null;
		switch(field.getValue().getType()){
			case BOOL:
				tf = new LayoutField(field.getValue().getValue(), LayoutFieldType.BOOL, field.getValue().getName());
				break;
			case GUID:
				tf = new LayoutField(field.getValue().getValue(), LayoutFieldType.GUID, field.getValue().getName());
				break;
			case INTEGER:
				tf = new LayoutField(field.getValue().getValue(), LayoutFieldType.INTEGER, field.getValue().getName());
				break;
			case LIST:
				ArrayList<LayoutEntry> tocEntries = new ArrayList<LayoutEntry>();
				for (TreeItem<TreeViewEntry> item : field.getChildren()){
					tocEntries.add(getTocEntry(item, LayoutEntryType.ORDINARY));
				}
				tf = new LayoutField(tocEntries, LayoutFieldType.LIST, field.getValue().getName());
				break;
			case LONG:
				tf = new LayoutField(field.getValue().getValue(), LayoutFieldType.LONG, field.getValue().getName());
				break;
			case SHA1:
				tf = new LayoutField(field.getValue().getValue(), LayoutFieldType.SHA1, field.getValue().getName());
				break;
			case STRING:
				tf = new LayoutField(field.getValue().getValue(), LayoutFieldType.STRING, field.getValue().getName());
				break;
			case RAW:
				tf = new LayoutField(field.getValue().getValue(), LayoutFieldType.RAW, field.getValue().getName());
				break; 
		}
		return tf;
	}
	/*END OF TO TOC*/

	/*START OF CONVERTED TOC*/
	public static TreeItem<TreeViewEntry> getTreeView(ConvertedTocFile cTocF){
		TreeItem<TreeViewEntry> rootnode = new TreeItem<TreeViewEntry>(new TreeViewEntry(cTocF.getName(), new ImageView(JavaFXHandler.ICON_DOCUMENT), null, EntryType.LIST));
				
		/*BUNDLES*/
		TreeItem<TreeViewEntry> bundles = new TreeItem<TreeViewEntry>(new TreeViewEntry("bundles - "+cTocF.getBundles().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (TocEntry link : cTocF.getBundles()){
			String[] name  = null;
			if (!link.getID().equals("")){
				name = link.getID().split("/");
			}
			if (!link.getGuid().equals("")){
				name = link.getGuid().split("/");
			}
			ImageView icon = null;
			if (link.isDelta()){
				icon = new ImageView(JavaFXHandler.ICON_UPDATE);
			}
			if (icon==null){
				icon = new ImageView(JavaFXHandler.ICON_INSTANCE);
			}
			TreeViewEntry childEntry = new TreeViewEntry(name[name.length-1], icon, link, EntryType.STRING);
			TreeItem<TreeViewEntry> child = new TreeItem<TreeViewEntry>(childEntry);
			childEntry.setTooltip("Offset: 0x"+FileHandler.bytesToHex(FileHandler.toBytes(link.getOffset(), ByteOrder.BIG_ENDIAN))+
					" Size: 0x"+FileHandler.bytesToHex(FileHandler.toBytes(link.getSize(), ByteOrder.BIG_ENDIAN)));
			pathToTree(bundles, link.getID(), child);
		}
		rootnode.getChildren().add(bundles);
		
		/*CHUNKS*/
		TreeItem<TreeViewEntry> chunks = new TreeItem<TreeViewEntry>(new TreeViewEntry("chunks - "+cTocF.getChunks().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (TocEntry link : cTocF.getChunks()){
			String name = "";
			if (!link.getID().equals("")){
				name = link.getID();
			}
			if (!link.getGuid().equals("")){
				name = link.getGuid();
			}
			chunks.getChildren().add(new TreeItem<TreeViewEntry>(new TreeViewEntry(name, new ImageView(JavaFXHandler.ICON_INSTANCE), link, EntryType.STRING)));
		}
		rootnode.getChildren().add(chunks);
		
		
		return rootnode;
	}	
	/*END OF CONVERTED TOC*/
	
	/*START OF CONVERTED CasBundle*/
	public static TreeItem<TreeViewEntry> getTreeView(CasBundle part){
		TreeItem<TreeViewEntry> rootnode = new TreeItem<TreeViewEntry>(new TreeViewEntry(part.getBasePath(), new ImageView(JavaFXHandler.ICON_DOCUMENT), null, EntryType.LIST));
		
		File modFilePack = new File(FileHandler.normalizePath(
				Core.getGame().getCurrentMod().getPath()+ModTools.PACKAGEFOLDER+
				Core.getGame().getCurrentFile().replace(Core.gamePath, "")+ModTools.PACKTYPE)
		);
		Package modPackage = null;
		if (modFilePack.exists()){
			modPackage = Core.getModTools().readPackageInfo(modFilePack);
		}
		
		/*EBX*/
		TreeItem<TreeViewEntry> ebx = new TreeItem<TreeViewEntry>(new TreeViewEntry("ebx - "+part.getEbx().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (ResourceLink link : part.getEbx()){
			if (modPackage!=null){
				for (PackageEntry entry : modPackage.getEntries()){
					if (entry.getSubPackage().equalsIgnoreCase(part.getBasePath())&&//mp_playground/content
							entry.getResourcePath().equalsIgnoreCase(link.getName()+".ebx")//levels/mp_playground/content/layer2_buildings.ebx
					){
						link.setHasModFile(true);
						break;
					}
				}
				//has NO mod file
			}
			String[] name = link.getName().split("/");
			TreeViewEntry childEntry = new TreeViewEntry(name[name.length-1]+" ("+link.getSha1()+")", new ImageView(JavaFXHandler.ICON_STRUCTURE), link, EntryType.STRING);
			if (link.getCasPatchType()!=0){
				childEntry.setName(childEntry.getName()+"_(Patched: "+link.getCasPatchType()+")");
			}
			String isReferenced = " ref. needs work treeviewconverter";
			/*if (Core.getGame().getEBXFileGUIDs().get(link.getEbxFileGUID().toUpperCase()) != null){
				isReferenced += " (referenced) ";
			}*/
			childEntry.setTooltip("GUID: "+link.getEbxFileGUID()+isReferenced);
			TreeItem<TreeViewEntry> child = new TreeItem<TreeViewEntry>(childEntry);
			pathToTree(ebx, link.getName(), child);
		}
		rootnode.getChildren().add(ebx);
		
		/*DBX*/
		TreeItem<TreeViewEntry> dbx = new TreeItem<TreeViewEntry>(new TreeViewEntry("dbx - "+part.getDbx().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (ResourceLink link : part.getDbx()){
			String[] name = link.getName().split("/");
			TreeViewEntry childEntry = new TreeViewEntry(name[name.length-1]+" ("+link.getSha1()+")", new ImageView(JavaFXHandler.ICON_STRUCTURE), link, EntryType.STRING);
			TreeItem<TreeViewEntry> child = new TreeItem<TreeViewEntry>(childEntry);
			pathToTree(dbx, link.getName(), child);
		}
		rootnode.getChildren().add(dbx);
		
		/*RES*/
		TreeItem<TreeViewEntry> res = new TreeItem<TreeViewEntry>(new TreeViewEntry("res - "+part.getRes().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (ResourceLink link : part.getRes()){
			/*//TODO Res has mod file's too!
			if (modPackage!=null){
				for (PackageEntry entry : modPackage.getEntries()){
					if (entry.getSubPackage().equalsIgnoreCase(part.getPath())&&//mp_playground/uiloading_sp
							entry.getResourcePath().startsWith(link.getName())//levels/mp_playground/uiloading/cuteKitties.itexture
					){
						link.setHasModFile(true);
						break;
					}
				}
				//has NO mod file
			}*/
			String[] name = link.getName().split("/");
			TreeViewEntry childEntry = new TreeViewEntry(name[name.length-1]+" ("+link.getSha1()+", "+link.getType()+")", null, link, EntryType.STRING);
			ImageView graphic = getGraphic(link.getType());
			childEntry.setGraphic(graphic);
			TreeItem<TreeViewEntry> child = new TreeItem<TreeViewEntry>(childEntry);
			pathToTree(res, link.getName(), child);
		}
		rootnode.getChildren().add(res);
		
		/*CHUNKS*/
		TreeItem<TreeViewEntry> chunks = new TreeItem<TreeViewEntry>(new TreeViewEntry("chunks - "+part.getChunks().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (ResourceLink link : part.getChunks()){
			chunks.getChildren().add(new TreeItem<TreeViewEntry>(
					new TreeViewEntry(link.getId()+" SHA1: "+link.getSha1()+" (offset: 0x"+FileHandler.toHexInteger(link.getLogicalOffset())+" , size: 0x"+FileHandler.toHexInteger(link.getLogicalSize())+")", 
							new ImageView(JavaFXHandler.ICON_INSTANCE), link, EntryType.STRING)));
		}
		rootnode.getChildren().add(chunks);
		
		/*CHUNKMETA*/
		TreeItem<TreeViewEntry> chunkmeta = new TreeItem<TreeViewEntry>(new TreeViewEntry("chunkmeta - "+part.getChunkMeta().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (ResourceLink link : part.getChunkMeta()){
			chunkmeta.getChildren().add(new TreeItem<TreeViewEntry>(new TreeViewEntry(link.getH32()+"", new ImageView(JavaFXHandler.ICON_RAW), link, EntryType.RAW)));
		}
		rootnode.getChildren().add(chunkmeta);
		
		return rootnode;
	}
	/*END OF CONVERTED CasBundle*/
	
	
	/*START OF CONVERTED NonCasBundle*/
	public static TreeItem<TreeViewEntry> getTreeView(NonCasBundle nonCas){
		TreeItem<TreeViewEntry> rootnode = new TreeItem<TreeViewEntry>(new TreeViewEntry(nonCas.getName(), new ImageView(JavaFXHandler.ICON_DOCUMENT), null, EntryType.LIST));
		
		File modFilePack = new File(FileHandler.normalizePath(
				Core.getGame().getCurrentMod().getPath()+ModTools.PACKAGEFOLDER+
				Core.getGame().getCurrentFile().replace(Core.gamePath, "")+ModTools.PACKTYPE)
		);
		Package modPackage = null;
		if (modFilePack.exists()){
			modPackage = Core.getModTools().readPackageInfo(modFilePack);
		}
		
		/*EBX*/
		TreeItem<TreeViewEntry> ebx = new TreeItem<TreeViewEntry>(new TreeViewEntry("ebx - "+nonCas.getEbx().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (NonCasBundleEntry nonCasBundleEntry : nonCas.getEbx()){
			if (modPackage!=null){
				for (PackageEntry entry : modPackage.getEntries()){
					if (entry.getSubPackage().equalsIgnoreCase(nonCas.getBasePath())&&//mp_playground/content
							entry.getResourcePath().equalsIgnoreCase(nonCasBundleEntry.getName()+".ebx")//levels/mp_playground/content/layer2_buildings.ebx
					){
						break;
					}
				}
				//has NO mod file
			}
			String[] name = nonCasBundleEntry.getName().split("/");
			TreeViewEntry childEntry = new TreeViewEntry(name[name.length-1]+" ("+nonCasBundleEntry.getSha1()+")", new ImageView(JavaFXHandler.ICON_STRUCTURE), nonCasBundleEntry, EntryType.STRING);
			TreeItem<TreeViewEntry> child = new TreeItem<TreeViewEntry>(childEntry);
			pathToTree(ebx, nonCasBundleEntry.getName(), child);
		}
		rootnode.getChildren().add(ebx);
		
				
		/*RES*/
		TreeItem<TreeViewEntry> res = new TreeItem<TreeViewEntry>(new TreeViewEntry("res - "+nonCas.getRes().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (NonCasBundleEntry nonCasBundleEntry : nonCas.getRes()){
			String[] name = nonCasBundleEntry.getName().split("/");
			TreeViewEntry childEntry = new TreeViewEntry(name[name.length-1]+" ("+nonCasBundleEntry.getSha1()+", "+nonCasBundleEntry.getResourceType()+")", null, nonCasBundleEntry, EntryType.STRING);
			ImageView graphic = getGraphic(nonCasBundleEntry.getResourceType());
			childEntry.setGraphic(graphic);
			TreeItem<TreeViewEntry> child = new TreeItem<TreeViewEntry>(childEntry);
			pathToTree(res, nonCasBundleEntry.getName(), child);
		}
		rootnode.getChildren().add(res);
		
		/*CHUNKS*/
		TreeItem<TreeViewEntry> chunks = new TreeItem<TreeViewEntry>(new TreeViewEntry("chunks - "+nonCas.getChunks().size()+" Children", new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
		for (NonCasBundleChunk nonCasBundleChunk : nonCas.getChunks()){
			chunks.getChildren().add(new TreeItem<TreeViewEntry>(
					new TreeViewEntry(nonCasBundleChunk.getId()//+" SHA1: "+link.getSha1()+" (offset: 0x"+FileHandler.toHexInteger(link.getLogicalOffset())+" , size: 0x"+FileHandler.toHexInteger(link.getLogicalSize())+"
					,new ImageView(JavaFXHandler.ICON_INSTANCE), nonCasBundleChunk, EntryType.STRING)));
		}
		rootnode.getChildren().add(chunks);
		
		
		return rootnode;
	}
	/*END OF CONVERTED NonCasBundle*/
	
	public static void pathToTree(TreeItem<TreeViewEntry> root, String path, TreeItem<TreeViewEntry> child){
		pathToTree(root, path, child, null);
	}
	public static void pathToTree(TreeItem<TreeViewEntry> root, String path, TreeItem<TreeViewEntry> child, Color backgroundColor){
		String[] splittedPart = path.split("/");
		
		TreeItem<TreeViewEntry> parentNode = null;
		int counter = 1;
		for (String part : splittedPart){
			if (part.equals("")){
				break; //DONE
			}
			if (parentNode == null){ //FIRST RUN
				for (TreeItem<TreeViewEntry> rootChildren : root.getChildren()){
					if (rootChildren.getValue().getName().equals(part) && rootChildren.getValue().getType() == EntryType.LIST){
						parentNode = rootChildren;
						break;
					}
				}
				if (parentNode == null){
					//NO PARENT EXISTS :(
					TreeItem<TreeViewEntry> newNode = null;
					if (counter>=splittedPart.length){
						newNode = child;
					}else{
						newNode = new TreeItem<TreeViewEntry>(new TreeViewEntry(part, new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
					}
					if (backgroundColor!=null){
						newNode.getValue().setBackgroundColor(backgroundColor);
					}
					root.getChildren().add(newNode);
					parentNode = newNode;
				}
			}else{//PARENT DOES EXIST.
				boolean found = false;
				for (TreeItem<TreeViewEntry> parentChild : parentNode.getChildren()){//DOES IT HAS A EXISTING CHILD ALREADY ?
					if (parentChild.getValue().getName().equals(part) && parentChild.getValue().getType() == EntryType.LIST){
						parentNode = parentChild;
						found = true;
						break;
					}
				}
				if (found == false){
					//CHILD DOES NOT EXIST, CREATE IT!
					TreeItem<TreeViewEntry> newNode = null;
					if (counter>=splittedPart.length){
						newNode = child;
					}else{
						newNode = new TreeItem<TreeViewEntry>(new TreeViewEntry(part, new ImageView(JavaFXHandler.ICON_LIST), null, EntryType.LIST));
					}
					if (backgroundColor!=null){
						newNode.getValue().setBackgroundColor(backgroundColor);
					}
					parentNode.getChildren().add(newNode);
					parentNode = newNode;
				}
			}
			counter++;
		}
	}
	
	public static TreeItem<Entity> getTreeView(ArrayList<EntityLayer> layers){
		TreeItem<Entity> root = new TreeItem<Entity>(null);
		for (EntityLayer eL : layers){
			TreeItem<Entity> et = getTreeView(eL);
			if (et!=null){
				root.getChildren().add(et);
			}
		}
		return root;
		
	}
	
	
	public static TreeItem<Entity> getTreeView(EntityLayer layer){
		TreeItem<Entity> layerRoot = new TreeItem<Entity>(new LayerEntity(layer.getName()));
		layerRoot.setGraphic(new ImageView(JavaFXHandler.ICON_STRUCTURE));
		for (Entity e : layer.getEntities()){
			TreeItem<Entity> et = getTreeView(e);
			if (et!=null){
				layerRoot.getChildren().add(et);
			}
		}
		return layerRoot;
		
	}
	
	private static TreeItem<Entity> getTreeView(Entity entity){
		TreeItem<Entity> ent = new TreeItem<Entity>(entity);
		
		switch(entity.getType()){
			case Light:
				ent.setGraphic(new ImageView(JavaFXHandler.ICON_ASTTERISK_YELLOW));
				break;
			case Object:
				ent.setGraphic(new ImageView(JavaFXHandler.ICON_INSTANCE/*box.png*/));
				break;
		}		
		for (Entity child : entity.getChildrens()){
			TreeItem<Entity> childEnt = getTreeView(child);
			if (childEnt!=null){
				ent.getChildren().add(childEnt);
			}
		}		
		return ent;
	}
	
	public static ImageView getGraphic(ResourceType resType){
		ImageView graphic = null;
		switch (resType) {
			case CHUNK:
				graphic = new ImageView(JavaFXHandler.ICON_RAW);
				break;
			case ITEXTURE:
				graphic = new ImageView(JavaFXHandler.ICON_IMAGE);
				break;
			case LUAC:
				graphic = new ImageView(JavaFXHandler.ICON_LUA);
				break;
			case OCCLUSIONMESH:
				graphic = new ImageView(JavaFXHandler.ICON_GEOMETRY_2);
				break;
			case MESH:
				graphic = new ImageView(JavaFXHandler.ICON_GEOMETRY);
				break;
			default:
				graphic = new ImageView(JavaFXHandler.ICON_RESOURCE);
				break;
		}
		return graphic;
	}
}
