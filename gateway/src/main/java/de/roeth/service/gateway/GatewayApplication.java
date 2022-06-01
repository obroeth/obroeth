/*
 * ARELAS Copyright
 */

package de.roeth.service.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

  @Bean
  public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes().route(p -> p.path("/read_coils").uri("lb://modbus/read_coils"))
        .route(p -> p.path("/read_discrete_input").uri("lb://modbus/read_discrete_input"))
        .route(p -> p.path("/flip_named_coil").uri("lb://modbus/flip_named_coil"))
        .route(p -> p.path("/write_time_coil").uri("lb://modbus/write_time_coil"))
        .route(p -> p.path("/interrupt_time_coil").uri("lb://modbus/interrupt_time_coil"))
        .route(p -> p.path("/flip_coil").uri("lb://modbus/flip_coil")).build();
  }

}
