package tk.greydynamics.Event;

import java.util.ArrayList;

import tk.greydynamics.Game.Core;

public class EventHandler {
	ArrayList<Event> eventList = new ArrayList<Event>();
	ArrayList<Event> removeList = new ArrayList<Event>();
	
	public void listen(){
		try{
			for(Event e : removeList){
				removeEvent(e);
			}
			
			for(Event e : eventList){
				try{
					if (Core.currentTick%e.tick==0){
						e.runnable.run();
						e.count--;
						if (e.count<=0&&e.count>-1){
							addRemoveList(e);
						}
					}
				}catch (Exception ex){	
				}
			}
		}catch(Exception e){
		}
	}
		
	public void addEvent(Event e){
		eventList.add(e);
	}
	
	public void removeEvent(Event e){
		eventList.remove(e);
	}
	
	public void addRemoveList(Event e){
		removeList.add(e);
	}
}
