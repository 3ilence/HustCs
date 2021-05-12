package LRUCache;

import java.util.HashMap;
import java.util.Map;

//实现一个LRU缓存算法
//get(key)
//set(key,val)
//要求get、set时间复杂度为O(1)
public class LRUCache<K, V> {
    //缓存的容量
    private int capacity = 10;
    //Node记录表
    private Map<K, Node<K, V>> table = new HashMap<>();

    private Node<K, V> head;
    private Node<K, V> tail;

    public LRUCache() {
        head = new Node<K, V>();
        tail = new Node<K, V>();
        //双向链表
        head.next = tail;
        head.pre = tail;
        tail.pre = head;
        tail.next = head;
    }

    public LRUCache(int capacity) {
        //调用无参构造函数
        this();
        this.capacity = capacity;
    }

    public void put(K key, V value) {
        //判断表中是否已经存在该节点
        Node<K, V> node = table.get(key);
        //如果不存在的话会返回null
        if (node == null) {
            //判断缓存是否已满
            if (table.size() == capacity) {
                //如果超出了容量，需要移除尾部的节点
                //尾节点的上一个节点代表最久未使用的节点
                table.remove(tail.pre.key);//移除尾节点的上一个节点
                tail.pre.pre.next = tail;//尾部的上上个节点的下个节点设置为尾节点
                tail.pre = tail.pre.pre;//尾部的上个节点设置为尾部的上上个节点
            }
            //将新节点加入到table
            node = new Node<K, V>();
            node.key = key;
            node.value = value;
            table.put(key, node);
        }
        //一次put操作也相当于一次访问，因为想访问才put
        //故也需要将节点移到头部
        //无论该节点是否已存在都会被移到头部
        head.next.pre = node;
        node.pre = head;
        node.next = head.next;
        head.next = node;
    }

    public V get(K key) {
        Node<K, V> node = table.get(key);
        //如果Node不在表中，代表缓存中没有
        if (node == null) {
            return null;
        }
        //如果存在，就将Node移动到表头
        //首先先把node节点离开后的断开的地方补上
        node.pre.next = node.next;
        node.next.pre = node.pre;
        //再把node加入到head的next处
        node.pre = head;
        node.next = head.next;
        head.next.pre = node;
        head.next = node;
        return node.value;
    }

    public void printCache() {
        //如果head == tail说明链表为空
        Node<K, V> tmp = head.next;
        while(tmp != tail) {
            System.out.print(tmp.value + " ");
            tmp = tmp.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LRUCache<Integer, Integer> cache = new LRUCache<Integer, Integer>(4);
        cache.put(1,1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.put(4, 4);
        cache.put(5, 5);
        cache.printCache();
        //执行到这里的时候缓存里的数据应该是：5，4，3，2
        cache.get(2);
        cache.printCache();
        //这里因为2被访问了，所以2应该在头部，2，5，4，3
        cache.put(6,6);
        cache.printCache();
        //3会被移除，加入6：6，2，5，4
        cache.get(5);
        cache.printCache();
        //5，6，2，4
        cache.put(7,7);
        cache.put(8,8);
        cache.printCache();
        //8,7,5,6
    }


}
