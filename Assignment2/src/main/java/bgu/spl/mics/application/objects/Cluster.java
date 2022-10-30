package bgu.spl.mics.application.objects;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private static Cluster instance = null;

	/**
     * Retrieves the single instance of this class.
     */
	ArrayList<CPU> cpus;
	ArrayList<GPU> gpus;
	LinkedBlockingQueue<DataBatch> UnProcessedDataBatch;
	private int CpuTime;
	private int GpuTime;
	private int ProcessedBatches;

	public static synchronized Cluster getInstance() {
		if(instance==null)
			instance = new Cluster();
		return instance;
	}

	private Cluster(){
		cpus = null;
		gpus = null;
		UnProcessedDataBatch = new LinkedBlockingQueue<DataBatch>();
		CpuTime = 0;
		GpuTime = 0;
		ProcessedBatches = 0;
	}

	public void setCpus(ArrayList<CPU> cpus) {
		this.cpus = cpus;
	}

	public void setGpus(ArrayList<GPU>  gpus) {
		this.gpus = gpus;
	}

	public synchronized void Process(DataBatch db){
		UnProcessedDataBatch.add(db);
	}

	public synchronized void sendToGPU(DataBatch db){
		gpus.get(db.getGPUid()).receiveFromCluster(db);
	}

	public synchronized  DataBatch getDB(){
		return UnProcessedDataBatch.poll();
	}

	public synchronized void UpdateCPUTIME(int time){
		CpuTime = CpuTime + time;
	}

	public synchronized void UpdateGPUTIME(int time){
		GpuTime = GpuTime + time;
	}
	public synchronized void UpdateBatches(int batch){
		ProcessedBatches = ProcessedBatches + batch;
	}

	public int getCpuTime() {
		return CpuTime;
	}

	public int getGpuTime() {
		return GpuTime;
	}

	public int getProcessedBatches() {
		return ProcessedBatches;
	}
}
