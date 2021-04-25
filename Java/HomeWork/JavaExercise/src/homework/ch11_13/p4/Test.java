package homework.ch11_13.p4;

public class Test {
    public static void main(String[] args) {
        Component computer = ComponentFactory.create();
        System.out.println(computer);
        //利用迭代器遍历组件树的根节点及子节点
        //首先打印根节点
        System.out.println("id: " + computer.getId() +  " ,name:  " + computer.getName()
                                    + " , price: " + computer.getPrice());
        Iterator it = computer.iterator();//得到根节点的迭代器
        while (it.hasNext()) {
            Component c = it.next();
            System.out.println("id: " + c.getId() + ", name: " +
                    c.getName() + ", price:" + c.getPrice());
        }
    }
}
