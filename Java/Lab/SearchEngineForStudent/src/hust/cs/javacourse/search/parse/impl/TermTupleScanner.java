package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

/**
 * <pre>
 *     TermTupleScanner是AbstractTermTupleScanner的抽象子类，一个具体的TermTupleScanner对象也是
 *     一个AbstractTermTupleStream流对象，它利用java.io.BufferedReader去读取文本文件得到一个个三元组TermTuple.
 *
 *     其具体子类需要重新实现next方法获得文本文件里的三元组
 * </pre>
 */
public class TermTupleScanner extends AbstractTermTupleScanner {

    /**
     * 缺省构造函数
     */
    public  TermTupleScanner(){ };

    /**
     * 构造函数
     * @param input：指定输入流对象，应该关联到一个文本文件
     */
    public  TermTupleScanner(BufferedReader input) {
        super(input);
    }

    Queue<AbstractTermTuple> buffer = new LinkedList<>();
    int pos = 0;
    /**
     * 每次从文件中读取一行，将得到的tuple入队，每次只出队一个tuple
     * 当队列为空，则所有三元组都已经读取
     * 获得下一个三元组
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next(){
        if (buffer.isEmpty()) {
            String string = null;
            try {
                string = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (string == null) {
                return null;
            }
            //去空格
            while (string.trim().length() == 0) {
                try {
                    string = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (string == null) {
                    return null;
                }
            }
            StringSplitter splitter = new StringSplitter();
            splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
            for (String word : splitter.splitByRegex(string)) {
                TermTuple tuple = new TermTuple();
                tuple.curPos = pos;
                //是否忽略大小写
                if (Config.IGNORE_CASE) {
                    tuple.term = new Term(word.toLowerCase());
                } else {
                    tuple.term = new Term(word);
                }
                buffer.add(tuple);
                pos++;
            }
        }
        return buffer.poll();
    }
}
