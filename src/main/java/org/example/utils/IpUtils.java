package org.example.utils;

import org.lionsoul.ip2region.xdb.Searcher;
import java.io.*;

public class IpUtils {
    private static final String DB_RESOURCE_PATH = "ipdb/ip2region_v4.xdb"; // 类路径下的资源
    private static Searcher searcher;

    static {
        try {
            // 从类路径加载资源流
            InputStream inputStream = IpUtils.class.getClassLoader().getResourceAsStream(DB_RESOURCE_PATH);
            if (inputStream == null) {
                throw new IOException("未找到IP数据库文件: " + DB_RESOURCE_PATH);
            }

            // 写入临时文件（因为Searcher需要文件路径）
            File tempFile = File.createTempFile("ip2region_v4", ".xdb");
            tempFile.deleteOnExit(); // JVM退出时自动删除
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }

            // 初始化Searcher
            searcher = Searcher.newWithFileOnly(tempFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("IP数据库初始化失败，程序无法运行", e);
        }
    }

    /**
     * 获取IP对应的城市信息
     * @param ip IP地址（IPv4）
     * @return 解析结果（如"中国|北京|北京"）或错误信息
     */
    public static String getCityInfo(String ip) {
        // 校验IP合法性（简化版，实际可添加正则校验）
        if (ip == null || ip.trim().isEmpty()) {
            return "错误：IP地址不能为空";
        }

        // 过滤局域网IP
        if ("127.0.0.1".equals(ip) || ip.startsWith("192.168.")) {
            return "局域网IP";
        }

        try {
            String region = searcher.search(ip);
            if (region == null || region.trim().isEmpty()) {
                return "未查询到该IP的信息";
            }
            // 清除无效占位符
            return region.replace("|0", "").replace("0|", "");
        } catch (Exception e) {
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 关闭资源（适用于长时间运行的服务）
     */
    public static void close() {
        if (searcher != null) {
            try {
                searcher.close();
                System.out.println("IP数据库资源已释放");
            } catch (IOException e) {
                System.err.println("关闭IP数据库失败：" + e.getMessage());
            }
        }
    }
}