package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private final Data.Type type;
    private Boolean processed;
    private int GPUid;
    private int batchIndex;

    public DataBatch(Data.Type type_ , int index ,int id){
        this.type = type_;
        this.processed = false;
        this.batchIndex = index;
        this.GPUid = id;
    }
    public Data.Type getType(){
        return type;
    }

    public void Processed(){
        processed = true;
    }

    public Boolean isProcessed(){
        return processed;
    }

    public int TimeToProcess(){
        if(type == Data.Type.Images)
            return 4;
        if(type == Data.Type.Text)
            return 2;
        if(type == Data.Type.Tabular)
            return 1;
        return 0;
    }

    public int getGPUid() {
        return GPUid;
    }
}
