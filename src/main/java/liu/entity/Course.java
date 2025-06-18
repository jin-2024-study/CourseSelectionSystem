package liu.entity;

/**
 * @description: 课程表
 * @author: liu jin
 * @create: 2025-04-18 19:37
 **/
import java.util.List;

public class Course {
   private int course_id;
   private String course_code;
   private String course_name;
   private String course_type;
   private String semester;
   private int credit_hours;

   private double credits;

   public Course() {
   }

   public Course(int course_id, String course_code, String course_name, String course_type, String semester, int credit_hours, double credits) {
      this.course_id = course_id;
      this.course_code = course_code;
      this.course_name = course_name;
      this.course_type = course_type;
      this.semester = semester;
      this.credit_hours = credit_hours;
      this.credits = credits;
   }

   public int getCourse_id() {
      return course_id;
   }

   public void setCourse_id(int course_id) {
      this.course_id = course_id;
   }

   public String getCourse_code() {
      return course_code;
   }

   public void setCourse_code(String course_code) {
      this.course_code = course_code;
   }

   public String getCourse_name() {
      return course_name;
   }

   public void setCourse_name(String course_name) {
      this.course_name = course_name;
   }

   public String getCourse_type() {
      return course_type;
   }

   public void setCourse_type(String course_type) {
      this.course_type = course_type;
   }

   public String getSemester() {
      return semester;
   }

   public void setSemester(String semester) {
      this.semester = semester;
   }

   public int getCredit_hours() {
      return credit_hours;
   }

   public void setCredit_hours(int credit_hours) {
      this.credit_hours = credit_hours;
   }

   public double getCredits() {
      return credits;
   }

   public void setCredits(double credits) {
      this.credits = credits;
   }

   @Override
   public String toString() {
      return "Course{" +
              "course_id=" + course_id +
              ", course_code='" + course_code + '\'' +
              ", course_name='" + course_name + '\'' +
              ", course_type='" + course_type + '\'' +
              ", semester='" + semester + '\'' +
              ", credit_hours=" + credit_hours +
              ", credits=" + credits +
              '}';
   }
}