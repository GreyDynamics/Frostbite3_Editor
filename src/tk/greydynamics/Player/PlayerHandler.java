package tk.greydynamics.Player;


public class PlayerHandler {
	public PlayerEntity pe;
	
	public PlayerHandler() {
		resetPlayer();
	}
	
	public void resetPlayer(){
		pe = new PlayerEntity(0, 0, 0);
	}
	
	public void update(){		
		//Player Logic		
		pe.onGround = false;
		
		
		if (pe.posY < 0.0f){
			pe.onGround = true;
		}
		
	}
	
	public PlayerEntity getPlayerEntity(){
		return pe;
	}
}
