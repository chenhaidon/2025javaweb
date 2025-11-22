package org.example.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.Dao.UserDao;
import org.example.entity.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@MultipartConfig
@WebServlet("/Register")
public class Register extends HttpServlet {
    UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String username = req.getParameter("username");
        String password1 = req.getParameter("password1");
        String password2 = req.getParameter("password2");
        String phone = req.getParameter("phone");
        String gender = req.getParameter("gender");
        String[] hobby = req.getParameterValues("hobby");
        Part photo = req.getPart("photo");
        resp.setContentType("text/html;charset=UTF-8");
        StringBuffer sb = new StringBuffer();
        if (username.trim().isEmpty())
            sb.append("用户名不能为空");
        else if (password1.trim().isEmpty())
            sb.append("密码不能为空");
        else if (password2.trim().isEmpty())
            sb.append("密码不能为空");
        else if (!password1.equals(password2))
            sb.append("两次密码不一致");
        else if (phone.trim().isEmpty())
            sb.append("手机号不能为空");
        else {
            // 手机号正则：11位数字，以1开头，第二位为3-9
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!phone.trim().matches(phoneRegex)) {
                sb.append("手机号格式不正确（请输入11位有效手机号）");
            }
        }
        if (gender.trim().isEmpty())
            sb.append("性别不能为空");
        if (hobby == null || hobby.length == 0)
            sb.append("爱好不能为空");
        if (sb.length() != 0) {
            resp.getWriter().println(sb.toString());
            return;
        }
        StringBuilder h = new StringBuilder();
        for (int i = 0; i < hobby.length; i++) {
            h.append(hobby[i]);
            if (i < hobby.length - 1) {
                h.append(";");
            }
        }
        List<byte[]> photos = new LinkedList<>();
        Collection<Part> parts = req.getParts();
        for (Part p : parts) {
            if (p.getSubmittedFileName() == null)
                continue;
            InputStream is = p.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            while (is.read(b) > 0) {
                baos.write(b);
            }
            b = baos.toByteArray();
            photos.add(b);
        }
        User u = new User(0, username, h.toString(), phone, password1, gender, photos);
        int newUserId = userDao.addUser(u);
        // 4. 重定向到成功页面的Servlet，并传递用户ID
        if (newUserId != -1) {
            // 使用重定向防止表单重复提交
            // 动态获取上下文路径，保证项目部署在任何路径下都能正常工作
            String contextPath = req.getContextPath();
            resp.sendRedirect(contextPath + "/RegisterSuccessServlet?userId=" + newUserId);
        } else {
            // 注册失败，可以跳转到一个错误页面
            resp.getWriter().println("注册失败，请稍后再试！");
        }
//        u.setId(id);
        // 在 Servlet 的 doGet 或 doPost 方法中
//        req.setAttribute("userId", id);

// 获取请求调度器，指定目标JSP页面的路径
//        RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/register-success.jsp");

// 执行转发
//        dispatcher.forward(req, resp);
        // 存储上传后的图片路径（单个文件用 String 即可，多个文件用 List<String>）
