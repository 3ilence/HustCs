package homework.ch11_13.p4;

import java.util.ArrayList;
import java.util.List;

public class CompositeIterator implements Iterator{
    /**
     * 保存遍历到的每个节点的迭代器的列表
     */
    protected List<Iterator> iterators = new ArrayList<>();

    /**
     * 构造函数
     * @param iterator  要迭代的组件树的根节点的迭代器
     */
    public CompositeIterator(Iterator iterator) {
        iterators.add(iterator);
    }

    /**
     * 是否还有元素
     * @return 如果元素还没有迭代完，返回true；否则返回false
     */
    @Override
    /*public boolean hasNext() {
        //如果迭代器列表不为空
        if (iterators.size() > 0) {
            //且迭代器还未遍历完
            if (iterators.get(0).hasNext()) {
                return true;
            } else {
                //如果队首迭代器编译完，则移除该迭代器，获得下一个迭代器
                iterators.remove(0);
                //因为这既然是还未遍历过的迭代器，迭代器存在的话就有下一个元素
                if (iterators.size() > 0)
                    return true;
            }
        }
        return false;
    }*/
    public boolean hasNext() {
        //如果迭代器列表里还有迭代器
        if(iterators.size() > 0){
            Iterator it = iterators.get(0); //取出队列里的第一个迭代器
            if(!it.hasNext()){  //如果这个迭代器已经迭代完毕
                iterators.remove(0); //从列表里删除这个迭代器
                return hasNext(); //递归调用hasNext
            }
            else {
                return true;
            }
        }
        else {
            return false;  //当迭代器列表为空，说明所有元素迭代完毕
        }
    }

    /**
     * 获取下一个组件
     * @return
     */
    @Override
    public Component next() {
       if (hasNext()) {
           //取出队列里的第一个迭代器
           Iterator it = iterators.get(0);
           //从迭代器中取出下一个组件
           Component c = it.next();
           //因为这个组件可能是个复合组件，所以需要把该组件迭代器入队
           iterators.add(c.iterator());
           return c;
       }
       return null;
    }
}
