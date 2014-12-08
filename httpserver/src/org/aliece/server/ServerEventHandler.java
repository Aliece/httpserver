package org.aliece.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.aliece.http.ServletContext;

public class ServerEventHandler {
	private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
	private ServletContext context;
	private Queue q;
	private int socketConnects = 0;

	public ServerEventHandler(String root, int workerThreads)
			throws IOException {
		q = new Queue();
		context = new ServletContext(new File(root));
		for (int i = 0; i < workerThreads; i++)
			new RequestHandlerThread(context, q).start();
	}

	public void disconnectClient(SelectionKey key) throws IOException {
		key.attach(null);
		key.channel().close();
	}

	public void acceptNewClient(Selector selector, SelectionKey key)
			throws IOException, ClosedChannelException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel channel = server.accept();
		channel.configureBlocking(false);
		SelectionKey readKey = channel.register(selector, SelectionKey.OP_READ);
		readKey.attach(new Client(readKey, q));
	}

	public void readDataFromSocket(SelectionKey key) throws IOException {
		int count = ((SocketChannel) key.channel()).read(byteBuffer);
		if (count > 0) {
			byteBuffer.flip();
			byte[] data = new byte[count];
			byteBuffer.get(data, 0, count);
			((Client) key.attachment()).write(data);
		} else if (count < 0) {
			key.channel().close();
		}
		byteBuffer.clear();
	}
}
