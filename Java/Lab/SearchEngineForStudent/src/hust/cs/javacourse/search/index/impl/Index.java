package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <pre>
 *      一个倒排索引对象包含了一个文档集合的倒排索引.
 *      内存中的倒排索引结构为HashMap，key为Term对象，value为对应的PostingList对象.
 *      另外在AbstractIndex里还定义了从docId和docPath之间的映射关系.
 *      必须实现下面接口:
 *          FileSerializable：可序列化到文件或从文件反序列化.
 * </pre>
 */
public class Index extends AbstractIndex {

    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("docId-----docPath mapping\n");
        for (Map.Entry<Integer, String> entry : docIdToDocPathMapping.entrySet()) {
            builder.append(entry.getKey());
            builder.append("\t---->\t");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        builder.append("PostingList: \n");
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            builder.append(entry.getKey().toString());
            builder.append("\t---->\t");
            builder.append(entry.getValue().toString());
        }
        return builder.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        docIdToDocPathMapping.put(document.getDocId(), document.getDocPath());
        for (AbstractTermTuple termTuple : document.getTuples()) {
            //如果这是没有被创建过的key的话
            if (!termToPostingListMapping.containsKey(termTuple.term)) {
                Posting posting = new Posting();
                posting.setDocId(document.getDocId());
                posting.setFreq(termTuple.freq);
                List<Integer> positions = new ArrayList<>();
                positions.add(termTuple.curPos);
                posting.setPositions(positions);
                termToPostingListMapping.put(termTuple.term,  new PostingList());
                termToPostingListMapping.get(termTuple.term).add(posting);
            } else {
                boolean flag = false;
                //如果该termTuple的文档id已经被添加了的话，就会找到匹配的list的那一项，seq加一，并新增一个pos
                for (int i = 0; i < termToPostingListMapping.get(termTuple.term).size(); i++) {
                    if (termToPostingListMapping.get(termTuple.term).get(i).getDocId() == document.getDocId()) {
                        termToPostingListMapping.get(termTuple.term).get(i).setFreq(termToPostingListMapping.get(termTuple.term).get(i).getFreq() + 1);
                        termToPostingListMapping.get(termTuple.term).get(i).getPositions().add(termTuple.curPos);
                        flag = true;
                    }
                }
                //flag == false说明termTuple.term映射得到的List中不包含该document.getdocId()那一项
                if (flag ==false) {
                    Posting posting = new Posting();
                    posting.setDocId(document.getDocId());
                    posting.setFreq(termTuple.freq);
                    List<Integer> positions = new ArrayList<>();
                    positions.add(termTuple.curPos);
                    posting.setPositions(positions);
                    termToPostingListMapping.get(termTuple.term).add(posting);
                }
            }
        }

        optimize();
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        if (file == null)
            return;
        try {
            readObject(new ObjectInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try {
            writeObject(new ObjectOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        //
        return termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            // 先对term根据position排序
            for (int i = 0; i < entry.getValue().size(); i++) {
                //posting类实现了将内部positions排序
                entry.getValue().get(i).sort();
            }
            //再整体对docId排序
            //PostingList类也实现了根据docId排序的方法
            entry.getValue().sort();
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(docIdToDocPathMapping.size());
            out.writeObject(termToPostingListMapping.size());
            for (Map.Entry<Integer, String> entry : docIdToDocPathMapping.entrySet()) {
                out.writeObject(entry.getKey());//Integer
                out.writeObject(entry.getValue());
            }
            for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
                //term类和PostingList类都实现了fileSerializable接口，都有实例方法writeObject
                entry.getKey().writeObject(out);
                entry.getValue().writeObject(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        int docSize = 0;
        int termSize = 0;
        try {
            docSize = (Integer) in.readObject();
            termSize = (Integer) in.readObject();
            for (int i = 0; i < docSize; i++) {
                Integer docId = (Integer) in.readObject();
                String docPath = (String) in.readObject();
                docIdToDocPathMapping.put(docId, docPath);
            }
            for (int i = 0; i < termSize; i++) {
                AbstractTerm term = new Term();
                AbstractPostingList postingList = new PostingList();
                term.readObject(in);
                postingList.readObject(in);
                termToPostingListMapping.put(term, postingList);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void writePlainText(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(toString());
            writer.close();
            //必须关闭，不关闭
            //new BufferedWriter(new FileWriter(file)).write(toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
