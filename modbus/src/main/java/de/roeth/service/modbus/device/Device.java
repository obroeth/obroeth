/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus.device;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Device {

  private int serverAddress;
  private int deviceNumber;

  public Device(String code) {
    String[] split = code.split(",");
    serverAddress = Integer.parseInt(split[0]);
    deviceNumber = Integer.parseInt(split[1]);
  }

  public String encode() {
    return serverAddress + "," + deviceNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Device device = (Device) o;
    return serverAddress == device.serverAddress && deviceNumber == device.deviceNumber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(serverAddress, deviceNumber);
  }

  @Override
  public String toString() {
    return "Device{" + "serverAddress=" + serverAddress + ", deviceNumber=" + deviceNumber + '}';
  }
}
