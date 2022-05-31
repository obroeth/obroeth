/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class ModbusProperties {

  @Getter
  @Value("${modbus.config.numberSlaves}")
  private int numberOfSlaves;

}
