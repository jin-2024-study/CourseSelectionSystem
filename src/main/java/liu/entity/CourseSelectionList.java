package liu.entity;

/**
 * @description: 选课清单表
 * @author: liu jin
 * @create: 2025-04-18 19:37
 **/
public class CourseSelectionList {
    private int list_id;
    private int selection_id;
    private int course_id;

    private Course course;


    public CourseSelectionList() {
    }


    public CourseSelectionList(int list_id, int selection_id, int course_id, Course course) {
        this.list_id = list_id;
        this.selection_id = selection_id;
        this.course_id = course_id;
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    public int getSelection_id() {
        return selection_id;
    }

    public void setSelection_id(int selection_id) {
        this.selection_id = selection_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    @Override
    public String toString() {
        return "CourseSelectionList{" +
                "list_id=" + list_id +
                ", selection_id=" + selection_id +
                ", course_id=" + course_id +
                ", course=" + course +
                '}';
    }
}
