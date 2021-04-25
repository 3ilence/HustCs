package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.index.impl.PostingList;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.util.Config;

import java.io.File;
import java.util.*;

import static hust.cs.javacourse.search.query.AbstractIndexSearcher.LogicalCombination.AND;

/**
 * <pre>
 *  IndexSearcher是检索具体实现的实现类
 *  继承自AbstractIndexSearcher
 * </pre>
 */
public class IndexSearcher extends AbstractIndexSearcher {


    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     * @param indexFile ：指定索引文件
     */
    public  void open(String indexFile) {
        index.load(new File(indexFile ));
        //index.load(new File(Config.PROJECT_HOME_DIR + "/index" + "/index.dat" ));
    }

    /**
     * 根据单个检索词进行搜索
     * @param queryTerm ：检索词
     * @param sorter ：排序器
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        //是否忽略大小写
        if (Config.IGNORE_CASE) {
            queryTerm.setContent(queryTerm.getContent().toLowerCase());
        }
        AbstractPostingList postingList = index.search(queryTerm);
        if (postingList == null) {
            return new Hit[0];
        }
       AbstractHit[] hits = new AbstractHit[postingList.size()];
       for(int i = 0; i < postingList.size(); i++) {
           Map<AbstractTerm, AbstractPosting> map = new TreeMap<>();
           map.put(queryTerm, postingList.get(i));
           hits[i] = new Hit(postingList.get(i).getDocId(), index.getDocName(postingList.get(i).getDocId()), map);
           //更新score为queryTerm在该文档中的出现次数，即feq
           hits[i].setScore(hits[i].getTermPostingMapping().get(queryTerm).getFreq());
       }
        List<AbstractHit> result  = Arrays.asList(hits);
        sorter.sort(result);
        //下面这种写法是不行的，因为无参的toArray会返回Object数组，
        // 而且这个Object数组不支持强制类型转换
       //return result.toArray();
        //FIXME 这里可能有问题，hits可能不是被覆盖写，而是扩展写
        //FIXME 经验证看来是覆盖写
        return result.toArray(hits);
    }

    /**
     *
     * 根据二个检索词进行搜索，暂未将queryTerm1 == queryTerm2的特殊情况提取出来
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter ：    排序器
     * @param combine ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, AbstractIndexSearcher.LogicalCombination combine) {
        List<AbstractHit> searchRes = new ArrayList<>();
        AbstractPostingList list1 = index.search(queryTerm1);
        AbstractPostingList list2 = index.search(queryTerm2);
        //set用于删选出可能符合条件的文档
        //对于AND来说文档必须既包含queryTerm1又包含queryTerm2
        //对于OR来说，包含其中任意一个的文档都归属到查找结果
        Set<Integer> set = new TreeSet<>();
        if(combine == AND) {
            if (list1 == null || list2 == null) {
                return new AbstractHit[0];
            }
            for(int i = 0;i < list1.size(); i++) {
                //只有当list2和list1的docId有交集，才能将该docId加入set
                if(list2.indexOf(list1.get(i).getDocId()) != -1)
                    set.add(list1.get(i).getDocId());
            }
            //判断两个postinglist的docId是否有交集
            if (set.size() == 0) {
                return new AbstractHit[0];
            }
            for(Integer e : set) {
                //indexOf返回postinglist中对应文档id的posting的下标
                int index1 = list1.indexOf(e);
                int index2 = list2.indexOf(e);
                Map<AbstractTerm, AbstractPosting> map1 = new TreeMap<>();
                Map<AbstractTerm, AbstractPosting> map2 = new TreeMap<>();
                map1.put(queryTerm1, list1.get(index1));
                map2.put(queryTerm2, list2.get(index2));
                AbstractHit hit1 = new Hit(e, index.getDocName(e), map1);
                AbstractHit hit2 = new Hit(e, index.getDocName(e), map2);
                //更新hit的socre属性值为posting的feq
                hit1.setScore(list1.get(index1).getFreq());
                hit2.setScore(list2.get(index2).getFreq());

                searchRes.add(hit1);
                searchRes.add(hit2);
            }
        } else {
            if (list1 == null && list2 == null) {
                return new AbstractHit[0];
            }
            if (list1 != null) {
                for(int i = 0;i < list1.size(); i++) {
                    set.add(list1.get(i).getDocId());
                }
            }
            if (list2 != null) {
                for(int i = 0; i < list2.size(); i++) {
                    set.add(list2.get(i).getDocId());
                }
            }
            for (Integer e : set) {
                //indexOf返回postinglist中对应文档id的posting的下标
                if (list1 != null) {
                    int index1 = list1.indexOf(e);
                    if(index1 != -1) {
                        Map<AbstractTerm, AbstractPosting> map1 = new TreeMap<>();
                        map1.put(queryTerm1, list1.get(index1));
                        AbstractHit hit1 = new Hit(e, index.getDocName(e), map1);
                        //计算命中文档的得分
                        hit1.setScore(hit1.getScore() + list1.get(index1).getFreq());
                        searchRes.add(hit1);
                    }
                }
                if (list2 != null) {
                    int index2 = list2.indexOf(e);
                    if(index2 != -1) {
                        Map<AbstractTerm, AbstractPosting> map2 = new TreeMap<>();
                        map2.put(queryTerm2, list2.get(index2));
                        AbstractHit hit2 = new Hit(e, index.getDocName(e), map2);
                        //计算命中文档的得分
                        hit2.setScore(hit2.getScore() + list2.get(index2).getFreq());
                        searchRes.add(hit2);
                    }
                }

            }
        }
        //排序
        sorter.sort(searchRes);

        AbstractHit[] res = new AbstractHit[searchRes.size()];
        return searchRes.toArray(res);
    }

    /**
     *
     * 根据二个检索词构成的短语进行搜索
     * @param queryTerm1 ：短语的第一个单词
     * @param queryTerm2 ：短语的最后一个单词
     * @param sorter ：    排序器
     * @return ：命中结果数组
     */
    public AbstractHit[] phraseSearch(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter) {
        List<AbstractHit> searchRes = new ArrayList<>();
        AbstractPostingList list1 = index.search(queryTerm1);
        AbstractPostingList list2 = index.search(queryTerm2);
        //用于删选出可能符合条件的文档
        // 该文档既包含了queryTerm1，又包含了queryTerm2，还要
        Set<Integer> set = new TreeSet<>();
        if (list1 == null || list2 == null) {
            return new AbstractHit[0];
        }
        for(int i = 0;i < list1.size(); i++) {
            //只有当list2和list1的docId有交集，才能将该docId加入set
            if(list2.indexOf(list1.get(i).getDocId()) != -1)
                set.add(list1.get(i).getDocId());
        }
        //判断两个postinglist的docId是否有交集
        if (set.size() == 0) {
            return new AbstractHit[0];
        }

        //先判断是否为特殊情况queryTerm1 = queryTerm2
        if(queryTerm1.equals(queryTerm2)) {
            for (Integer e :set) {
                int index = list1.indexOf(e);
                //phrasePos是短语的位置的集合
                List<Integer> phrasePos = new ArrayList<>();
                List<Integer> positions = list1.get(index).getPositions();
                for (int i = 0; i < positions.size() - 1; i++) {
                    if ( positions.get(i + 1) == positions.get(i) + 1) {
                        phrasePos.add(positions.get(i));
                        //i++是为了避免三个连续相同的短语这种情况
                        //但是不确定这种情况需不需要排除，比较也确实符合查找要求
                        //i++;
                    }
                }
                if (phrasePos.size() == 0) {
                    //一开始写成下面这样，debug了好久
                    //这个循环是遍历所有文档，一个文档不符合要求应该还要继续遍历下一个文档
                    //return new AbstractHit[0];
                    continue;
                }
                Map<AbstractTerm, AbstractPosting> map = new TreeMap<>();
                AbstractPosting phrasePosting = new Posting(e, phrasePos.size(), phrasePos);
                map.put(new Term(queryTerm1 + " " + queryTerm2), phrasePosting);
                //这里的index前面要加this，因为前面声明了一个int类型的index，其实这是不推荐的，但是index又却是最合适的变量名
                AbstractHit hit = new Hit(e, this.index.getDocName(e), map);

                //更新hit的socre属性值为phrasePos的大小
                hit.setScore(phrasePos.size());
                searchRes.add(hit);
            }
        } else {
            for (Integer e : set) {
                //indexOf返回postinglist中对应文档id的posting的下标
                int index1 = list1.indexOf(e);
                int index2 = list2.indexOf(e);
                //获取该文档中的queryTerm1和queryTerm2出现的所有位置
                //并且判断是否存在两个相邻的位置，如果存在，该文档就被添加到结果列表searchRes中
                //phrasePos用来存储所有满足条件的短语的位置
                List<Integer> phrasePos = new ArrayList<>();
                List<Integer> positions1 = list1.get(index1).getPositions();
                List<Integer> positions2 = list2.get(index2).getPositions();
                for (Integer integer : positions1) {
                    if(positions2.contains(integer + 1)) {
                        phrasePos.add(integer);
                    }
                }
                if(phrasePos.size() == 0) {
                    //return new AbstractHit[0];
                    continue;
                }
                Map<AbstractTerm, AbstractPosting> map = new TreeMap<>();
                AbstractPosting phrasePosting = new Posting(e, phrasePos.size(), phrasePos);
                map.put(new Term(queryTerm1 + " " + queryTerm2), phrasePosting);
                AbstractHit hit = new Hit(e, index.getDocName(e), map);

                //更新hit的socre属性值为phrasePos的大小
                hit.setScore(phrasePos.size());
                searchRes.add(hit);
            }
        }

        sorter.sort(searchRes);
        AbstractHit[] hits = new AbstractHit[searchRes.size()];
        return searchRes.toArray(hits);
    }

}
