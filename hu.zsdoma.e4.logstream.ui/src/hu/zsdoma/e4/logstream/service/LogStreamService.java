package hu.zsdoma.e4.logstream.service;

public interface LogStreamService {

  void registerCallback(LogStreamCallback logStreamCallback);

  void start();

  void stop();

  void unregisterCallback(LogStreamCallback logStreamCallback);
}
