/*
 * ARELAS Copyright
 */
package de.roeth.service.modbus.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WriteRequest extends Request<String> {

  private boolean status;

  public WriteRequest(int serverAddress, int startAddress, boolean status) {
    super(serverAddress, startAddress);
    this.status = status;
  }

}
