package liu.service;

import liu.entity.User;
import liu.service.impl.CustomUserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomUserDetailsService测试类
 * 验证接口+实现类结构是否正确工作
 * @author liu
 */
@SpringBootTest
@Transactional // 测试后回滚
public class CustomUserDetailsServiceTest {

    @Resource
    private CustomUserDetailsService customUserDetailsService;

    @Resource
    private UserService userService;

    @Test
    public void testServiceInjection() {
        // 验证服务正确注入
        assertNotNull(customUserDetailsService);
        assertTrue(customUserDetailsService instanceof CustomUserDetailsServiceImpl);
        System.out.println("CustomUserDetailsService服务注入成功: " + customUserDetailsService.getClass().getSimpleName());
    }

    @Test
    public void testLoadUserByUsername() {
        // 创建测试用户
        User testUser = userService.createUser("testuser", "password", "test@example.com", "STUDENT");
        assertNotNull(testUser);

        // 测试加载用户
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");
        
        // 验证用户详情
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertFalse(userDetails.getAuthorities().isEmpty());
        
        System.out.println("用户加载成功: " + userDetails.getUsername());
        System.out.println("用户权限: " + userDetails.getAuthorities());
    }

    @Test
    public void testLoadNonExistentUser() {
        // 测试加载不存在的用户
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });
        
        System.out.println("正确抛出UsernameNotFoundException异常");
    }

    @Test
    public void testIsUserValid() {
        // 创建测试用户
        User testUser = userService.createUser("validuser", "password", "valid@example.com", "ADMIN");
        assertNotNull(testUser);

        // 测试用户有效性验证
        assertTrue(customUserDetailsService.isUserValid("validuser"));
        assertFalse(customUserDetailsService.isUserValid("invaliduser"));
        
        System.out.println("用户有效性验证测试通过");
    }

    @Test
    public void testRefreshUserAuthorities() {
        // 创建测试用户
        User testUser = userService.createUser("refreshuser", "password", "refresh@example.com", "TEACHER");
        assertNotNull(testUser);

        // 测试刷新用户权限
        UserDetails userDetails = customUserDetailsService.refreshUserAuthorities("refreshuser");
        
        // 验证刷新后的用户详情
        assertNotNull(userDetails);
        assertEquals("refreshuser", userDetails.getUsername());
        assertFalse(userDetails.getAuthorities().isEmpty());
        
        System.out.println("用户权限刷新成功: " + userDetails.getAuthorities());
    }

    @Test
    public void testCustomUserPrincipal() {
        // 创建测试用户
        User testUser = userService.createUser("principaluser", "password", "principal@example.com", "ADMIN");
        assertNotNull(testUser);

        // 加载用户并转换为CustomUserPrincipal
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("principaluser");
        assertTrue(userDetails instanceof CustomUserDetailsServiceImpl.CustomUserPrincipal);
        
        CustomUserDetailsServiceImpl.CustomUserPrincipal principal = 
            (CustomUserDetailsServiceImpl.CustomUserPrincipal) userDetails;
        
        // 验证CustomUserPrincipal的扩展方法
        assertNotNull(principal.getUser());
        assertEquals("principaluser", principal.getUsername());
        assertEquals("ADMIN", principal.getRole());
        assertEquals("principal@example.com", principal.getEmail());
        assertNotNull(principal.getUserId());
        
        System.out.println("CustomUserPrincipal测试通过");
        System.out.println("用户详情: " + principal.toString());
    }

    @Test
    public void testRoleBasedAuthorities() {
        // 测试不同角色的权限
        String[] roles = {"ADMIN", "TEACHER", "STUDENT"};
        
        for (String role : roles) {
            String username = "user_" + role.toLowerCase();
            User testUser = userService.createUser(username, "password", username + "@example.com", role);
            assertNotNull(testUser);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            assertNotNull(userDetails);
            
            System.out.println("角色 " + role + " 的权限: " + userDetails.getAuthorities());
            
            // 验证角色权限包含ROLE_前缀
            assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role)));
        }
    }
} 