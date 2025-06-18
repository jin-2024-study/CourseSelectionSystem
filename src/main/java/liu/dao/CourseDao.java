package liu.dao;

import liu.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CourseDao {
    Course getCourseById(int courseId);
    
    List<Course> getAllCourses();
    
    int insertCourse(Course course);

    int updateCourse(Course course);
    
    int deleteCourse(int courseId);
    
    Course getCourseByCode(String courseCode);
    
    List<Course> getCoursesByType(String courseType);
    
    List<Course> getCoursesBySemester(String semester);
}
