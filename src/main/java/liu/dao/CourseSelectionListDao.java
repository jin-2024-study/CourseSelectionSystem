package liu.dao;

import liu.entity.CourseSelectionList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseSelectionListDao {
    CourseSelectionList getCourseSelectionListById(int listId);
    
    List<CourseSelectionList> getCourseSelectionLists();
    
    int insertCourseSelectionList(CourseSelectionList courseSelectionList);
    
    int updateCourseSelectionList(CourseSelectionList courseSelectionList);
    
    int deleteCourseSelectionList(int listId);

    List<CourseSelectionList> getListsByCourseSelectionId(int SelectionId);
    
    // 为懒加载提供的方法
    List<CourseSelectionList> getListBySelectionId(int selectionId);

    List<CourseSelectionList> getListsByCourseId(int courseId);

    CourseSelectionList getListByCourseSelectionAndCourse(int SelectionId, int courseId);

}
