package tk.greydynamics.Resource.Frostbite3.EBX;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class EBXGUIDHandler {
	public HashMap<String, String> guids;
	
	public EBXGUIDHandler(String guidTablePath){
		guids = new HashMap<String, String>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(guidTablePath));
	        String line = "";
	        while ((line = in.readLine()) != null) {
	            String parts[] = line.split(" : ");
	            guids.put(parts[0].toUpperCase(), parts[1]);
	        }
	        in.close();
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("Could not parse GUIDTable in handler: "+guidTablePath);
		}
		
	}
	
	public String getFileName(String guid){
		return guids.get(guid.toUpperCase());
	}
}
