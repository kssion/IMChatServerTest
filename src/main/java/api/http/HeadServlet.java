/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @time    2016.04.01
 * @author  Chance
 */
public class HeadServlet extends BaseServlet {
    
    public HeadServlet() {
        super();
    }
    
    @Override
    public void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/tools.json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            process(request, response);
	} catch (Exception ex) {
            throw new ServletException(ex);
	}
    }
}
