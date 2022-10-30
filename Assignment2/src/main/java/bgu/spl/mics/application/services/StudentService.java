package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Student;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    public enum State{ //The state of the current model
        PreTrained, isTraining, SentForTesting, SentForPublish
    }

    Student student;
    State currentState;
    Future<Model> futureModel;
    Model currentModel;

    public StudentService(String name , Student s) {
        super("Student " + name + " service");
        this.student = s;
        currentState = State.PreTrained;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConferenceBroadcast.class,(PublishConferenceBroadcast m)->{
            student.updateAfterConference(m.getPublication());
        });
        subscribeBroadcast(TickBroadcast.class , (TickBroadcast t) ->{

            switch (currentState){
                case PreTrained:{
                    if(student.getModels().peek().getStatus()== Model.Status.PreTrained) {
                        currentModel = student.getModels().poll();
                        futureModel = this.sendEvent(new TrainModelEvent(currentModel));
                        currentState = State.isTraining;
                        student.getModels().add(currentModel);
                    }
                    break;
                }
                case isTraining:{
                    if(futureModel != null && futureModel.isDone()){
                        futureModel = this.sendEvent(new TestModelEvent(currentModel));
                        currentState = State.SentForTesting;
                    }
                    break;
                }
                case SentForTesting:{
                    if(currentModel != null && currentModel.getStatus() == Model.Status.Tested) {
                        if (currentModel.getResult() == Model.Results.Good) {
                            futureModel = this.sendEvent(new PublishResultsEvent(currentModel));
                            currentState = State.SentForPublish;
                        }
                        else{
                            futureModel = null;
                            currentModel = null;
                            currentState = State.PreTrained;
                        }
                    }
                    break;
                }
                case SentForPublish:{
                    student.incSentTOPublications();
                    futureModel = null;
                    currentModel = null;
                    currentState = State.PreTrained;
                }
                break;
            }
        });
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast m )-> {
            this.terminate();
        });
    }
}
