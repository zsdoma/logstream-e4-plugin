package hu.zsdoma.e4.logstream.service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

public class LogStreamServiceImpl implements LogStreamService,
    LogReaderEventListener {
  private String filaName;

  private static class FileReaderTask implements Runnable {

    private static final int REFRESH_DELAY = 1000;
    private String fileName;
    private LogReaderEventListener logReaderEventListener;
    private boolean stop;

    public FileReaderTask(final String fileName,
        final LogReaderEventListener logReaderEventListener) {
      super();
      this.fileName = fileName;
      this.logReaderEventListener = logReaderEventListener;
    }

    @Override
    public void run() {
      FileReaderRunnable fileReaderRunnable = null;
      try {
        fileReaderRunnable = new FileReaderRunnable(fileName);
        new Thread(fileReaderRunnable).start();
        while (!stop) {
          List<LoggerLineDTO> pufferedLines = fileReaderRunnable.pufferedLines();
          if (pufferedLines.isEmpty()) {
            refreshWaiter();
          } else {
            logReaderEventListener.onEvent(pufferedLines);
          }
        }
      } catch (FileNotFoundException e) {
        System.out.println("File not found");
        // TODO logging
        // TODO Ide kell valami callback hívás, hogy a UI is tudja, mi a gond.
      } finally {
        if (fileReaderRunnable != null) {
          fileReaderRunnable.stop();
        }
      }
    }

    /**
     * Wait {@link #REFRESH_DELAY} to refresh file content.
     */
    private void refreshWaiter() {
      try {
        Thread.sleep(REFRESH_DELAY);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
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
  public void onEvent(final List<LoggerLineDTO> lines) {
    if (logStreamCallback != null) {
      logStreamCallback.onStreamChanged(lines);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    fileReaderTask.stop();
  }

}
