package de.roeth.service.modbus.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class DeviceMapper {

  private final Map<String, Device> map = new HashMap<>();

  public static void main(String[] args) throws JsonProcessingException {
    DeviceMapper deviceMapper = new DeviceMapper();
    deviceMapper.getMap().put("test", new Device(0, 0));
    deviceMapper.getMap().put("test2", new Device(0, 1));
    String s = new ObjectMapper().writeValueAsString(deviceMapper);
    DeviceMapper deviceMapper1 = new ObjectMapper().readValue(s, DeviceMapper.class);
    System.out.println(s);
  }

  @JsonIgnore
  public Device getDevice(String name) {
    return map.get(name);
  }

}
