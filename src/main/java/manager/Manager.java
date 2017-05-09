package manager;

import api.http.GetServlet;
import tools.db.DBUtil_v2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Chance on 2017/5/2.
 */
@WebServlet(name = "manager")
public class Manager extends GetServlet {

    public Object update(Map<String, String> params) {

        String key = params.get("key");
        if (null == key || "".equals(key)) {
            return "null";
        }
        switch (key) {
            case "db": {

                break;
            }
            default: {

            }
        }
        return "success";
    }
}
