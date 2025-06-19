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
} 