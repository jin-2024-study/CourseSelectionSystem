package liu.config;

import liu.util.SqlTimeInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis配置类
 * 用于配置MyBatis相关的Bean，包括拦截器注册
 */
@Configuration
public class MyBatisConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * 配置SqlSessionFactory
     * 注册自定义拦截器
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // 设置mapper文件位置
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:liu/mapper/*.xml")
        );
        
        // 设置实体类别名包
        sessionFactory.setTypeAliasesPackage("liu.entity");
        
        // 注册拦截器
        sessionFactory.setPlugins(new SqlTimeInterceptor());
        
        return sessionFactory.getObject();
    }
} 