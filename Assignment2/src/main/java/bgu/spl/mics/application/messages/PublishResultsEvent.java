package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Model> {
    Model current;

    public PublishResultsEvent(Model m){
        current = m;
    }

    public Model getModel(){
        return current;
    }
}
