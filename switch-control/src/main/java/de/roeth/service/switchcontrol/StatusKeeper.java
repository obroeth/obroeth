/*
 * ARELAS Copyright
 */
package de.roeth.service.switchcontrol;

import java.util.HashMap;
import java.util.Map;

public class StatusKeeper {

  private final Map<Device, Boolean> control = new HashMap<>();

  public void addDevice(Device device, boolean status) {
    control.put(device, status);
  }

  public boolean flipStatus(Device device) {
    control.put(device, !control.get(device));
    return control.get(device);
  }

  //  public void flipStatus(Device device) {
  //    control.put(device, !control.get(device));
  //    WebClient client = WebClient.builder().baseUrl("http://localhost:8080/write_coil")
  //        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE).build();
  //    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
  //    formData.add("code", device.encode());
  //    formData.add("status", String.valueOf(control.get(device)));
  //    client.post().body(BodyInserters.fromFormData(formData)).retrieve().bodyToMono(String.class).block();
  //  }

  public boolean getStatus(Device device) {
    return control.get(device);
  }

  @Override
  public String toString() {
    return "StatusKeeper{" + "control=" + control + '}';
  }
}
