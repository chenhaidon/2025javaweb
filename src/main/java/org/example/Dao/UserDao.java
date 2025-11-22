package org.example.Dao;

import org.example.entity.User;
import org.example.utils.DruidUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public int addUser(User user) {
        Connection conn = DruidUtil.getConnection();
        int user_id;

        try {
            PreparedStatement pstmt1 = conn.prepareStatement("INSERT INTO user(name,password,gender, phone, hobby) VALUES (?, ?, ?, ?, ?)"
                    , PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO photo(user_id,image) VALUES ( ?, ?)"
            );
            conn.setAutoCommit(false);
            pstmt1.setString(1, user.getUsername());
            pstmt1.setString(2, user.getPassword());
            pstmt1.setString(3, user.getGender());
            pstmt1.setString(4, user.getPhone());
            pstmt1.setString(5, user.getHobby());
            pstmt1.execute();
            ResultSet generatedKeys = pstmt1.getGeneratedKeys();
            if (generatedKeys.next()) {
                user_id = generatedKeys.getInt(1);
            } else
                throw new RuntimeException("插入用户记录错误");
            for (byte[] b : user.getAvatar()) {
                pstmt2.setInt(1, user_id);
                pstmt2.setBytes(2, b);
                pstmt2.execute();
            }
            conn.commit();
            pstmt1.close();
            pstmt2.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
        return user_id;
    }

    public User selectUser(int userId) {

        User user = null;
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;

        try {
            conn = DruidUtil.getConnection();
            String sql = "select u.id, u.name, u.password, u.gender, u.phone, u.hobby, p.image from user u join photo p on u.id = p.user_id where u.id=? limit 1";
            pstmt1 = conn.prepareStatement(sql);
            pstmt1.setInt(1, userId);
            rs = pstmt1.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(userId);
                user.setUsername(rs.getString("name"));
                user.setPassword(rs.getString("password")); // 注意：实际项目中不会返回密码
                user.setGender(rs.getString("gender"));
                user.setPhone(rs.getString("phone"));
                user.setHobby(rs.getString("hobby"));
                byte[] imageBytes = rs.getBytes("image");
                List<byte[]> avatarList = new ArrayList<>();
                avatarList.add(imageBytes);
                user.setAvatar(avatarList);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 在 finally 块中关闭所有资源
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
            }
            try {
                if (pstmt1 != null) pstmt1.close();
            } catch (SQLException e) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }

        return user; // 返回 User 对象，而不是 ResultSet

    }
}
