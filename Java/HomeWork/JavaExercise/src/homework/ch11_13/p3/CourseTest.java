package homework.ch11_13.p3;

public class CourseTest {
    public static void main(String[] args) throws CloneNotSupportedException {
        Person teacher = new Faculty( "James Gosling" , 65, 0000 ,"professor",
        "http: / /nighthacks.com/jag/bio/index.html");
        Course javaCourse = new Course(  "Java Language Programming " ,teacher);
        Person student1 = new Student( "aa" ,  20, 20180101,  "cs" , "CS1704");
        Person student2 = new Student( "bbb" ,  20,  20170102, "cs" ,  "CS1705");
        Person student3 = new Student(  "ccc" ,  20, 20170103, "cs" ,  "CS1706");
        javaCourse.register(student1);
        javaCourse.register(student2);
        javaCourse.register(student3);
        System.out.println(javaCourse);//打印课程详细信息
        javaCourse.unregister(student3);
        System.out.println("\n去除student3之后\n");
        System.out.println(javaCourse);//测试是否为深拷贝
//        Person student4 = (Student) student3.clone();
//        System.out.println(student4);
//        System.out.println(student4.getName() == student3.getName());
//        Person teacher2 = (Faculty) teacher.clone();
//        System.out.println(teacher2);
//        System.out.println(teacher2.getName() == teacher.getName());

        Course javaCourse2 = (Course) javaCourse.clone();
        System.out.println("接下来是javaCourse深拷贝得到的javaCourse2：\n");
        System.out.println(javaCourse2);
        System.out. println("javaCourse和javaCourse2是否内容相等：" + javaCourse.equals(javaCourse2));//测试对象内容是否相等
        System.out.println("javaCourse和javaCourse2是否courseName引用相等：" + ( javaCourse.getCourseName() == javaCourse2.getCourseName()));//测试所有引用类型数据成员是否指问相同对家system.out.println(javacourse.getTeacher() != javacourse2.getTeacher());
        System.out.println("javaCourse和javaCourse2是否Student列表引用相等：" + (javaCourse.getStudents() == javaCourse2.getStudents()));
        //System.out.println(javaCourse2);

    }
}
