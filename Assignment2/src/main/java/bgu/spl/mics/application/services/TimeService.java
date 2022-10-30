package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private final int duration; //how many ticks for the entire program
	private final int tickTime;
	int currentTick;

	public TimeService(int duration,int tickTime) {
		super("TimeService");
		this.duration = duration;
		this.tickTime = tickTime;
		this.currentTick = 1;

	}

	@Override
	protected void initialize() {
		Timer t = new Timer("TimeService",false);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if(currentTick < duration) {
					sendBroadcast(new TickBroadcast());
					currentTick++;
				}
				else{
					sendBroadcast(new TerminateBroadcast());
					t.cancel();
				}
			}
		};
		t.schedule(timerTask,0, tickTime);
		this.terminate();
	}
}
