/*
 * ARELAS Copyright
 */

package de.roeth.service.switchcontrol;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
@RestController
public class SwitchControlLoop {

  private final List<Device> pressed = new ArrayList<>();
  @Autowired
  ModbusDelegate modbusDelegate;
  @Autowired
  SwitchControlProperties properties;

  @Scheduled(fixedDelay = 1)
  public void loop() {
    try {
      for (int i = 1; i <= properties.getNumberOfSlaves(); i++) {
        boolean[] coils = modbusDelegate.queueReadDiscreteInputRequest(i, 0, 8);
        if (coils != null) {
          for (int j = 0; j < coils.length; j++) {
            Device device = new Device(i, j);
            if (coils[j] && !pressed.contains(device)) {
              modbusDelegate.queueFlipCoilRequest(device.getServerAddress(), device.getDeviceNumber());
              pressed.add(device);
            } else if (!coils[j]) {
              pressed.remove(device);
            }
          }
        } else {
          log.error("Read discrete input return null!");
        }
      }
    } catch (Exception e) {
      log.error("Unknown error in switch control loop!", e);
    }
  }

}
