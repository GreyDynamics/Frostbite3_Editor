package tk.greydynamics.JavaFX.CellFactories;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.JavaFX.Controller.EBXWindowController;
import tk.greydynamics.JavaFX.Windows.EBXWindow;
import tk.greydynamics.JavaFX.Windows.ListSelectWindow;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXArrayRepeater;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplexDescriptor;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXEnumHelper;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXExternalGUID;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFieldDescriptor;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class JavaFXebxTCF extends TreeCell<Object> {
	private TextField textField;
	private ContextMenu contextMenu = new ContextMenu();
	private MenuItem remove, follow, edit, duplicate;

	private EBXFile ebxFile;
	private boolean isOriginal;
	private EBXWindowController controller;
	
	public JavaFXebxTCF(EBXWindowController controller, EBXFile ebxFile, boolean isOriginal) {
		this.ebxFile = ebxFile;
		this.isOriginal = isOriginal;
		this.controller = controller;
		setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton()==MouseButton.SECONDARY){
					if (getTreeItem()!=null){
						contextMenu.getItems().clear();
						if (getTreeItem().getValue() instanceof EBXField){
							EBXField ebxField = (EBXField) getTreeItem().getValue();
							if (getTreeItem().getParent()!=null){
								if (getTreeItem().getParent().getValue() instanceof EBXField){
									EBXField parentField = (EBXField) getTreeItem().getParent().getValue();
									if (parentField.getType()==FieldValueType.ArrayComplex){
										contextMenu.getItems().add(duplicate);
									}
								}
							}
							
							
							if (ebxField.getType()==FieldValueType.ExternalGuid||ebxField.getType()==FieldValueType.Guid){
								contextMenu.getItems().add(follow);
							}
							if (ebxField.getType()==FieldValueType.Complex){								
								//REPLACE
							}else if (ebxField.getType()==FieldValueType.ArrayComplex){
								//REPLACE
							}else{
								contextMenu.getItems().add(edit);
							}
						}else if (getTreeItem().getValue() instanceof EBXInstance){
							EBXInstance value = (EBXInstance) getTreeItem().getValue();
							
							//If its the last instance in the file, it can have a diffrent padding and will not allow to
							//have a instance on it's ass. While this is not taken into account in the creator, there is no duplicate method for this instance.
							int instanceIndex = -1;
							ArrayList<EBXInstance> instances = ebxFile.getInstances();
							for (int i=0; i<instances.size(); i++){
								if (value==instances.get(i)){
									instanceIndex = i;
									break;
								}
							}
							if (instanceIndex<instances.size()-1&&instanceIndex!=-1){
								contextMenu.getItems().add(duplicate);
							}
						}
						setContextMenu(contextMenu);
						//System.out.println(getTreeItem().getValue().getClass());
					}
				}
			}
		});   

		//            rename = new MenuItem("Rename");
		//            rename.setGraphic(new ImageView(JavaFXHandler.ICON_PENCIL));
		//            rename.setOnAction(new EventHandler<ActionEvent>() {
		//                public void handle(ActionEvent t) {
		//                	modifyOp = Operation.Name;
		//                	startEdit();
		//                }
		//            });


		//            remove = new MenuItem("Remove");
		//            remove.setGraphic(new ImageView(JavaFXHandler.ICON_REMOVE));
		//            remove.setOnAction(new EventHandler<ActionEvent>() {
		//                public void handle(ActionEvent t) {
		//                	if (getTreeItem().getParent()!=null){
		//                		getTreeItem().getParent().getChildren().remove(getTreeItem());
		//                	}
		//                }
		//            });

		edit = new MenuItem("Edit");
		edit.setGraphic(new ImageView(JavaFXHandler.ICON_PENCIL));
		edit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if (getTreeItem()!=null){
					if (getTreeItem().getValue() instanceof EBXField && getTreeItem().getValue()!=null){
						EBXField ebxField = (EBXField) getTreeItem().getValue();
						if (ebxField.getType()==FieldValueType.Enum){
							if (ebxField.getValue() instanceof EBXEnumHelper) {
								EBXEnumHelper enumHelper = (EBXEnumHelper) ebxField.getValue();
								
//								Platform.runLater(new Runnable() {
//									@Override
//									public void run() {
										ArrayList<String> values = new ArrayList<>();
										for (EBXFieldDescriptor enumEntry : enumHelper.getEntries()){
											values.add(enumEntry.getName());
										}
										ListSelectWindow lsw = new ListSelectWindow("Enum Selection",
												"Please make a choise for the "+enumHelper.getEnumName()+" Enum!", values, 500, 500);
										//System.out.println(lsw.getSelectedString());
										if (lsw.getSelectedString()!=null){
											for (EBXFieldDescriptor enumEntry : enumHelper.getEntries()){
												if (lsw.getSelectedString().equals(enumEntry.getName())){
													enumHelper.setSelectedIndex(enumEntry.getOffset());
													break;
												}
											}
											updateItem(getTreeItem().getValue(), getTreeItem().getValue()==null);
										}
//								}});
							}else{
								System.err.println("This is not a editable Enum!");
							}
						}else if (ebxField.getType()==FieldValueType.ExternalGuid || ebxField.getType()==FieldValueType.Guid){
							if (ebxField.getValue() instanceof String) {
								String currentValue = (String) ebxField.getValue();
								if (currentValue!=null&&!currentValue.startsWith("*")){
//									Platform.runLater(new Runnable() {
//									@Override
//									public void run() {
										ArrayList<String> values = new ArrayList<>();
										if (ebxField.getType()==FieldValueType.ExternalGuid){
											for (EBXWindow ebxWindow : Core.getJavaFXHandler().getMainWindow().getEBXWindows()){
												if (ebxWindow.getEBXFile()!=null){
													if (ebxFile!=ebxWindow.getEBXFile()){
														EBXFile ebxFileFromWindow = ebxWindow.getEBXFile();
														for (EBXInstance ebxInstance : ebxFileFromWindow.getInstances()){
															values.add(ebxWindow.getName()+" "+ebxInstance.getGuid()+" ("+ebxInstance.getComplex().getComplexDescriptor().getName()+")");
														}
													}
												}
											}
											for (EBXExternalGUID externalGUID : ebxFile.getExternalGUIDs()){
												values.add(externalGUID.getFileGUID()+" "+externalGUID.getInstanceGUID());
											}
										}else{
											if (ebxFile!=null){//EBXFile from current Window.
												EBXField[] fields = null;
												if (getTreeItem().getParent()!=null){
													if (getTreeItem().getParent().getValue() instanceof EBXField&&getTreeItem().getParent().getValue()!=null){
														EBXField parentField = (EBXField) getTreeItem().getParent().getValue();
														if (parentField.getType()==FieldValueType.ArrayComplex){
															fields = parentField.getValueAsComplex().getFields();
														}
													}
												}
												for (EBXInstance ebxInstance : ebxFile.getInstances()){
													boolean found = false;
													if (fields!=null){
														for (int i=0; i<fields.length; i++){
															if (ebxInstance.getGuid().equalsIgnoreCase((String) fields[i].getValue())){
																found = true;
																break;
															}
														}
														
													}
													if (!found){
														values.add(ebxInstance.getGuid()+" ("+ebxInstance.getComplex().getComplexDescriptor().getName()+")");
													}
												}
											}
										}
										ListSelectWindow lsw = new ListSelectWindow("Guid Selection",
												"Please select a guid. (Data is from all open EBXWindows!)", values, 700, 500);
										//System.out.println(lsw.getSelectedString());
										if (lsw.getSelectedString()!=null){
											String[] split = lsw.getSelectedString().split(" ");
											if (split.length==2&&split[1].contains("(")){
												//Internal
												ebxField.setValue(split[0], ebxField.getType());
											}else if (split.length==3&&split[2].contains("(")){
												//External
												String externalGUID = (String) convertToObject(split[0]+" "+split[1], ebxField);
												if (externalGUID!=null){
													ebxField.setValue(externalGUID, ebxField.getType());
												}else{
													System.err.println("The selected data couldn't be parsed! (String to FileGUID)");
												}
											}else if (split.length==2){
												//External from currently assigned
												ebxField.setValue(split[0]+" "+split[1], ebxField.getType());
											}else{
												System.err.println("The selected data couldn't be parsed!");
											}
											updateItem(getTreeItem().getValue(), getTreeItem().getValue()==null);
										}
//								}});
								}else{
									System.err.println("This guid uses a emty payload!");
								}
							}else{
								System.err.println("This is not a editable Guid!");
							}
						}else{
							startEdit();
						}
					}
				}
			}
		});
		duplicate = new MenuItem("Duplicate");
		duplicate.setGraphic(new ImageView(JavaFXHandler.ICON_PLUS));
		duplicate.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if (getTreeItem()!=null){
					if (getTreeItem().getValue() instanceof EBXField){
						EBXField value = (EBXField) getTreeItem().getValue();
						EBXField newField = EBXField.clone(value);
						if (getTreeItem().getParent()!=null){
							if (getTreeItem().getParent().getValue() instanceof EBXField){
								EBXField parentField = (EBXField) getTreeItem().getParent().getValue();
								if (parentField.getType()==FieldValueType.Complex||parentField.getType()==FieldValueType.ArrayComplex){
									EBXComplex parentComplex = parentField.getValueAsComplex();
									parentComplex.extendFields(newField);
								}
							}else{
								System.out.println("parent not a ebxField");
							}
							//getTreeItem().getParent().getChildren().add(new TreeItem<Object>(newField));
							//updateTreeView(getTreeView());
							controller.update(ebxFile);
						}else{
							System.err.println("Can't duplicate a EBXFile! (NoParent)");
						}
					}else if (getTreeItem().getValue() instanceof EBXInstance){
						EBXInstance newInstance = EBXInstance.clone((EBXInstance) getTreeItem().getValue());
						if (newInstance.getGuid().length()>=16){
							newInstance.assignRandomGUID();
							
						//* Put below duplicated. *//
							int originalIndex = -1;
							ArrayList<EBXInstance> ebxInstances = ebxFile.getInstances();
							for (int i=0; i<ebxInstances.size(); i++){
								if ((EBXInstance) getTreeItem().getValue()==ebxInstances.get(i)){
									originalIndex = i;
								}
							}
							ArrayList<EBXInstance> instanceBufferList = new ArrayList<>();
							for (int i=0;i<originalIndex+1;i++){
								instanceBufferList.add(ebxInstances.get(i));
							}
							instanceBufferList.add(newInstance);
							for (int i=originalIndex+1;i<(ebxInstances.size());i++){
								instanceBufferList.add(ebxInstances.get(i));
							}
							ebxFile.getInstances().clear();
							for (EBXInstance instnace : instanceBufferList){
								ebxFile.getInstances().add(instnace);
							}
						//* END of Put below duplicated. *//
							
							
							controller.update(ebxFile);
							Core.getJavaFXHandler().getDialogBuilder().showInfo("INFO", "You may have to change the \"Flags\" value!");
						}else{
							System.err.println("Not implemented yet! (Duplicate Instance without public guid)");
						}
					}else{
						System.err.println("Duplicate ??");
					}
				}
			}
		});

		follow = new MenuItem("Follow");
		follow.setGraphic(new ImageView(JavaFXHandler.ICON_ARROR_RIGHT));
		follow.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				try{
					if (getTreeItem()!=null){
						if (getTreeItem().getValue() instanceof EBXField){
							EBXField ebxField = (EBXField) getTreeItem().getValue();
							if (ebxField.getType()==FieldValueType.ExternalGuid||ebxField.getType()==FieldValueType.Guid){
								String target = null;
								if (ebxField.getValue() instanceof EBXExternalGUID){
									EBXExternalGUID externalGUID = (EBXExternalGUID) ebxField.getValue();
									target = externalGUID.getFileGUID()+" "+externalGUID.getInstanceGUID();
								}else{
									target = (String) (ebxField.getValue());
								}
								if (target!=null){
									String[] targetArr = target.split(" ");
									if (targetArr.length==2){//guid has a file guid and instance guid
										ResourceHandler rs = Core.getGame().getResourceHandler();
										EBXHandler eh = rs.getEBXHandler();

										boolean readOriginal = false;
										EBXFile file = eh.getEBXFileByGUID(targetArr[0], true/*tryLoad*/, readOriginal);
										ResourceLink resLink = rs.getResourceLinkByEBXGUID(targetArr[0]);
										if (file!=null&&resLink!=null){
											Core.getJavaFXHandler().getMainWindow().createEBXWindow(null, file, resLink.getName(), readOriginal);
										}else{
											System.err.println("Link can't be followed, cuz off missing data or link.");
										}
									}else{
										System.err.println("Internal GUID's can't be followed!");
									}
								}
							}
						}else{
							System.err.println("Unsupported field to follow.");
						}
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("Invaild link to follow.");
				}
			}
		});
	}

	@Override
	public void startEdit() {
		super.startEdit();
		createTextField(getTreeItem());
		textField.selectAll();
		setText(null);
		setGraphic(textField);
	}

	public void commitEdit(String newValue) {
		if (getTreeItem()!=null){
			if (getTreeItem().getValue()!=null){
				if (getTreeItem().getValue() instanceof EBXField){
					EBXField ebxField = (EBXField) getTreeItem().getValue();
					Object newFieldValue = convertToObject(newValue, ebxField);
					if (newFieldValue!=null){
						ebxField.setValue(newFieldValue, ebxField.getType());
					}else{
						System.err.println("EBXEditor was unable to parse modified value to the original type.");
					}
				}
			}
		}
		cancelEdit();
	};    

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		updateItem(getTreeItem().getValue(), getTreeItem().getValue()==null);
	}

	@Override
	public void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				setText(null);
				setGraphic(null);
			} else {
				if (item != null){
					if (item instanceof EBXFile){
						EBXFile ebxFile = (EBXFile) item;
						setText(ebxFile.getTruePath()+" ("+ebxFile.getGuid()+")");
					}else if (item instanceof EBXField){
						EBXField ebxField = (EBXField) item;
						setGraphic(getIcon(ebxField.getType()));
//						if (ebxField.getType()==FieldValueType.ArrayComplex){
//							if (ebxField.getValue() instanceof EBXArrayRepeater){
//								//Emty array
//								setText(ebxField.getFieldDescritor().getName()+"::array (emty)"+" ( DEBUG: "+ebxField.indexDEBUG+" )");
//							}else{
//								setText(ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName()+" ( DEBUG: "+ebxField.indexDEBUG+" )");
//							}
//						}else if (ebxField.getType()==FieldValueType.Complex){
//							setText(ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName()+" ( DEBUG: "+ebxField.getValueAsComplex().getComplexDescriptor().getAlignment()+" - "+ebxField.getValueAsComplex().getComplexDescriptor().getSize()+" )");
//						}else{
//							String fieldValue = convertToString(ebxField.getValue(), ebxField.getType());
//
//							setText(ebxField.getFieldDescritor().getName()+": "+fieldValue+" ( DEBUG: "+ebxField.getFieldDescritor().getSize()+" )");
//						}
						if (ebxField.getType()==FieldValueType.ArrayComplex){
							if (ebxField.getValue() instanceof EBXArrayRepeater){
								//Emty array
								setText(ebxField.getFieldDescritor().getName()+"::array (emty)"+" ( DEBUG: "+EBXHandler.hasher(ebxField.getFieldDescritor().getName().getBytes())+" )");
							}else{
								setText(ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName()+" ( DEBUG: "+EBXHandler.hasher(ebxField.getFieldDescritor().getName().getBytes())+" )");
							}
						}else if (ebxField.getType()==FieldValueType.Complex){
							setText(ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName()+" ( DEBUG: "+EBXHandler.hasher(ebxField.getFieldDescritor().getName().getBytes())+" - "+EBXHandler.hasher(ebxField.getValueAsComplex().getComplexDescriptor().getName().getBytes())+" )");
						}else{
							String fieldValue = convertToString(ebxField.getValue(), ebxField.getType());

							setText(ebxField.getFieldDescritor().getName()+": "+fieldValue+" ( DEBUG: "+EBXHandler.hasher(ebxField.getFieldDescritor().getName().getBytes())+" )");
						}
					}else if (item instanceof EBXComplex){
						setText("complex");
					}else if (item instanceof EBXInstance){
						EBXInstance ebxInstance = (EBXInstance) item;
						setText(ebxInstance.getComplex().getComplexDescriptor().getName()+" "+ebxInstance.getGuid()+" (DEBUG: "+EBXHandler.hasher(ebxInstance.getComplex().getComplexDescriptor().getName().getBytes()));
						setGraphic(new ImageView(JavaFXHandler.ICON_LIST));
					}else{
						//ERROR
						setText(item.getClass().toString());
					}
				}else{
					setText(null);
				}
			}
		}
	}
	private void createTextField(TreeItem<Object> treeItem) {
		textField = new TextField("");
		if (treeItem!=null){
			if (treeItem.getValue()!=null){
				if (treeItem.getValue() instanceof EBXField){
					EBXField ebxField = (EBXField) treeItem.getValue();
					String fieldValue = convertToString(ebxField.getValue(), ebxField.getType());
					if (fieldValue!=null){
						textField.setText(fieldValue);
					}else{
						//ERROR
					}
				}
				textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent t) {
						if (t.getCode() == KeyCode.ENTER) {
							commitEdit(textField.getText());
						} else if (t.getCode() == KeyCode.ESCAPE) {
							cancelEdit();
						}
					}
				});
			}
		}
	}

	public String convertToString(Object value, FieldValueType type){
		if (value!=null){
			switch(type){
			case String:
				return (String)value;
			case ExternalGuid:
				EBXExternalGUID externalGUID = (EBXExternalGUID) value;
				EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
				if (ebxHandler.getEBXFiles()!=null){//DEBUG-
					EBXFile file = ebxHandler.getEBXFileByGUID(externalGUID.getFileGUID(), false/*aka. don't try to load*/, false);
					if (file!=null){//Table with EBXFile
						return file.getTruePath()+" "+externalGUID.getInstanceGUID();
					}else{//Table with ResourceLink's Name
						ResourceLink resLink = Core.getGame().getResourceHandler().getResourceLinkByEBXGUID(externalGUID.getFileGUID());
						if (resLink!=null){
							return resLink.getName()+" "+externalGUID.getInstanceGUID();
						}
					}
				}
				return externalGUID.getFileGUID()+" "+externalGUID.getInstanceGUID();
			case Float:
				return ((Float)value).toString()+"f";
			case Short:
				return ((Short)value).toString();
			case Integer:
				return ((Integer)value).toString();
			case UInteger:
				return ((Long)value).toString();
				//		    		case LONG:
				//		    			return ((Long)value).toString();
				//		    		case ARRAY:
				//		    			return item.getName();
				//		    		case LIST:
				//		    			return item.getName();
			case Bool:
				if (((Boolean)value)==true){
					return "TRUE";
				}else{
					return "FALSE";
				}
			case Hex8:
				return (String)value;
			case Byte:
				return FileHandler.byteToHex(((Byte)value));
			case Enum:
				String enumValue = null;
				if (value instanceof EBXComplexDescriptor){
					enumValue = "*null enum*";
				}else if (value instanceof EBXEnumHelper){
					EBXEnumHelper enumHelper = (EBXEnumHelper) value;
					for (EBXFieldDescriptor desc : enumHelper.getEntries()){
						if (desc.getOffset()==enumHelper.getSelectedIndex()){
							enumValue = desc.getName();
							break;
						}
					}
				}
				return enumValue;
				//		    		case RAW:
				//		    			return FileHandler.bytesToHex((byte[]) value);
				//		    		case NULL:
				//		    			return ("NULL"); //DEFINED NULL
			case Guid:
				return (String)value;
			case ChunkGuid:
				return (String)value;
			default:
				return type+" NOT CONVERTED!";
			}
		}
		return null;
	}

	public Object convertToObject(String value, EBXField field){
		try{
			EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
			if (value.equals("null")){//hasNoPayloadData! aka. undefined null
				return null;
			}else{
				byte[] tempValue = null;
				switch(field.getType()){
				case String:
					return(value);
					//			    		case Enum:
					//			    			return;
				case Hex8:
					return(value);
					//			    		case LIST:
					//			    			return(value);
					//			    		case ARRAY:
					//			    			return(value);
				case Float:
					float f = Float.valueOf(value);
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), f, EntryType.FLOAT);
					
					tempValue = FileHandler.toBytes((float) f);
					FileHandler.addBytes(tempValue, controller.getOriginalBytes(), field.getOffset());
					
					return(f);
					//			    		case DOUBLE:
					//			    			return(Double.valueOf(value));
				case Short:
					short sh = Short.valueOf(value);
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), sh, EntryType.SHORT);

					tempValue = FileHandler.toBytes((short) sh, ebxFile.getByteOrder());
					FileHandler.addBytes(tempValue, controller.getOriginalBytes(), field.getOffset());
					
					return(sh);
				case Integer:
					Integer i = Integer.valueOf(value);
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), i, EntryType.INTEGER);
					
					tempValue = FileHandler.toBytes((int) i, ebxFile.getByteOrder());
					FileHandler.addBytes(tempValue, controller.getOriginalBytes(), field.getOffset());
					
					return(i);
					//			    		case LONG:
					//			    			Long lon = Long.valueOf(value);
					//			    			//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), lon, EntryType.LONG);
					//			    			return(lon);
				case UInteger:
					long l = (Long.valueOf(value))& 0xffffffffL;
					Integer ui = Integer.valueOf((int) (l&0xFFFFFFFF));
					
					tempValue = FileHandler.toBytes((int) ui, ebxFile.getByteOrder());
					FileHandler.addBytes(tempValue, controller.getOriginalBytes(), field.getOffset());
					
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), ui, EntryType.UINTEGER);
					return(l);
				case Byte:
					byte b = FileHandler.hexToByte(value);
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), b, EntryType.BYTE);

					FileHandler.addBytes(new byte[]{b}, controller.getOriginalBytes(), field.getOffset());
					
					return(b);
					//			    		case RAW:
					//			    			return(FileHandler.hexStringToByteArray(value));
				case Bool:
					if (value.equals("TRUE")){
						//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), (byte) 0x1, EntryType.BOOL);

						FileHandler.addBytes(new byte[]{0x01}, controller.getOriginalBytes(), field.getOffset());
						
						return true;
					}else{
						//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), (byte) 0x0, EntryType.BOOL);
						
						FileHandler.addBytes(new byte[]{0x00}, controller.getOriginalBytes(), field.getOffset());
						return false;
					}
					//			    		case NULL:
					//			    			return("NULL"); //DEFINED NULL ("NULL")
				case ExternalGuid:
					if (value.contains("/")){
						String[] split = value.split(" ");			    				
						if (split.length==2){
							EBXFile file = ebxHandler.getEBXFileByTrueFileName(split[0]);//Table with EBXFile
							if (file!=null){
								return (file.getGuid()+" "+split[1]);
							}else{
								String guid = ebxHandler.getEBXGUIDByResourceName(split[0]);
								if (guid!=null){
									//return (guid+" "+split[1]);
									return new EBXExternalGUID(guid, split[1]);
								}
							}
						}
						System.err.println("EXTERNAL GUID PATH COULD NOT BE FOUND IN DATABASE. NO CONVERTION TO FILEGUID POSSIBLE!");
						return(null);
					}else{
						String[] split = value.split(" ");			    				
						if (split.length==2){
							return new EBXExternalGUID(split[0], split[1]);
						}
						return null;
					}
				case ChunkGuid:
					return(value);
				case Guid:
					return(value);
				default:
					System.out.println("EBX TreeCellFactory does not know what to do with "+field.getType()+" in toObject function.");
					return null; //UNDEFINED NULL ("null")
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static ImageView getIcon(FieldValueType type){
		switch (type){
		case ArrayComplex:
			return new ImageView(JavaFXHandler.ICON_ARRAY);
		case Bool:
			return new ImageView(JavaFXHandler.ICON_BOOL);
		case Byte:
			return new ImageView(JavaFXHandler.ICON_BYTE);
		case ChunkGuid:
			return new ImageView(JavaFXHandler.ICON_DISK);
		case Complex:
			return new ImageView(JavaFXHandler.ICON_INSTANCE);
		case Enum:
			return new ImageView(JavaFXHandler.ICON_EDIT_LIST);
		case ExternalGuid:
			return new ImageView(JavaFXHandler.ICON_DOCUMENT);
		case Float:
			return new ImageView(JavaFXHandler.ICON_FLOAT);
		case Guid:
			return new ImageView(JavaFXHandler.ICON_HASH);
		case Hex8:
			return new ImageView(JavaFXHandler.ICON_AMP);
		case Integer:
			return new ImageView(JavaFXHandler.ICON_INTEGER);
		case Short:
			return new ImageView(JavaFXHandler.ICON_SHORT);
		case String:
			return new ImageView(JavaFXHandler.ICON_TEXT);
		case UInteger:
			return new ImageView(JavaFXHandler.ICON_INTEGER_UNSIGNED);
		default:
			return null;        	
		}
	}



}
