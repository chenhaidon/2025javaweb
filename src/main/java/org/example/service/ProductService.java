package org.example.service;

import org.example.Dao.ProductDao;
import org.example.entity.Product;
import java.util.List;

public class ProductService {
    private ProductDao productDao = new ProductDao();

    /**
     * 保存商品
     * @param product 商品对象
     * @return 是否保存成功
     */
    public boolean saveProduct(Product product) {
        return productDao.saveProduct(product);
    }

    /**
     * 获取所有商品
     * @return 商品列表
     */
    public List<Product> getAllProducts() {
        return productDao.getAllProducts();
    }
}