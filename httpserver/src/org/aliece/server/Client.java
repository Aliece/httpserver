package org.aliece.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.channels.SelectionKey;

public class Client {
	public SelectionKey key;
	public PipedInputStream clientInputStream;
	private OutputStream clientOut;
	private Queue myQueue;
	private boolean requestReady = false;

	public Client(SelectionKey readKey, Queue q) throws IOException {
		key = readKey;
		myQueue = q;
		initializePipe();
	}

	private void initializePipe() throws IOException {
		PipedOutputStream pos = new PipedOutputStream();
		clientInputStream = new PipedInputStream(pos);
		clientOut = new BufferedOutputStream(pos, 1048);
	}

	public synchronized void write(byte[] data) throws IOException {
		clientOut.write(data);
		clientOut.flush();
		if (!requestReady) {
			requestReady = true;
			myQueue.insert(this);
		}
	}

	public synchronized boolean notifyRequestDone() throws IOException {
		if (clientInputStream.available() <= 0)
			requestReady = false;
		return !requestReady;
	}
}
