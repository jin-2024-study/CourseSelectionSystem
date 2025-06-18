package liu.service;

import liu.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 根据ID查找用户
     */
    User findById(Integer id);

    /**
     * 查询所有用户
     */
    List<User> findAll();

    /**
     * 创建新用户
     */
    User createUser(String username, String password, String email, String role);

    /**
     * 更新用户信息
     */
    User updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Integer id);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 验证用户登录
     */
    boolean validateUser(String username, String password);
} 