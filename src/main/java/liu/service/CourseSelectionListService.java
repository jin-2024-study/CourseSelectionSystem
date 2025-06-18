package liu.service;

import liu.entity.CourseSelectionList;

import java.util.List;

public interface CourseSelectionListService {
    CourseSelectionList getCourseSelectionListById(int detailId);
    
    List<CourseSelectionList> getAllCourseSelectionLists();
    
    boolean insertCourseSelectionList(CourseSelectionList courseSelectionList);
    
    boolean updateCourseSelectionList(CourseSelectionList courseSelectionList);
    
    boolean deleteCourseSelectionList(int detailId);
    
    List<CourseSelectionList> getListsBySelectionId(int selectionId);
    
    List<CourseSelectionList> getListByCourseId(int courseId);
    
    CourseSelectionList getListByCourseSelectionAndCourse(int selectionId, int courseId);
} 