package liu.controller;

import liu.entity.Student;
import liu.service.FileService;
import liu.service.StudentService;
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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/students/{studentId}/photo")
public class StudentPhotoController {

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
        System.out.println("开始处理学生照片上传请求，学生ID: " + studentId);
        System.out.println("上传的文件名: " + photo.getOriginalFilename());
        System.out.println("文件大小: " + photo.getSize() + " 字节");
        System.out.println("文件类型: " + photo.getContentType());
        
        try {
            // 获取学生信息
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                System.err.println("学生不存在，ID: " + studentId);
                redirectAttributes.addFlashAttribute("error", "学生不存在");
                return "redirect:/courseSelection";
            }
            System.out.println("获取到学生信息: " + student);
            System.out.println("当前照片路径: " + student.getPhoto_path());
            
            // 如果学生已有照片，删除原来的照片
            if (student.getPhoto_path() != null && !student.getPhoto_path().isEmpty()) {
                System.out.println("准备删除原照片: " + student.getPhoto_path());
                boolean deleted = fileService.deleteFile(student.getPhoto_path());
                System.out.println("原照片删除结果: " + (deleted ? "成功" : "失败"));
            }
            
            // 保存新照片
            System.out.println("准备保存新照片");
            String photoPath = fileService.saveStudentPhoto(studentId, photo);
            System.out.println("新照片保存路径: " + photoPath);
            
            // 更新学生信息
            System.out.println("准备更新学生信息 - 添加照片路径");
            student.setPhoto_path(photoPath);
            boolean updateResult = studentService.updateStudent(student);
            System.out.println("学生信息更新结果: " + (updateResult ? "成功" : "失败"));
            
            if (!updateResult) {
                System.err.println("数据库更新失败");
                redirectAttributes.addFlashAttribute("error", "照片上传成功，但数据库更新失败");
                return "redirect:/students/" + studentId + "/photo/upload";
            }
            
            // 重新查询学生信息，确认照片路径已更新
            Student updatedStudent = studentService.getStudentById(studentId);
            System.out.println("更新后的学生照片路径: " + (updatedStudent != null ? updatedStudent.getPhoto_path() : "null"));
            
            redirectAttributes.addFlashAttribute("success", "照片上传成功");
            return "redirect:/students/" + studentId;
        } catch (IOException e) {
            System.err.println("照片上传过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "照片上传失败: " + e.getMessage());
            return "redirect:/students/" + studentId + "/photo/upload";
        }
    }

    /**
     * 查看学生照片
     */
    @GetMapping
    public ResponseEntity<Resource> viewStudentPhoto(@PathVariable Integer studentId) {
        System.out.println("接收到查看学生照片请求，学生ID: " + studentId);
        try {
            // 获取学生信息
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                System.err.println("学生不存在，ID: " + studentId);
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("获取到学生信息: " + student);
            System.out.println("照片路径: " + student.getPhoto_path());
            
            // 检查照片路径
            if (student.getPhoto_path() == null || student.getPhoto_path().isEmpty()) {
                System.err.println("学生无照片");
                return ResponseEntity.notFound().build();
            }
            
            // 获取照片资源
            System.out.println("准备获取照片资源: " + student.getPhoto_path());
            Resource resource;
            try {
                // 使用FileService获取照片
                resource = fileService.viewFile(student.getPhoto_path());
                
                // 检查资源是否存在
                if (resource == null || !resource.exists()) {
                    System.err.println("照片文件不存在: " + student.getPhoto_path());
                    return ResponseEntity.notFound().build();
                }
                
            } catch (IOException e) {
                System.err.println("无法获取照片资源: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("照片文件存在，文件名: " + resource.getFilename());
            
            String contentType = determineContentType(resource.getFilename());
            System.out.println("设置Content-Type: " + contentType);
            
            // 返回照片资源
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            System.err.println("查看照片时发生错误: " + e.getMessage());
            e.printStackTrace();
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
                System.err.println("无法下载照片: " + e.getMessage());
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
            System.err.println("下载照片时发生错误: " + e.getMessage());
            e.printStackTrace();
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