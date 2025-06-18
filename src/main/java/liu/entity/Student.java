package liu.entity;

/**
 * @description: 学生表
 * @author: liu jin
 * @create: 2025-04-18 19:37
 **/

import java.util.Date;
import java.util.List;

public class Student {
    private int student_id;
    private String student_number;
    private String student_name;
    private String college;
    private Date enrollment_date;
    private String photo_path;

    private List<CourseSelection> courseSelections;

    public Student() {
    }

    public Student(int student_id, String student_number, String student_name, String college, Date enrollment_date) {
        this.student_id = student_id;
        this.student_number = student_number;
        this.student_name = student_name;
        this.college = college;
        this.enrollment_date = enrollment_date;
    }

    public Student(int student_id, String student_number, String student_name, String college, Date enrollment_date, List<CourseSelection> courseSelections) {
        this.student_id = student_id;
        this.student_number = student_number;
        this.student_name = student_name;
        this.college = college;
        this.enrollment_date = enrollment_date;
        this.courseSelections = courseSelections;
    }

    public Student(int student_id, String student_number, String student_name, String college, Date enrollment_date, String photo_path, List<CourseSelection> courseSelections) {
        this.student_id = student_id;
        this.student_number = student_number;
        this.student_name = student_name;
        this.college = college;
        this.enrollment_date = enrollment_date;
        this.photo_path = photo_path;
        this.courseSelections = courseSelections;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public String getStudent_number() {
        return student_number;
    }

    public void setStudent_number(String student_number) {
        this.student_number = student_number;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Date getEnrollment_date() {
        return enrollment_date;
    }

    public void setEnrollment_date(Date enrollment_date) {
        this.enrollment_date = enrollment_date;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public List<CourseSelection> getCourseSelections() {
        return courseSelections;
    }

    public void setCourseSelections(List<CourseSelection> courseSelections) {
        this.courseSelections = courseSelections;
    }

    @Override
    public String toString() {
        return "Student{" +
                "student_id=" + student_id +
                ", student_number='" + student_number + '\'' +
                ", student_name='" + student_name + '\'' +
                ", college='" + college + '\'' +
                ", enrollment_date=" + enrollment_date +
                ", photo_path='" + photo_path + '\'' +
                '}';
    }
}