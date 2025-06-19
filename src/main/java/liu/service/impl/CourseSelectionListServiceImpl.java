package liu.service.impl;


import liu.dao.CourseSelectionListDao;
import liu.entity.CourseSelectionList;
import liu.service.CourseSelectionListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseSelectionListServiceImpl implements CourseSelectionListService {
    
    @Autowired
    private CourseSelectionListDao courseSelectionListDao;

    
    @Override
    public CourseSelectionList getCourseSelectionListById(int listId) {
        return courseSelectionListDao.getCourseSelectionListById(listId);
    }
    
    @Override
    public List<CourseSelectionList> getAllCourseSelectionLists() {
        return courseSelectionListDao.getCourseSelectionLists();
    }
    
    @Override
    @Transactional
    public boolean insertCourseSelectionList(CourseSelectionList courseSelectionList) {
        try {
            int result = courseSelectionListDao.insertCourseSelectionList(courseSelectionList);
            if (result <= 0) {
                throw new RuntimeException("插入选课详情失败");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("插入选课详情时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean updateCourseSelectionList(CourseSelectionList courseSelectionList) {
        try {
            int result = courseSelectionListDao.updateCourseSelectionList(courseSelectionList);
            if (result <= 0) {
                throw new RuntimeException("更新选课详情" + courseSelectionList.getList_id() + "失败");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新选课详情时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteCourseSelectionList(int detailId) {
        try {
            int result = courseSelectionListDao.deleteCourseSelectionList(detailId);
            if (result <= 0) {
                throw new RuntimeException("删除选课详情" + detailId + "失败");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除选课详情时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<CourseSelectionList> getListsBySelectionId(int selectionId) {
        return courseSelectionListDao.getListsByCourseSelectionId(selectionId);
    }
    
    @Override
    public List<CourseSelectionList> getListByCourseId(int courseId) {
        return courseSelectionListDao.getListsByCourseId(courseId);
    }
    
    @Override
    public CourseSelectionList getListByCourseSelectionAndCourse(int selectionId, int courseId) {
        return courseSelectionListDao.getListByCourseSelectionAndCourse(selectionId, courseId);
    }
} 