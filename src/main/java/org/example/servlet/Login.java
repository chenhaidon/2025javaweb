package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.utils.IpUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/Login")
public class Login extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String usertype = request.getParameter("usertype");
        // 获取登录后跳转的目标页面，默认为首页
        String redirect = request.getParameter("redirect");
        if (redirect == null || redirect.isEmpty()) {
            redirect = "html/index.html";
        }
        // 获取可能需要添加到购物车的商品ID
        String productId = request.getParameter("productId");

// 简单的登录验证（实际应用中应连接数据库验证）
        boolean loginSuccess = false;
        if (username != null && !username.isEmpty() &&
                password != null && !password.isEmpty()) {
            // 这里仅做简单验证，实际应用中需要查询数据库
            loginSuccess = true;
        }

        if (loginSuccess) {
            // 登录成功，创建会话
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("usertype", usertype);

            // 记录登录时间和IP
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            session.setAttribute("loginTime", now.format(formatter));
            session.setAttribute("loginIp", request.getRemoteAddr());

            // 登录成功后，若有商品ID则添加到购物车
            // 在redirect后拼接商品ID，让目标页面（如cart.html）处理添加
            if (productId != null && !productId.isEmpty()) {
                redirect = "html/"+redirect+(redirect.contains("?") ? "&" : "?") + "productId=" + productId;
            }

            // 重定向到目标页面
            response.sendRedirect(redirect+(redirect.contains("?") ? "&" : "?")+"username="+session.getAttribute("username"));
        } else {
            // 登录失败，重定向回登录页并提示错误
            String errorMsg = URLEncoder.encode("用户名或密码错误", StandardCharsets.UTF_8.name());
            response.sendRedirect("login.html?error=" + errorMsg + "&redirect=" + URLEncoder.encode(redirect, StandardCharsets.UTF_8.name()) +
                    (productId != null ? "&productId=" + productId : ""));
        }
        // 如果index.html在webapp下的pages目录下，使用下面的路径
        // response.sendRedirect("pages/index.html");
        // } else {
        //     // 登录失败处理，例如跳回登录页并显示错误信息
        //     response.sendRedirect("login.html?error=1");
        // }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}