package liu.dao;

import liu.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentDao {
    List<Student> findStudentAndCourseResultMap();
    
    Student getStudentById(int studentId);
    
    List<Student> getAllStudents();
    
    int insertStudent(Student student);
    
    int updateStudent(Student student);
    
    int deleteStudent(int studentId);
    
    Student getStudentByNumber(String studentNumber);
    
    List<Student> getStudentsByCollege(String college);
}
