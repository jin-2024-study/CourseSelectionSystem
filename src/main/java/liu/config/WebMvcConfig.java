package liu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SpringMVC Web配置类
 * 兼容Spring Boot的自动配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置CSS文件访问
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/", "/css/");
        
        // 配置JavaScript文件访问
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/", "/js/");
                
        // 配置图片文件访问
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", "/images/");
                
        // 配置字体文件访问
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/", "/fonts/");
    }
    
    /**
     * 支持PUT和DELETE HTTP方法的过滤器
     * 用于在表单中通过_method参数模拟这些HTTP方法
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
} 