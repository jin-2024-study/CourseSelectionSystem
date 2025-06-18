package liu.service.impl;

import liu.entity.User;
import liu.service.CustomUserDetailsService;
import liu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 自定义用户详情服务实现类
 * 用于Spring Security用户认证
 * @author liu
 */
@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("尝试加载用户: {}", username);
        
        User user = userService.findByUsername(username);
        
        if (user == null) {
            logger.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (!user.getEnabled()) {
            logger.warn("用户已禁用: {}", username);
            throw new UsernameNotFoundException("用户已禁用: " + username);
        }

        logger.info("用户加载成功: {}, 角色: {}", username, user.getRole());
        return new CustomUserPrincipal(user);
    }

    @Override
    public boolean isUserValid(String username) {
        try {
            User user = userService.findByUsername(username);
            return user != null && user.getEnabled();
        } catch (Exception e) {
            logger.error("验证用户有效性时发生错误: {}", username, e);
            return false;
        }
    }

    @Override
    public UserDetails refreshUserAuthorities(String username) throws UsernameNotFoundException {
        logger.info("刷新用户权限: {}", username);
        return loadUserByUsername(username);
    }

    /**
     * 自定义UserDetails实现
     */
    public static class CustomUserPrincipal implements UserDetails {
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            // 添加角色权限，Spring Security要求角色以"ROLE_"开头
            if (user.getRole() != null && !user.getRole().isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                
                // 根据角色添加特定权限
                switch (user.getRole().toUpperCase()) {
                    case "ADMIN":
                        authorities.add(new SimpleGrantedAuthority("ADMIN_READ"));
                        authorities.add(new SimpleGrantedAuthority("ADMIN_WRITE"));
                        authorities.add(new SimpleGrantedAuthority("USER_MANAGE"));
                        authorities.add(new SimpleGrantedAuthority("COURSE_MANAGE"));
                        break;
                    case "TEACHER":
                        authorities.add(new SimpleGrantedAuthority("COURSE_READ"));
                        authorities.add(new SimpleGrantedAuthority("COURSE_WRITE"));
                        authorities.add(new SimpleGrantedAuthority("STUDENT_READ"));
                        break;
                    case "STUDENT":
                        authorities.add(new SimpleGrantedAuthority("COURSE_READ"));
                        authorities.add(new SimpleGrantedAuthority("COURSE_SELECT"));
                        break;
                    default:
                        // 默认权限
                        authorities.add(new SimpleGrantedAuthority("USER_READ"));
                        break;
                }
            }
            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            // 可以根据业务需求添加账户过期逻辑
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            // 可以根据业务需求添加账户锁定逻辑
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            // 可以根据业务需求添加密码过期逻辑
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.getEnabled();
        }

        /**
         * 获取原始用户对象
         * @return User 用户实体
         */
        public User getUser() {
            return user;
        }

        /**
         * 获取用户ID
         * @return Integer 用户ID
         */
        public Integer getUserId() {
            return user.getId();
        }

        /**
         * 获取用户角色
         * @return String 用户角色
         */
        public String getRole() {
            return user.getRole();
        }

        /**
         * 获取用户邮箱
         * @return String 用户邮箱
         */
        public String getEmail() {
            return user.getEmail();
        }

        @Override
        public String toString() {
            return "CustomUserPrincipal{" +
                    "username='" + user.getUsername() + '\'' +
                    ", role='" + user.getRole() + '\'' +
                    ", enabled=" + user.getEnabled() +
                    '}';
        }
    }
} 