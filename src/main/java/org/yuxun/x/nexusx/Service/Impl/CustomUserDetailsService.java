package org.yuxun.x.nexusx.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.yuxun.x.nexusx.Service.UserDetailsService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final DataSource dataSource;

    @Autowired
    public CustomUserDetailsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneOrEmail) throws UsernameNotFoundException {
        // 查询用户信息 SQL
        String userSql = "SELECT userId, username, password FROM users WHERE phone = ? OR email = ?";

        // 查询角色信息 SQL
        String rolesSql = "SELECT r.role_name FROM roles r " +
                "JOIN user_roles ur ON r.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement userStmt = connection.prepareStatement(userSql)) {

            // 设置参数并执行查询
            userStmt.setString(1, phoneOrEmail);
            userStmt.setString(2, phoneOrEmail);
            ResultSet userResultSet = userStmt.executeQuery();

            if (userResultSet.next()) {
                // 获取用户基础信息
                Long userId = userResultSet.getLong("userId");
                String username = userResultSet.getString("username");
                String password = userResultSet.getString("password"); // AES加密密码

                // 查询用户的角色信息
                try (PreparedStatement rolesStmt = connection.prepareStatement(rolesSql)) {
                    rolesStmt.setLong(1, userId);
                    ResultSet rolesResultSet = rolesStmt.executeQuery();

                    // 收集角色信息到权限集合
                    List<String> roles = new ArrayList<>();
                    while (rolesResultSet.next()) {
                        roles.add(rolesResultSet.getString("role_name"));
                    }

                    // Spring Security的权限格式
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());


                    // 构建并返回单个用户对象
                    return User.withUsername(username)
                            .password(password) // 加密密码
                            .authorities(authorities) // 附加角色作为权限
                            .build();
                }
            } else {
                throw new UsernameNotFoundException("User not found with phoneOrEmail: " + phoneOrEmail);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying the database for user and roles", e);
        }
    }

}
