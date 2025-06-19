package liu.controller;

import liu.entity.Student;
import liu.service.FileService;
import liu.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 学生照片控制器
 * 处理学生照片的上传、查看、下载等功能
 */
@Controller
@RequestMapping("/students/{studentId}/photo")
public class StudentPhotoController {

    private static final Logger logger = LoggerFactory.getLogger(StudentPhotoController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private FileService fileService;

    /**
     * 显示上传学生照片的页面
     */
    @GetMapping("/upload")
    public String showUploadForm(@PathVariable Integer studentId, Model model) {
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            return "redirect:/courseSelection";
        }
        
        model.addAttribute("student", student);
        return "student/uploadPhoto";
    }

    /**
     * 处理照片上传请求
     */
    @PostMapping
    public String handlePhotoUpload(@PathVariable Integer studentId,
                                   @RequestParam("photo") MultipartFile photo,
                                   RedirectAttributes redirectAttributes) {
        try {
            logger.info("开始处理学生照片上传请求，学生ID: {}", studentId);
            logger.debug("上传的文件名: {}", photo.getOriginalFilename());
            logger.debug("文件大小: {} 字节", photo.getSize());
            logger.debug("文件类型: {}", photo.getContentType());
            
            // 获取学生信息
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                logger.error("学生不存在，ID: {}", studentId);
                redirectAttributes.addFlashAttribute("error", "学生不存在");
                return "redirect:/courseSelection";
            }
            logger.debug("获取到学生信息: {}", student);
            logger.debug("当前照片路径: {}", student.getPhoto_path());
            
            // 如果学生已有照片，删除原来的照片
            if (student.getPhoto_path() != null && !student.getPhoto_path().isEmpty()) {
                logger.debug("准备删除原照片: {}", student.getPhoto_path());
                boolean deleted = fileService.deleteFile(student.getPhoto_path());
                logger.debug("原照片删除结果: {}", deleted ? "成功" : "失败");
            }
            
            // 保存新照片
            logger.debug("准备保存新照片");
            String photoPath = fileService.saveStudentPhoto(studentId, photo);
            logger.info("新照片保存路径: {}", photoPath);
            
            // 更新学生信息
            logger.debug("准备更新学生信息 - 添加照片路径");
            student.setPhoto_path(photoPath);
            boolean updateResult = studentService.updateStudent(student);
            logger.debug("学生信息更新结果: {}", updateResult ? "成功" : "失败");
            
            if (!updateResult) {
                logger.error("数据库更新失败");
                redirectAttributes.addFlashAttribute("error", "照片上传成功，但数据库更新失败");
                return "redirect:/students/" + studentId + "/photo/upload";
            }
            
            // 重新查询学生信息，确认照片路径已更新
            Student updatedStudent = studentService.getStudentById(studentId);
            logger.debug("更新后的学生照片路径: {}", 
                    updatedStudent != null ? updatedStudent.getPhoto_path() : "null");
            
            redirectAttributes.addFlashAttribute("success", "照片上传成功");
            return "redirect:/students/" + studentId;
        } catch (IOException e) {
            logger.error("照片上传过程中发生错误: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "照片上传失败: " + e.getMessage());
            return "redirect:/students/" + studentId + "/photo/upload";
        }
    }

    /**
     * 查看学生照片
     */
    @GetMapping
    public ResponseEntity<Resource> viewStudentPhoto(@PathVariable Integer studentId) {
        try {
            logger.debug("接收到查看学生照片请求，学生ID: {}", studentId);
            // 获取学生信息
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                logger.error("学生不存在，ID: {}", studentId);
                return ResponseEntity.notFound().build();
            }
            
            logger.debug("获取到学生信息: {}", student);
            logger.debug("照片路径: {}", student.getPhoto_path());
            
            // 检查照片路径
            if (student.getPhoto_path() == null || student.getPhoto_path().isEmpty()) {
                logger.warn("学生无照片");
                return ResponseEntity.notFound().build();
            }
            
            // 获取照片资源
            logger.debug("准备获取照片资源: {}", student.getPhoto_path());
            Resource resource;
            try {
                // 使用FileService获取照片
                resource = fileService.viewFile(student.getPhoto_path());
                
                // 检查资源是否存在
                if (resource == null || !resource.exists()) {
                    logger.error("照片文件不存在: {}", student.getPhoto_path());
                    return ResponseEntity.notFound().build();
                }
                
            } catch (IOException e) {
                logger.error("无法获取照片资源: {}", e.getMessage(), e);
                return ResponseEntity.notFound().build();
            }
            
            logger.debug("照片文件存在，文件名: {}", resource.getFilename());
            
            String contentType = determineContentType(resource.getFilename());
            logger.debug("设置Content-Type: {}", contentType);
            
            // 返回照片资源
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            logger.error("查看照片时发生错误: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 下载学生照片
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadStudentPhoto(@PathVariable Integer studentId) {
        try {
            Student student = studentService.getStudentById(studentId);
            if (student == null || student.getPhoto_path() == null || student.getPhoto_path().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource;
            try {
                resource = fileService.downloadFile(student.getPhoto_path());
            } catch (IOException e) {
                logger.error("无法下载照片: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            String filename = "student_" + studentId + "_photo";
            
            // 从资源文件名中提取扩展名
            if (resource.getFilename() != null && resource.getFilename().contains(".")) {
                String extension = resource.getFilename().substring(resource.getFilename().lastIndexOf("."));
                filename += extension;
            }
            
            // 设置Content-Disposition头，使浏览器弹出下载框
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            String contentDisposition = "attachment; filename=\"" + encodedFilename + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            logger.error("下载照片时发生错误: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据文件名确定内容类型
     */
    private String determineContentType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }
        
        String lowercase = filename.toLowerCase();
        if (lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg") || lowercase.endsWith(".jfif")) {
            return "image/jpeg";
        } else if (lowercase.endsWith(".png")) {
            return "image/png";
        } else if (lowercase.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
} 