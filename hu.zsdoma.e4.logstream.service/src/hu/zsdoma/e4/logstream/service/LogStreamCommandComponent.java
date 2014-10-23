package hu.zsdoma.e4.logstream.service;

import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

public class LogStreamCommandComponent implements CommandProvider,
		LogStreamCallback {
	private LogStreamService logStreamService;

	public void bindLogStreamService(LogStreamService logStreamService) {
		this.logStreamService = logStreamService;
	}

	public void unbindLogStreamService(LogStreamService logStreamService) {
		this.logStreamService = null;
	}

	@Override
	public String getHelp() {
		return "Fogalmazza át a kérést!";
	}

	public void _config(CommandInterpreter ci) {
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

	@Override
	public void onStreamChanged(List<String> pufferedLines) {
		for (String string : pufferedLines) {
			System.out.println(string);
		}
	}

}
