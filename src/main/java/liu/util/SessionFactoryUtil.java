package liu.util;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SessionFactoryUtil {
    private SessionFactoryUtil(){}
    static SqlSessionFactory factory;
    static {
        String path="F:\\JavaEE\\TestMybatis\\src\\main\\resources\\mybatis-config.xml";
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        try {
            factory=builder.build(new FileInputStream(new File(path) ));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static SqlSession openSession(){
        return factory.openSession();
    }
}
