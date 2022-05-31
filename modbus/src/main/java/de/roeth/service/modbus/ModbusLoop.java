/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus;

import de.roeth.service.modbus.request.ReadCoilsRequest;
import de.roeth.service.modbus.request.ReadDiscreteInputRequest;
import de.roeth.service.modbus.request.Request;
import de.roeth.service.modbus.request.WriteRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
  @Autowired
  public ModbusProperties properties;

  private int counter = 0;

  @Scheduled(fixedDelay = 1)
  public void loop() {
    if (!requests.isEmpty()) {
      List<Request> doneRequests = new ArrayList<>();
      requests.forEach(request -> {
        processRequest(request);
        doneRequests.add(request);
      });
      doneRequests.forEach(requests::remove);
    }
  }

  private void processRequest(Request request) {
    if (request instanceof ReadDiscreteInputRequest) {
      ReadDiscreteInputRequest readDiscreteInputRequest = (ReadDiscreteInputRequest) request;
      if ((counter >= 3 && counter < 5) || (counter > 10 && counter < 14)) {
        boolean[] ans = new boolean[8];
        ans[0] = true;
        readDiscreteInputRequest.setResponse(ans);
      } else {
        readDiscreteInputRequest.setResponse(new boolean[8]);
      }
      counter++;
    } else if (request instanceof ReadCoilsRequest) {
      ReadCoilsRequest readCoilsRequest = (ReadCoilsRequest) request;
      readCoilsRequest.setResponse(new boolean[8]);
    } else if (request instanceof WriteRequest) {
      WriteRequest writeRequest = (WriteRequest) request;
      log.info("Answered write!");
    }
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

  @PostMapping("/write_coil")
  public boolean queueWriteCoilRequest(@RequestParam(value = "server_address") int serverAddress,
      @RequestParam(value = "device_number") int deviceNumber, @RequestParam(value = "status") boolean status) {
    WriteRequest request = new WriteRequest(serverAddress, deviceNumber, status);
    requests.add(request);
    while (!doneRequestIds.contains(request.getUuid())) {
    }
    doneRequestIds.remove(request.getUuid());
    return true;
  }
}
