package homework.ch11_13.program1;

public class WriteTask implements Task{
    String taskName;
    public WriteTask() {

    }

    public WriteTask(String taskName) {
        this.taskName = taskName;
    }

    public void execute() {
        System.out.println("WriteTask:  " + taskName + "开始执行");
    }
}
