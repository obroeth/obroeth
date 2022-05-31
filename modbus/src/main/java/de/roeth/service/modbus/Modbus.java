/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort.BaudRate;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort.Parity;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortException;
import com.intelligt.modbus.jlibmodbus.slave.ModbusSlave;
import com.intelligt.modbus.jlibmodbus.slave.ModbusSlaveFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Modbus {

  private final List<ModbusSlave> slaves;
  private final ModbusProperties properties;
  @Getter
  private ModbusMaster master;

  private Modbus(ModbusProperties properties) {
    this.properties = properties;
    slaves = new ArrayList<>();
    try {
      init();
    } catch (ModbusException e) {
      log.error("Failed to instantiate modbus!", e);
    }
  }

  private void init() throws ModbusException {
    SerialParameters sp = new SerialParameters();
    sp.setParity(Parity.EVEN);
    sp.setStopBits(1);
    sp.setDataBits(8);
    sp.setBaudRate(BaudRate.BAUD_RATE_19200);
    sp.setDevice("/dev/ttyUSB0");
    try {
      master = ModbusMasterFactory.createModbusMasterRTU(sp);
      master.setResponseTimeout(1000);
    } catch (SerialPortException e) {
      throw new ModbusException("Failed to create master!", e);
    }
    try {
      for (int i = 1; i <= properties.getNumberOfSlaves(); i++) {
        sp.setDevice(String.valueOf(i));
        ModbusSlave slave = ModbusSlaveFactory.createModbusSlaveRTU(sp);
        slave.setServerAddress(i);
        slaves.add(slave);
      }
    } catch (SerialPortException e) {
      throw new ModbusException("Failed to create slaves!", e);
    }
    try {
      for (ModbusSlave slave : slaves) {
        slave.listen();
      }
      master.connect();
    } catch (ModbusIOException e) {
      throw new ModbusException("Failed to start master and slave!", e);
    }
  }
}
