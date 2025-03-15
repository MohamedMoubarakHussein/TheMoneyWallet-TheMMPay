package com.gatewayApi.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gatewayApi.config.security.AuthenticationFilter;

import lombok.RequiredArgsConstructor;
/**
 * Building the routing table 
 * 
 * <table border="1"  >
 * <caption>Routing table</caption>
 *   <tr>     <th>URL</th>     <th>Service</th>   </tr>
 *   <tr>    <td>/auth/**</td>     <td>auth-service</td> </tr>
 *   <tr>     <td>/user/**</td>     <td>user-managment-service</td> </tr>
 *   <tr>     <td>/wallet/**</td>     <td>wallet-service</td></tr>
 *    <tr>  <td>/history/**</td>   <td>history-service</td>  </tr>
 *    <tr>    <td>/notification/**</td>    <td>notification-service</td>   </tr>
 * </table>
 * 
 * 
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationFilter filter;

  @Bean
  public RouteLocator routesLocator(RouteLocatorBuilder routeBuilder) {
    return routeBuilder.routes()
        .route("auth-service",
            r -> r.path("/auth/**").filters(f -> f.filter(this.filter)).uri("lb://authentication-service"))
        .route("user-managment-service",
            r -> r.path("/user/**").filters(f -> f.filter(this.filter)).uri("lb://USER-MANAGMENT-SERVICE"))
       //     .route("wallet-service",
      //      r -> r.path("/wallet/**").filters(f -> f.filter(this.filter)).uri("lb://USER-MANAGMENT-SERVICE"))
       //     .route("history-service",
       //     r -> r.path("/history/**").filters(f -> f.filter(this.filter)).uri("lb://USER-MANAGMENT-SERVICE"))
       //     .route("notification-service",
       //     r -> r.path("/notification/**").filters(f -> f.filter(this.filter)).uri("lb://USER-MANAGMENT-SERVICE"))

        .build();
  }

}
