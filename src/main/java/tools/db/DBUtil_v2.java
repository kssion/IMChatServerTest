package tools.db;

import tools.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 数据库管理工具
 * Created by Chance on 16/4/14.
 */

public class DBUtil_v2 {

    /* 阿里云数据库 */
    //数据库用户名
    private static String DB_USERNAME;
    //数据库密码
    private static String DB_PASSWORD;
    //驱动信息
    private static String DB_DRIVER;
    //数据库地址
    private static String DB_URL;

    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet resultSet;

    private static boolean isLoadDirver = false;
    static  {
        Properties config = new Properties();
        try {
            String path = DBUtil_v2.class.getClassLoader().getResource("config.properties").getPath();
            config.load(new FileInputStream(path));
            DB_DRIVER = config.getProperty("jdbc.driverClassName");
            DB_URL = config.getProperty("jdbc.url");
            DB_USERNAME = config.getProperty("jdbc.username");
            DB_PASSWORD = config.getProperty("jdbc.password");

            System.out.println("获取数据库信息成功！");

            try {
                Class.forName(DB_DRIVER);
                System.out.println("数据库驱动加载成功！");
                isLoadDirver = true;

            } catch(Exception e) {
                System.out.println("数据库驱动加载失败！");
                isLoadDirver = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得数据库的连接
     * @return conn
     */
    private Connection getConnection() {
        if (isLoadDirver) {
            try {
//                System.out.println(DB_DRIVER + "\n" + DB_URL + "\n" + DB_USERNAME + "\n" + DB_PASSWORD);
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            } catch (SQLException e) {
                System.err.println("获取数据库连接失败:"+e.getMessage());
            }
        } else {
            System.out.println("数据库驱动加载失败！");
            return null;
        }
        return connection;
    }

    /**
     * 增、删、改
     * @param sql sql
     * @param args 参数列表
     * @return 多条记录
     * @throws SQLException 抛出异常
     */
    private int _execUpdate(String sql, Object... args) throws SQLException {
        int result;

        this.getConnection();
        pstmt = connection.prepareStatement(sql);
        for(int i = 0; i < args.length; i++) {
            pstmt.setObject(1 + i, args[i]);
        }
        result = pstmt.executeUpdate();
        pstmt.close();
        connection.close();
        return result;
    }

    /**
     * 查询单条记录
     * @param sql sql
     * @param args 参数列表
     * @return 一条记录
     * @throws SQLException 抛出异常
     */
    private JSONObject _querySimpleResult(String sql, Object... args) throws SQLException {
        JSONObject json = null;
        this.getConnection();
        pstmt = connection.prepareStatement(sql);
        for(int i = 0; i < args.length; i++) {
            pstmt.setObject(1 + i, args[i]);
        }
        resultSet = pstmt.executeQuery();//返回查询结果
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col_len = metaData.getColumnCount();
        if (resultSet.next()) {
            json = this.generateMap(col_len, metaData, resultSet);
        }
        resultSet.close();
        pstmt.close();
        return json;
    }

    /**
     * 查询多条记录
     * @param sql sql
     * @param args 参数列表
     * @return 多条记录
     * @throws SQLException 抛出异常
     */
    private ArrayList<JSONObject> _queryModeResult(String sql, Object... args) throws SQLException {
        ArrayList<JSONObject> list = new ArrayList<>();
        
        this.getConnection();
        pstmt = connection.prepareStatement(sql);
        for(int i = 0; i < args.length; i++) {
            pstmt.setObject(1 + i, args[i]);
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while(resultSet.next()){
            JSONObject json = this.generateMap(cols_len, metaData, resultSet);
            list.add(json);
        }
        resultSet.close();
        pstmt.close();

        return list;
    }

    private JSONObject generateMap(int cols_len, ResultSetMetaData metaData, ResultSet rs) {
        JSONObject json = new JSONObject();
        for(int i = 0; i < cols_len; i++){
            String cols_name;
            try {
                cols_name = metaData.getColumnName(i + 1);
                Object cols_value = rs.getObject(cols_name);
                if(cols_value == null){
                    cols_value = "";
                }
                json.put(cols_name, cols_value);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return json;
    }
    
    /**
     * 增、删、改
     * @param sql sql
     * @param args 参数列表
     * @return 多条记录
     * @throws SQLException 抛出异常
     */
    public static int executeUpdate(String sql, Object... args) throws SQLException {
        DBUtil_v2 dbUtil_v2 = new DBUtil_v2();
        return dbUtil_v2._execUpdate(sql, args);
    }
    
    /**
     * 查询单条记录
     * @param sql sql
     * @param args 参数列表
     * @return 多条记录
     * @throws SQLException 抛出异常
     */
    public static JSONObject querySimpleResult(String sql, Object... args) throws SQLException {
        DBUtil_v2 dbUtil_v2 = new DBUtil_v2();
        return dbUtil_v2._querySimpleResult(sql, args);
    }

    /**
     * 查询多条记录
     * @param sql sql
     * @param args 参数列表
     * @return 多条记录
     * @throws SQLException 抛出异常
     */
    public static ArrayList<JSONObject> queryMultipleResult(String sql, Object... args) throws SQLException {
        DBUtil_v2 dbUtil_v2 = new DBUtil_v2();
        return dbUtil_v2._queryModeResult(sql, args);
    }

}
