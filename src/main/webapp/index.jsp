<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>商品首页</title>
    <link href="css/index.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div class="header">
    <h3> 你好，<span id="welcomeUsername" class="bg-white/20 px-2 py-1 rounded-md relative inline-block">
        用户
        <span class="absolute -bottom-1 left-0 w-full h-1 bg-white/40 rounded-full"></span>
    </span>
        <a href="html/cart.html" style="float: right; margin-right: 20px; text-decoration: none; color: #4285f4;">查看购物车</a>
    </h3>
</div>

<h1 style="text-align: center; margin: 20px 0;">商品列表</h1>

<div class="product-container" id="productContainer">
    <c:forEach var="product" items="${products}">
        <div class="product-card">
            <img src="${product.mainImageUrl}" alt="${product.name}" class="product-image">
            <div class="product-name">${product.name}</div>
            <div class="product-price">¥${product.price}</div>
            <button class="buy-button" onclick="addToCart(${product.id})">购买</button>
        </div>
    </c:forEach>
</div>

<!-- 调试信息 -->
<div style="display: none;" id="debugInfo">
    requestScope.products size: ${empty requestScope.products ? 'empty' : requestScope.products.size()}<br/>
    requestScope.products: ${requestScope.products}<br/>
</div>

<script>
    // 设置欢迎词中的用户名
    function setWelcomeUsername() {
        // 从sessionStorage中获取用户名（如果之前已保存）
        const username = sessionStorage.getItem('username');
        if (username) {
            document.getElementById('welcomeUsername').textContent = username;
        } else {
            // 尝试从URL参数获取用户名
            const urlParams = new URLSearchParams(window.location.search);
            const usernameParam = urlParams.get('username');
            if (usernameParam) {
                document.getElementById('welcomeUsername').textContent = usernameParam;
                // 保存到sessionStorage供后续使用
                sessionStorage.setItem('username', usernameParam);
            }
        }
    }

    // 使用EL表达式渲染商品数据
    function renderProductsFromEL() {
        // 通过JSTL标签在服务器端构建JavaScript数组
        const container = document.getElementById('productContainer');

        // 调试信息
        const debugInfo = document.getElementById('debugInfo').textContent;
        console.log('Debug info:', debugInfo);

        // 使用JSTL标签构建JavaScript数组
        let products = [];
        <c:if test="${not empty requestScope.products}">
        products = [
            <c:forEach var="product" items="${requestScope.products}" varStatus="loop">
            {
                id: ${product.id},
                name: "${product.name}",
                price: ${product.price},
                mainImageUrl: "${product.mainImageUrl != null ? product.mainImageUrl : ''}"
            }<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];
        </c:if>

        console.log('Products array:', products);
        console.log('Products count:', products.length);

        if (products && products.length > 0 && products[0].id !== undefined) {
            let productHTML = '';
            // 使用forEach方法替换for循环
            products.forEach((product, index) => {
                console.log(`Processing product ${index}:`, product);
                // 确保商品属性存在且不为空，处理 null 和 undefined
                const id = (product.id != null && product.id !== undefined) ? product.id : '';
                const name = (product.name != null && product.name !== undefined) ? String(product.name) : '';
                const price = (product.price != null && product.price !== undefined) ? Number(product.price) : 0;
                const mainImageUrl = (product.mainImageUrl != null && product.mainImageUrl !== undefined) ? String(product.mainImageUrl) : '';

                // 转义特殊字符以防止破坏HTML结构
                const escapedName = name.replace(/&/g, '&amp;')
                    .replace(/</g, '&lt;')
                    .replace(/>/g, '&gt;')
                    .replace(/"/g, '&quot;')
                    .replace(/'/g, '&#x27;');

                // 格式化价格
                const formattedPrice = price.toFixed(2);

                productHTML += `
                    <div class="product-card">
                        <img src="${mainImageUrl}" alt="${escapedName}" class="product-image"
                             onerror="this.src='https://via.placeholder.com/200x200?text=No+Image'">
                        <div class="product-info">
                            <div class="product-name">${escapedName}</div>
                            <div class="product-price">¥${formattedPrice}</div>
                            <button class="buy-button" onclick="buyProduct(${id})">立即购买</button>
                            <button class="cart-button" onclick="addToCart(${id})">加入购物车</button>
                        </div>
                    </div>
                `;
            });
            container.innerHTML = productHTML;
            console.log('Products rendered successfully');
            // 只打印前200个字符避免日志过长
            console.log('Generated HTML:', productHTML.substring(0, 200) + (productHTML.length > 200 ? '...' : ''));
        } else {
            // 如果没有数据，则尝试AJAX加载
            console.log('No products found, falling back to AJAX loading');
            loadProducts();
        }
    }

    // 动态加载商品数据
    function loadProducts() {
        fetch('products', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => response.json())
            .then(products => {
                const container = document.getElementById('productContainer');
                console.log('AJAX loaded products:', products);
                if (products && products.length > 0) {
                    let productHTML = '';
                    // 使用forEach方法替换for循环
                    products.forEach((product, index) => {
                        console.log(`Processing AJAX product ${index}:`, product);
                        // 确保商品属性存在且不为空，处理 null 和 undefined
                        const id = (product.id != null && product.id !== undefined) ? product.id : '';
                        const name = (product.name != null && product.name !== undefined) ? String(product.name) : '';
                        const price = (product.price != null && product.price !== undefined) ? Number(product.price) : 0;
                        const mainImageUrl = (product.mainImageUrl != null && product.mainImageUrl !== undefined) ? String(product.mainImageUrl) : '';

                        // 转义特殊字符以防止破坏HTML结构
                        const escapedName = name.replace(/&/g, '&amp;')
                            .replace(/</g, '&lt;')
                            .replace(/>/g, '&gt;')
                            .replace(/"/g, '&quot;')
                            .replace(/'/g, '&#x27;');

                        // 格式化价格
                        const formattedPrice = price.toFixed(2);

                        productHTML += `
                        <div class="product-card">
                            <img src="${mainImageUrl}" alt="${escapedName}" class="product-image"
                                 onerror="this.src='https://via.placeholder.com/200x200?text=No+Image'">
                            <div class="product-info">
                                <div class="product-name">${escapedName}</div>
                                <div class="product-price">¥${formattedPrice}</div>
                                <button class="buy-button" onclick="buyProduct(${id})">立即购买</button>
                                <button class="cart-button" onclick="addToCart(${id})">加入购物车</button>
                            </div>
                        </div>
                    `;
                    });
                    container.innerHTML = productHTML;
                    console.log('Products loaded via AJAX successfully');
                    console.log('Generated AJAX HTML:', productHTML);
                } else {
                    container.innerHTML = '<div style="text-align: center; width: 100%;"><p>暂无商品信息</p></div>';
                    console.log('No products found via AJAX');
                }
            })
            .catch(error => {
                console.error('加载商品数据失败:', error);
                document.getElementById('productContainer').innerHTML = '<div style="text-align: center; width: 100%;"><p>加载商品数据失败</p></div>';
            });
    }

    // 购买商品功能
    function buyProduct(productId) {
        console.log('Buying product:', productId);
        if (!productId) {
            console.error('Product ID is missing');
            return;
        }

        // 发送请求到服务器检查登录状态
        fetch('CartServlet', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                // 检查响应的内容类型
                const contentType = response.headers.get("content-type");
                console.log('Buy response content-type:', contentType);
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    return response.json();
                } else {
                    // 如果不是JSON响应，可能是重定向到登录页
                    window.location.href = 'html/login.html?redirect=cart.html&productId=' + productId;
                    throw new Error('Redirecting to login');
                }
            })
            .then(data => {
                console.log('Buy response data:', data);
                if (data.needLogin) {
                    // 用户未登录，跳转到登录页面，同时传递目标页面和商品ID
                    window.location.href = 'html/login.html?redirect=cart.html&productId=' + productId;
                } else if (data.success) {
                    // 用户已登录，添加商品到购物车
                    return fetch('CartServlet', {
                        method: 'POST',
                        credentials: 'include',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'X-Requested-With': 'XMLHttpRequest'
                        },
                        body: 'action=add&productId=' + productId + '&quantity=1'
                    });
                }
            })
            .then(response => {
                if (response) {
                    // 检查响应的内容类型
                    const contentType = response.headers.get("content-type");
                    console.log('Add to cart response content-type:', contentType);
                    if (contentType && contentType.indexOf("application/json") !== -1) {
                        return response.json();
                    } else {
                        throw new Error('Unexpected response format');
                    }
                }
            })
            .then(data => {
                console.log('Add to cart response data:', data);
                if (data && data.success) {
                    alert('商品已添加到购物车');
                    // 跳转到购物车页面
                    window.location.href = 'html/cart.html';
                } else if (data && data.message) {
                    alert('添加购物车失败: ' + data.message);
                }
            })
            .catch(error => {
                if (error.message !== 'Redirecting to login') {
                    console.error('Error:', error);
                    alert('操作失败，请稍后重试');
                }
            });
    }

    // 添加到购物车功能
    function addToCart(productId) {
        console.log('Adding to cart product:', productId);
        if (!productId) {
            console.error('Product ID is missing');
            return;
        }

        // 发送请求到服务器检查登录状态
        fetch('CartServlet', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                // 检查响应的内容类型
                const contentType = response.headers.get("content-type");
                console.log('Add to cart response content-type:', contentType);
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    return response.json();
                } else {
                    // 如果不是JSON响应，可能是重定向到登录页
                    window.location.href = 'html/login.html?redirect=cart.html&productId=' + productId;
                    throw new Error('Redirecting to login');
                }
            })
            .then(data => {
                console.log('Add to cart response data:', data);
                if (data.needLogin) {
                    // 用户未登录，跳转到登录页面，同时传递目标页面和商品ID
                    window.location.href = 'html/login.html?redirect=cart.html&productId=' + productId;
                } else if (data.success) {
                    // 用户已登录，添加商品到购物车
                    return fetch('CartServlet', {
                        method: 'POST',
                        credentials: 'include',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'X-Requested-With': 'XMLHttpRequest'
                        },
                        body: 'action=add&productId=' + productId + '&quantity=1'
                    });
                }
            })
            .then(response => {
                if (response) {
                    // 检查响应的内容类型
                    const contentType = response.headers.get("content-type");
                    console.log('Add to cart POST response content-type:', contentType);
                    if (contentType && contentType.indexOf("application/json") !== -1) {
                        return response.json();
                    } else {
                        throw new Error('Unexpected response format');
                    }
                }
            })
            .then(data => {
                console.log('Add to cart POST response data:', data);
                if (data && data.success) {
                    alert('商品已添加到购物车');
                    // 跳转到购物车页面
                    window.location.href = 'html/cart.html';
                } else if (data && data.message) {
                    alert('添加购物车失败: ' + data.message);
                }
            })
            .catch(error => {
                if (error.message !== 'Redirecting to login') {
                    console.error('Error:', error);
                    alert('操作失败，请稍后重试');
                }
            });
    }

    // 页面加载完成后执行
    document.addEventListener('DOMContentLoaded', () => {
        console.log('Page loaded, starting product rendering');
        setWelcomeUsername();
        // renderProductsFromEL(); // 优先使用EL表达式渲染数据
    });
</script>
</body>
</html>