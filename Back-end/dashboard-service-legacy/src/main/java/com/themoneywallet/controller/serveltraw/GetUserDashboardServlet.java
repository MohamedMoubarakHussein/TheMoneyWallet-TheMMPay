package com.themoneywallet.controller.serveltraw;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class GetUserDashboardServlet implements Servlet {
    ServletConfig servletConfig;
    @Override
    public void destroy() {
        System.out.println("Servelt destoried");
    }

    @Override
    public ServletConfig getServletConfig() {
     return null;
    }

    @Override
    public String getServletInfo() {
      return "My first servelt";
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.servletConfig = config;
        System.out.println("The servelt has been initlized ");
    }

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        // need to cast to get accessing to http specifics.
        HttpServletRequest httpReq = (HttpServletRequest) req;

        String token = httpReq.getHeader("Authorization");
        
        //TODO prepare the dashboard  

        //TODO need to write unified response with the dashboard
        //TODO how to return unified response inside responseEntity
        PrintWriter writer = resp.getWriter();
        writer.println("<h1> Hello world </h1>");

    }
    
}
