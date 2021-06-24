package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.io.*;
import java.util.Scanner;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     * 搜索程序入口
     *
     * @param args ：命令行参数
     */
    /*public static void main(String[] args) throws IOException {
        Sort simpleSorter = new SimpleSorter();
        String indexFile = Config.INDEX_DIR + "index.dat";
        String searchResultTargetFile = Config.INDEX_DIR + "search_result.txt";

        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);

        File filename = new File(Config.DOC_DIR + "/用于检索的测试词.txt");//
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line;
        line = br.readLine();
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(searchResultTargetFile)));
        while (line != null) {
            line = br.readLine();// 一次读入一行数据
            if(line == null)
                break;
            else {
                AbstractHit[] hits = searcher.search(new Term(line), simpleSorter);
                System.out.println("*********查询单词:" + line);
                writer.write("*********查询单词:" + line);
                for (AbstractHit hit : hits) {
                    System.out.println(hit.toString());
                    writer.write(hit.toString());
                }
            }
        }
        writer.close();
    }*/
    public static void main(String[] args) throws IOException {
        AbstractIndexSearcher indexSearcher = new IndexSearcher();
        indexSearcher.open(Config.INDEX_DIR + "index.dat");
        Sort sorter = new SimpleSorter();
        //将结果写入到searchResultTargetFile文件中
        String searchResultTargetFile = Config.INDEX_DIR + "searchResult.txt";
        File file = new File(searchResultTargetFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("*************查找结果如下******************\n");

        System.out.println("Word Search Program Start");
        System.out.println("\t如果想执行单个单词查找，输入1，多个单词查找，输入2，结束查找输入3");
        Scanner in = new Scanner(System.in);
        String input;
        String sel;
        AbstractHit[] res;
        while (in.hasNext()) {
            input = in.nextLine();
            if (input.equals("1")) {
                System.out.println("\t请输入想要查找的单词：");
                input = in.nextLine();
                writer.write("************查找单词：" + input + "\n");
                res = indexSearcher.search(new Term(input), sorter);
                if (res.length == 0) {
                    writer.write("未查找成功" + "\n");
                    System.out.println("未查找成功");
                }
                for (AbstractHit hit : res) {
                    System.out.println(hit.toString());
                    writer.write(hit.toString());
                }
            } else if (input.equals("2")) {
                System.out.println("\t请输入你想查找的两个单词，单词之间用空格隔开：");
                input = in.nextLine();
                writer.write("************查找单词：" + input + "\n");
                String[] words = input.split(" ");
                if (words.length != 2) {
                    System.out.println("输入格式错误");
                    System.out.println("如果还想继续查询的话请输入1或2或3");
                    writer.write("输入异常\n");
                    continue;
                }
                System.out.println("\n请输入AND或者OR来表明进行与查询还是或查询：");
                sel = in.nextLine();
                writer.write("************查找方式: " + sel + "\n");
                if (sel.equals("AND")) {
                    res = indexSearcher.search(new Term(words[0]),
                            new Term(words[1]), sorter, AbstractIndexSearcher.LogicalCombination.AND);
                } else if (sel.equals("OR")) {
                    res = indexSearcher.search(new Term(words[0]),
                            new Term(words[1]), sorter, AbstractIndexSearcher.LogicalCombination.OR);
                } else {
                    System.out.println("输入格式错误!");
                    System.out.println("如果还想继续查询的话请输入1或2或3");
                    writer.write("输入异常\n");
                    continue;
                }
                if (res.length == 0) {
                    writer.write("未查找成功\n");
                    System.out.println("未查找成功");
                }
                for (AbstractHit hit : res) {
                    System.out.println(hit.toString());
                    writer.write(hit.toString());
                }
            } else if (input.equals("3")) {
                break;
            } else {
                System.out.print("输入格式错误");
                System.out.println("如果还想继续查询的话请输入1或2或3");
                continue;
            }
            System.out.println("如果还想继续查询的话请输入1或2或3");
            writer.write("\n\n");
        }
        writer.close();
    }
}
