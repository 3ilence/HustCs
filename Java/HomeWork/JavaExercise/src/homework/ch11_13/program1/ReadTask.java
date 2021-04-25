package homework.ch11_13.program1;

public class ReadTask implements Task{
    String taskName;
    public ReadTask() {

    }

    public ReadTask(String taskName) {
        this.taskName = taskName;
    }

    public void execute() {
        System.out.println("ReadTask:  " + taskName + "开始执行");
    }
}
