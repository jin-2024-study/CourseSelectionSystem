package liu.service;

import liu.entity.CourseSelection;

import java.util.List;

public interface CourseSelectionService {
    CourseSelection findCourseSelectAndStudent();
    CourseSelection findCourseSelectionAndList();
    
    CourseSelection getCourseSelectionById(int SelectionId);
    
    List<CourseSelection> getAllCourseSelections();
    
    boolean insertCourseSelection(CourseSelection courseSelection);
    
    boolean updateCourseSelection(CourseSelection courseSelection);
    
    boolean deleteCourseSelection(int SelectionId);
    
    List<CourseSelection> getCourseSelectionsByStudentId(int studentId);
    

}