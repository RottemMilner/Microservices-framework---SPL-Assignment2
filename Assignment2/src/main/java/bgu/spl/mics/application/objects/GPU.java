package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    public static int ID = 0;
    private Type type;
    private int VRAM;
    private int ticksPerTrain;
    private int currentTick;
    private int dataBatchesToProcess; ///how many batches we need to process
    private int dataBatchesCurrentlyInProcess;
    private int dataBatchesProcessed; ///how many batches we have already processed
    private Boolean Available;
    private Model model;
    private LinkedBlockingDeque<DataBatch> RawDataBatch; /// dataBatches to send to the cluster
    private LinkedBlockingDeque<DataBatch> VRAMarray; ///dataBatches returned from the cluster
    private int id;
    private int GPUTIME;


    public GPU(String tp){
        if (tp.equals("RTX3090")){
            type=Type.RTX3090;
            VRAM = 32;
            ticksPerTrain = 1;
        }
        if (tp.equals("RTX2080")){
            type=Type.RTX2080;
            VRAM = 16;
            ticksPerTrain = 2;
        }
        if (tp.equals("GTX1080")){
            type=Type.GTX1080;
            VRAM = 8;
            ticksPerTrain =4;
        }
        model = null;
        RawDataBatch = new LinkedBlockingDeque<DataBatch>();
        VRAMarray = new LinkedBlockingDeque<DataBatch>();
        this.id = ID;
        ID++;
        GPUTIME = 0;
    }

    /**
     * @PRE: getModel() == null
     * @POST: getModel() == @param m
     * @param m - model to add to GPU
     */
    public void setModel(Model m){
        currentTick = 0;
        dataBatchesProcessed = 0;
        model = m;
        if(m != null) {
            if (m.getStatus() == Model.Status.PreTrained) {
                RawDataBatch = new LinkedBlockingDeque<DataBatch>();
                VRAMarray = new LinkedBlockingDeque<DataBatch>();
                splitData();
                m.setTraining();
            }
            if (m.getStatus() == Model.Status.Trained) {
                m.setTested();
            }
        }
    }

    /**
     * @PRE: getModel() !=null && isFull()==false && DataBatch.isProcessed()
     * @POST: getDataBatchesProcessed() > @PRE(getDataBatchesProcessed())
     */
    public void receiveFromCluster(DataBatch d){
        VRAMarray.addLast(d);
    }

    /**
     * @PRE: getModel()!=null && getDataBatchesToProcess()>0
     * @POST: getDataBatchesToProcess() < @PRE(getDataBatchesToProcess())
     */
    public void sendToCluster(){
        Cluster.getInstance().Process(RawDataBatch.poll());
    }

    /**
     * @PRE: getModel()!=null && getDataBatchesToProcess()>0
     * @POST: getDataBatchesToProcess() < @PRE(getDataBatchesToProcess())
     * @return return a dataBatch that will be sent to the cluster
     */
    public DataBatch createDB(){return null; }

    /**
     * @PRE: getModel()!=null && getVRAM()>0
     * @POST: getVRAM() < @PRE(getVRAM()) && getDataBatchesProcessed() > @PRE(getDataBatchesProcessed())
     * @param d - data batch to train
     */
    public void trainDataBatch(DataBatch d){
        if(currentTick >= ticksPerTrain){
            currentTick = 0;
            VRAMarray.poll();
            dataBatchesProcessed++;
            dataBatchesCurrentlyInProcess--;
            dataBatchesToProcess --;
            GPUTIME++;
        }
    }

    public Model getModel(){return model;}

    public boolean isFull(){return (VRAM==0);}
    public Boolean isAvailable(){return Available;}
    public int getVRAM(){return VRAM;}
    public int getDataBatchesToProcess(){return dataBatchesToProcess;}
    public int getDataBatchesProcessed(){return dataBatchesProcessed;}
    public int getID(){return this.id;}

    public void splitData(){
        for(int i = 0; i < model.getData().getSize();i += 1000){
            DataBatch d = new DataBatch(model.getData().getType(),i,id);
            RawDataBatch.addLast(d);
        }
        dataBatchesToProcess = RawDataBatch.size();
        dataBatchesProcessed = 0;
        dataBatchesCurrentlyInProcess = 0;
    }

    public void UpdateTick(){
        currentTick++;
        while(dataBatchesCurrentlyInProcess<VRAM && RawDataBatch.size()>0){
            sendToCluster();
            dataBatchesCurrentlyInProcess++;
        }
        if(VRAMarray.size()>0){
            trainDataBatch(VRAMarray.peek());
        }

        if(dataBatchesToProcess == 0){
            model.setTrained();
        }
    }
    public int getGPUTIME(){
        return GPUTIME;
    }
}
