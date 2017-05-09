package api.user;

import api.http.GetServlet;
import api.pub.JsonModel;
import socket.IMClient;
import socket.IMClientManager;
import socket.IMMessageManager;
import tools.db.DBUtil_v2;
import tools.json.JSONArray;
import tools.json.JSONObject;
import tools.utils.Foundation;
import tools.utils.Usersx;

import javax.servlet.annotation.WebServlet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * s
 * Created by Chance on 2017/4/26.
 */
@WebServlet(name = "amigo", urlPatterns = "/amigo/*")
public class Amigo extends GetServlet {

    public Object find(Map<String, String> params) {
        String unid = params.get("unid");

        if (unid == null) {
            return new JsonModel(1, "请输入unid");
        }

        JsonModel json;
        try {
            ArrayList list = DBUtil_v2.queryMultipleResult("SELECT i.unid,i.register_date FROM tb_account AS a,tb_user_info AS i WHERE a.unid LIKE ? AND a.unid=i.unid", "%" + unid + "%");
            JSONArray findList = new JSONArray(list);
            if (list != null) {
                json = new JsonModel(0, "查询成功");
            } else {
                json = new JsonModel(1, "用户不存在");
            }

            json.data.put("FindList", findList);
            return json;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        json = new JsonModel(1, "查询失败");
        return json;
    }

    /**
     * 添加好友
     */
    public Object apply(Map<String, String> params) {

        String unid = params.get("unid");
        String unid_amigo = params.get("unid_apply");
        String token = params.get("token");
        {
            /*
             token：sha256(unid:unid_amigo#token)
              */
        }

        if (unid == null) {
            return new JsonModel(1, "unid不能为空");
        }

        if (unid_amigo == null) {
            return new JsonModel(1, "好友unid不能为空");
        }

        if (unid.equals(unid_amigo)) {
            return new JsonModel(1, "不能添加自己为好友");
        }

        if (Usersx.existsAmigo(unid, unid_amigo)) {
            return new JsonModel(1, "已经是好友");
        }

        JsonModel json = null;

        try {

            JSONObject data = DBUtil_v2.querySimpleResult("SELECT count(*) AS count FROM tb_account WHERE unid=?", unid_amigo);
            if (data.getInt("count") == 0) {
                json = new JsonModel(1, "用户不存在");
            } else {

                String t = Usersx.getToken(unid);
                if (t != null) {
                    String a_token = Foundation.SHA256(unid + ":" + unid_amigo + "#" + t);
                    if (a_token.equals(token)) {

                        // 查询有没有申请记录
//                        JSONObject record = DBUtil_v2.querySimpleResult("SELECT * FROM tb_apply_amigo WHERE unid=? AND to_unid=?", unid, unid_amigo);
//                        if (record != null) {
//                            return new JsonModel(0, "已经发送过加好友请求，等待对方同意！");
//                        }

                        String msgid = System.currentTimeMillis() + "";
                        String aid = Foundation.MD5(msgid);
                        double time = System.currentTimeMillis() / 1000.0;

                        // 加好友请求信息
                        JSONObject rjson = new JSONObject("{'code':'20005'}");
                        rjson.put("unid", 10005);
                        rjson.put("apply_unid", unid);
                        rjson.put("msgid", msgid);
                        rjson.put("aid", aid);
                        rjson.put("time", time);

                        int i = DBUtil_v2.executeUpdate("INSERT INTO tb_apply_amigo(aid, unid, to_unid) VALUES (?,?,?)",
                                aid, unid, unid_amigo);
                        if (i > 0) {
                            IMClient amigo = IMClientManager.getClient(unid_amigo);
                            if (amigo != null) {
                                amigo.writeMessage(rjson.getString("msgid"), rjson);
                            } else {
                                IMMessageManager.addOfflineMessage(unid_amigo, rjson.toString());
                            }
                            json = new JsonModel(0, "加好友请求已经发送");
                        } else {
                            json = new JsonModel(1, "申请添加好友失败");
                        }

                    } else {
                        json = new JsonModel(1, "身份验证失败");
                    }
                } else {
                    json = new JsonModel(1, "尚未登录");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return json;
    }

    /*

     */
    public Object reply(Map<String, String> params) {
        // {aid:xxxxx, unid:xxx, token:xxx} # token: sha256(aid@unid#token)

        String aid = params.get("aid");
        String unid = params.get("unid"); // 自己的unid
        String unid_amigo = params.get("unid_amigo"); // 申请人的unid
        String token = params.get("token");
        String status = params.get("status");
        {
            /*
             token：sha256(aid@unid#token)
              */
        }

        if (aid == null) {
            return new JsonModel(1, "事件id不能为空");
        }

        if (unid == null) {
            return new JsonModel(1, "unid不能为空");
        }

        if (unid_amigo == null) {
            return new JsonModel(1, "unid_amigo不能为空");
        }

        JsonModel json = null;

        try {

            JSONObject data = DBUtil_v2.querySimpleResult("SELECT count(*) AS count FROM tb_apply_amigo WHERE aid=?", aid);
            if (data.getInt("count") == 0) {
                return new JsonModel(1, "事件id不存在");
            }

            String t = Usersx.getToken(unid);

            if (t != null) {
                String a_token = Foundation.SHA256(aid + "@" + unid + "#" + t);
                if (a_token.equals(token)) {

                    boolean isOK = true;
                    if ("1".equals(status)) { // 同意
                        // 更新好友
                        isOK = DBUtil_v2.executeUpdate("INSERT INTO tb_amigo (unid_u, unid_n) VALUES (?,?)", unid_amigo, unid) == 1;

                        if (isOK) {

                            String msgid = System.currentTimeMillis() + "";

                            // 加好友请求信息
                            JSONObject rjson = new JSONObject("{'code':'200050'}");
                            rjson.put("unid", 10005);
                            rjson.put("unid_amigo", unid);
                            rjson.put("msgid", msgid);
                            rjson.put("aid", aid);
                            rjson.put("msg", "对方已同意好友申请");

                            // 通知申请人
                            IMClient amigo = IMClientManager.getClient(unid_amigo);
                            if (amigo != null) {
                                amigo.writeMessage(rjson.getString("msgid"), rjson);
                            } else {
                                IMMessageManager.addOfflineMessage(unid_amigo, rjson.toString());
                            }

                            json = new JsonModel(0, "已同意好友申请");
                        } else {
                            return new JsonModel(1, "添加好友失败");
                        }

                    } else {
                        json = new JsonModel(0, "已拒绝好友申请");
                    }

                    // 删除好友请求
                    isOK &= DBUtil_v2.executeUpdate("DELETE FROM tb_apply_amigo WHERE aid=?", aid) == 1;

                } else {
                    json = new JsonModel(1, "身份验证失败");
                }
            } else {
                json = new JsonModel(1, "尚未登录");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
