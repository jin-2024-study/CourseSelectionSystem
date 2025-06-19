package liu.service.impl;

import liu.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    
    private final Path fileStorageLocation;
    
    public FileServiceImpl() {
        // 设置文件存储的基础路径（在当前工作目录下的uploads文件夹）
        // 尝试先使用user.dir，避免使用Tomcat的catalina.base可能导致的权限问题
        String currentDir = System.getProperty("user.dir");
        this.fileStorageLocation = Paths.get(currentDir, "uploads").toAbsolutePath().normalize();
        try {
            // 创建目录（如果不存在）
            Files.createDirectories(this.fileStorageLocation);
            logger.info("文件存储目录初始化成功: {}", this.fileStorageLocation);
            
            // 测试目录是否可写
            Path testFile = this.fileStorageLocation.resolve("test_write.txt");
            Files.write(testFile, "测试写入权限".getBytes());
            logger.info("文件存储目录写入测试成功");
            Files.deleteIfExists(testFile);
            
            // 确保学生照片目录存在
            Path studentPhotosDir = this.fileStorageLocation.resolve("student_photos");
            Files.createDirectories(studentPhotosDir);
            logger.info("学生照片目录创建/确认成功: {}", studentPhotosDir);
        } catch (IOException ex) {
            logger.error("无法创建或访问文件上传目录: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法创建或访问文件上传目录: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public String saveStudentPhoto(Integer studentId, MultipartFile file) throws IOException {
        logger.info("开始保存学生照片，学生ID: {}", studentId);
        
        if (file.isEmpty()) {
            logger.error("上传的文件为空");
            throw new IOException("文件为空");
        }
        
        logger.debug("文件大小: {} 字节", file.getSize());
        logger.debug("文件类型: {}", file.getContentType());
        
        try {
            // 创建学生照片目录
            Path studentPhotosDir = this.fileStorageLocation.resolve("student_photos");
            Files.createDirectories(studentPhotosDir);
            logger.debug("学生照片目录创建/确认成功: {}", studentPhotosDir);
            
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            logger.debug("原始文件名: {}", originalFilename);
            
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 将jfif扩展名转换为jpg以提高兼容性
            if (".jfif".equalsIgnoreCase(fileExtension)) {
                fileExtension = ".jpg";
            }
            
            String fileName = studentId + "_" + UUID.randomUUID().toString() + fileExtension;
            logger.debug("生成的文件名: {}", fileName);
            
            // 保存文件
            Path targetPath = studentPhotosDir.resolve(fileName);
            logger.debug("目标文件路径: {}", targetPath);
            
            // 使用try-with-resources确保输入流被正确关闭
            try (java.io.InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            logger.info("文件保存成功: {}", targetPath);
            
            // 检查文件是否存在
            if (Files.exists(targetPath)) {
                logger.debug("文件确认存在，大小: {} 字节", Files.size(targetPath));
            } else {
                logger.error("文件保存后不存在");
                throw new IOException("文件保存失败");
            }
            
            // 返回相对路径
            String relativePath = "student_photos/" + fileName;
            logger.debug("返回的相对路径: {}", relativePath);
            return relativePath;
        } catch (IOException e) {
            logger.error("保存学生照片时发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public Resource downloadFile(String filePath) throws IOException {
        try {
            // 常规路径尝试
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            logger.debug("尝试加载文件: {}", file.toAbsolutePath());
            
            // 检查文件是否存在
            if (!Files.exists(file)) {
                logger.warn("文件不存在: {}", file.toAbsolutePath());
                
                // 尝试修复路径 - 检查是否为jfif文件，如果是，尝试寻找同名的jpg文件
                if (filePath.toLowerCase().endsWith(".jfif")) {
                    String jpgPath = filePath.substring(0, filePath.length() - 5) + ".jpg";
                    Path jpgFile = this.fileStorageLocation.resolve(jpgPath).normalize();
                    logger.debug("尝试寻找对应的jpg文件: {}", jpgFile.toAbsolutePath());
                    
                    if (Files.exists(jpgFile)) {
                        logger.info("找到对应的jpg文件，使用替代文件");
                        file = jpgFile;
                    }
                }
                
                // 如果文件还是不存在，尝试寻找同ID的其他文件
                if (!Files.exists(file) && filePath.contains("_")) {
                    String idPrefix = filePath.substring(0, filePath.indexOf("_") + 1);
                    logger.debug("尝试寻找同ID前缀的文件: {}", idPrefix);
                    
                    File dir = this.fileStorageLocation.resolve("student_photos").toFile();
                    File[] matchingFiles = dir.listFiles((d, name) -> name.startsWith(idPrefix));
                    
                    if (matchingFiles != null && matchingFiles.length > 0) {
                        logger.info("找到{}个匹配的文件，使用第一个", matchingFiles.length);
                        file = matchingFiles[0].toPath();
                    }
                }
                
                // 如果还是找不到，抛出异常
                if (!Files.exists(file)) {
                    throw new IOException("无法读取文件: " + filePath);
                }
            }
            
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                logger.debug("成功加载文件资源: {}", resource.getFilename());
                return resource;
            } else {
                logger.error("文件存在但无法读取: {}", file.toAbsolutePath());
                throw new IOException("无法读取文件: " + filePath);
            }
        } catch (MalformedURLException e) {
            logger.error("文件URL格式错误: {}", e.getMessage(), e);
            throw new IOException("无法读取文件: " + filePath, e);
        }
    }
    
    @Override
    public Resource viewFile(String filePath) throws IOException {
        return downloadFile(filePath);
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            logger.error("删除文件时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }
} 