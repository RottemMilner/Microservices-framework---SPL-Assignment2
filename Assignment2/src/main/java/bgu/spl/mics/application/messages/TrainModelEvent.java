package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model> {
    Model model;

    public TrainModelEvent(Model m){
        this.model = m;
    }

    public Model getModel() {
        return model;
    }
}
