package liu.service.impl;

import liu.dao.CourseSelectionDao;
import liu.dao.CourseSelectionListDao;
import liu.entity.CourseSelection;
import liu.entity.CourseSelectionList;
import liu.service.CourseSelectionService;
import liu.service.CourseSelectionListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseSelectionServiceImpl implements CourseSelectionService {
    
    @Autowired
    private CourseSelectionDao courseSelectionDao;
    
    @Autowired
    private CourseSelectionListService CourseSelectionListService;
    
    @Override
    public CourseSelection findCourseSelectAndStudent() {
        return courseSelectionDao.findCourseSelectionStudent();
    }
    
    @Override
    public CourseSelection findCourseSelectionAndList() {
        return courseSelectionDao.findCourseSelectionAndList();
    }
    
    @Override
    public CourseSelection getCourseSelectionById(int selectionId) {
        return courseSelectionDao.getCourseSelectionById(selectionId);
    }
    
    @Override
    public List<CourseSelection> getAllCourseSelections() {
        return courseSelectionDao.getAllCourseSelection();
    }
    
    @Override
    @Transactional
    public boolean insertCourseSelection(CourseSelection courseSelection) {
        return courseSelectionDao.insertCourseSelection(courseSelection) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateCourseSelection(CourseSelection courseSelection) {
        return courseSelectionDao.updateCourseSelection(courseSelection) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteCourseSelection(int selectionId) {
        try {
            // 1. 查询关联的选课详情
            List<CourseSelectionList> details = CourseSelectionListService.getListsBySelectionId(selectionId);
            
            // 2. 级联删除选课详情
            if (details != null && !details.isEmpty()) {
                for (CourseSelectionList detail : details) {
                    boolean result = CourseSelectionListService.deleteCourseSelectionList(detail.getList_id());
                    if (!result) {
                        throw new RuntimeException("删除选课记录" + selectionId + "关联的选课详情" + detail.getList_id() + "失败");
                    }
                }
            }
            
            // 3. 删除选课记录
            int result = courseSelectionDao.deleteCourseSelection(selectionId);
            if (result <= 0) {
                throw new RuntimeException("删除选课记录" + selectionId + "失败");
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 由于添加了@Transactional注解，发生异常时会自动回滚事务
            throw new RuntimeException("删除选课记录及相关数据时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<CourseSelection> getCourseSelectionsByStudentId(int studentId) {
        return courseSelectionDao.getCourseSelectionByStudentId(studentId);
    }

}