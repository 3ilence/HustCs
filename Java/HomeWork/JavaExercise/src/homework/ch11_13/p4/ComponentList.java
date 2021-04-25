package homework.ch11_13.p4;

import java.util.ArrayList;

public class ComponentList extends ArrayList<Component> implements Iterator {
    /**
     * 记录自定义迭代器当前迭代的位置
     */
    private int position;
    public ComponentList() {
        position = 0;
    }

    /**
     * 是否还有元素
     * @return
     */
    public boolean hasNext() {
        if (position < this.size()) {
            return true;
        }
        return false;
    }

    public Component next() {
        if (hasNext()) {
            return this.get(position++);
        }
        return null;
    }
}
