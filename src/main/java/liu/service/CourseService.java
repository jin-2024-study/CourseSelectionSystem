package liu.service;

import liu.entity.Course;
import java.util.List;

public interface CourseService {
    Course getCourseById(int courseId);
    
    List<Course> getAllCourses();
    
    boolean insertCourse(Course course);
    
    boolean updateCourse(Course course);
    
    boolean deleteCourse(int courseId);
    
    Course getCourseByCode(String courseCode);
    
    List<Course> getCoursesByType(String courseType);
    
    List<Course> getCoursesBySemester(String semester);
} 