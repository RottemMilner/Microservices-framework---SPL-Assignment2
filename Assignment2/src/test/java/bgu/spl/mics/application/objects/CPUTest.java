package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


import static org.junit.Assert.*;

public class CPUTest {
    private CPU cpu;
    private DataBatch data;

    @Before
    public void setUp(){
        cpu = new CPU(16);
        data = new DataBatch(Data.Type.Tabular,1,0);
        GPU gpu = new GPU("RTX3090");
        Cluster.getInstance();
        ArrayList<CPU> arrCPU = new ArrayList<CPU>();
        arrCPU.add(cpu);
        ArrayList<GPU> arrGPU = new ArrayList<GPU>();
        arrGPU.add(gpu);
        Cluster.getInstance().setCpus(arrCPU);
        Cluster.getInstance().setGpus(arrGPU);
    }

    @Test
    public void Test(){
        Assert.assertNull(cpu.getDataBatch()); //checking that at the beginning the data is null
        Assert.assertTrue(cpu.IsAvailable()); //checking that cpu is available to get dataBatch
        cpu.SetDataBatch(data); //set data as the cpu dataBatch
        Assert.assertEquals(cpu.getDataBatch(),data); //checking that the data in cpu equals the right dataBatch
        Assert.assertFalse(cpu.IsAvailable()); //checking that after SetDataBatch cpu is not available
        cpu.processAndComplete(); //process the dataBatch and send it to back to the Cluster
        Assert.assertNull(cpu.getDataBatch()); //check that after completion the cpu's dataBatch is null
        Assert.assertTrue(cpu.IsAvailable()); //checking that cpu is now available  again to get another dataBatch
    }

    @After
    public void tearDown(){
        cpu.SetDataBatch(null);
    }

}