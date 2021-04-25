package homework.ch11_13.p3;

public class Student extends Person {
    private int studentId;
    private String department;
    private String classNo;

    public Student() {
        super();
    }

    public Student(String name, int age, int studentId, String department, String classNo) {
        super(name, age);
        this.studentId = studentId;
        this.department = department;
        this.classNo = classNo;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }

    @Override
    public String toString() {
        return super.toString() +
                    ", studentId : " + studentId +
                    ", department : " + department +
                    ", classNo : " + classNo;
    }

    public boolean equals(Object o) {
        if (o instanceof Student) {
            if (this.getName().equals(((Student) o).getName()) &&
                this.getAge() == ((Student) o).getAge() &&
                this.getStudentId() == ((Student) o).getStudentId() &&
                    ((this.department.equals(((Student) o).getDepartment())) || ((this.department == null) && (((Student) o).getDepartment()) == null)) &&
                    ((this.classNo.equals(((Student) o).getClassNo())) || ((this.classNo == null) && (((Student) o).getClassNo()) == null)))
                return true;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Student newObj = (Student) super.clone();
        //newObj.setName(this.getName());
        newObj.setStudentId(this.getStudentId());
        System.out.println(newObj.studentId);
        newObj.setDepartment(new String(this.getDepartment()));
        //System.out.println(newObj.department);
        newObj.setClassNo(new String(this.getClassNo()));
        //System.out.println(newObj.classNo);
        return newObj;
    }
}
