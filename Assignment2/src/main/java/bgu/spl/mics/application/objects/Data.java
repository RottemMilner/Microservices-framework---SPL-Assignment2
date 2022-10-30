package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(String type_,int size_){
        processed = 0;
        size = size_;
        switch (type_){
            case "Images":
                type=Type.Images;
                break;
            case "Text":
                type=Type.Text;
                break;
            case "Tabular":
                type=Type.Tabular;
                break;
        }
    }

    public int getSize(){
        return size;
    }

    public int getProcessed(){
        return processed;
    }

    public Type getType(){
        return type;
    }

}
