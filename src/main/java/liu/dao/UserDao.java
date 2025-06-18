package liu.dao;

import liu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserDao {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据ID查找用户
     */
    User findById(@Param("id") Integer id);

    /**
     * 查询所有用户
     */
    List<User> findAll();

    /**
     * 插入新用户
     */
    int insert(User user);

    /**
     * 更新用户信息
     */
    int update(User user);

    /**
     * 删除用户
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(@Param("username") String username);
} 