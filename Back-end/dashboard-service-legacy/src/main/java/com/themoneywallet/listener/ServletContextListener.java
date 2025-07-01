package com.themoneywallet.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ServletContextListener  implements jakarta.servlet.ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
   
            ServletContext servletContext = sce.getServletContext();
            servletContext.setAttribute("dezzz", "mohamed");
            System.out.println("servlet initlizaed . ");
    }

     
}