//        List<String> photoPaths = new ArrayList<>();
//        for (Part part : parts) {
//            String fileName = part.getSubmittedFileName();
//            if (fileName == null || fileName.trim().isEmpty()) {
//                continue;
//            }
//            UUID uuid = UUID.randomUUID();
//            String sfn = photo.getSubmittedFileName();
//            String suffix = sfn.substring(sfn.lastIndexOf("."));
//            String savePath = "D:/photo/" + uuid.toString() + suffix;
//            part.write(savePath);
//            photoPaths.add(savePath);
//            //         String fn = "D:/IDEAWorkSpace/ch04/src/test/resources/photo/"+uuid.toString()+suffix;
////            String mime=getServletContext().getMimeType(fn);
////            resp.setContentType(mime);
////            try(FileInputStream fis= new FileInputStream(fn)){
////                byte[] b= new byte[1024];
////                while(fis.read(b)>0){
////                    resp.getOutputStream().write(b);
////                }
////            }
//        }
//        sb.append("用户名：").append(username).append("<br>");
//        sb.append("密码：").append(password1).append("<br>");
//        sb.append("电话号码：").append(phone).append("<br>");
//        sb.append("性别：").append(gender).append("<br>");
//        sb.append("爱好：");
//        for (String h1 : hobby) {
//            sb.append(h1).append(" ");
//        }
//        sb.append("<br>");
//// 关键修改3：循环显示所有上传的图片
//        sb.append("上传的图片：<br>");
//        if (photoPaths.isEmpty()) {
//            sb.append("未上传任何图片<br>");
//        } else {
//            for (String path : photoPaths) {
//                try (FileInputStream fis = new FileInputStream(path);
//                     ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
//
//                    byte[] buffer = new byte[1024];
//                    int len;
//                    while ((len = fis.read(buffer)) != -1) {
//                        bos.write(buffer, 0, len);
//                    }
//                    byte[] imageBytes = bos.toByteArray();
//                    // 转换为Base64格式（自动适配图片类型）
//                    String base64Img = "data:image/*;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes);
//                    // 添加图片标签（设置宽高）
//                    sb.append("<img src='").append(base64Img).append("' width='200' height='200' style='margin:10px;'>");
//
//                } catch (Exception e) {
//                    sb.append("图片加载失败：").append(path).append("<br>");
//                }
//            }
//        }
//
//        resp.getWriter().println(sb);
//
//        log(sb.toString());
    }

    private void displayUser(HttpServletResponse resp, User u) {
        try {
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter out = resp.getWriter();

            // 开始构建 HTML 内容
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>");
            html.append("<html lang=\"zh-CN\">");
            html.append("<head>");
            html.append("<meta charset=\"UTF-8\">");
            html.append("<title>注册成功 - 用户信息</title>");
            html.append("<style>");
            html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
            html.append(".container { background-color: #fff; padding: 30px 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); width: 100%; max-width: 600px; text-align: center; }");
            html.append("h1 { color: #333; margin-bottom: 20px; border-bottom: 3px solid #007bff; padding-bottom: 10px; display: inline-block; }");
            html.append(".info-item { margin: 15px 0; text-align: left; padding: 10px; background-color: #f8f9fa; border-left: 4px solid #007bff; border-radius: 4px; }");
            html.append(".info-item strong { display: inline-block; width: 80px; color: #555; }");
            html.append(".avatar-container { margin: 20px 0; }");
            html.append(".avatar { width: 150px; height: 150px; border-radius: 50%; object-fit: cover; border: 5px solid #007bff; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }");
            html.append(".photos-container { margin-top: 20px; }");
            html.append(".photo-gallery { display: flex; flex-wrap: wrap; gap: 10px; justify-content: center; margin-top: 10px; }");
            html.append(".gallery-item { width: 100px; height: 100px; border-radius: 5px; object-fit: cover; border: 2px solid #ddd; }");
            html.append("a { display: inline-block; margin-top: 30px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; transition: background-color 0.3s; }");
            html.append("a:hover { background-color: #0056b3; }");
            html.append("</style>");
            html.append("</head>");
            html.append("<body>");
            html.append("<div class=\"container\">");
            html.append("<h1>注册成功！</h1>");

            // 嵌入用户信息
            html.append("<div class=\"info-item\"><strong>用户名：</strong>").append(u.getUsername()).append("</div>");
            html.append("<div class=\"info-item\"><strong>手机号：</strong>").append(u.getPhone()).append("</div>");
            html.append("<div class=\"info-item\"><strong>性别：</strong>").append(u.getGender()).append("</div>");
            html.append("<div class=\"info-item\"><strong>爱好：</strong>").append(u.getHobby()).append("</div>");

            // 处理并嵌入头像
            List<byte[]> photos = u.getAvatar();
            if (photos != null && !photos.isEmpty()) {
                // 假设第一张图片作为主头像
                byte[] mainAvatarBytes = photos.get(0);
                String mainAvatarBase64 = Base64.getEncoder().encodeToString(mainAvatarBytes);
                html.append("<div class=\"avatar-container\">");
                html.append("<strong>头像：</strong><br>");
                html.append("<img src=\"data:image/*;base64,").append(mainAvatarBase64).append("\" alt=\"用户头像\" class=\"avatar\">");
                html.append("</div>");

                // 如果有更多图片，展示在相册中
                if (photos.size() > 1) {
                    html.append("<div class=\"photos-container\">");
                    html.append("<strong>更多上传图片：</strong>");
                    html.append("<div class=\"photo-gallery\">");
                    for (int i = 1; i < photos.size(); i++) {
                        byte[] photoBytes = photos.get(i);
                        String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                        html.append("<img src=\"data:image/*;base64,").append(photoBase64).append("\" alt=\"上传图片 ").append(i).append("\" class=\"gallery-item\">");
                    }
                    html.append("</div>"); // .photo-gallery
                    html.append("</div>"); // .photos-container
                }
            } else {
                html.append("<p>未上传头像。</p>");
            }

            html.append("<a href=\"#\">返回首页</a>"); // 这里的 # 可以替换为你的首页 URL
            html.append("</div>"); // .container
            html.append("</body>");
            html.append("</html>");

            // 将构建好的 HTML 写入响应
            out.println(html.toString());
            out.flush();

        } catch (IOException e) {
            // 在实际应用中，这里应该有更完善的日志记录
            e.printStackTrace();
        }
    }
}