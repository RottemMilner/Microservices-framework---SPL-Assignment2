package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import sun.nio.cs.ext.GB18030;

import java.io.*;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        File input = new File(args[0]);
        ArrayList<GPU> GPUS = new ArrayList<>();
        ArrayList<CPU> CPUS = new ArrayList<>();
        LinkedList<ConfrenceInformation> Conf = new LinkedList<>();
        Integer Duration = -1;
        Integer TickTime = -1;
        LinkedList<Student> Students = new LinkedList<>();
        try {
            JsonElement element = JsonParser.parseReader(new FileReader(input));
            JsonObject object = element.getAsJsonObject();
            Duration = object.get("Duration").getAsInt();
            TickTime = object.get("TickTime").getAsInt();
            JsonArray JsonGPUS = object.get("GPUS").getAsJsonArray();
            for (JsonElement gpu : JsonGPUS) {
                String GPUtype = gpu.getAsString();
                GPUS.add(new GPU(GPUtype));
            }
            JsonArray JsonCpus = object.get("CPUS").getAsJsonArray();
            for (JsonElement cpu : JsonCpus) {
                CPUS.add(new CPU(cpu.getAsInt()));
            }
            JsonArray JsonConf = object.get("Conferences").getAsJsonArray();
            for (JsonElement c : JsonConf) {
                JsonObject co = c.getAsJsonObject();
                String name_ = co.get("name").getAsString();
                int date_ = co.get("date").getAsInt();
                Conf.add(new ConfrenceInformation(name_, date_));
            }
            JsonArray JsonStudents = object.get("Students").getAsJsonArray();
            for (JsonElement currentstudent : JsonStudents) {
                JsonObject Stud = currentstudent.getAsJsonObject();
                String name = Stud.get("name").getAsString();
                String department = Stud.get("department").getAsString();
                String status = Stud.get("status").getAsString();//changed Student Constructor
                Student current = new Student(name, department, status, null);
                JsonArray Jsonmodels = Stud.get("models").getAsJsonArray();
                LinkedList<Model> StudentModels = new LinkedList<>();
                for (JsonElement jsonmodel : Jsonmodels) {
                    JsonObject Mod = jsonmodel.getAsJsonObject();
                    String name_ = Mod.get("name").getAsString();
                    String type_ = Mod.get("type").getAsString();
                    int size_ = Mod.get("size").getAsInt();
                    Data curr = new Data(type_, size_);
                    Model cur = new Model(name_, curr, status);
                    StudentModels.add(cur);
                }
                current.setModels(StudentModels);
                Students.add(current);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//      LinkedList<GPU> GPUS = new LinkedList<>();
//      LinkedList<CPU> CPUS = new LinkedList<>();
//      LinkedList<ConfrenceInformation> Conf = new LinkedList<>();
//      Integer Duration = -1;
//      Integer TickTime = -1;
        Cluster.getInstance().setCpus(CPUS);
        Cluster.getInstance().setGpus(GPUS);
        LinkedList<Thread> threads = new LinkedList<>();
        Integer count = 0;
        for (GPU g :
                GPUS) {
          Thread GPU = new Thread((new GPUService(count.toString(), g)));
          GPU.start();
          threads.add(GPU);
          count++;
        }
        count=0;
      for (CPU c :
              CPUS) {
        Thread CPU = new Thread((new CPUService(count.toString(), c)));
        CPU.start();
        threads.add(CPU);
        count++;
      }
      count=0;
      for (Student s :
              Students) {
        Thread Student = new Thread((new StudentService(count.toString(), s)));
        Student.start();
        threads.add(Student);
        count++;

      }
      count  =0;
      for (ConfrenceInformation c :
              Conf) {
        Thread Con = new Thread((new ConferenceService(count.toString(),c,TickTime)));
        Con.start();
        threads.add(Con);
        count++;
      }
      Thread timeS = new Thread(new TimeService(Duration, TickTime));
      try {
        TimeUnit.SECONDS.sleep(2);
      }
      catch (Exception e){};
      timeS.start();
        try { timeS.join();
            for (Thread thread : threads) {
                thread.join();
            }}
        catch (InterruptedException ex){} //all threads now finished
        System.out.println("closed!");
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("outputTest.json"));
            writer.write("{ \n");
            //print students
            writer.write("\t\"students\": [");
            int i=0;
            for(Student s: Students)
            {
                i++;
                writer.write(" \n \t\t{");
                writer.write("\n \t\t\t \"name\": \""+s.getName()+"\",");
                writer.write("\n \t\t\t \"department\": \""+s.getDepartment()+"\",");
                writer.write("\n \t\t\t \"status\": \""+s.getStatus()+"\",");
                writer.write("\n \t\t\t \"publication:\": \""+s.getPublications()+"\",");
                writer.write("\n \t\t\t \"papersRead:\": \""+s.getPapersRead()+"\",");
                writer.write("\n \t\t\t \"trainedModels:\": [");
                int j=0;
                if(s.getModels().size()==0)
                    writer.write("]");
                else// case models empty
                {
                    for(Model m:s.getModels())
                    {
                        j++;
                        writer.write("\n\t\t\t\t{");
                        writer.write("\n\t\t\t\t\t\"name\": \""+m.getName()+"\",");
                        writer.write("\n\t\t\t\t\t\"data\": {");
                        writer.write("\n\t\t\t\t\t\t\"type\": \""+m.getData().getType()+"\",");
                        writer.write("\n\t\t\t\t\t\t\"size\": "+m.getData().getSize());
                        writer.write("\n\t\t\t\t\t},");
                        writer.write("\n\t\t\t\t\t\"status\": \""+m.getStatus()+"\",");
                        writer.write("\n\t\t\t\t\t\"result\": \""+m.getResult()+"\"");
                        writer.write("\n\t\t\t\t}");
                        if(j!=s.getModels().size())
                            writer.write(",");

                    }//end of models
                    writer.write("\n \t\t\t]");
                }
                writer.write("\n \t\t}");
                if(i!= Students.size()-1)
                    writer.write(",");
            }//end print students
            writer.write("\n\t],");
            //print confrences
            writer.write("\n\t\"conferences\": [");
            i=0;
            for(ConfrenceInformation c : Conf)
            {
                i++;
                writer.write("\n\t\t{");
                writer.write("\n\t\t\t\"name\":" +"\""+c.getName()+"\",");
                writer.write("\n\t\t\t\"date\":" +"\""+c.getDate()+"\",");
                writer.write("\n\t\t\t\"publications\": [");
                int j=0;
                if(c.getGoodTrainedModels().size()==0)//case publications empty
                    writer.write("]");
                else
                {
                    for(Model m:c.getGoodTrainedModels())//publications
                    {
                        j++;
                        writer.write("\n\t\t\t\t{");
                        writer.write("\n\t\t\t\"name\": \""+m.getName()+"\",");
                        writer.write("\n\t\t\t\"data\": {");
                        writer.write("\n\t\t\t\t\"type\": \""+m.getData().getType()+"\",");
                        writer.write("\n\t\t\t\t\"size\": "+m.getData().getSize());
                        writer.write("\n\t\t\t\t},");
                        writer.write("\n\t\t\t\t\t\"status\": \""+m.getStatus()+"\",");
                        writer.write("\n\t\t\t\t\t\"result\": \""+m.getResult()+"\"");
                        writer.write("\n\t\t\t\t}");
                        if(j !=c.getGoodTrainedModels().size()-1)
                            writer.write(",");
                    }//end of publicatoins
                    writer.write("\n\t\t\t]");
                }

                writer.write("\n\t\t}");
                if(i!= Conf.size() -1)
                    writer.write(",");
            }
            writer.write("\n\t],");
            //CpuTimeUsed
            writer.write("\n\"cpuTimeUsed\": "+ Cluster.getInstance().getCpuTime()+",");
            //GpuTimeUsed
            writer.write("\n\"gpuTimeUsed\": "+ Cluster.getInstance().getGpuTime()+",");
            //BatchesProccessed
            writer.write("\n\"batchesProcessed\": "+ Cluster.getInstance().getProcessedBatches());
            writer.write("\n}");
            writer.close();

        }catch (IOException e) {
            e.printStackTrace();
        }



