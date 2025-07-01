package com.themoneywallet.controller.httpservlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet(urlPatterns = {"/download"})
public class DownloadFilesServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = "/WEB-INF";
        String fileName = "/web.xml";
        resp.setContentType("APPLICATION/OCTET-STREAM");
        resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        PrintWriter writer = resp.getWriter();
        InputStream resourceAsStream = getServletContext().getResourceAsStream(path+fileName);
        InputStreamReader isr = new InputStreamReader(resourceAsStream);
        BufferedReader bufferedReader = new BufferedReader(isr);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            writer.println(line);
        }
    }
    
}
