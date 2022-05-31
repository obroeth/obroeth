/*
 * ARELAS Copyright
 */

package de.roeth.service.switchcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
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
  private final StatusKeeper keeper = new StatusKeeper();
  @Autowired
  ModbusDelegate modbusDelegate;
  @Autowired
  SwitchControlProperties properties;

  @PostConstruct
  public void init() {
    log.info("Initializing Status Keeper");
    for (int i = 1; i <= properties.getNumberOfSlaves(); i++) {
      boolean[] coils = modbusDelegate.queueReadCoilsRequest(i, 0, 8);
      log.info("Slave " + i + ": " + Arrays.toString(coils));
      for (int j = 0; j < coils.length; j++) {
        keeper.addDevice(new Device(i, j), coils[j]);
      }
    }
  }

  @Scheduled(fixedDelay = 2000)
  public void loop() {
    for (int i = 1; i <= properties.getNumberOfSlaves(); i++) {
      boolean[] coils = modbusDelegate.queueReadDiscreteInputRequest(i, 0, 8);
      for (int j = 0; j < coils.length; j++) {
        Device device = new Device(i, j);
        if (coils[j] && !pressed.contains(device)) {
          boolean status = keeper.flipStatus(device);
          modbusDelegate.queueWriteCoilRequest(device.getServerAddress(), device.getDeviceNumber(), status);
          pressed.add(device);
        } else if (!coils[j]) {
          pressed.remove(device);
        }
      }
    }
  }

}
