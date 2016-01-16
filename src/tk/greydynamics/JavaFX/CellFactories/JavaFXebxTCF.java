package tk.greydynamics.JavaFX.CellFactories;

import java.util.HashMap;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXArrayRepeater;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXComplex;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXField;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFieldDescriptor;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXFile;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXInstance;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXHandler.FieldValueType;

public class JavaFXebxTCF extends TreeCell<Object> {
		private enum Operation {Name, Value};
		private Operation modifyOp;
        private TextField textField;
        private ContextMenu contextMenu = new ContextMenu();
        private MenuItem remove, rename, follow;
        
        private EBXFile ebxFile;
        private boolean isOriginal;
        public JavaFXebxTCF(EBXFile ebxFile, boolean isOriginal) {
        	this.ebxFile = ebxFile;
        	this.isOriginal = isOriginal;
        	
                      
            
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
            
//            follow = new MenuItem("Follow");
//            follow.setGraphic(new ImageView(JavaFXHandler.ICON_ARROR_RIGHT));
//            follow.setOnAction(new EventHandler<ActionEvent>() {
//                public void handle(ActionEvent t) {
//                	try{
//                		String target = ((String)getTreeItem().getValue().getValue());
//                		if (target!=null){
//                			String[] targetArr = target.split(" ");
//    	                	if (targetArr.length==2){//guid has a file guid and instance guid
//    	                		ResourceHandler rs = Core.getGame().getResourceHandler();
//    	                		EBXHandler eh = rs.getEBXHandler();
//    	                		
//    	                		boolean readOriginal = false;
//    	                		EBXFile file = eh.getEBXFileByGUID(targetArr[0], true/*tryLoad*/, readOriginal);
//    	                		ResourceLink resLink = rs.getResourceLinkByEBXGUID(targetArr[0]);
//    							if (file!=null&&resLink!=null){
//    								Core.getJavaFXHandler().getMainWindow().createEBXWindow(file, resLink.getName(), readOriginal);
//    							}else{
//    								System.err.println("Link can't be followed, cuz off missing data or link.");
//    							}
//    						}else{
//    	                		System.err.println("Internal GUID's can't be followed!");
//    	                	}
//	                	}
//                	}catch (Exception e){
//                		System.out.println("Invaild link to follow.");
//                	}
//                }
//            });
        }
       
//        @Override
//        public void startEdit() {
//            super.startEdit();
//            if (modifyOp == Operation.Name){
//            	createTextField(getTreeItem());
//		        setText(null);
//		        setGraphic(textField);
//		        textField.selectAll();
//            }else if (modifyOp == null){
//            	TreeViewEntry entry = getTreeItem().getValue();
//    	        if (entry.getType()==EntryType.BOOL){
//    	        	Object bv = null;
//    	        	if ((boolean) entry.getValue()){
//    	        		bv = convertToObject("FALSE", entry);
//    	        	}else{
//    	        		bv = convertToObject("TRUE", entry);
//    	        	}
//    	        	entry.setValue(bv);
//    	        	commitEdit(getTreeItem().getValue());
//    	        }else if(entry.getType()==EntryType.ENUM&&entry.getValue() instanceof HashMap<?,?>){
//    	        	HashMap<EBXFieldDescriptor, Boolean> enums = (HashMap<EBXFieldDescriptor, Boolean>) entry.getValue();
//	    			Boolean done = false;
//	    			Boolean selectNext = false;
//	    			for (EBXFieldDescriptor desc : enums.keySet()){
//	    				boolean selected = enums.get(desc);
//	    				if (selected){
//	    					enums.put(desc, false);//TODO workaround to keep index
//	    					System.err.println("TODO: workaround to keep index");
//	    					selectNext = true;
//	    				}else if (selectNext){
//	    					enums.put(desc, true);//TODO workaround to keep index
//	    					selectNext = false;
//	    					done = true;
//	    				}
//	    			}
//	    			if (!done){
//	    				for (EBXFieldDescriptor desc : enums.keySet()){
//	    					enums.put(desc, true);
//		    				break;
//		    			}
//	    			}
//    	        }else{
//    	        	//if (textField == null) { //USELESS ?
//    		        createTextField(getTreeItem());
//    		        //}
//    		        setText(null);
//    		        setGraphic(textField);
//    		        textField.selectAll();
//    	        }
//            }
//        }
         
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
	            			if (getTreeItem().getGraphic()==null){
	            				getTreeItem().setGraphic(getIcon(ebxField.getType()));
	            			}
	            			if (ebxField.getType()==FieldValueType.ArrayComplex){
	            				if (ebxField.getValue() instanceof EBXArrayRepeater){
	            					//Emty array
	            					setText(ebxField.indexDEBUG+" - "+ebxField.getFieldDescritor().getName()+"::array (emty)");
	            				}else{
	            					setText(ebxField.indexDEBUG+" - "+ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName());
	            				}
	            			}else if (ebxField.getType()==FieldValueType.Complex){
	            				setText(ebxField.indexDEBUG+" - "+ebxField.getFieldDescritor().getName()+"::"+ebxField.getValueAsComplex().getComplexDescriptor().getName()+" - "+ebxField.getFieldDescritor().getRef());
	            			}else{
	            				String fieldValue = convertToString(ebxField.getValue(), ebxField.getType());

	            				setText(ebxField.indexDEBUG+" - "+ebxField.getFieldDescritor().getName()+": "+fieldValue);
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
        
//        private void createTextField(TreeItem<Object> treeItem) {
//        	if (modifyOp == Operation.Name){
//        		textField = new TextField(treeItem.getValue().getName());
//        	}else{
//        		textField = new TextField(convertToString(treeItem.getValue()));
//        	}
//            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
// 
//                @Override
//                public void handle(KeyEvent t) {
//                    if (t.getCode() == KeyCode.ENTER) {
//                    	if (modifyOp == Operation.Name){
//	                    	treeItem.getValue().setName(textField.getText());
//	                        commitEdit(treeItem.getValue());
//	                        modifyOp = null;
//                    	}else if(modifyOp == null){
//                    		Object obj = convertToObject(textField.getText(), treeItem.getValue());
//	                    	if (obj != null && (treeItem.getValue().getType() == EntryType.ARRAY) || treeItem.getValue().getType() == EntryType.LIST){
//	                    		treeItem.getValue().setName((String) obj);
//	                            commitEdit(treeItem.getValue());
//	                            modifyOp = null;
//	                    	}else if (obj != null){
//	                    		treeItem.getValue().setValue(obj);
//	                            commitEdit(treeItem.getValue());
//	                            modifyOp = null;
//	                    	}else{
//	                    		modifyOp = null;
//	                    		cancelEdit();
//	                    	}
//                    	}
//                    } else if (t.getCode() == KeyCode.ESCAPE) {
//                    	modifyOp = null;
//                        cancelEdit();
//                    }
//                }
//            });  
//            
//        }
        
        public String convertToString(Object value, FieldValueType type){
        	if (value!=null){
        		switch(type){
		    		case String:
		    			return (String)value;
//		    		case SHA1:
//		    			return (String)value;
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
		    			return byteToHex(((Byte)value));
//		    		case Enum:
//		    			HashMap<EBXFieldDescriptor, Boolean> enums = (HashMap<EBXFieldDescriptor, Boolean>) value;
//		    			String valuee = "null";
//		    			for (EBXFieldDescriptor desc : enums.keySet()){
//		    				boolean selected = enums.get(desc);
//		    				if (selected){
//		    					valuee = desc.getName();
//		    					break;
//		    				}
//		    			}
//		    			return valuee;
//		    		case RAW:
//		    			return FileHandler.bytesToHex((byte[]) value);
//		    		case NULL:
//		    			return ("NULL"); //DEFINED NULL
		    		case Guid:
		    			String fileGUIDName = null;
		    			String[] split = ((String)value).split(" ");
//						if (ebxHandler.getEBXFiles()!=null&&split.length==2){//DEBUG-
//							EBXFile file = ebxHandler.getEBXFileByGUID(split[0], false/*aka. don't try to load*/, false);
//							if (file!=null){//Table with EBXFile
//								return file.getTruePath()+" "+split[1];
//							}else{//Table with ResourceLink's Name
//								ResourceLink resLink = rs.getResourceLinkByEBXGUID(split[0]);
//								if (resLink!=null){
//									return resLink.getName()+" "+split[1];
//								}
//							}
//						}
						return (String)value;
		    		case ChunkGuid:
		    			return (String)value;
		    		default:
		    			return "NOT CONVERTED, ERROR!";
	        	}
        	}
        	return null;
        }
//        
//        public Object convertToObject(String value, TreeViewEntry item){
//        	try{
//        		EBXHandler ebxHandler = Core.getGame().getResourceHandler().getEBXHandler();
//        		if (value.equals("null")){//hasNoPayloadData! aka. undefined null
//        			return null;
//        		}else{
//		        	switch(item.getType()){
//			    		case STRING:
//			    			return(value);
//			    		case ENUM:
//			    			return(item.getValue()/*RETURNS STRING(if null) OR HASHMAP*/);
//			    		case HEX8:
//			    			return(value);
//			    		case LIST:
//			    			return(value);
//			    		case ARRAY:
//			    			return(value);
//			    		case FLOAT:
//			    			float f = Float.valueOf(value);
//			    			ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), f, EntryType.FLOAT);
//			    			return(f);
//			    		case DOUBLE:
//			    			return(Double.valueOf(value));
//			    		case SHORT:
//			    			short sh = Short.valueOf(value);
//			    			ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), sh, EntryType.SHORT);
//			    			return(sh);
//			    		case INTEGER:
//			    			Integer i = Integer.valueOf(value);
//			    			ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), i, EntryType.INTEGER);
//			    			return(i);
//			    		case LONG:
//			    			Long lon = Long.valueOf(value);
//			    			ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), lon, EntryType.LONG);
//			    			return(lon);
//			    		case UINTEGER:
//			    			long l = (Long.valueOf(value))& 0xffffffffL;
//			    			Integer ui = Integer.valueOf((int) (l&0xFFFFFFFF));
//			    			ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), ui, EntryType.UINTEGER);
//			    			return(l);
//			    		case BYTE:
//			    			byte b = hexToByte(value);
//			    			ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), b, EntryType.BYTE);
//			    			return(b);
//			    		case RAW:
//			    			return(FileHandler.hexStringToByteArray(value));
//			    		case BOOL:
//			    			if (value.equals("TRUE")){
//			    				ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), (byte) 0x1, EntryType.BOOL);
//			    				return true;
//			    			}else{
//			    				ebxHandler.getModifyHandler().addChange(ebxFile.getGuid(), ebxFile.getByteOrder(), isOriginal, item.getOffset(), (byte) 0x0, EntryType.BOOL);
//			    				return false;
//			    			}
//			    		case NULL:
//			    			return("NULL"); //DEFINED NULL ("NULL")
//			    		case GUID:
//			    			if (value.contains("/")){
//			    				String[] split = value.split(" ");			    				
//			    				if (split.length==2){
//				    				EBXFile file = ebxHandler.getEBXFileByTrueFileName(split[0]);//Table with EBXFile
//			    					if (file!=null){
//			    						return (file.getGuid()+" "+split[1]);
//			    					}else{
//			    						String guid = ebxHandler.getEBXGUIDByResourceName(split[0]);
//			    						if (guid!=null){
//			    							return (guid+" "+split[1]);
//			    						}
//			    					}
//								}
//			    				System.err.println("EXTERNAL GUID PATH COULD NOT BE FOUND IN DATABASE. NO CONVERTION TO FILEGUID POSSIBLE!");
//			    				return("ERROR");
//			    			}else{
//			    				return(value);
//			    			}
//			    		case CHUNKGUID:
//			    			return(value);
//			    		case SHA1:
//			    			return(value);
//						default:
//							return null; //UNDEFINED NULL ("null")
//		        	}
//        		}
//        	}catch(Exception e){
//        		e.printStackTrace();
//        		System.err.println("Couldn't not parse entry with name "+item.getName()+" in JavaFXTreeCellFactory!");
//        		return null;
//        	}
//        }
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
					return new ImageView(JavaFXHandler.ICON_AMP);
				case Float:
					return new ImageView(JavaFXHandler.ICON_FLOAT);
				case Guid:
					return new ImageView(JavaFXHandler.ICON_AMP);
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
        
        byte hexToByte(String s) {
    	    return (byte)((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1), 16));
    	}
        
    	String byteToHex(byte in) {
    		return String.format("%02x", in).toUpperCase();
    	}  	
    	
}
