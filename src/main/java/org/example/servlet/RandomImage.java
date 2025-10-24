package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class RandomImage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Random r = new Random();
        int which = Math.abs(r.nextInt() % 6 + 1);
        String name="风景0"+which+".jpg";
        String fn="D:/Temp/"+name;
        String mime = getServletContext().getMimeType(fn);
        resp.setContentType(mime);
        try (FileInputStream fis = new FileInputStream(fn)) {
            // byte[] b = new byte[1024];
            // 创建一个大小为 1024 字节的字节数组，用作缓冲区，用于分块读取文件。
            byte[] b = new byte[1024];

            // while(fis.read(b) > 0) { ... }
            // 循环读取文件内容到字节数组 b 中。
            // fis.read(b) 方法会读取数据到 b 中，并返回实际读取的字节数。
            // 当读取到文件末尾时，它会返回 -1，循环就会结束。
            while (fis.read(b) > 0) {

                // resp.getOutputStream().write(b);
                // 获取 HTTP 响应的输出流（用于发送二进制数据，如图片）。
                // 将从文件中读取的字节数据 b 写入到输出流中，发送给客户端。
                resp.getOutputStream().write(b);
            }

        }
    }
}
