package homework.ch11_13.p3;

public class Person implements Cloneable{
    private String name;
    private int age;
    public Person() {

    }

    public Person(String name, int age) {
        this.age = age;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "name : " + name + ", age : " + age;
    }


    public boolean equals(Object o) {
        if (o instanceof Person) {
            if (((Person) o).age == this.age && ((Person) o).name.equals(this.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Person newObj = (Person) super.clone();
        newObj.name = new String(this.name);
        newObj.age = this.age;
        return newObj;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        Person p1 = new Person("silence",20);
        Person p2 = (Person) p1.clone();
        System.out.println(p2.getName() == p1.getName());
    }
}
