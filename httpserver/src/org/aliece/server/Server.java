package org.aliece.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Server {
	private int myPort;
	private Selector selector;
	private ServerEventHandler myHandler;

	public Server(int port, ServerEventHandler handler) throws IOException {
		myPort = port;
		myHandler = handler;
		selector = SelectorFactory.getSelector(myPort);
	}

	public void listen() {
		SelectionKey key = null;
		try {
			for (;;) {
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					key = (SelectionKey) it.next();
					handleKey(key);
					it.remove();
				}
			}
		} catch (IOException e) {
			key.cancel();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void handleKey(SelectionKey key) throws IOException {
		if (key.isAcceptable())
			myHandler.acceptNewClient(selector, key);
		else if (key.isReadable())
			myHandler.readDataFromSocket(key);
	}
}