//        try{
//      FileWriter writer = new FileWriter("output.txt");
//      Gson g = new Gson();
//      g.toJson(Students ,  writer);
//      g.toJson(Conf ,writer);
//      g.toJson(Cluster.getInstance().stats() ,writer);
//      writer.flush();
//      writer.close();
//      }
//      catch (Exception e){}
    }


}


/*
        LinkedList<Model> models1 = new LinkedList<>();
        Student s1 = new Student("Simba", "ComputerScience", "MsC", models1);
        models1.add(new Model("YOLO10", new Data("Tabular", 20000), s1.getStatus().toString()));
        models1.add(new Model("ResNet9000", new Data("Images", 20000), s1.getStatus().toString()));
        models1.add(new Model("LessEfficientNet", new Data("Images", 20000), s1.getStatus().toString()));


//      LinkedList<Model> models2 = new LinkedList<>();
//      Student s2 = new Student("Ban", "ComputerScience", "MsC", models2);
//      models2.add(new Model("Y310", new Data("Tabular", 100000), s2));
//      models2.add(new Model("Reg00", new Data("Images", 200000), s2));
//      models2.add(new Model("LessEwtNet", new Data("Tabular", 19000), s2));
//
//      LinkedList<Model> models3 = new LinkedList<>();
//      Student s3 = new Student("ererererer", "ComputerScience", "MsC", models3);
//      models3.add(new Model("nvcvn", new Data("Tabular", 100000), s3));
//      models3.add(new Model("kjk", new Data("Images", 200000), s3));
//      models3.add(new Model("sdfdvxct", new Data("Tabular", 19000), s3));

      ArrayList<CPU> cArr = new ArrayList<>();
        CPU c0 = new CPU(32);
        CPU c1 = new CPU(32);
        CPU c2 = new CPU(32);
//        CPU c3 = new CPU(16);
//        CPU c4 = new CPU(16);
//        CPU c5 = new CPU(16);
//        CPU c6 = new CPU(16);

        cArr.add(c0);
        cArr.add(c1);
        cArr.add(c2);
//        cArr.add(c3);
//        cArr.add(c4);
//        cArr.add(c5);
//        cArr.add(c6);

      ArrayList<GPU> gArr = new ArrayList<>();
        GPU g0 = new GPU("RTX3090");
//        GPU g1 = new GPU("RTX3090");
//        GPU g2 = new GPU("RTX2080");
//        GPU g3 = new GPU("GTX1080");
        gArr.add(g0);
//        gArr.add(g1);
//        gArr.add(g2);
//        gArr.add(g3);

        Cluster c = Cluster.getInstance();
        c.setCpus(cArr);
        c.setGpus(gArr);

        ConfrenceInformation con1 = new ConfrenceInformation("ICML", 1000);
        ConfrenceInformation con2 = new ConfrenceInformation("NeurIPS", 2400);
        ConfrenceInformation con3 = new ConfrenceInformation("CVPR", 3000);
        MessageBusImpl mb = MessageBusImpl.getInstance();

        int tickTime = 1;
        int duration = 500;

        Thread S1 = new Thread(new StudentService("Simba", s1));
//        Thread S2 = new Thread(new StudentService("Yoni", s2));
//        Thread S3 = new Thread(new StudentService("erererere", s3));

        Thread CpuS0 = new Thread((new CPUService("c0", c0)));
        Thread CpuS1 = new Thread((new CPUService("c1", c1)));
        Thread CpuS2 = new Thread((new CPUService("c2", c2)));
//        Thread CpuS3 = new Thread((new CPUService("c3", c3)));
//        Thread CpuS4 = new Thread((new CPUService("c4", c4)));
//        Thread CpuS5 = new Thread((new CPUService("c5", c5)));
//        Thread CpuS6 = new Thread((new CPUService("c6", c6)));

        Thread GpuS1 = new Thread((new GPUService("g0", g0)));
//        Thread GpuS2 = new Thread((new GPUService("g1", g1)));
//        Thread GpuS3 = new Thread((new GPUService("g2", g2)));
//        Thread GpuS4 = new Thread((new GPUService("g3", g3)));
//        Thread Con1s = new Thread(new ConferenceService("con1", con1, tickTime));
//        Thread Con2s = new Thread(new ConferenceService("con2", con2, tickTime));
//        Thread Con3s = new Thread(new ConferenceService("con3", con3, tickTime));
        Thread timeS = new Thread(new TimeService(duration, tickTime));


        S1.start();
        //S2.start();
//        S3.start();
        CpuS0.start();
        CpuS1.start();
        CpuS2.start();
//        CpuS3.start();
//        CpuS4.start();
//        CpuS5.start();
//        CpuS6.start();
        GpuS1.start();
        //GpuS2.start();
//        GpuS3.start();
//        GpuS4.start();
//        Con1s.start();
        //Con2s.start();
        //Con3s.start();
        try{
            TimeUnit.SECONDS.sleep(3);
        }
        catch (Exception e){}
        timeS.start();
        try{timeS.join();
        GpuS1.join();
        CpuS0.join();
        CpuS1.join();
        CpuS2.join();
        S1.join();}
        catch (InterruptedException e){};
        System.out.println("done");
    }
}*/

