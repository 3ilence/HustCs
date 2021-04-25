package homework.ch11_13.p4;

public class CompositeComponent extends Component{
    protected ComponentList childs = new ComponentList();

    public CompositeComponent() {

    }

    public CompositeComponent(int id, String name, double price) {
        super(id, name, price);
    }

    @Override
    public void add(Component component) throws UnsupportedOperationException {
        childs.add(component);
        //增加子组件的时候更新组件的价格
        this.price += component.calcPrice();
    }

    @Override
    public void remove(Component component) throws UnsupportedOperationException {
        childs.remove(component);
        //删除子组件的时候更新组件的价格
        this.price -= component.getPrice();
    }

    @Override
    public double calcPrice() {
//        double sum = 0;
//        for (Component component : childs) {
//            sum += component.getPrice();
//        }
        //this.price = sum;
        //因为在add操作和remove操作中更新了价格，所以直接返回，无需计算
        return price;
    }

    @Override
    public Iterator iterator() {
        return new CompositeIterator(childs);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString() + "\n");
        sb.append("sub-compoments of "+this.getName()+"\n");
        for (Component component : childs) {
            sb.append(component.toString() + "\n");
        }
        return sb.toString();
    }
}
