package tk.greydynamics.Maths;

import java.security.MessageDigest;

import tk.greydynamics.Resource.FileHandler;

public class Hash {
	public static String getSHA1(String text){
		try{
	        MessageDigest md;
	        md = MessageDigest.getInstance("SHA-1");
	        byte[] sha1hash = new byte[20];
	        md.update(text.getBytes("UTF-8"), 0, text.length());
	        sha1hash = md.digest();
	     
	        return FileHandler.bytesToHex(sha1hash);
	    }catch(Exception e){
	    	return null;
	    }
	}
}
