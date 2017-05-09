package api.user;

import api.http.GetServlet;
import api.pub.JsonModel;
import tools.json.JSONObject;
import socket.IMClient;
import socket.IMClientManager;
import socket.IMMessageManager;
import tools.db.DBUtil_v2;
import tools.utils.Foundation;

import javax.servlet.annotation.WebServlet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 用户接口
 * Created by Chance on 2017/2/26.
 */
@WebServlet(name = "users", urlPatterns = {"/users/*"})
public class Users extends GetServlet {

    /**
     * 登录
     */
    public Object login(Map<String, String> params) {

        String unid = params.get("unid");
        String pwd = params.get("pwd");
        String sign = params.get("sign");

        if (unid == null || pwd == null) {
            return new JsonModel(1, "请输入用户名或密码");
        }

        if (sign == null) {
            return new JsonModel(1, "授权失败");
        }

        JsonModel model;

        try {

            JSONObject result = DBUtil_v2.querySimpleResult("SELECT password FROM tb_account WHERE unid=?", unid);
            if (null != result) {
                String p = result.getString("password");

                String time_sign = Foundation.decryption_sign(sign);
                String p_v = Foundation.SHA256(time_sign + "#" + p);

                if (pwd.equals(p_v)) {
                    // 生成token 写入数据库
                    String token = Foundation.MD5(String.valueOf(System.currentTimeMillis()));
                    DBUtil_v2.executeUpdate("REPLACE INTO tb_login_info(unid, token) VALUE (?, ?)", unid, token);
                    model = new JsonModel(0, "登录成功");
                    model.data.put("token", token);
                    return model;
                }
            }
            model = new JsonModel(1, "用户名或密码不正确");
            return model;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        model = new JsonModel(1, "登录失败，请重试！");
        return model;
    }

    /**
     * 注册
     */
    public Object register(Map<String, String> params) {

        String unid = params.get("unid");
        String pwd = params.get("pwd");

        if (unid == null || pwd == null) {
            return new JsonModel(1, "请输入用户名或密码");
        }

//        Foundation.getIpAddress(params);

        JsonModel json;

        try {

            JSONObject result = DBUtil_v2.querySimpleResult("SELECT count(*) AS count FROM tb_account WHERE unid=?", unid);
            int count = result.getInt("count");
            if (count > 0) {
                json = new JsonModel(1, "用户名已经注册");
                return json;

            } else {

                count = DBUtil_v2.executeUpdate("INSERT INTO tb_account(unid, password) VALUE (?, ?)", unid, pwd);

                if (count > 0) {
                    // 记录注册时间
                    DBUtil_v2.executeUpdate("INSERT INTO tb_user_info(unid) VALUE (?)", unid);
                    json = new JsonModel(0, "注册成功");
                    return json;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        json = new JsonModel(1, "注册失败，请重试！");
        return json;
    }

    /**
     * 获取用户信息
     */
    public Object getInfo(Map<String, String> params) {

        String unid = params.get("unid");

        if (unid == null) {
            return new JsonModel(1, "请输入unid");
        }

        JsonModel json;
        try {
            JSONObject data = DBUtil_v2.querySimpleResult("SELECT i.unid,i.register_date FROM tb_account AS a,tb_user_info AS i WHERE a.unid=? AND a.unid=i.unid", unid);

            if (data != null) {
                json = new JsonModel(0, "查询成功");
            } else {
                json = new JsonModel(1, "用户不存在");
            }

            json.data = data;
            return json;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        json = new JsonModel(1, "查询失败");
        return json;
    }

}
