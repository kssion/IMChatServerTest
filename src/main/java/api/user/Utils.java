package api.user;

import api.http.GetServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Chance on 2017/3/20.
 */
@WebServlet(name = "utils", urlPatterns = "/utils/*")
public class Utils extends GetServlet {

    /**
     * 同步时间
     */
    public Object synctime(HttpServletRequest request) {
        return System.currentTimeMillis() / 1000.00;
    }

}
