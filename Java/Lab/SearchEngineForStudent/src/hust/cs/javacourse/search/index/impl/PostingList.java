package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *      PostingList对象包含了一个单词的Posting列表.
 *      必须实现下面接口:
 *          FileSerializable：可序列化到文件或从文件反序列化.
 * </pre>
 */
public class PostingList extends AbstractPostingList {


    /**
     * 添加Posting,要求不能有内容重复的posting
     * @param posting：Posting对象
     */
    public  void add(AbstractPosting posting) {
        if(this.list.contains(posting)) {
            return;
        }
        list.add(posting);
    }

    /**
     * 获得PosingList的字符串表示
     * @return ： PosingList的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean flag = false;
        for (AbstractPosting posting : this.list) {
            if (flag) {
                builder.append(" -> ");
            }
            flag = true;
            builder.append(posting.toString());
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * 添加Posting列表,,要求不能有内容重复的posting
     * @param postings：Posting列表
     */
    public void add(List<AbstractPosting> postings) {
        for(AbstractPosting posting: postings) {
            //直接调用另一个add函数
            //add(posting);
            if(this.list.contains(posting))
                continue;
           this.list.add(posting);
        }
    }

    /**
     * 返回指定下标位置的Posting
     * @param index ：下标
     * @return： 指定下标位置的Posting
     */
    public AbstractPosting get(int index) {
        return this.list.get(index);
    }

    /**
     * 返回指定Posting对象的下标
     * @param posting：指定的Posting对象
     * @return ：如果找到返回对应下标；否则返回-1
     */
    public int indexOf(AbstractPosting posting) {
        if(this.list.contains(posting))
            return this.list.indexOf(posting);
        return -1;
    }

    /**
     * 返回指定文档id的Posting对象的下标
     * @param docId ：文档id
     * @return ：如果找到返回对应下标；否则返回-1
     */
    public int indexOf(int docId) {
        for(AbstractPosting posting: this.list) {
            if(posting.getDocId() == docId)
                return this.list.indexOf(posting);
        }
        return -1;
    }

    /**
     * 是否包含指定Posting对象
     * @param posting： 指定的Posting对象
     * @return : 如果包含返回true，否则返回false
     */
    public boolean contains(AbstractPosting posting) {
        return this.list.contains(posting);
    }

    /**
     * 删除指定下标的Posting对象
     * @param index：指定的下标
     */
    public void remove(int index) {
        this.list.remove(index);
    }

    /**
     * 删除指定的Posting对象
     * @param posting ：定的Posting对象
     */
    public void remove(AbstractPosting posting) {
        this.list.remove(posting);
    }

    /**
     * 返回PostingList的大小，即包含的Posting的个数
     * @return ：PostingList的大小
     */
    public int size() {
        return this.list.size();
    }

    /**
     * 清除PostingList
     */
    public void clear() {
        this.list.clear();
    }

    /**
     * PostingList是否为空
     * @return 为空返回true;否则返回false
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /**
     * 根据文档id的大小对PostingList进行从小到大的排序
     */
    public  void sort() {
        //Collections.sort(list);//不确定sort函数是不是正确的调用了子类的compareTo方法，按理来说是的
        quickSort(list,0,list.size() - 1);
    }
    /**
     * 快速排序
     * @param list 需要进行排序的列表
     * @param left 排序位置起点
     * @param right 排序位置终点
     */
    public void quickSort(List<AbstractPosting> list,int left,int right) {
        if(left >= right)
            return;
        int leftIndex = left;
        int rightIndex = right;
        AbstractPosting key = list.get(left);
        while(leftIndex < rightIndex) {
            while(leftIndex < rightIndex && list.get(rightIndex).getDocId() >= key.getDocId() )
                rightIndex--;
            list.set(leftIndex,list.get(rightIndex));
            while(leftIndex < rightIndex && list.get(leftIndex).getDocId() <= key.getDocId())
                leftIndex++;
            list.set(rightIndex,list.get(leftIndex));
        }
        list.set(leftIndex,key);
        quickSort(list,left,leftIndex - 1);
        quickSort(list,leftIndex+1,right);
    }

    /**
     * 写到二进制文件
     * @param out :输出流对象
     */
    public  void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.list.size());
            for (AbstractPosting posting : list) {
                //out.writeObject(posting);//就是这里错了
                posting.writeObject(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     * @param in ：输入流对象
     */
    public  void readObject(ObjectInputStream in) {
        try {
            int size = (Integer) in.readObject();
//            这里有一个问题，使用add还是用set；也就是说positions是空list还是非空
//            for(int i = 0;i < size;i++) {
//                this.list.add((AbstractPosting) in.readObject());
//            }
            for (int i = 0; i < size; i++) {
                AbstractPosting posting = new Posting();
                posting.readObject(in);
                list.add(posting);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
