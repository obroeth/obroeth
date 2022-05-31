/*
 * ARELAS Copyright
 */

package de.roeth.service.delegate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "modbus")
public interface ModbusDelegate {

  @GetMapping("/test")
  ResponseEntity<String> queue();

}
