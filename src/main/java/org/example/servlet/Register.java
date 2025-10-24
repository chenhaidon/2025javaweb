package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@MultipartConfig
@WebServlet("/Register")
public class Register extends HttpServlet {

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
        Collection<Part> parts = req.getParts();
        // 存储上传后的图片路径（单个文件用 String 即可，多个文件用 List<String>）
        List<String> photoPaths = new ArrayList<>();
        for (Part part : parts) {
            String fileName = part.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                continue;
            }
            UUID uuid = UUID.randomUUID();
            String sfn = photo.getSubmittedFileName();
            String suffix = sfn.substring(sfn.lastIndexOf("."));
            String savePath = "D:/photo/" + uuid.toString() + suffix;
            part.write(savePath);
            photoPaths.add(savePath);
            //         String fn = "D:/IDEAWorkSpace/ch04/src/test/resources/photo/"+uuid.toString()+suffix;
//            String mime=getServletContext().getMimeType(fn);
//            resp.setContentType(mime);
//            try(FileInputStream fis= new FileInputStream(fn)){
//                byte[] b= new byte[1024];
//                while(fis.read(b)>0){
//                    resp.getOutputStream().write(b);
//                }
//            }
        }
        sb.append("用户名：").append(username).append("<br>");
        sb.append("密码：").append(password1).append("<br>");
        sb.append("电话号码：").append(phone).append("<br>");
        sb.append("性别：").append(gender).append("<br>");
        sb.append("爱好：");
        for (String h : hobby) {
            sb.append(h).append(" ");
        }
        sb.append("<br>");
// 关键修改3：循环显示所有上传的图片
        sb.append("上传的图片：<br>");
        if (photoPaths.isEmpty()) {
            sb.append("未上传任何图片<br>");
        } else {
            for (String path : photoPaths) {
                try (FileInputStream fis = new FileInputStream(path);
                     ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    byte[] imageBytes = bos.toByteArray();
                    // 转换为Base64格式（自动适配图片类型）
                    String base64Img = "data:image/*;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes);
                    // 添加图片标签（设置宽高）
                    sb.append("<img src='").append(base64Img).append("' width='200' height='200' style='margin:10px;'>");

                } catch (Exception e) {
                    sb.append("图片加载失败：").append(path).append("<br>");
                }
            }
        }

        resp.getWriter().println(sb);

        log(sb.toString());
    }
}