/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import de.roeth.service.modbus.device.Device;
import de.roeth.service.modbus.device.StatusKeeper;
import de.roeth.service.modbus.request.FlipCoilRequest;
import de.roeth.service.modbus.request.ReadCoilsRequest;
import de.roeth.service.modbus.request.ReadDiscreteInputRequest;
import de.roeth.service.modbus.request.Request;
import de.roeth.service.modbus.request.TimeRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
@RestController
public class ModbusLoop {

  private final List<Request> requests = Collections.synchronizedList(new ArrayList<>());
  private final List<UUID> doneRequestIds = Collections.synchronizedList(new ArrayList<>());
  private final StatusKeeper keeper = new StatusKeeper();
  @Autowired
  public ModbusProperties properties;
  //  private Modbus modbus;
  //  private DeviceMapper deviceMapper;

  @PostConstruct
  public void init() {
    log.info("Initializing Modbus interface");
    //    modbus = new Modbus(properties);
    log.info("Initializing Device Mapper");
    //    try {
    //      deviceMapper = new ObjectMapper().readValue(properties.getDevices(), DeviceMapper.class);
    //    } catch (JsonProcessingException e) {
    //      log.error("Failed to initialize Device Mapper!", e);
    //    }
    log.info("Initializing Status Keeper");
    try {
      for (int i = 1; i <= properties.getNumberOfSlaves(); i++) {
        ReadCoilsRequest request = new ReadCoilsRequest(i, 0, 8);
        processReadCoilsRequest(request);
        boolean[] coils = request.getResponse();
        log.info("Slave " + i + ": " + Arrays.toString(coils));
        for (int j = 0; j < coils.length; j++) {
          keeper.addDevice(new Device(i, j), coils[j]);
        }
      }
    } catch (ModbusNumberException | ModbusProtocolException | ModbusIOException e) {
      log.error("Failed to initialize Status Keeper!", e);
    }
  }

  @Scheduled(fixedDelay = 1)
  public void loop() {
    if (!requests.isEmpty()) {
      List<Request> doneRequests = new ArrayList<>();
      requests.forEach(request -> {
        processRequest(request);
        if (doneRequestIds.contains(request.getUuid())) {
          doneRequests.add(request);
        }
      });
      doneRequests.forEach(requests::remove);
    }
  }

  private void processRequest(Request request) {
    try {
      if (request instanceof ReadDiscreteInputRequest) {
        ReadDiscreteInputRequest readDiscreteInputRequest = (ReadDiscreteInputRequest) request;
        processReadDiscreteInputRequest(readDiscreteInputRequest);
      } else if (request instanceof ReadCoilsRequest) {
        ReadCoilsRequest readCoilsRequest = (ReadCoilsRequest) request;
        processReadCoilsRequest(readCoilsRequest);
      } else if (request instanceof FlipCoilRequest) {
        FlipCoilRequest flipCoilRequest = (FlipCoilRequest) request;
        processFlipCoilRequest(flipCoilRequest);
      } else if (request instanceof TimeRequest) {
        TimeRequest timeRequest = (TimeRequest) request;
        processTimeRequest(timeRequest);
      }
    } catch (ModbusNumberException | ModbusProtocolException | ModbusIOException e) {
      log.error("Failed to perform request!", e);
    }
  }

  private void processTimeRequest(TimeRequest request) {
    Device device = new Device(request.getServerAddress(), request.getStartAddress());
    if (!request.isStarted()) {
      request.start();
      keeper.setStatus(device, true);
      log.info("Started time request for device: " + device);
    } else {
      request.update();
    }
    if (request.isDone()) {
      log.info("Finalized time request for device: " + device);
      doneRequestIds.add(request.getUuid());
    }
  }

  private void processFlipCoilRequest(FlipCoilRequest request)
      throws ModbusNumberException, ModbusProtocolException, ModbusIOException {
    Device device = new Device(request.getServerAddress(), request.getStartAddress());
    boolean newStatus = keeper.flipStatus(device);
    //    modbus.getMaster().writeSingleCoil(request.getServerAddress(), request.getStartAddress(), newStatus);
    log.info(device + " has new status: " + newStatus);
    log.info("Keeper: " + keeper);
    doneRequestIds.add(request.getUuid());
  }

  private void processReadCoilsRequest(ReadCoilsRequest request)
      throws ModbusNumberException, ModbusProtocolException, ModbusIOException {
    //    boolean[] coils =
    //        modbus.getMaster().readCoils(request.getServerAddress(), request.getStartAddress(), request.getQuantity());
    boolean[] coils = new boolean[8];
    request.setResponse(coils);
    doneRequestIds.add(request.getUuid());
  }

  private void processReadDiscreteInputRequest(ReadDiscreteInputRequest request)
      throws ModbusNumberException, ModbusProtocolException, ModbusIOException {
    //    boolean[] coils = modbus.getMaster()
    //        .readDiscreteInputs(request.getServerAddress(), request.getStartAddress(), request.getQuantity());
    boolean[] coils = new boolean[8];
    request.setResponse(coils);
    doneRequestIds.add(request.getUuid());
  }

  @GetMapping("/actuator/health")
  public ResponseEntity<String> health() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/read_discrete_input")
  public boolean[] queueReadDiscreteInputRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "start_address") int startAddress, @RequestParam(value = "quantity") int quantity) {
    ReadDiscreteInputRequest request = new ReadDiscreteInputRequest(serverAddress, startAddress, quantity);
    requests.add(request);
    while (!doneRequestIds.contains(request.getUuid())) {
    }
    doneRequestIds.remove(request.getUuid());
    return request.getResponse();
  }

  @PostMapping("/read_coils")
  public boolean[] queueReadCoilsRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "start_address") int startAddress, @RequestParam(value = "quantity") int quantity) {
    ReadCoilsRequest request = new ReadCoilsRequest(serverAddress, startAddress, quantity);
    requests.add(request);
    while (!doneRequestIds.contains(request.getUuid())) {
    }
    doneRequestIds.remove(request.getUuid());
    return request.getResponse();
  }

  @PostMapping("/flip_coil")
  public boolean queueFlipCoilRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "device_number") int deviceNumber) {
    FlipCoilRequest request = new FlipCoilRequest(serverAddress, deviceNumber);
    requests.add(request);
    while (!doneRequestIds.contains(request.getUuid())) {
    }
    doneRequestIds.remove(request.getUuid());
    return true;
  }

  @PostMapping("/write_time_coil")
  public boolean queueWriteTimeCoilRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "device_number") int deviceNumber, @RequestParam(value = "duration") long duration) {
    TimeRequest request = new TimeRequest(serverAddress, deviceNumber, duration);
    requests.add(request);
    while (!doneRequestIds.contains(request.getUuid())) {
    }
    doneRequestIds.remove(request.getUuid());
    return true;
  }

  @PostMapping("/flip_named_coil")
  public boolean queueFlipCoilRequest(@RequestParam(value = "name") String name) {
    //    Device device = deviceMapper.getDevice(name);
    //    if (device != null) {
    //      FlipCoilRequest request = new FlipCoilRequest(device.getServerAddress(), device.getDeviceNumber());
    //      requests.add(request);
    //      while (!doneRequestIds.contains(request.getUuid())) {
    //      }
    //      doneRequestIds.remove(request.getUuid());
    //      return true;
    //    }
    return false;
  }
}
