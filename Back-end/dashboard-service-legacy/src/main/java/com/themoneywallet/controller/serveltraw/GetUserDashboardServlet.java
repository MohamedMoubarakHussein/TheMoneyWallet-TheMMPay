package com.themoneywallet.controller.serveltraw;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        HttpServletResponse httpResp = (HttpServletResponse) resp;
        httpResp.setContentType("application/json");

        // The legacy servlet simply echoes a placeholder. Production code should delegate
        // to the new dashboard micro-service.
        String json = "{\"message\":\"Legacy dashboard endpoint has been replaced.\"}";
        try (PrintWriter writer = httpResp.getWriter()) {
            writer.print(json);
        }

    }
    
}
