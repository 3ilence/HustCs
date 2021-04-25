package homework.ch11_13.p3;

public class Faculty extends Person{
    private int facultyId;
    private String title;
    private String email;

    public Faculty() {
        super();
    }

    public Faculty(String name, int age, int facultyId, String title, String email) {
        super(name, age);
        this.facultyId = facultyId;
        this.title = title;
        this.email = email;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", facultyId : " + facultyId +
                ", title : " + title +
                ", email : " + email;
    }

    public boolean equals(Object o) {
        if (o instanceof Faculty) {
            if (this.getName().equals(((Faculty) o).getName()) &&
                    this.getAge() == ((Faculty) o).getAge() &&
                    this.getFacultyId() == ((Faculty) o).getFacultyId() &&
                    this.title.equals(((Faculty) o).getTitle()) &&
                    this.email.equals(((Faculty) o).getEmail()))
                return true;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Faculty newObj = (Faculty) super.clone();
        //newObj.setName(this.getName());
        newObj.setFacultyId(this.getFacultyId());
        newObj.setTitle(new String(this.getTitle()));
        newObj.setEmail(new String(this.getEmail()));
        return newObj;
    }
}
