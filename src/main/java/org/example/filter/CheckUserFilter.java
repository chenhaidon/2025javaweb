package org.example.filter;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
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
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        // 检查用户是否登录
        if (username == null || username.isEmpty()) {
            // 检查请求是否为AJAX请求
            String ajaxHeader = request.getHeader("X-Requested-With");
            boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
            
            if (isAjax) {
                // AJAX请求返回JSON
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                Gson gson = new Gson();
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "请先登录");
                result.put("needLogin", true);
                out.print(gson.toJson(result));
                out.flush();
                out.close(); // 确保流被正确关闭
            } else {
                // 非AJAX请求重定向到登录页面
                // 获取当前请求的参数，以便登录后可以跳转回来
                String queryString = request.getQueryString();
                String redirectUrl = "html/login.html?redirect=html/cart.html";
                if (queryString != null) {
                    redirectUrl += "&" + queryString;
                }
                response.sendRedirect(request.getContextPath() + "/" + redirectUrl);
            }
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}