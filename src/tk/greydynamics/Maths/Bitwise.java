package tk.greydynamics.Maths;

import org.lwjgl.util.vector.Vector2f;

public class Bitwise {
	public static int OR(int first, int next, int target){
		
		target = first;
		target |= next;
		return target;
	}
	
	public static Vector2f split1v7(int num){
		Vector2f vec = new Vector2f();
		// #0x7A945CF1 => (7, 0xA945CF1)
		
		vec.x = num>>28&0xf; //3x 1.Byte + 4 Bits
		vec.y = num&0xfffffff;//cut out (3) + 1/2 Bytes from right
		
		
		//System.out.println("split1v7 "+vec.x+" "+vec.y);
		return vec;
	}
	
	
}
