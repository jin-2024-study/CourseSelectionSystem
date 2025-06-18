package liu.entity;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description: 选课表
 * @author: liu jin
 * @create: 2025-04-18 19:33
 **/
public class CourseSelection {
    private int selection_id;
    private int student_id;
    private String academic_year;
    private String semester;
    


    private List<CourseSelectionList> courseSelectionLists;
    private Student student;

    public CourseSelection() {
    }

    public CourseSelection(int selection_id, int student_id, String academic_year, String semester, Student student) {
        this.selection_id = selection_id;
        this.student_id = student_id;
        this.academic_year = academic_year;
        this.semester = semester;
        this.student = student;
    }


    public int getSelection_id() {
        return selection_id;
    }

    public void setSelection_id(int selection_id) {
        this.selection_id = selection_id;
    }

    public List<CourseSelectionList> getCourseSelectionLists() {
        return courseSelectionLists;
    }

    public void setCourseSelectionLists(List<CourseSelectionList> courseSelectionLists) {
        this.courseSelectionLists = courseSelectionLists;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }



    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<CourseSelectionList> getEnrollmentDetails() {
        return courseSelectionLists;
    }

    public void setEnrollmentDetails(List<CourseSelectionList> courseSelectionLists) {
        this.courseSelectionLists = courseSelectionLists;
    }

    @Override
    public String toString() {
        return "CourseSelection{" +
                "selection_id=" + selection_id +
                ", student_id=" + student_id +
                ", academic_year='" + academic_year + '\'' +
                ", semester='" + semester + '\'' +
                ", courseSelectionLists=" + courseSelectionLists +
                ", student=" + student +
                '}';
    }
}
