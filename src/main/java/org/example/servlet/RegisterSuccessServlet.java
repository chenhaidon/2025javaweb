package org.example.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Dao.UserDao;
import org.example.entity.User;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/RegisterSuccessServlet")
public class RegisterSuccessServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UserDao userDao = new UserDao();
        response.setContentType("text/html;charset=UTF-8");
        // 1. 获取 userId 参数
        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            response.getWriter().println("无效的用户ID！");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            response.getWriter().println("无效的用户ID格式！");
            return;
        }
        User user = userDao.selectUser(userId);


        if (user != null) {


            // 3. 将查询到的信息设置到 request 属性中

            request.setAttribute("username", user.getUsername());
            request.setAttribute("gender", user.getGender());
            request.setAttribute("phone", user.getPhone());
            request.setAttribute("hobby", user.getHobby());
            request.setAttribute("image", user.getAvatar().get(0));
            request.setAttribute("userId", user.getId());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/register-success.jsp");
            dispatcher.forward(request, response);
        } else {
            response.getWriter().println("未找到用户信息！");

        }
    }
}

