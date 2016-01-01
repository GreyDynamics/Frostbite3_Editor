package tk.greydynamics.Event;


public class Event {
	int tick;
	int count;
	Runnable runnable;
	
	
	public Event(int tick, int count, Runnable runnable) {
		this.runnable =  runnable;
		this.tick = tick;
		this.count = count;
	}
}
