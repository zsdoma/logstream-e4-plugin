package hu.zsdoma.e4.logstream.service;

import java.util.List;
import java.util.Objects;

public class LogStreamServiceImpl implements LogStreamService,
		LogReaderEventListener {
	/**
	 * /tmp/test.log
	 */
	private String filaName;

	private static class FileReaderTask implements Runnable {

		private static final int REFRESH_DELAY = 1000;
		private String filaName;
		private LogReaderEventListener logReaderEventListener;
		private boolean stop;

		public FileReaderTask(final String filaName,
				final LogReaderEventListener logReaderEventListener) {
			super();
			this.filaName = filaName;
			this.logReaderEventListener = logReaderEventListener;
		}

		@Override
		public void run() {
			FileReaderRunnable fileReaderRunnable = new FileReaderRunnable(
					filaName);
			new Thread(fileReaderRunnable).start();
			while (!stop) {
				List<String> pufferedLines = fileReaderRunnable.pufferedLines();
				if (pufferedLines.isEmpty()) {
					try {
						Thread.sleep(REFRESH_DELAY);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				} else {
					logReaderEventListener.onEvent(pufferedLines);
				}
			}
			fileReaderRunnable.stop();
		}

		public void stop() {
			stop = true;
		}

	}

	private LogStreamCallback logStreamCallback;
	private FileReaderTask fileReaderTask;

	public LogStreamServiceImpl(final String filaName) {
		super();
		this.filaName = filaName;
		fileReaderTask = new FileReaderTask(filaName, this);
	}

	@Override
	public void registerCallback(final LogStreamCallback logStreamCallback) {
		Objects.requireNonNull(logStreamCallback, "LogStreamCallback is null!");
		this.logStreamCallback = logStreamCallback;
		System.out.println("Callback activated!");
	}

	@Override
	public void unregisterCallback(final LogStreamCallback logStreamCallback) {
		this.logStreamCallback = null;
		System.out.println("Callback deactivated!");
	}

	@Override
	public void start() {
		new Thread(fileReaderTask).start();
	}

	@Override
	public void stop() {
		fileReaderTask.stop();
	}

	@Override
	public void onEvent(final List<String> lines) {
		if (logStreamCallback != null) {
			logStreamCallback.onStreamChanged(lines);
		}
	}

	@Override
	protected void finalize() throws Throwable {
	    fileReaderTask.stop();
	}

}
