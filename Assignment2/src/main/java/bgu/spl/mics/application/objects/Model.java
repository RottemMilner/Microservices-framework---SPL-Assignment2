package bgu.spl.mics.application.objects;

import com.sun.org.apache.xpath.internal.operations.Mod;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class Model {
    public enum Status {PreTrained, Training, Trained, Tested}
    public enum Results {None,Good,Bad}
    private String name;
    private Data data;
    private Status status;
    private Results result;
    private Student.Degree ownerEdu;


    public Model(String name_,Data data_,String ownerStat){
        name = name_;
        data = data_;
        status = Status.PreTrained;
        result = Results.None;
        if(ownerStat.equals("MsC"))
            ownerEdu = Student.Degree.MSc;
        else
            ownerEdu = Student.Degree.PhD;
    }

    public Data getData(){
        return data;
    }

    public void setTraining(){
        status = Status.Training;
    }

    public void setTrained(){
        status = Status.Trained;
    }

    public void setTested(){
        status = Status.Tested;
    }

    public void setResult(Results results){
        result = results;
    }

    public String getName(){
        return name;
    }

    public Status getStatus(){ return  status;}

    public Results getResult(){return result;}

    public Student.Degree getDegree(){
        return ownerEdu;
    }



}