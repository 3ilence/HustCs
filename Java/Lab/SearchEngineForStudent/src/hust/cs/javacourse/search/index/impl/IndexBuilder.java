package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *      需要实例化一个具体子类对象完成索引构造的工作
 * </pre>
 */
public class IndexBuilder extends AbstractIndexBuilder {

    public IndexBuilder(AbstractDocumentBuilder docBuilder){
        super(docBuilder);
    }

    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引.
     *      需要遍历和解析目录下的每个文本文件, 得到对应的Document对象，再依次加入到索引，并将索引保存到文件.
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    public AbstractIndex buildIndex(String rootDirectory) {
        AbstractIndex index = new Index();
        //获得文件路径列表
        List<String> filePaths = FileUtil.list(rootDirectory);
        //Collections.reverse(filePaths);//顺序排列
        AbstractDocument document = null;
        for (String docPath : filePaths) {
            try {
                document = docBuilder.build(docId, docPath, new File(docPath));
                //document包含文档id，文档绝对路径以及文档包含的所有的三元组termtuple
                index.addDocument(document);//将文档中的termtuple倒序构建索引
                docId += 1;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return index;
    }
}
