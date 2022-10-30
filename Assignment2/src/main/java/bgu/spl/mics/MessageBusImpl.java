package bgu.spl.mics;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap <Event, Future> futureHashMap;
	private ConcurrentHashMap <Class<? extends Broadcast> , ConcurrentLinkedQueue <MicroService>> broadcastHashMap;
	private ConcurrentHashMap<Class<? extends Event> , ConcurrentLinkedQueue <MicroService>> eventHashMap;
	private ConcurrentHashMap <MicroService , LinkedBlockingQueue <Message>> messageHashMap;

	private MessageBusImpl(){
		futureHashMap = new ConcurrentHashMap<>();
		broadcastHashMap = new ConcurrentHashMap<>();
		eventHashMap = new ConcurrentHashMap<>();
		messageHashMap = new ConcurrentHashMap<>();
	}

	public synchronized static MessageBusImpl getInstance() {
		return MessageBusImpHolder.instance;
	}

	private static class MessageBusImpHolder {
		private static volatile MessageBusImpl instance = new MessageBusImpl();
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!messageHashMap.containsKey(m)){
			throw new IllegalArgumentException("the service is not subscribed to this event");}
		eventHashMap.putIfAbsent(type , new ConcurrentLinkedQueue<MicroService>());
		eventHashMap.get(type).add(m);
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!messageHashMap.containsKey(m)){
			throw new IllegalArgumentException("the service is not subscribed to this event");}
		broadcastHashMap.putIfAbsent(type , new ConcurrentLinkedQueue<MicroService>());
		broadcastHashMap.get(type).add(m);
	}

	@Override
	public <T>  void complete(Event<T> e, T result) {
		Future future = futureHashMap.remove(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(!broadcastHashMap.containsKey(b.getClass()) || broadcastHashMap.get(b.getClass()).isEmpty())
			throw new IllegalStateException("No service is registered");
		for (MicroService m : broadcastHashMap.get(b.getClass())) {
			try {
				messageHashMap.get(m).add(b);
			}
			catch (NullPointerException e) {}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(futureHashMap.containsKey(e))
			throw new IllegalArgumentException("Event already appeared in message bus queue");
		ConcurrentLinkedQueue<MicroService> eventQueue = eventHashMap.get(e.getClass());
		Future<T> f = new Future<>();
		futureHashMap.put(e,f);
		if (!eventHashMap.containsKey(e.getClass()))
			return null;
		synchronized (eventQueue) {
			if(eventQueue.isEmpty())
				return null;
			boolean eventSent = false;
			while (!eventSent) {
				try {
					MicroService m = eventQueue.poll();
					messageHashMap.get(m).add(e);
					eventQueue.add(m);
					eventSent = true;
				}
				catch (NullPointerException exc) { }
			}
		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		if(messageHashMap.containsKey(m))
			throw new IllegalArgumentException("This service is already registered to the messageBus!");
		messageHashMap.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		if (!messageHashMap.containsKey(m))
			throw new IllegalArgumentException("This service is not registered to the messageBus!");
		messageHashMap.remove(m);
		Collection<ConcurrentLinkedQueue<MicroService>> events = eventHashMap.values();
		for (ConcurrentLinkedQueue<MicroService> list : events)
			list.remove(m);
		Collection<ConcurrentLinkedQueue<MicroService>> broadcasts = broadcastHashMap.values();
		for (ConcurrentLinkedQueue<MicroService> list : broadcasts)
			list.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!messageHashMap.containsKey(m))
			throw new IllegalArgumentException("This service is not registered to the messageBus!");
		return messageHashMap.get(m).take();
	}
}
