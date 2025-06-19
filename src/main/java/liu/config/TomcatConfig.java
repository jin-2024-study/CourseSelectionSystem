package liu.config;

import org.apache.catalina.session.StandardManager;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat配置类
 * 自定义Tomcat会话管理，解决会话序列化问题
 */
@Configuration
public class TomcatConfig {

    /**
     * 定制Tomcat服务器，禁用会话持久化
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addContextCustomizers(context -> {
                // 获取会话管理器
                StandardManager manager = new StandardManager();
                // 禁用会话持久化到磁盘
                manager.setPathname(null);
                // 设置会话管理器
                context.setManager(manager);
            });
        };
    }
} 















