package liu.dao;

import liu.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseSelectionDao {
    CourseSelection findCourseSelectionStudent();
    CourseSelection findCourseSelectionAndList();
    
    CourseSelection getCourseSelectionById(int selectionId);
    
    List<CourseSelection> getAllCourseSelection();
    
    int insertCourseSelection(CourseSelection courseSelection);
    
    int updateCourseSelection(CourseSelection courseSelection);
    
    int deleteCourseSelection(int selection_id);

    List<CourseSelection> getCourseSelectionByStudentId(int studentId);
    

}
