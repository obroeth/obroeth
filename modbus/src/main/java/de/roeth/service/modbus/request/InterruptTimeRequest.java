/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterruptTimeRequest extends Request<String> {
  public InterruptTimeRequest(int serverAddress, int startAddress) {
    super(serverAddress, startAddress);
  }
}
