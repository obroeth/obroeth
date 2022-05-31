/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus.request;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Request<T> {

  private int serverAddress;
  private int startAddress;
  private T response;
  private UUID uuid;

  public Request(int serverAddress, int startAddress) {
    this.serverAddress = serverAddress;
    this.startAddress = startAddress;
    this.uuid = UUID.randomUUID();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Request<?> request = (Request<?>) o;
    return Objects.equals(uuid, request.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }
}
