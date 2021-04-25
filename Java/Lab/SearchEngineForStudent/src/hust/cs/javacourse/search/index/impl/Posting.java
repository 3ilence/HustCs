package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * <pre>
 *      Posting对象代表倒排索引里每一项， 一个Posting对象包括:
 *          包含单词的文档id.
 *          单词在文档里出现的次数.
 *          单词在文档里出现的位置列表（位置下标不是以字符为编号，而是以单词为单位进行编号.
 *      必须实现下面二个接口:
 *          Comparable：可比较大小（按照docId大小排序）,
 *                      当检索词为二个单词时，需要求这二个单词对应的PostingList的交集,
 *                      如果每个PostingList按docId从小到大排序，可以提高求交集的效率.
 *          FileSerializable：可序列化到文件或从文件反序列化
 *  </pre>
 */
public class Posting extends AbstractPosting {
    /**
     * 缺省构造函数
     */
    public Posting(){ };

    /**
     * 构造函数
     * @param docId ：包含单词的文档id
     * @param freq  ：单词在文档里出现的次数
     * @param positions   ：单词在文档里出现的位置
     */
    public Posting(int docId, int freq, List<Integer> positions){
        super(docId,freq,positions);
    }

    /**
     * 判断二个Posting内容是否相同
     * @param obj ：要比较的另外一个Posting
     * @return 如果内容相等返回true，否则返回false
     */

    public  boolean equals(Object obj)  {
        if(obj instanceof AbstractPosting) {
            return (this.docId == ((Posting)obj).getDocId()
                    && this.freq == ((Posting)obj).getFreq()
                    && this.positions.containsAll(((Posting) obj).getPositions())
                    && ((Posting) obj).getPositions().containsAll(this.getPositions())
            );
        }
        return false;
    }

    /**
     * 返回Posting的字符串表示
     * @return 字符串
     */
    @Override
    public  String toString()  {
//        ArrayList类重写了toString方法
        return "{docId: " + this.docId
                + ", freq: " + this.freq
                + ", positions: " + this.positions
                + "}";//如果不想让posting都显示在一行的话在末尾加上换行符
    }

    /**
     * 返回包含单词的文档id
     * @return ：文档id
     */
    public  int getDocId() {
        return this.docId;
    }

    /**
     * 设置包含单词的文档id
     * @param docId：包含单词的文档id
     */
    public  void setDocId(int docId) {
        this.docId = docId;
    }

    /**
     * 返回单词在文档里出现的次数
     * @return ：出现次数
     */
    public  int getFreq() {
        return this.freq;
    }

    /**
     * 设置单词在文档里出现的次数
     * @param freq:单词在文档里出现的次数
     */
    public  void setFreq(int freq) {
        this.freq = freq;
    }

    /**
     * 返回单词在文档里出现的位置列表
     * @return ：位置列表
     */
    public  List<Integer> getPositions() {
        return this.positions;
    }

    /**
     * 设置单词在文档里出现的位置列表
     * @param positions：单词在文档里出现的位置列表
     */
    public  void setPositions(List<Integer> positions) {
        this.positions = positions;
    }

    /**
     * 比较二个Posting对象的大小（根据docId）
     * @param o： 另一个Posting对象
     * @return ：二个Posting对象的docId的差值
     */
    @Override
    public  int compareTo(AbstractPosting o) {
        return this.docId - o.getDocId();
    }

    /**
     * 对内部positions从小到大排序
     */
    public  void sort() {
        //Collections.sort(this.positions);//正确的排序方法
        //List<Integer> list = this.positions;
        quickSort(positions,0,positions.size() - 1);
    }
    /**
     * 快速排序
     * @param list 需要进行排序的列表
     * @param left 排序位置起点
     * @param right 排序位置终点
     */
    public void quickSort(List<Integer> list,int left,int right) {
        if(left >= right)
            return;
        int leftIndex = left;
        int rightIndex = right;
        //int key = list.get(left);
        Integer key = list.get(left);
        while(leftIndex < rightIndex) {
            while(leftIndex < rightIndex && list.get(rightIndex) >= key )
            //while(leftIndex < rightIndex && list.get(rightIndex) >= list.get(leftIndex) )
                rightIndex--;
            list.set(leftIndex,list.get(rightIndex));
            while(leftIndex < rightIndex && list.get(leftIndex) <= key)
            //while(leftIndex < rightIndex && list.get(leftIndex) <= list.get(leftIndex))
                leftIndex++;
            list.set(rightIndex,list.get(leftIndex));
        }
        //list.set(leftIndex, list.get(left));
        list.set(leftIndex, key);
        quickSort(list,left,leftIndex - 1);
        quickSort(list,leftIndex+1,right);
    }

    /**
     * 写到二进制文件
     * @param out :输出流对象
     */
    public  void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.docId);
            out.writeObject(this.freq);
            out.writeObject(this.positions.size());
//            for(int i = 0;i < positions.size();i++) {
//                out.writeObject(positions.get(i));
//            }
            for(Integer i : positions) {
                out.writeObject(i);
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
            this.docId = (Integer) in.readObject();
            this.freq = (Integer) in.readObject();
            int size = (Integer) in.readObject();
//            这里有一个问题，使用add还是用set；也就是说positions是空list还是非空
            for (int i = 0; i < size; i++) {
                positions.add((Integer) in.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
