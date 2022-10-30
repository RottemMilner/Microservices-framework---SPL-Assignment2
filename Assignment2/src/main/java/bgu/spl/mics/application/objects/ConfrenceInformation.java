package bgu.spl.mics.application.objects;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> GoodTrainedModels;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        GoodTrainedModels = new LinkedList<Model>();
    }

    public void AddGoodModel(Model m) {
        GoodTrainedModels.add(m);
    }

    public int getDate() {
        return date;
    }

    public LinkedList<Model> getGoodTrainedModels() {
        return GoodTrainedModels;
    }

    public String getName() {
        return name;
    }
}
