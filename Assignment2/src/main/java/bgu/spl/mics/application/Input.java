package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;

public class Input {
    private Student[] students;
    private GPU[] GPUS;
    private CPU[] CPUS;
    private ConfrenceInformation[] Conferences;
    private int TickTime;
    private int Duration;

    public Input(Student[] students, GPU[] GPUS, CPU[] CPUS,
                 ConfrenceInformation[] Conferences,int TickTime,int Duration){
        this.students = students;
        this.GPUS = GPUS;
        this.CPUS = CPUS;
        this.Conferences = Conferences;
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

}
