package homework.ch11_13.program1;

import java.util.ArrayList;

public class TaskServiceImpl implements TaskService{
    /**
     * 待完成任务列表
     */
    ArrayList<Task> taskList = new ArrayList<>();

    /**
     * 执行任务接口列表中的每个任务
     */
    public void exeuteTasks() {
        while (!taskList.isEmpty()) {
            taskList.get(taskList.size() - 1).execute();
            taskList.remove(taskList.size() - 1);
        }
    }
    /**
     * 添加任务
     * @param t 新添加的任务
     */
    public void addTask(Task t) {
        taskList.add(t);
    }

    public static void main(String[] args) {
        TaskService pool = new TaskServiceImpl();
        Task t1 = new CodeTask("codeTask1");
        Task t2 = new WriteTask("writeTask2");
        Task t3 = new ReadTask("readTask3");
        pool.addTask(t1);
        pool.addTask(t2);
        pool.addTask(t3);
        pool.exeuteTasks();
    }
}
