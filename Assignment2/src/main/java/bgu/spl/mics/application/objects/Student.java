package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    public Student(String name, String department, String status, LinkedList<Model> models) {
        this.name = name;
        this.department = department;
        if(status == "PhD")
            this.status = Degree.PhD;
        else this.status = Degree.MSc;
        this.publications = 0;
        this.papersRead = 0;
        this.models = models;
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private LinkedList<Model> models;
    private int sentToPublishedSinceLastConference;

    public void incSentTOPublications() {
        this.sentToPublishedSinceLastConference++;
    }

    public void updateAfterConference(int publishedInConference ){
        publications = publications + sentToPublishedSinceLastConference;
        papersRead = papersRead + publishedInConference - sentToPublishedSinceLastConference;
        sentToPublishedSinceLastConference = 0;
    }

    public LinkedList<Model> getModels(){
        return models;
    }

    public Degree getStatus(){return status;}

    public void setModels(LinkedList<Model> models){
        this.models = models;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }
}