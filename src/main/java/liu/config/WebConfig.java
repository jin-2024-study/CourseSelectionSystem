package liu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 学生照片静态资源映射
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadPath.toString() + "/";
        
        registry.addResourceHandler("/student_photos/**")
                .addResourceLocations(uploadLocation + "student_photos/");
        
        System.out.println("配置静态资源映射:");
        System.out.println("- 照片URL路径: /student_photos/**");
        System.out.println("- 照片存储位置: " + uploadLocation + "student_photos/");
    }
} 















