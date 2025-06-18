package liu.service.impl;

import liu.dao.StudentDao;
import liu.entity.CourseSelection;
import liu.entity.Student;
import liu.service.StudentService;
import liu.service.CourseSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    @Override
    public Student getStudentById(int studentId) {
        return studentDao.getStudentById(studentId);
    }
    
    @Override
    public List<Student> getAllStudents() {
        return studentDao.getAllStudents();
    }
    
    @Override
    @Transactional
    public boolean insertStudent(Student student) {
        System.out.println("StudentService.insertStudent 开始执行，学生对象: " + student);
        System.out.println("插入前 student_id: " + student.getStudent_id());
        
        int result = studentDao.insertStudent(student);
        
        System.out.println("StudentDao.insertStudent 返回结果: " + result);
        System.out.println("插入后 student_id: " + student.getStudent_id());
        
        boolean success = result > 0;
        System.out.println("StudentService.insertStudent 执行结果: " + success);
        
        return success;
    }
    
    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        return studentDao.updateStudent(student) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteStudent(int studentId) {
        try {
            // 1. 查询该学生的所有选课记录
            List<CourseSelection> courseSelections = courseSelectionService.getCourseSelectionsByStudentId(studentId);
            
            // 2. 级联删除所有关联的选课记录（EnrollmentService 会处理选课详情的删除）
            if (courseSelections != null && !courseSelections.isEmpty()) {
                for (CourseSelection courseSelection : courseSelections) {
                    boolean result = courseSelectionService.deleteCourseSelection(courseSelection.getSelection_id());
                    if (!result) {
                        throw new RuntimeException("删除学生" + studentId + "关联的选课记录" + courseSelection.getSelection_id() + "失败");
                    }
                }
            }
            
            // 3. 最后删除学生记录
            int result = studentDao.deleteStudent(studentId);
            if (result <= 0) {
                throw new RuntimeException("删除学生" + studentId + "失败");
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 由于添加了@Transactional注解，发生异常时会自动回滚事务
            throw new RuntimeException("删除学生及相关数据时发生错误: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Student getStudentByNumber(String studentNumber) {
        return studentDao.getStudentByNumber(studentNumber);
    }
    
    @Override
    public List<Student> getStudentsByCollege(String college) {
        return studentDao.getStudentsByCollege(college);
    }
    
    @Override
    public List<Student> findStudentAndCourseInfo() {
        return studentDao.findStudentAndCourseResultMap();
    }
} 