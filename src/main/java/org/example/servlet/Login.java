package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.utils.IpUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/Login")
public class Login extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String usertype = request.getParameter("usertype");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("登录成功" + username + "，" + password + "，" + usertype + "，");

        String ip = IpUtils.getCityInfo(request.getRemoteAddr());

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = now.format(formatter); // 结果示例：2025-10-24 09:34:06
        HttpSession session = request.getSession();

        if (session.getAttribute("ip") == null) {

            session.setAttribute("ip", ip);

            session.setAttribute("currentTime", currentTime);
            response.getWriter().println("欢迎首次登录");

        }
        else {

            String ip1 = IpUtils.getCityInfo(request.getRemoteAddr());

            response.getWriter().println("欢迎登录,上次登录是地址：" + ip1 + "，上次登录时间：" + currentTime);
        }

    }
}
