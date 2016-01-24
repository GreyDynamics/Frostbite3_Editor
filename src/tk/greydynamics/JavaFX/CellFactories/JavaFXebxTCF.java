package tk.greydynamics.JavaFX.CellFactories;

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
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXArrayRepeater;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplexDescriptor;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXEnumHelper;
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
	public JavaFXebxTCF(EBXFile ebxFile, boolean isOriginal) {
		this.ebxFile = ebxFile;
		this.isOriginal = isOriginal;
		setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton()==MouseButton.SECONDARY){
					if (getTreeItem()!=null){
						contextMenu.getItems().clear();
						if (getTreeItem().getValue() instanceof EBXField){
							EBXField ebxField = (EBXField) getTreeItem().getValue();
							if (ebxField.getType()==FieldValueType.ExternalGuid||ebxField.getType()==FieldValueType.Guid){
								contextMenu.getItems().add(follow);
							}
							if (ebxField.getType()==FieldValueType.Complex||ebxField.getType()==FieldValueType.ArrayComplex){
								//REPLACE
							}else{
								contextMenu.getItems().add(edit);
							}
							setContextMenu(contextMenu);
						}
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
					if (getTreeItem().getValue() instanceof EBXField){
						startEdit();
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
								String target = (String) (ebxField.getValue());
								if (target!=null){
									String[] targetArr = target.split(" ");
									if (targetArr.length==2){//guid has a file guid and instance guid
										ResourceHandler rs = Core.getGame().getResourceHandler();
										EBXHandler eh = rs.getEBXHandler();

										boolean readOriginal = false;
										EBXFile file = eh.getEBXFileByGUID(targetArr[0], true/*tryLoad*/, readOriginal);
										ResourceLink resLink = rs.getResourceLinkByEBXGUID(targetArr[0]);
										if (file!=null&&resLink!=null){
											Core.getJavaFXHandler().getMainWindow().createEBXWindow(file, resLink.getName(), readOriginal);
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
					Object newFieldValue = convertToObject(newValue, ebxField.getType());
					if (newFieldValue!=null){
						ebxField.setValue(newFieldValue, ebxField.getType());
					}else{
						System.err.println("EBXEditor was unable to parse modified value to the original type.");
					}
				}
			}
		}
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
						if (ebxField.getType()==FieldValueType.ArrayComplex){
							if (ebxField.getValue() instanceof EBXArrayRepeater){
								//Emty array
								setText(ebxField.getFieldDescritor().getName()+"::array (emty)"+" ( DEBUG: "+ebxField.indexDEBUG+" )");
							}else{
								setText(ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName()+" ( DEBUG: "+ebxField.indexDEBUG+" )");
							}
						}else if (ebxField.getType()==FieldValueType.Complex){
							setText(ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName()+" ( DEBUG: "+ebxField.indexDEBUG+" - "+ebxField.getFieldDescritor().getRef()+" )");
						}else{
							String fieldValue = convertToString(ebxField.getValue(), ebxField.getType());

							setText(ebxField.getFieldDescritor().getName()+": "+fieldValue+" ( DEBUG: "+ebxField.indexDEBUG+" )");
						}
					}else if (item instanceof EBXComplex){
						setText("complex");
					}else if (item instanceof EBXInstance){
						EBXInstance ebxInstance = (EBXInstance) item;
						setText(ebxInstance.getComplex().getComplexDescriptor().getName()+" "+ebxInstance.getGuid());
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
				String[] externalGUIDSplit = ((String) value).split(" ");
				EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
				if (ebxHandler.getEBXFiles()!=null&&externalGUIDSplit.length==2){//DEBUG-
					EBXFile file = ebxHandler.getEBXFileByGUID(externalGUIDSplit[0], false/*aka. don't try to load*/, false);
					if (file!=null){//Table with EBXFile
						return file.getTruePath()+" "+externalGUIDSplit[1];
					}else{//Table with ResourceLink's Name
						ResourceLink resLink = Core.getGame().getResourceHandler().getResourceLinkByEBXGUID(externalGUIDSplit[0]);
						if (resLink!=null){
							return resLink.getName()+" "+externalGUIDSplit[1];
						}
					}
				}
				return (String)value;
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

	public Object convertToObject(String value, FieldValueType type){
		try{
			EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
			if (value.equals("null")){//hasNoPayloadData! aka. undefined null
				return null;
			}else{
				switch(type){
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
					return(f);
					//			    		case DOUBLE:
					//			    			return(Double.valueOf(value));
				case Short:
					short sh = Short.valueOf(value);
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), sh, EntryType.SHORT);
					return(sh);
				case Integer:
					Integer i = Integer.valueOf(value);
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), i, EntryType.INTEGER);
					return(i);
					//			    		case LONG:
					//			    			Long lon = Long.valueOf(value);
					//			    			//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), lon, EntryType.LONG);
					//			    			return(lon);
				case UInteger:
					long l = (Long.valueOf(value))& 0xffffffffL;
					Integer ui = Integer.valueOf((int) (l&0xFFFFFFFF));
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), ui, EntryType.UINTEGER);
					return(l);
				case Byte:
					byte b = FileHandler.hexToByte(value);
					//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), b, EntryType.BYTE);
					return(b);
					//			    		case RAW:
					//			    			return(FileHandler.hexStringToByteArray(value));
				case Bool:
					if (value.equals("TRUE")){
						//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), (byte) 0x1, EntryType.BOOL);
						return true;
					}else{
						//ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), (byte) 0x0, EntryType.BOOL);
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
									return (guid+" "+split[1]);
								}
							}
						}
						System.err.println("EXTERNAL GUID PATH COULD NOT BE FOUND IN DATABASE. NO CONVERTION TO FILEGUID POSSIBLE!");
						return(null);
					}else{
						return(value);
					}
				case ChunkGuid:
					return(value);
				case Guid:
					return(value);
				default:
					System.out.println("EBX TreeCellFactory does not know what to do with "+type+" in toObject function.");
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
