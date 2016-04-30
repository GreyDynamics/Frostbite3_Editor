package tk.greydynamics.Game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.Entities.InstanceEntity;
import tk.greydynamics.Player.PlayerEntity;
import tk.greydynamics.Player.PlayerHandler;
import tk.greydynamics.Render.GizmoHandler;
import tk.greydynamics.Render.GizmoHandler.GizmoType;
import tk.greydynamics.Resource.Frostbite3.EBX.EBXLinearTransform;

public class InputHandler {
	
	public float speedMultipShift = 1f;
	
	public void listen() {
//		System.out.println(speedMultipShift);
		Entity en = Core.getGame().getEntityHandler().getObjectEntityPicker().getEntityPICKED();
		GizmoHandler gizmoHandler = Core.getGame().getEntityHandler().getGizmoHandler();
		speedMultipShift += ((float) Mouse.getDWheel()/1500);
		if (speedMultipShift<=0.0001f){
	    	speedMultipShift = 0.0001f;
	    }
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
	    {
			speedMultipShift = 0.1f;
	    }
		PlayerHandler pl = Core.getGame().getPlayerHandler();
		PlayerEntity pe = pl.getPlayerEntity();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
	    {
	        pe.velZ -= 0.1f*speedMultipShift;
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
	    {
	    	pe.velZ += 0.1f*speedMultipShift;
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left
	    {
	    	pe.velX -= 0.1f*speedMultipShift;
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right
	    {
	    	pe.velX += 0.1f*speedMultipShift;
	    }
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))//move up
	    {
	    	pe.velY += pe.jumpStrength*speedMultipShift;
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))//move down
	    {
	    	pe.velY -= pe.jumpStrength*speedMultipShift;
	    }
	    
	    if (Mouse.isButtonDown(2)){//middle click
	    	System.err.println("Middle mouse click TODO!");
	    }
	    
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_ADD))
	    {
	    	Core.getRender().updateProjectionMatrix(Core.FOV+1, Core.DISPLAY_WIDTH, Core.DISPLAY_HEIGHT, Core.zNear, Core.zFar);
	    }
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT))
	    {
	    	Core.getRender().updateProjectionMatrix(Core.FOV-1, Core.DISPLAY_WIDTH, Core.DISPLAY_HEIGHT, Core.zNear, Core.zFar);
	    }
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0))
	    {
	    	Core.getGame().getEntityHandler().clear();
	    }
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1))
	    {
	    	System.err.println("Reloading Terrain DEBUG!");
	    	Core.getGame().getTerrainHandler().getTerrainList().clear();
	    	Core.getGame().getTerrainHandler().generate(0, 0);
	    	Core.getGame().getTerrainHandler().distance=10;
	    }
	    
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))//KEY_ESCAPE down
	    {
	    	Mouse.setGrabbed(false);
	    	Core.DISPLAY_RATE=24;
	    }
	    
	    if (Mouse.isButtonDown(1)&&!Mouse.isGrabbed()){//Right Mouse
	    	Core.DISPLAY_RATE=60;
	    	Mouse.setGrabbed(true);
	    }
	    
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_R)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_T))//Object Left
	    {
	    	if (en!=null){
	    		Vector2f relCoords = pe.moveLeft(speedMultipShift);
	    		en.changePosition(new Vector3f(relCoords.x, 0, -relCoords.y), false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_R)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_T))//Object Right
	    {
	    	if (en!=null){
	    		Vector2f relCoords = pe.moveRight(speedMultipShift);
	    		en.changePosition(new Vector3f(relCoords.x, 0, -relCoords.y), false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_UP)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_R)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_T))//Object Forward
	    {
	    	if (en!=null){
	    		Vector2f relCoords = pe.moveForward(speedMultipShift);
	    		en.changePosition(new Vector3f(relCoords.x, 0, -relCoords.y), false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_R)&&
	    		!Keyboard.isKeyDown(Keyboard.KEY_T))//Object Backwards
	    {
	    	if (en!=null){
	    		Vector2f relCoords = pe.moveBackwards(speedMultipShift);
	    		en.changePosition(new Vector3f(-relCoords.x, 0, relCoords.y), false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_T)&&Keyboard.isKeyDown(Keyboard.KEY_UP))//Object Up
	    {
	    	if (en!=null){
	    		Vector3f rel = new Vector3f(0f, speedMultipShift, 0f);
	    		pe.changePosition(rel);
	    		en.changePosition(rel, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_T)&&Keyboard.isKeyDown(Keyboard.KEY_DOWN))//Object Down
	    {
	    	if (en!=null){
	    		Vector3f rel = new Vector3f(0f, -speedMultipShift, 0f);
	    		pe.changePosition(rel);
	    		en.changePosition(rel, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_R)){
	    	gizmoHandler.setCurrentGizmoType(GizmoType.GIZMO_ROTATE);
	    }else if (Keyboard.isKeyDown(Keyboard.KEY_T)){
	    	gizmoHandler.setCurrentGizmoType(GizmoType.GIZMO_SCALE);
	    }else if (Keyboard.isKeyDown(Keyboard.KEY_E)){
	    	gizmoHandler.setCurrentGizmoType(GizmoType.GIZMO_MOVE);
	    }
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_R)&&Keyboard.isKeyDown(Keyboard.KEY_DOWN))//Rotate Neg X
	    {
	    	if (en!=null){
	    		en.changeRotation(-speedMultipShift*0.01f, 0f, 0f, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_R)&&Keyboard.isKeyDown(Keyboard.KEY_UP))//Rotate Pos X
	    {
	    	if (en!=null){
	    		en.changeRotation(speedMultipShift*0.01f, 0f, 0f, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_R)&&Keyboard.isKeyDown(Keyboard.KEY_LEFT))//Rotate Neg Z
	    {
	    	if (en!=null){
	    		en.changeRotation(0f, 0f, -speedMultipShift*0.01f, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_R)&&Keyboard.isKeyDown(Keyboard.KEY_RIGHT))//Rotate Pos Z
	    {
	    	if (en!=null){
	    		en.changeRotation(0f, 0f, speedMultipShift*0.01f, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_T)&&Keyboard.isKeyDown(Keyboard.KEY_LEFT))//Scale Neg XYZ
	    {
	    	if (en!=null){
	    		en.changeScaleing(-speedMultipShift*0.01f, -speedMultipShift*0.01f, -speedMultipShift*0.01f, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_T)&&Keyboard.isKeyDown(Keyboard.KEY_RIGHT))//Scale Pos XYZ
	    {
	    	if (en!=null){
	    		en.changeScaleing(speedMultipShift*0.01f, speedMultipShift*0.01f, speedMultipShift*0.01f, false);
	    	}
	    }
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5))//Reset Object
	    {
	    	if (en!=null){
	    		en.setScaleing(new Vector3f(1f, 1f, 1f), false);
	    		en.setRotation(new Vector3f(), false);
	    	}
	    }
	    
	    
	    if (Keyboard.isKeyDown(Keyboard.KEY_Q))
	    {
	    	if (en!=null){
	    		en.changeRotation(0f, -speedMultipShift*0.01f, 0f, false);
	    	}
	    }
	    if (Keyboard.isKeyDown(Keyboard.KEY_E))
	    {
	    	if (en!=null){
	    		en.changeRotation(0f, speedMultipShift*0.01f, 0f, false);
	    	}
	    }
	}
}
