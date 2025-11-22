package org.example.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DruidUtil {
    private static DataSource ds = null;

    public static Connection getConnection() {
        if (ds == null) {
            InputStream in = DruidUtil.class.getClassLoader().getResourceAsStream("druid.properties");
            Properties prop = new Properties();
            try {
                prop.load(in);
                ds = DruidDataSourceFactory.createDataSource(prop);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}