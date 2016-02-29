package hu.zsdoma.e4.logstream.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.Status;

import hu.zsdoma.e4.logstream.ui.Activator;

public class FileReaderRunnable implements Runnable {
  private String fileName;
  private BufferedReader bufferedReader;
  private final ConcurrentLinkedQueue<LoggerLineDTO> concurrentLinkedQueue =
      new ConcurrentLinkedQueue<LoggerLineDTO>();
  private boolean stop = false;

  public FileReaderRunnable(final String filaName) throws FileNotFoundException {
    super();
    fileName = filaName;
    init();
  }

  private void init() throws FileNotFoundException {
    bufferedReader = null;
    bufferedReader = new BufferedReader(new InputStreamReader(
        new FileInputStream(fileName)));
  }

  public List<LoggerLineDTO> pufferedLines() {
    List<LoggerLineDTO> pufferedLineList = new ArrayList<LoggerLineDTO>();
    if (!concurrentLinkedQueue.isEmpty()) {
      int size = concurrentLinkedQueue.size();
      for (int i = 0; i < size; i++) {
        pufferedLineList.add(concurrentLinkedQueue.remove());
      }
    }
    return pufferedLineList;
  }

  @Override
  public void run() {
    try {
      String line = null;
      while (!stop) {
        line = bufferedReader.readLine();
        processLine(line);
      }
    } catch (IOException | InterruptedException e) {
      Activator.getDefault().getLog()
          .log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void processLine(String line) throws InterruptedException {
    if (line == null) {
      Thread.sleep(1000);
    } else {
      LoggerLineDTO loggerLineDTO =
          new LoggerLineDTO().timestamp(System.currentTimeMillis()).message(line);
      concurrentLinkedQueue
          .add(loggerLineDTO);
    }
  }

  public void stop() {
    stop = true;
  }
}
