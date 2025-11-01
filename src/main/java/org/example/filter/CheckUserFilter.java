package org.example.filter;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebFilter(filterName = "CheckUserFilter" ,urlPatterns = "/CartServlet")
public class CheckUserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        // 检查用户是否登录
        if (username == null || username.isEmpty()) {
            result.put("success", false);
            result.put("message", "请先登录");
            result.put("needLogin", true);
            out.print(gson.toJson(result));
            return;
        }
//        if (httpServletRequest.getSession(false)==null){
//            String p = httpServletRequest.getContextPath();
//            ((HttpServletResponse) servletResponse).sendRedirect(p+"html/login.html");
//            return;
//        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
