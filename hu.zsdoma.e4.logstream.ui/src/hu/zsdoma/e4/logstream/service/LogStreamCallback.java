package hu.zsdoma.e4.logstream.service;

import java.util.List;

public interface LogStreamCallback {
	void onStreamChanged(List<LoggerLineDTO> pufferedLines);
}
