package hu.zsdoma.e4.logstream.service;

import java.util.List;

public interface LogReaderEventListener {
  void onEvent(List<LoggerLineDTO> lines);
}
