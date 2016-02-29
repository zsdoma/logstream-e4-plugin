package hu.zsdoma.e4.logstream.service;

public class LoggerLineDTO {
  public long timestamp;
  public String message;

  public LoggerLineDTO timestamp(final long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public LoggerLineDTO message(final String message) {
    this.message = message;
    return this;
  }
}
