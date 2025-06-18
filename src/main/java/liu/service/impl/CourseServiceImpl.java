package liu.service.impl;

import liu.dao.CourseDao;
import liu.entity.Course;
import liu.entity.CourseSelectionList;
import liu.service.CourseService;
import liu.service.CourseSelectionListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private CourseSelectionListService CourseSelectionListService;
    
    @Override
    public Course getCourseById(int courseId) {
        return courseDao.getCourseById(courseId);
    }
    
    @Override
    public List<Course> getAllCourses() {
        return courseDao.getAllCourses();
    }
    
    @Override
    @Transactional
    public boolean insertCourse(Course course) {
        try {
            int result = courseDao.insertCourse(course);
            if (result <= 0) {
                throw new RuntimeException("插入课程失败");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("插入课程时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean updateCourse(Course course) {
        try {
            int result = courseDao.updateCourse(course);
            if (result <= 0) {
                throw new RuntimeException("更新课程" + course.getCourse_id() + "失败");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新课程时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteCourse(int courseId) {
        try {
            // 1. 查询与课程关联的选课详情
            List<CourseSelectionList> details = CourseSelectionListService.getListByCourseId(courseId);
            
            // 2. 批量删除选课详情（级联删除）
            if (details != null && !details.isEmpty()) {
                for (CourseSelectionList detail : details) {
                    boolean detailDeleteResult = CourseSelectionListService.deleteCourseSelectionList(detail.getList_id());
                    if (!detailDeleteResult) {
                        throw new RuntimeException("删除课程" + courseId + "关联的选课详情" + detail.getList_id() + "失败");
                    }
                }
            }
            
            // 3. 删除课程
            int result = courseDao.deleteCourse(courseId);
            if (result <= 0) {
                throw new RuntimeException("删除课程" + courseId + "失败");
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 事务会自动回滚
            throw new RuntimeException("删除课程及关联数据时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Course getCourseByCode(String courseCode) {
        return courseDao.getCourseByCode(courseCode);
    }
    
    @Override
    public List<Course> getCoursesByType(String courseType) {
        return courseDao.getCoursesByType(courseType);
    }
    
    @Override
    public List<Course> getCoursesBySemester(String semester) {
        return courseDao.getCoursesBySemester(semester);
    }
} 