package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * 测试搜索
 * 测试包含二个单词的短语检索
 * 这个程序在查找情况：queryTerm1 = queryTerm2的时候会有问题
 * 需要修改
 */
public class TestPhraseSearchIndex {
    public static void main(String[] args) throws IOException {
        IndexSearcher indexSearcher = new IndexSearcher();
        indexSearcher.open(Config.INDEX_DIR + "index.dat");
        Sort sorter = new SimpleSorter();
        String searchResultTargetFile = Config.INDEX_DIR + "phraseSearchResult.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(searchResultTargetFile)));
        System.out.println("Phase Search Program Start");
        System.out.println("\t请输入要查找的短语，短语的格式为\"word1 word2\"");
        Scanner in = new Scanner(System.in);
        String input;
        AbstractHit[] res;
        input = in.nextLine();
        String[] words = input.split(" ");
        if(words.length != 2) {
            System.out.println("请输入正确格式的短语！");
            return;
        }
        writer.write("查找短语: " + words[0] + " " + words[1] + "\n");
        //必须把res的类型设定为IndexSearcher，因为我不能修改AbstractIndexSearcher
        //所以phraseSearcher方法只在实现类里有
        res = indexSearcher.phraseSearch(new Term(words[0] ), new Term(words[1]), sorter);
        if(res.length == 0) {
            System.out.println("未查找到符合条件的短语！");
            writer.write("未查找成功\n");
        }
        //遍历hit列表
        for (AbstractHit hit : res) {
            System.out.println(hit.toString());
            writer.write(hit.toString());
        }
        writer.close();
    }
}
