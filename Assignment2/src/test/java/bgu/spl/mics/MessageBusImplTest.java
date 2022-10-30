package bgu.spl.mics;

import bgu.spl.mics.application.objects.MicroServiceTestExample;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.*;
import org.junit.Before;
import org.junit.Test;




public class MessageBusImplTest {
    private MessageBusImpl messageBus;
    private ExampleEvent event1;
    private ExampleBroadcast broadcast;
    private  MicroServiceTestExample microservice1;
    private  MicroServiceTestExample microservice2;


    @Before
    public void setUp(){
        messageBus = MessageBusImpl.getInstance();
        event1 = new ExampleEvent();
        broadcast = new ExampleBroadcast();
        microservice1 = new MicroServiceTestExample("ms1");
        microservice2 = new MicroServiceTestExample("ms2");
        messageBus.register(microservice1);
        messageBus.register(microservice2);
    }


    @Test
    public void SubscribeAndCompleteEvent(){
        messageBus.subscribeEvent(event1.getClass(),microservice1);
        Future<String> check = messageBus.sendEvent(event1);
        messageBus.complete(event1,"Completed");
        Assert.assertEquals("Completed",check.get());
        Assert.assertTrue(check.isDone());
    }
    @Test
    public void sendBroadcast(){
        messageBus.subscribeBroadcast(broadcast.getClass(), microservice1); //subscribeBroadcast TEST
        messageBus.subscribeBroadcast(broadcast.getClass(), microservice2);
        messageBus.sendBroadcast(broadcast);//Both MicroServices should receive the broadcast
        Message msg1 = new ExampleBroadcast();
        Message msg2 = new ExampleBroadcast();
        try {///Asserting that SubscribedEvent worked
            msg1 = messageBus.awaitMessage(microservice1); //awaitMessage TEST
            msg2 = messageBus.awaitMessage(microservice1);//retrieving the last message received.
        }
        catch (InterruptedException e){ Assert.fail();}
        Assert.assertEquals(msg1,broadcast);
        Assert.assertEquals(msg2 ,broadcast);
    }

    @Test
    public void awaitMessageTest(){
        //checking if awaitMessage throws an exception
        messageBus.unregister(microservice1);
        try{
            messageBus.awaitMessage(microservice1);
            Assert.fail();
        }
        catch (Exception ignored) { }
        messageBus.register(microservice1);
    }

}

