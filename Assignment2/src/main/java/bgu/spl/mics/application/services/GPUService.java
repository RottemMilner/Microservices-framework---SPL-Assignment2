package bgu.spl.mics.application.services;


import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    GPU gpu;
    LinkedList<TrainModelEvent> events;
    TrainModelEvent currentEvent;

    public GPUService(String name ,GPU gpu_) {
        super(name);
        gpu = gpu_;
        events = new LinkedList<TrainModelEvent>() ;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class , (TickBroadcast m)->{
            if(gpu.getModel() == null){
                if(!events.isEmpty()) {
                    currentEvent = events.poll();
                    gpu.setModel(currentEvent.getModel());
                }
            }
            if(gpu.getModel() != null){
                gpu.UpdateTick();
                if(gpu.getModel().getStatus() == Model.Status.Trained){
                    gpu.setModel(null);
                    complete(currentEvent, currentEvent.getModel());
                }
            }
        });
        subscribeEvent(TrainModelEvent.class , (TrainModelEvent m) ->{
            events.add(m);
        });
        subscribeEvent(TestModelEvent.class , (TestModelEvent m) ->{//???
            Model model =  m.getModel();
            performTestModel(model);
            model.setTested();
            complete(m, model);
        });
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast m )-> {
            Cluster.getInstance().UpdateGPUTIME(this.gpu.getGPUTIME());
            this.terminate();
        });
    }

    public void performTestModel(Model model){
        if(model.getDegree() == Student.Degree.MSc){
            if(new Random().nextDouble() <= 0.6 ) {
                model.setResult(Model.Results.Good);
            }
            else {
                model.setResult(Model.Results.Bad);
            }
        }
        else{
            if(new Random().nextDouble() <= 0.8 ) {
                model.setResult(Model.Results.Good);
            }
            else {
                model.setResult(Model.Results.Bad);
            }
        }
    }
}
