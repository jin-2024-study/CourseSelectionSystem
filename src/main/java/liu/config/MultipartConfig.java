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
     * Spring Boot 2.x 使用内置的StandardServletMultipartResolver
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

    // 注释掉CommonsMultipartResolver，使用Spring Boot内置的StandardServletMultipartResolver
    // Spring Boot 2.x 默认使用StandardServletMultipartResolver，不需要额外配置
    /*
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        
        // 设置编码
        resolver.setDefaultEncoding("UTF-8");
        
        // 设置最大上传文件大小
        resolver.setMaxUploadSize(100 * 1024 * 1024); // 100MB
        resolver.setMaxUploadSizePerFile(10 * 1024 * 1024); // 10MB per file
        
        // 设置内存阈值，超过此值将写入临时文件
        resolver.setMaxInMemorySize(1024); // 1KB
        
        // 启用延迟解析
        resolver.setResolveLazily(true);
        
        return resolver;
    }
    */
} 