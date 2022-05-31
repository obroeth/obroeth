/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlipCoilRequest extends Request<String> {
  public FlipCoilRequest(int serverAddress, int startAddress) {
    super(serverAddress, startAddress);
  }
}
