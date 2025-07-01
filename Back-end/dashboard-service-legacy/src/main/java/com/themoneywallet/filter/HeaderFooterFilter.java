package com.themoneywallet.filter;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter(value = "*")
public class HeaderFooterFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
            
                PrintWriter writer = resp.getWriter();
                writer.println("Master mohamed is here");
                chain.doFilter(req, resp);
                writer.println("Master mohamed is leaving ");
        
    }
    
}
