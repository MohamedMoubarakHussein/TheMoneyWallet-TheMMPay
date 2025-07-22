package com.themoneywallet.sharedUtilities.Interceptor;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LoggerConfigurer {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManagerFactory emf;

    @PostConstruct
    public void wire() {
        SQLInterceptor.setDataSource(dataSource);
        SQLInterceptor.setSessionFactory(emf.unwrap(SessionFactory.class));
    }
}