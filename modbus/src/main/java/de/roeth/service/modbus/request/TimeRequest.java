/*
 * ARELAS Copyright
 */

package de.roeth.service.modbus.request;

public class TimeRequest extends Request<String> {

  private final long durationMs;
  private long runtime = 0;
  private long lastUpdate;

  public TimeRequest(int serverAddress, int startAddress, long durationMs) {
    super(serverAddress, startAddress);
    this.durationMs = durationMs;
  }

  public void update() {
    long now = System.currentTimeMillis();
    runtime += now - lastUpdate;
    lastUpdate = now;
  }

  public boolean isDone() {
    return runtime >= durationMs;
  }

  public void interrupt() {
    runtime = durationMs;
  }

  public boolean isStarted() {
    return runtime > 0;
  }

  public void start() {
    runtime = 1;
    lastUpdate = System.currentTimeMillis();
  }

}
