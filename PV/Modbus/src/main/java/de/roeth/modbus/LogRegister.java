//package de.roeth.modbus;
//
//import com.ghgande.j2mod.modbus.procimg.InputRegister;
//import com.ghgande.j2mod.modbus.procimg.Register;
//import de.roeth.communication.Utils;
//import org.json.JSONObject;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Date;
//
//public class LogRegister implements Register {
//
//    public long cacheTime;
//    private int value;
//
//    public LogRegister(String file, int register) {
//        try {
//            JSONObject root = Utils.createJsonObject(file);
//            cacheTime = root.getLong("cacheTime");
//            if (root.has(String.valueOf(register))) {
//                value = root.getInt(String.valueOf(register));
//            } else {
//                value = 0;
//            }
//        } catch (IOException e) {
//            value = 0;
//        }
//    }
//
//    public static void writeLogRegister(InputRegister[] registers, ModbusCallSequence sequence, String file) throws IOException {
//        JSONObject jsonObject = Utils.createJsonObject(file);
//        jsonObject.put("cacheTime", new Date().getTime());
//        for (int i = sequence.startRegister; i <= sequence.endRegister; i++) {
//            jsonObject.put(String.valueOf(i), registers[i - sequence.startRegister].toShort());
//        }
//        jsonObject.write(new FileWriter(file)).flush();
//    }
//
//    public static LogRegister[] readLogRegister(ModbusCallSequence sequence, String file) {
//        LogRegister[] fake = new LogRegister[sequence.length()];
//        for (int i = sequence.startRegister; i <= sequence.endRegister; i++) {
//            fake[i - sequence.startRegister] = new LogRegister(file, i);
//        }
//        return fake;
//    }
//
//    @Override
//    public int getValue() {
//        return value;
//    }
//
//    @Override
//    public void setValue(int i) {
//
//    }
//
//    @Override
//    public void setValue(short i) {
//
//    }
//
//    @Override
//    public void setValue(byte[] bytes) {
//
//    }
//
//    @Override
//    public int toUnsignedShort() {
//        return Math.abs(value);
//    }
//
//    @Override
//    public short toShort() {
//        return (short) value;
//    }
//
//    @Override
//    public byte[] toBytes() {
//        return new byte[0];
//    }
//}
