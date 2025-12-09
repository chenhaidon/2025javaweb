package org.example.servlet;

import com.google.gson.Gson;
import org.example.entity.Product;
import org.example.entity.User;
import org.example.service.ProductService;
import org.example.utils.ThymeleafUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private ProductService productService = new ProductService();
    private TemplateEngine templateEngine;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        templateEngine = ThymeleafUtil.getTemplateEngine(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 检查是否是AJAX请求
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        
        // 获取所有商品
        List<Product> products = productService.getAllProducts();
        
        if (isAjax) {
            // AJAX 请求，返回 JSON 数据
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(products));
        } else {
            // 普通请求，使用Thymeleaf模板
            JakartaServletWebApplication webApplication = 
                JakartaServletWebApplication.buildApplication(getServletContext());
            WebContext ctx = new WebContext(
                webApplication.buildExchange(request, response), 
                request.getLocale());
            ctx.setVariable("products", products);
            
            // 获取当前登录用户信息
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");
            if (username != null) {
                // 创建一个User对象用于模板渲染
                User user = new User();
                user.setUsername(username);
                Integer role = (Integer) session.getAttribute("role");
                if (role != null) {
                    user.setRole(role);
                } else {
                    // 如果session中没有role信息，默认设为普通用户
                    user.setRole(0);
                }
                ctx.setVariable("user", user);
            }
            
            templateEngine.process("index.html", ctx, response.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 处理添加商品请求
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        String imageUrl = request.getParameter("imageUrl");

        if (name != null && priceStr != null && imageUrl != null) {
            try {
                BigDecimal price = new BigDecimal(priceStr);
                Product product = new Product(name, price, imageUrl);
                productService.saveProduct(product);
            } catch (NumberFormatException e) {
                // 处理价格格式错误
                e.printStackTrace();
            }
        }

        // 重定向到商品列表
        response.sendRedirect(request.getContextPath() + "/products");
    }
}