package hu.zsdoma.e4.logstream.service;

public class LoggerLineDTO {
  private long timestamp;
  private String message;

  public LoggerLineDTO(long timestamp, String message) {
    super();
    this.timestamp = timestamp;
    this.message = message;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getMessage() {
    return message;
  }

}
