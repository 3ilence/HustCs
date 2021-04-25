package homework.ch30.program1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ArrayList<T>的包装类，未实现线程同步
 * @param <T>
 */
class Container<T>{
    private List<T> elements = new ArrayList<>();

    /**
     * 添加元素
     * @param e 要添加的元素
     */
    public  void add(T e){
        elements.add(e);
    }

    /**
     * 删除指定下标的元素
     * @param index 指定元素下标
     * @return 被删除的元素
     */
    public T remove(int index){
        return elements.remove(index);
    }

    /**
     * 获取容器里元素的个数
     * @return 元素个数
     */
    public int size(){
        return elements.size();
    }

    /**
     * 获取指定下标的元素
     * @param index 指定下标
     * @return 指定下标的元素
     */
    public T get(int index){
        return elements.get(index);
    }
}

/**
 * 实现了线程同步的container
 * @param <T>
 */
class SynchronizedContainer<T> {
    private List<T> elements = new ArrayList<>();

    /**
     * 给同步容器增加一个锁，同一时刻只有一个线程能够执行容器类的四个方法中的一个
     * 其他试图执行的线程会因为获取不到锁而阻塞
     */
    Lock lock = new ReentrantLock();

    /**
     * 添加元素
     * @param e 要添加的元素
     */
    public  void add(T e){
        //执行ArrayList的add方法前需要上锁
        lock.lock();
        elements.add(e);
        lock.unlock();
    }

    /**
     * 删除指定下标的元素
     * @param index 指定元素下标
     * @return 被删除的元素
     */
    public T remove(int index){
        lock.lock();
        T t = elements.remove(index);
        lock.unlock();
        return t;
    }

    /**
     * 获取容器里元素的个数
     * @return 元素个数
     */
    public int size(){
        lock.lock();
        int s = elements.size();
        lock.unlock();
        return s;
    }

    /**
     * 获取指定下标的元素
     * @param index 指定下标
     * @return 指定下标的元素
     */
    public T get(int index){
        lock.lock();
        T t = elements.get(index);
        lock.unlock();
        return t;
    }
}

public class Test {
    public static void testAdd(){
        Container<Integer> container = new Container<>();
        //同步容器的检测
        SynchronizedContainer<Integer> synContainer = new SynchronizedContainer<>();

        int addLoops = 10;  //addTask内的循环次数
        Runnable addTask = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < addLoops; i++){
                    container.add(i);
                }
            }
        };

        //使用了同步容器
        Runnable addSynTask = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < addLoops; i++){
                    synContainer.add(i);
                }
            }
        };

        int addTaskCount = 100; //addTask线程个数
        ExecutorService es = Executors.newCachedThreadPool();
        for(int i = 0; i < addTaskCount; i++){
            es.execute(addTask);
        }
        for(int i = 0; i < addTaskCount; i++){
            es.execute(addSynTask);
        }

        es.shutdown();
        while (!es.isTerminated()){}
        System.out.println("Test add " + (addLoops * addTaskCount) +
                " elements to container");
        System.out.println("Container size = " + container.size() +
                ", correct size = " + (addLoops * addTaskCount));
        System.out.println("Test add " + (addLoops * addTaskCount) +
                " elements to synContainer");
        System.out.println("SynContainer size = " + synContainer.size() +
                ", correct size = " + (addLoops * addTaskCount));

    }

    public static void testRemove(){
        Container<Integer> container = new Container<>();
        SynchronizedContainer<Integer> synContainer = new SynchronizedContainer<>();
        int removeLoops = 10; //removeTask内的循环次数
        int removeTaskCount = 100; //removeTask线程个数

        //首先添加removeLoops * removeTask个元素到容器
        for(int i = 0; i < removeLoops * removeTaskCount; i++){
            container.add(i);
        }
        //首先添加removeLoops * removeTask个元素到同步容器
        for(int i = 0; i < removeLoops * removeTaskCount; i++){
            synContainer.add(i);
        }

        Runnable removeTask = new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ; i < removeLoops; i++){
                    container.remove(0);
                }
            }
        };

        Runnable removeSynTask = new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ; i < removeLoops; i++){
                    synContainer.remove(0);
                }
            }
        };

        ExecutorService es = Executors.newCachedThreadPool();
        for(int i = 0; i < removeTaskCount; i++){
            es.execute(removeTask);
        }

        for(int i = 0; i < removeTaskCount; i++){
            es.execute(removeSynTask);
        }



        es.shutdown();
        while (!es.isTerminated()){}

        System.out.println("Test remove " + (removeLoops * removeTaskCount) +
                " elements from container");
        System.out.println("Container size = " + container.size() +
                ", correct size = 0");

        System.out.println("Test remove " + (removeLoops * removeTaskCount) +
                " elements from synContainer");
        System.out.println("SynContainer size = " + synContainer.size() +
                ", correct size = 0");
    }


    public static void main(String[] args){
        testAdd();
        testRemove();
    }
}

