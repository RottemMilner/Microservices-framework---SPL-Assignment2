package bgu.spl.mics.application.objects;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private DataBatch data;
    private int cores;
    private Cluster cluster;
    private int ticks;
    private Boolean Available;
    private int TicksPerBatch;
    private int ProcessedTicks;
    private int ProcessedBatches;


    public CPU(int cores_){
        data = null;
        cores =cores_;
        cluster = Cluster.getInstance();
        Available = true;
        ticks = 0;
        TicksPerBatch = 32/cores;
        ProcessedTicks = 0;
        ProcessedBatches = 0;
    }

    /**
     *
     * @param data_
     * @PRE: IsAvailable()==true
     * @POST: IsAvailable()==false && getDataBatch() == @param data_
     */
    public void SetDataBatch(DataBatch data_){
        if(data_ != null){
            data = data_;
            Available = false;
        }
    }

    /**
     * @PRE: IsAvailable()==false && getDataBatch()!=null
     * @POST: IsAvailable()==true && getDataBatch()==null
     */
    public void processAndComplete(){
        ticks++;
        ProcessedTicks++;
        if(ticks == TicksPerBatch*data.TimeToProcess()){
            ticks = 0;
            data.Processed();
            Cluster.getInstance().sendToGPU(data);
            data = null;
            Available = true;
            ProcessedBatches++;
        }
    }

    public DataBatch getDataBatch(){
        return data;
    }

    public Boolean IsAvailable(){
        return Available;
    }

    public int getTicks(){
        return ProcessedTicks;
    }

    public int getBatches(){
        return ProcessedBatches;
    }


}
