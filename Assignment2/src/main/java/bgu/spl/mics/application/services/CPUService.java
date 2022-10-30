package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;

    public CPUService(String name,CPU cpu_) {
        super(name);
        cpu = cpu_;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class , (TickBroadcast l) -> {
            if(cpu.getDataBatch() == null) {
                cpu.SetDataBatch(Cluster.getInstance().getDB());
            }
            else
                cpu.processAndComplete();
        });
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast m )-> {
            Cluster.getInstance().UpdateCPUTIME(this.cpu.getTicks());
            Cluster.getInstance().UpdateBatches(this.cpu.getBatches());
            this.terminate();
        });
    }
}
