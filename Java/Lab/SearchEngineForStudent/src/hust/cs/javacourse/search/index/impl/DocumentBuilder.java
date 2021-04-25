package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.parse.impl.LengthTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.PatternTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.StopWordTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.TermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <pre>
 *      Document构造器的功能应该是由解析文本文档得到的TermTupleStream，产生Document对象.
 * </pre>
 */
public class DocumentBuilder extends AbstractDocumentBuilder{
    /**
     * <pre>
     * 由解析文本文档得到的TermTupleStream,构造Document对象.
     * @param docId             : 文档id
     * @param docPath           : 文档绝对路径
     * @param termTupleStream   : 文档对应的TermTupleStream
     * @return ：Document对象
     * </pre>
     */
//    public AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream) throws IOException {
//        List<AbstractTermTuple> list = new ArrayList<>();
//        AbstractTermTuple tuple;
//        while (true) {
//            tuple = termTupleStream.next();
//            if(tuple == null)
//                break;
//            list.add(tuple);
//        }
//        //还要关闭输入流
//        termTupleStream.close();
//        return new Document(docId, docPath, list);
//    }

    public AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream) {
        AbstractDocument document = new Document(docId, docPath);
        AbstractTermTuple termTuple = termTupleStream.next();
        while (termTuple != null) {
            document.addTuple(termTuple);
            termTuple = termTupleStream.next();
        }
        termTupleStream.close();
        return document;
    }
    /**
     * <pre>
     * 由给定的File,构造Document对象.
     * 该方法利用输入参数file构造出AbstractTermTupleStream子类对象后,内部调用
     *      AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream)
     * @param docId     : 文档id
     * @param docPath   : 文档绝对路径
     * @param file      : 文档对应File对象
     * @return          : Document对象
     * </pre>
     */
    /*
    public AbstractDocument build(int docId, String docPath, File file) throws FileNotFoundException {
        List<AbstractTermTuple> list = new ArrayList<>();
        TermTuple tuple;
        int pos = 0;
        //从file文件里读取三元组
        Scanner input = new Scanner(file);
        //string用来一行一行地读取字符串
        String string;
        StringSplitter splitter = new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        while(input.hasNext()) {
            string = input.nextLine();
            while (string.trim().length() == 0) {
                string = input.nextLine();
                if (string == null) {
                    break;
                }
            }
            if(string == null)
                break;
            for(String word : splitter.splitByRegex(Config.STRING_SPLITTER_REGEX)) {
                tuple = new TermTuple();
                tuple.curPos = pos;
                if(Config.IGNORE_CASE) {
                    tuple.term = new Term(word.toLowerCase());
                } else {
                    tuple.term = new Term(word);
                }
                list.add(tuple);
            }
        }
        return new Document(docId, docPath, list);
    }*/
    public AbstractDocument build(int docId, String docPath, File file)  {
        AbstractDocument document = null;
        AbstractTermTupleStream ts = null;
        try {
            ts = new TermTupleScanner(new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)
            )));
            ts = new StopWordTermTupleFilter(ts);//停用词过滤器
            ts = new PatternTermTupleFilter(ts);//正则表达式过滤器
            ts = new LengthTermTupleFilter(ts);//单词长度过滤器
            document = build(docId, docPath, ts);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ts.close();
        }
        return document;
    }
}
