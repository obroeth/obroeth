/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ModbusApplication {

  public static void main(String[] args) {
    SpringApplication.run(ModbusApplication.class, args);
  }

}
