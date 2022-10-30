package bgu.spl.mics.application.objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    private GPU gpu;
    private Model m;
    private DataBatch data;

    @Before
    public void setUp(){
        gpu = new GPU("RTX2080");
      //  m = new Model("model1", new Data("Text", 3200), new Student());
        data = new DataBatch(Data.Type.Text,1 ,1 );
    }

    @Test
    public void TestSetModel(){
        Assert.assertNull(gpu.getModel());
        gpu.setModel(m);
        Assert.assertEquals(gpu.getModel(), m);
        Assert.assertTrue(gpu.isAvailable());
        Assert.assertFalse(gpu.isFull());
        Assert.assertFalse(gpu.createDB().isProcessed());
    }

    @Test
    public void ReceiveAndSend(){
        gpu.setModel(m);
        int i = gpu.getDataBatchesToProcess();
        gpu.sendToCluster();
        Assert.assertEquals(i-1, gpu.getDataBatchesToProcess());
        int k = gpu.getVRAM();
        gpu.receiveFromCluster(data);
        Assert.assertEquals(1,gpu.getDataBatchesProcessed());
        Assert.assertEquals(gpu.getVRAM()-1,k);
    }

    @Test
    public void OperationsOnNULLModel(){
        try {
            gpu.receiveFromCluster(data);
        }
        catch (Exception ignored){}
        try{
            gpu.createDB();
        }
        catch (Exception ignored){}
        try {
            gpu.sendToCluster();
        }
        catch (Exception ignored){}
    }

}