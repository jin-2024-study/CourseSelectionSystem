package liu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * 选课通知实体类
 * @author liu
 */
public class CourseSelectionNotification {
    
    public enum NotificationType {
        COURSE_SELECTION_SUCCESS("选课成功"),
        COURSE_SELECTION_FAILED("选课失败"),
        COURSE_CHANGE("课程变更"),
        COURSE_DELETION("课程删除"),
        NEW_SEMESTER_AVAILABLE("新学期开放选课");
        
        private final String description;
        
        NotificationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private Long id; // 数据库主键
    private Integer studentId;
    private String studentName;
    private String studentNumber;
    private NotificationType type;
    private String title;
    private String message;
    private String courseInfo; // 课程相关信息
    private String academicYear;
    private String semester;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt; // 数据库创建时间
    
    // 默认构造函数（用于反序列化）
    public CourseSelectionNotification() {
        this.timestamp = LocalDateTime.now();
    }
    
    public CourseSelectionNotification(Integer studentId, String studentName, String studentNumber, 
                                     NotificationType type, String title, String message) {
        this();
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.type = type;
        this.title = title;
        this.message = message;
    }
    
    // Getters and Setters
    public Integer getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getCourseInfo() {
        return courseInfo;
    }
    
    public void setCourseInfo(String courseInfo) {
        this.courseInfo = courseInfo;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "CourseSelectionNotification{" +
                "studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", courseInfo='" + courseInfo + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", semester='" + semester + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 