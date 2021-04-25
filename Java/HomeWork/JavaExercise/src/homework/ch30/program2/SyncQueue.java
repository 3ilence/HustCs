package homework.ch30.program2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个线程安全同步队列，模拟多线程环境下的生产者消费者机制
 * 一个生产者线程通过produce方法向队列里产生元素
 * 一个消费者线程通过consume方法从队列里消费元素
 * @param <T> 元素类型
 */
public class SyncQueue<T> {
    /**
     * 保存队列元素
     */
    private ArrayList<T> list = new ArrayList<>();

    public static Lock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();


    /**
     * 生产数据
     * @param elements  生产出的元素列表，需要将该列表元素放入队列
     * @throws InterruptedException
     */
    public void produce(List<T> elements) throws InterruptedException {
        lock.lock();
        while (list.size() != 0)
            condition.await();
        System.out.print("Produce elements: ");

        for (T t : elements) {
            list.add(t);
            System.out.print(t + " , ");
        }

        condition.signalAll();
        lock.unlock();

    }

    /**
     * 消费数据
     * @return 从队列中取出的数据
     * @throws InterruptedException
     */
    public List<T> consume() throws InterruptedException {
        lock.lock();
        List<T> tmp = new ArrayList<>();
        while (list.size() == 0)
            condition.await();

        System.out.print("Consume elements: ");
        for (int i = 0; i < list.size();i++) {
            System.out.print(list.get(i) + " , ");
            tmp.add(list.get(i));
        }
        list.clear();
        condition.signalAll();
        lock.unlock();
        return tmp;
    }
}

