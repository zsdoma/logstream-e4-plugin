package hu.zsdoma.e4.logstream.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileReaderRunnable implements Runnable {
	private String fileName;
	private BufferedReader bufferedReader;
	private final ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
	private boolean stop = false;

	public FileReaderRunnable(final String filaName) {
		super();
		fileName = filaName;
		init();
	}

	private void init() {
		bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<String> pufferedLines() {
		List<String> pufferedLineList = new ArrayList<String>();
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
				if (line == null) {
					Thread.sleep(1000);
				} else {
					concurrentLinkedQueue.add(System.currentTimeMillis() + ": "
							+ line);
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace(); // TODO logger
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

	public void stop() {
		stop = true;
	}
}