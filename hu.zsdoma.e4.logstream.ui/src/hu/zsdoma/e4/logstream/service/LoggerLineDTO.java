package hu.zsdoma.e4.logstream.service;

public class LoggerLineDTO {
  private long timestamp;
  private String message;

  public LoggerLineDTO(final long timestamp, final String message) {
    super();
    this.timestamp = timestamp;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public long getTimestamp() {
    return timestamp;
  }

}
