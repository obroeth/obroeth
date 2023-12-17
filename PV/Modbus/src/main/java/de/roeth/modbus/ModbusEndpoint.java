package de.roeth.modbus;

import com.ghgande.j2mod.modbus.util.SerialParameters;

public class ModbusEndpoint {

    public static ModbusEndpoint DEYE = new ModbusEndpoint("/dev/ttyUSB1", 1,9600, "none", 8, 1);
    public ModbusEndpoint(String port, int slave, int baudrate, String parity, int databits, int stopbits) {
        this.port = port;
        this.slave = slave;
        this.baudrate = baudrate;
        this.parity = parity;
        this.databits = databits;
        this.stopbits = stopbits;
    }

    public String port;
    public int slave;
    public int baudrate;
    public String parity;
    public int databits;
    public int stopbits;

    public SerialParameters getParameter() {
        SerialParameters param = new SerialParameters();
        param.setBaudRate(baudrate);
        param.setParity(parity);
        param.setStopbits(stopbits);
        param.setDatabits(databits);
        param.setPortName(port);
        return param;
    }
}
