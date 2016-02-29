package hu.zsdoma.e4.logstream.service;

import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

public class LogStreamCommandComponent implements CommandProvider,
    LogStreamCallback {
  private LogStreamService logStreamService;

  public void _config(final CommandInterpreter ci) {
    String nextArgument = ci.nextArgument();
    if (nextArgument == null) {
      return;
    }

    if ("activate".equals(nextArgument)) {
      logStreamService.registerCallback(this);
    } else if ("deactivate".equals(nextArgument)) {
      logStreamService.unregisterCallback(null);
    }
  }

  public void bindLogStreamService(final LogStreamService logStreamService) {
    this.logStreamService = logStreamService;
  }

  @Override
  public String getHelp() {
    return "Fogalmazza át a kérést!";
  }

  @Override
  public void onStreamChanged(final List<LoggerLineDTO> pufferedLines) {
    for (LoggerLineDTO event : pufferedLines) {
      System.out.println(event.message);
    }
  }

  public void unbindLogStreamService(final LogStreamService logStreamService) {
    this.logStreamService = null;
  }

}
