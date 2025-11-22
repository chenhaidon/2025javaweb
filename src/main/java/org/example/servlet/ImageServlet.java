package org.example.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Dao.UserDao;
import org.example.entity.User;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;

@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 获取 userId 参数
        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            // 如果没有ID，返回一个默认的错误图片或404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 2. 查询数据库，获取该用户的头像字节数组
        UserDao userDao = new UserDao();
        User user = userDao.selectUser(userId); // 假设这个方法能查询到包含头像的User对象

        byte[] imageBytes = null;
        if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            imageBytes = user.getAvatar().get(0); // 获取第一张图片
        }

        // 3. 设置响应头为图片类型
        // 注意：这里假设你的图片是JPG格式，如果是PNG，应改为 "image/png"
        response.setContentType("image/jpeg");

        // 4. 获取输出流并写入图片数据
        OutputStream out = response.getOutputStream();
        if (imageBytes != null) {
            out.write(imageBytes);
        } else {
            // 如果没有图片，可以返回一张默认图片
            // 这里需要你自己实现读取默认图片文件的逻辑
            // InputStream defaultImageStream = getServletContext().getResourceAsStream("/path/to/default.jpg");
            // byte[] buffer = new byte[defaultImageStream.available()];
            // defaultImageStream.read(buffer);
            // out.write(buffer);
        }
        out.flush();
        out.close();
    }
}

