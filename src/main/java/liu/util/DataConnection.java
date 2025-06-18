package liu.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @description:
 * @author: liu jin
 * @create: 2025-04-06 17:52
 **/
public class DataConnection {
    private String resource = "mybatis-config.xml";
    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    public SqlSession getSqlSession() throws IOException{
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }
}
