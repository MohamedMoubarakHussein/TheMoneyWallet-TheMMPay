package com.themoneywallet.controller.httpservletannotation;

import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
// don't forget to make web.xml data metadata-complete=  false
//@WebServlet("/home")
@WebServlet(urlPatterns = {"/home" , "/main"} , name = "ss" , description = "" 
    , initParams = {@WebInitParam(name = "" , value = "") ,@WebInitParam(name = "" , value = "") }    
)
public class GetUserDashboardServletAnn extends HttpServlet{
    
}
