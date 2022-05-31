/*
 * ARELAS Copyright
 */

package de.roeth.service.switchcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
public class SwitchControlApplication {

  public static void main(String[] args) {
    SpringApplication.run(SwitchControlApplication.class, args);
  }

}
