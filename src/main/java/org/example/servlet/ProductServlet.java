package org.example.servlet;

import org.example.entity.Product;
import org.example.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private ProductService productService = new ProductService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 检查是否是 AJAX 请求
//        String xhr = request.getHeader("X-Requested-With");
//        boolean isAjax = "XMLHttpRequest".equals(xhr);
        
        // 获取所有商品
        List<Product> products = productService.getAllProducts();
        
   /*     if (isAjax) {
            // AJAX 请求，返回 JSON 数据
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(products));
        } else {*/
            // 普通请求，转发到首页
            request.setAttribute("products", products);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
//        }
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