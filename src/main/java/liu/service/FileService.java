package liu.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件服务接口，用于处理文件上传和下载
 */
public interface FileService {
    
    /**
     * 保存学生照片
     * @param studentId 学生ID
     * @param file 上传的文件
     * @return 存储的文件路径
     */
    String saveStudentPhoto(Integer studentId, MultipartFile file) throws IOException;
    
    /**
     * 下载文件
     * @param filePath 文件路径
     * @return 资源
     */
    Resource downloadFile(String filePath) throws IOException;
    
    /**
     * 查看文件
     * @param filePath 文件路径
     * @return 资源
     */
    Resource viewFile(String filePath) throws IOException;
    
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
} 