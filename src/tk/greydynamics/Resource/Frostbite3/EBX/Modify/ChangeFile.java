package tk.greydynamics.Resource.Frostbite3.EBX.Modify;

import java.nio.ByteOrder;
import java.util.ArrayList;

import tk.greydynamics.Game.Core;
import tk.greydynamics.JavaFX.Windows.MainWindow.EntryType;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Resource.FileHandler;
import tk.greydynamics.Resource.ResourceHandler.LinkBundleType;
import tk.greydynamics.Resource.ResourceHandler.ResourceType;
import tk.greydynamics.Resource.Frostbite3.Toc.ResourceLink;

public class ChangeFile {
	
	private ArrayList<ChangeEntry> entries = new ArrayList<>();
	
	private String ebxFileGUID;
	private boolean isOriginal;
	private ByteOrder order;
	
	public boolean addChange(Object obj, EntryType type, int offset){
		for (ChangeEntry ce : entries){
			if (ce.getOffset()==offset){
				ce.setValue(obj);
				return true;
			}
		}
		entries.add(new ChangeEntry(obj, type, offset));
		return true;
	}
	
	public boolean applyChanges(boolean delteAfter){
		byte[] temp = null;
		byte[] tempValue = null;
		String tempValueAsString = null;
		boolean success = true;
		temp = Core.getGame().getResourceHandler().getEBXHandler().getEBXFileBytesByGUID(ebxFileGUID, isOriginal);
		System.out.println("Trying to Apply Changes to EBX ("+ebxFileGUID+") with ByteOrder "+order);
		if (temp!=null){
			for (ChangeEntry ce : entries){
				switch (ce.getType()){
					case INTEGER:
						tempValue = FileHandler.toBytes((int) ce.getValue(), order);
						tempValueAsString=""+(Integer) (ce.getValue());
						break;
					case LONG:
						tempValue = FileHandler.toBytes((long) ce.getValue(), order);
						tempValueAsString=""+(long) (ce.getValue());
						break;
					case FLOAT:
						tempValue = FileHandler.toBytes((float) ce.getValue());
						tempValueAsString=""+(float) (ce.getValue());
						break;
					case UINTEGER:
						tempValue = FileHandler.toBytes((int) ce.getValue(), order);
						tempValueAsString=""+(int) (ce.getValue());
						break;
					case SHORT:
						tempValue = FileHandler.toBytes((short) ce.getValue(), order);
						tempValueAsString=""+(short) (ce.getValue());
						break;
					case BOOL:
						tempValue = new byte[] {(byte) ce.getValue()};
						tempValueAsString=""+(byte) (ce.getValue());
						break;
					case BYTE:
						tempValue = new byte[] {(byte) ce.getValue()};
						tempValueAsString=""+(byte) (ce.getValue());
						break;
					default:
						tempValueAsString = null;
						tempValue = null;
						System.err.println("Type "+ce.getType()+" is currently not supported to be overriden!");
						success = false;
				}
				if (tempValue!=null&&tempValueAsString!=null){
					System.out.println("Going for "+ce.getType()+"("+tempValueAsString+") at 0x"+FileHandler.toHexInteger(ce.getOffset(), ByteOrder.BIG_ENDIAN)+"!");
					if (!FileHandler.overrideBytes(tempValue, temp, ce.getOffset())){
						success = false;
					}
				}
			}
			ResourceLink resLink = Core.getGame().getResourceHandler().getResourceLinkByEBXGUID(ebxFileGUID);
			if (resLink==null){
				System.err.println("Not able to apply changes for EBXFile with guid "+ebxFileGUID+", no ResourceLink found!");
				success = false;
			}else{
				Core.getModTools().extendCurrentPackage(LinkBundleType.BUNDLES,
						Core.getGame().getCurrentBundle().getBasePath(), 
						ResourceType.EBX,
						resLink.getName()+".ebx");
				
				FileHandler.writeFile(Core.getGame().getCurrentMod().getPath()+ModTools.FOLDER_RESOURCE+resLink.getName()+".ebx", temp);
			}
			
			//This will be moved over into main save.
			Core.getModTools().writePackages();
		}else{
			System.err.println("Not able to apply changes for file with guid "+ebxFileGUID+", file not found.");
			return false;
		}
		if (delteAfter){
			Core.getGame().getResourceHandler().getEBXHandler().getModifyHandler().getFiles().remove(this);
		}
		return success;
	}
	
	public ChangeFile(String ebxFileGUID, ByteOrder order, boolean isOriginal) {
		this.ebxFileGUID = ebxFileGUID;
		this.order = order;
		this.isOriginal = isOriginal;
	}
	
	public ArrayList<ChangeEntry> getEntries() {
		return entries;
	}
	public String getEbxFileGUID() {
		return ebxFileGUID;
	}
	public boolean isOriginalFile() {
		return isOriginal;
	}

	public ByteOrder getByteOrder() {
		return order;
	}
	
	
	
	
	
}
