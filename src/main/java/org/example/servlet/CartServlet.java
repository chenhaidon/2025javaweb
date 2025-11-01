package org.example.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 购物车项实体类
class CartItem {
    private int productId;
    private String name;
    private double price;
    private int quantity;
    private String image;

    public CartItem(int productId, String name, double price, int quantity, String image) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getImage() { return image; }
}

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {
    // 模拟商品数据库
    private List<Map<String, Object>> products;

    @Override
    public void init() throws ServletException {
        products = new ArrayList<>();
        // 初始化商品数据
        products.add(createProduct(1, "智能手表 Pro", 1299, "https://picsum.photos/id/96/300/300"));
        products.add(createProduct(2, "无线蓝牙耳机", 899, "https://picsum.photos/id/26/300/300"));
        products.add(createProduct(3, "笔记本电脑支架", 129, "https://picsum.photos/id/119/300/300"));
        products.add(createProduct(4, "机械键盘", 349, "https://picsum.photos/id/160/300/300"));
    }

    private Map<String, Object> createProduct(int id, String name, double price, String image) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("name", name);
        product.put("price", price);
        product.put("image", image);
        return product;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession();
//        String username = (String) session.getAttribute("username");

//        // 检查用户是否登录
//        if (username == null || username.isEmpty()) {
//            result.put("success", false);
//            result.put("message", "请先登录");
//            result.put("needLogin", true);
//            out.print(gson.toJson(result));
//            return;
//        }

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
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "无效的商品ID");
            out.print(gson.toJson(result));
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
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession();
//        String username = (String) session.getAttribute("username");

//        // 检查用户是否登录
//        if (username == null || username.isEmpty()) {
//            result.put("success", false);
//            result.put("message", "请先登录");
//            result.put("needLogin", true);
//            out.print(gson.toJson(result));
//            return;
//        }

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
    }

    // 添加商品到购物车
    private void addToCart(List<CartItem> cart, int productId, int quantity) {
        // 查找商品信息
        for (Map<String, Object> product : products) {
            if ((int) product.get("id") == productId) {
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
                    cart.add(new CartItem(
                            productId,
                            (String) product.get("name"),
                            (double) product.get("price"),
                            quantity,
                            (String) product.get("image")
                    ));
                }
                break;
            }
        }
    }

    // 从购物车移除商品
    private void removeFromCart(List<CartItem> cart, int productId) {
        cart.removeIf(item -> item.getProductId() == productId);
    }

    // 更新商品数量
    private void updateQuantity(List<CartItem> cart, int productId, int quantity) {
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
