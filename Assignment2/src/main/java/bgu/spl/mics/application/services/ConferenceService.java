package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import javax.jws.WebParam;


/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation conference;
    private int ticksPassed;
    private int publications;
    int tickTime;

    public ConferenceService(String name , ConfrenceInformation con, int tickTime) {
        super("Conference Service");
        this.conference = con;
        this.tickTime = tickTime;
        ticksPassed = 0;
        publications = 0;
    }

    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class ,(PublishResultsEvent m) ->{
            conference.AddGoodModel(m.getModel());
            complete(m , m.getModel());///check for correctness
            publications++;
        });
        subscribeBroadcast(TickBroadcast.class , (TickBroadcast t) ->{
            ticksPassed++;
            if (conference.getDate() <= ticksPassed*tickTime){
                for (Model m:
                     conference.getGoodTrainedModels()) {
                    System.out.println(m.getName());

                }
                this.sendBroadcast(new PublishConferenceBroadcast(publications));
                this.terminate();
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast m )-> {
            this.terminate();
        });
    }
}
