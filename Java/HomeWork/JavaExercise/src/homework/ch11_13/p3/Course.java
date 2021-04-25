package homework.ch11_13.p3;

import java.util.ArrayList;
import java.util.List;

public class Course implements Cloneable{
    private String courseName;
    private List<Person> students = new ArrayList<>();
    private Person teacher;

    public Course(String courseName, Person teacher) {
        this.courseName = courseName;
        this.teacher = teacher;
    }

    /**
     * 注册 注意去重
     * @param p
     */
    public void register(Person p) {
        if (!students.contains(p))
            students.add(p);
    }

    public String getCourseName() {
        return courseName;
    }

    public List<Person> getStudents() {
        return students;
    }

    public Person getTeacher() {
        return teacher;
    }

    public void unregister(Person p) {
        students.remove(p);
    }

    public int getNumberOfStudent() {
        return students.size();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Course newObj = (Course) super.clone();
        newObj.teacher =  (Faculty) teacher.clone();
        newObj.courseName = new String(courseName);
        //为什么？？？
        newObj.students = new ArrayList<>();
        for (int i = 0; i < this.students.size(); i++) {
            newObj.students.add( (Person) ((Student) (students.get(i))).clone());
        }
        return newObj;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course Name : " + courseName + "\nTeacher info : " + teacher.toString() +
                "\nStudent List : \n");
        for (Person p : students) {
            sb.append(p.toString() + '\n');
        }
        sb.append("Totally : " + this.getNumberOfStudent() + " students");
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof Course) {
            if (((Course) o).getCourseName().equals(courseName) &&
                    ((Course) o).getTeacher().equals(teacher) &&
                    ((Course) o).getStudents().containsAll(students) &&
                    students.containsAll(((Course) o).getStudents()))
                return true;
        }
        return false;
    }
}
