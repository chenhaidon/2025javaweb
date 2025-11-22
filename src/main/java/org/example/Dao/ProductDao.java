package org.example.Dao;

import org.example.entity.Product;
import org.example.utils.DruidUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    /**
     * 保存商品信息到数据库
     * @param product 商品对象
     * @return 是否保存成功
     */
    public boolean saveProduct(Product product) {
        String sql = "INSERT INTO products (name, price, main_image_url) VALUES (?, ?, ?)";
        try (Connection conn = DruidUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setBigDecimal(2, product.getPrice());
            pstmt.setString(3, product.getMainImageUrl());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从数据库获取所有商品
     * @return 商品列表
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price, main_image_url FROM products ORDER BY id DESC";
        
        try (Connection conn = DruidUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setMainImageUrl(rs.getString("main_image_url"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * 根据ID获取商品
     * @param id 商品ID
     * @return 商品对象，如果找不到返回null
     */
    public Product getProductById(long id) {
        String sql = "SELECT id, name, price, main_image_url FROM products WHERE id = ?";
        
        try (Connection conn = DruidUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setMainImageUrl(rs.getString("main_image_url"));
                    return product;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 检查产品表是否存在
     * @return 是否存在
     */
    public boolean isProductTableExists() {
        String sql = "SELECT 1 FROM products LIMIT 1";
        try (Connection conn = DruidUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}