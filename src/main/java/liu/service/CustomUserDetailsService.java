package liu.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 自定义用户详情服务接口
 * 扩展Spring Security的UserDetailsService接口
 * @author liu
 */
public interface CustomUserDetailsService extends UserDetailsService {

    /**
     * 根据用户名加载用户详情
     * @param username 用户名
     * @return UserDetails 用户详情
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * 根据用户名验证用户是否存在且启用
     * @param username 用户名
     * @return boolean 用户是否有效
     */
    boolean isUserValid(String username);

    /**
     * 刷新用户权限信息
     * @param username 用户名
     * @return UserDetails 更新后的用户详情
     * @throws UsernameNotFoundException 用户不存在异常
     */
    UserDetails refreshUserAuthorities(String username) throws UsernameNotFoundException;
} 