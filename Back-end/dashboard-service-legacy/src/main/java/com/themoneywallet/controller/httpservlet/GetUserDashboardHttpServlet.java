package com.themoneywallet.controller.httpservlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class GetUserDashboardHttpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // round trip to client then server  newReq
        
       // resp.sendRedirect("/jsp/registration.jsp"+"?Param1=val&param2=vall");
        resp.sendRedirect("/download");
        // internal redirect    if this doX(get-post ...)  it will invoke doX in second servlet
      //  RequestDispatcher requestDispatcher = req.getRequestDispatcher("MyThiredServlet?");
        // if i want to include the second result in the first 
     //   requestDispatcher.include(req, resp);

        // like the redirect
      //  requestDispatcher.forward(req, resp);


     //   HttpSession session = req.getSession(true);
      //  session.setAttribute("loggin", "true");
     //   session.getAttribute("loggin");
    }

   
    
}
