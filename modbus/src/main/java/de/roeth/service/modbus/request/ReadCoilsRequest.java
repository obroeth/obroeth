/*
 * ARELAS Copyright
 */
package de.roeth.service.modbus.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadCoilsRequest extends Request<boolean[]> {

  private int quantity;

  public ReadCoilsRequest(int serverAddress, int startAddress, int quantity) {
    super(serverAddress, startAddress);
    this.quantity = quantity;
  }

}
