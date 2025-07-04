package liu.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * 文件上传配置类
 * 使用Spring Boot内置的multipart支持，避免与CommonsMultipartResolver冲突
 */
@Configuration
public class MultipartConfig {

    /**
     * 配置文件上传参数
     * 使用内置的StandardServletMultipartResolver
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // 设置文件大小限制
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        
        // 设置临时文件存储位置（使用相对路径，避免权限问题）
        factory.setLocation("target/temp");
        
        // 设置文件大小阈值，超过此值将写入临时文件
        factory.setFileSizeThreshold(DataSize.ofKilobytes(1));
        
        return factory.createMultipartConfig();
    }
} 