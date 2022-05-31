/*
 * ARELAS Copyright
 */

package de.roeth.service.switchcontrol;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "modbus")
public interface ModbusDelegate {

  @PostMapping("/read_discrete_input")
  boolean[] queueReadDiscreteInputRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "start_address") int startAddress, @RequestParam(value = "quantity") int quantity);

  @PostMapping("/read_coils")
  boolean[] queueReadCoilsRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "start_address") int startAddress, @RequestParam(value = "quantity") int quantity);

  @PostMapping("/flip_coil")
  public boolean queueFlipCoilRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "device_number") int deviceNumber);

}
