package com.example.demo.service;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.dao.AccountDAO;
import com.example.demo.entity.Account;

public class UserDetailsServiceImplTest {

    @Mock
    private AccountDAO accountDAO;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    //====================================================
    // Test 1
    //====================================================

    @Test
    @DisplayName("Load user thành công")
    void loadUserByUsername_Success() {

        Account account = new Account();
        account.setUserName("admin");
        account.setEncrytedPassword("123456");
        account.setUserRole("MANAGER");
        account.setActive(true);

        when(accountDAO.findAccount("admin")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("admin");

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("123456", user.getPassword());
    }

    //====================================================
    // Test 2
    //====================================================

    @Test
    @DisplayName("Không tìm thấy user")
    void loadUserByUsername_UserNotFound() {

        when(accountDAO.findAccount("abc")).thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("abc")
        );
    }

    //====================================================
    // Test 3
    //====================================================

    @Test
    @DisplayName("Kiểm tra tài khoản Active")
    void loadUserByUsername_ActiveUser() {

        Account account = new Account();
        account.setUserName("admin");
        account.setEncrytedPassword("123456");
        account.setUserRole("MANAGER");
        account.setActive(true);

        when(accountDAO.findAccount("admin")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("admin");

        assertTrue(user.isEnabled());
    }

    //====================================================
    // Test 4
    //====================================================

    @Test
    @DisplayName("Kiểm tra tài khoản bị khóa")
    void loadUserByUsername_InactiveUser() {

        Account account = new Account();
        account.setUserName("admin");
        account.setEncrytedPassword("123456");
        account.setUserRole("MANAGER");
        account.setActive(false);

        when(accountDAO.findAccount("admin")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("admin");

        assertFalse(user.isEnabled());
    }

    //====================================================
    // Test 5
    //====================================================

    @Test
    @DisplayName("Kiểm tra Role MANAGER")
    void loadUserByUsername_ManagerRole() {

        Account account = new Account();
        account.setUserName("manager");
        account.setEncrytedPassword("123");
        account.setUserRole(Account.ROLE_MANAGER);
        account.setActive(true);

        when(accountDAO.findAccount("manager")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("manager");

        Collection<? extends GrantedAuthority> roles =
                user.getAuthorities();

        assertTrue(
                roles.stream()
                        .anyMatch(a ->
                                a.getAuthority().equals(Account.ROLE_MANAGER))
        );
    }

    //====================================================
    // Test 6
    //====================================================

    @Test
    @DisplayName("Kiểm tra Role EMPLOYEE")
    void loadUserByUsername_EmployeeRole() {

        Account account = new Account();
        account.setUserName("employee");
        account.setEncrytedPassword("123");
        account.setUserRole(Account.ROLE_EMPLOYEE);
        account.setActive(true);

        when(accountDAO.findAccount("employee")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("employee");

        Collection<? extends GrantedAuthority> roles =
                user.getAuthorities();

        assertTrue(
                roles.stream()
                        .anyMatch(a ->
                                a.getAuthority().equals(Account.ROLE_EMPLOYEE))
        );
    }

    //====================================================
    // Test 7
    //====================================================

    @Test
    @DisplayName("Kiểm tra AccountDAO được gọi đúng")
    void verifyDAOCalled() {

        Account account = new Account();
        account.setUserName("admin");
        account.setEncrytedPassword("123");
        account.setUserRole("MANAGER");
        account.setActive(true);

        when(accountDAO.findAccount("admin")).thenReturn(account);

        service.loadUserByUsername("admin");

        verify(accountDAO, times(1))
                .findAccount("admin");
    }

    //====================================================
    // Test 8
    //====================================================

    @Test
    @DisplayName("Kiểm tra Account không bị khóa")
    void accountNonLocked() {

        Account account = new Account();
        account.setUserName("admin");
        account.setEncrytedPassword("123");
        account.setUserRole("MANAGER");
        account.setActive(true);

        when(accountDAO.findAccount("admin")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("admin");

        assertTrue(user.isAccountNonLocked());
    }

    //====================================================
    // Test 9
    //====================================================

    @Test
    @DisplayName("Kiểm tra Credentials không hết hạn")
    void credentialsNonExpired() {

        Account account = new Account();
        account.setUserName("admin");
        account.setEncrytedPassword("123");
        account.setUserRole("MANAGER");
        account.setActive(true);

        when(accountDAO.findAccount("admin")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("admin");

        assertTrue(user.isCredentialsNonExpired());
    }

    //====================================================
    // Test 10
    //====================================================

    @Test
    @DisplayName("Kiểm tra Account không hết hạn")
    void accountNonExpired() {

        Account account = new Account();
        account.setUserName("admin");
        account.setEncrytedPassword("123");
        account.setUserRole("MANAGER");
        account.setActive(true);

        when(accountDAO.findAccount("admin")).thenReturn(account);

        UserDetails user = service.loadUserByUsername("admin");

        assertTrue(user.isAccountNonExpired());
    }

}