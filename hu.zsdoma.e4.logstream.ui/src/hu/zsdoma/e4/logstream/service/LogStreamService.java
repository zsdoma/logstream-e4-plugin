package hu.zsdoma.e4.logstream.service;

public interface LogStreamService {

	void start();

	void stop();

	void registerCallback(LogStreamCallback logStreamCallback);

	void unregisterCallback(LogStreamCallback logStreamCallback);
}
