package org.example.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.entity.CartItem;
import org.example.entity.Product;
import org.example.Dao.ProductDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {
    private ProductDao productDao = new ProductDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession();

        // 获取购物车，不存在则创建
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // 处理操作
        String action = request.getParameter("action");
        String productIdStr = request.getParameter("productId");

        if (productIdStr == null || productIdStr.isEmpty()) {
            result.put("success", false);
            result.put("message", "商品ID不能为空");
            out.print(gson.toJson(result));
            out.flush();
            out.close();
            return;
        }

        long productId;
        try {
            productId = Long.parseLong(productIdStr);
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "无效的商品ID");
            out.print(gson.toJson(result));
            out.flush();
            out.close();
            return;
        }

        // 根据操作类型处理购物车
        switch (action) {
            case "add":
                int quantity = request.getParameter("quantity") != null ?
                        Integer.parseInt(request.getParameter("quantity")) : 1;
                addToCart(cart, productId, quantity);
                result.put("message", "商品已添加到购物车");
                break;
            case "remove":
                removeFromCart(cart, productId);
                result.put("message", "商品已从购物车移除");
                break;
            case "update":
                int newQuantity = Integer.parseInt(request.getParameter("quantity"));
                updateQuantity(cart, productId, newQuantity);
                result.put("message", "购物车已更新");
                break;
            case "clear":
                cart.clear();
                result.put("message", "购物车已清空");
                break;
            default:
                result.put("success", false);
                result.put("message", "无效的操作");
                out.print(gson.toJson(result));
                out.flush();
                out.close();
                return;
        }

        // 更新会话中的购物车
        session.setAttribute("cart", cart);

        // 返回更新后的购物车信息
        result.put("success", true);
        result.put("cartItems", cart);
        result.put("totalItems", getTotalItems(cart));
        result.put("totalPrice", getTotalPrice(cart));

        out.print(gson.toJson(result));
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession();

        // 获取购物车
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // 返回购物车信息
        result.put("success", true);
        result.put("cartItems", cart);
        result.put("totalItems", getTotalItems(cart));
        result.put("totalPrice", getTotalPrice(cart));

        out.print(gson.toJson(result));
        out.flush();
        out.close();
    }

    // 添加商品到购物车
    private void addToCart(List<CartItem> cart, long productId, int quantity) {
        // 从数据库查找商品信息
        Product product = productDao.getProductById(productId);
        if (product != null) {
            // 检查商品是否已在购物车中
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getProductId() == productId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            // 如果不在购物车中，则添加新项
            if (!found) {
                CartItem cartItem = new CartItem();
                cartItem.setProductId((int) productId);
                cartItem.setName(product.getName());
                cartItem.setPrice(product.getPrice().doubleValue());
                cartItem.setQuantity(quantity);
                cartItem.setImage(product.getMainImageUrl());
                cart.add(cartItem);
            }
        }
    }

    // 从购物车移除商品
    private void removeFromCart(List<CartItem> cart, long productId) {
        cart.removeIf(item -> item.getProductId() == productId);
    }

    // 更新商品数量
    private void updateQuantity(List<CartItem> cart, long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(cart, productId);
            return;
        }

        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(quantity);
                break;
            }
        }
    }

    // 计算购物车商品总数
    private int getTotalItems(List<CartItem> cart) {
        return cart.stream().mapToInt(CartItem::getQuantity).sum();
    }

    // 计算购物车总价
    private double getTotalPrice(List<CartItem> cart) {
        return cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }
}