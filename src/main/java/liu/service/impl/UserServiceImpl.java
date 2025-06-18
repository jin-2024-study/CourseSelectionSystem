package liu.service.impl;

import liu.dao.UserDao;
import liu.entity.User;
import liu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findById(Integer id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User createUser(String username, String password, String email, String role) {
        // 检查用户名是否已存在
        if (existsByUsername(username)) {
            throw new RuntimeException("用户名已存在: " + username);
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // 密码加密
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(true);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 保存用户
        int result = userDao.insert(user);
        if (result > 0) {
            return user;
        }
        throw new RuntimeException("创建用户失败");
    }

    @Override
    public User updateUser(User user) {
        user.setUpdateTime(new Date());
        int result = userDao.update(user);
        if (result > 0) {
            return user;
        }
        throw new RuntimeException("更新用户失败");
    }

    @Override
    public boolean deleteUser(Integer id) {
        return userDao.deleteById(id) > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    @Override
    public boolean validateUser(String username, String password) {
        User user = findByUsername(username);
        if (user != null && user.getEnabled()) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
} 