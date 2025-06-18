package liu.service;

import liu.entity.Student;
import java.util.List;

public interface StudentService {
    Student getStudentById(int studentId);
    
    List<Student> getAllStudents();
    
    boolean insertStudent(Student student);
    
    boolean updateStudent(Student student);
    
    boolean deleteStudent(int studentId);
    
    Student getStudentByNumber(String studentNumber);
    
    List<Student> getStudentsByCollege(String college);
    
    List<Student> findStudentAndCourseInfo();
} 