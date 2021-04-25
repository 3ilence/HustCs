package homework.ch11_13.program1;

public class CodeTask implements Task{
    String taskName;
    public CodeTask() {

    }

    public CodeTask(String taskName) {
        this.taskName = taskName;
    }

    public void execute() {
        System.out.println("CodeTask:  " + taskName + "开始执行");
    }
}
