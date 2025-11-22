<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2025/11/14
  Time: 20:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>注册成功</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f0f2f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); text-align: center; width: 350px; }
        h2 { color: #28a745; }
        .info { text-align: left; margin: 20px 0; line-height: 1.6; }
        .info span { font-weight: bold; color: #007bff; }
        a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; }
        a:hover { background-color: #0056b3; }
    </style>
</head>
<body>
<div class="container">
    <h2>恭喜！注册成功！</h2>

    <div class="info">
        <p>用户名: <span>${username}</span></p>
        <p>手机号: <span>${phone}</span></p>
        <p>性别: <span>${gender}</span></p>
        <p>爱好: <span>${hobby}</span></p>
        <p>头像: </p>
        <!-- 关键：调用 ImageServlet 来显示图片 -->
        <img src="<%= request.getContextPath() %>/ImageServlet?userId=${userId}" alt="用户头像" style="width: 100px; height: 100px;">
    </div>

    <a href="../html/index.jsp">返回首页</a>
</div>
</body>
</html>
