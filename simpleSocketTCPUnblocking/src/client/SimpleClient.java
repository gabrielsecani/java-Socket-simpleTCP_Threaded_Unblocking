package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SimpleClient {
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 9009;
	private String host;
	private int port;
	private SocketChannel clientChannel;

	public SimpleClient() {
		this.host = SimpleClient.DEFAULT_HOST;
		this.port = SimpleClient.DEFAULT_PORT;
	}

	public SimpleClient(String host) {
		this.host = host;
		this.port = SimpleClient.DEFAULT_PORT;
	}

	public SimpleClient(int port) {
		this.host = SimpleClient.DEFAULT_HOST;
		this.port = port;
	}

	public SimpleClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void Connect() {
		try {
			SocketChannel client = SocketChannel.open();
			client.configureBlocking(false);
			client.connect(new java.net.InetSocketAddress(host, port));
			Selector selector = Selector.open();
			client.register(selector, SelectionKey.OP_CONNECT);
			while (selector.select(500) > 0) {
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> i = keys.iterator();
				while (i.hasNext()) {
					SelectionKey key = i.next();
					i.remove();
					clientChannel = (SocketChannel) key.channel();
					if (key.isConnectable()) {
						System.out.println("Conexao realizada");
						if (clientChannel.isConnectionPending())
							clientChannel.finishConnect();
						handleConnection();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleConnection() {
		String msg = null;
		ByteBuffer buffer = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		do {
			try {
				System.out.print("Entre com um comando:");
				msg = in.readLine();
				buffer = ByteBuffer.wrap(msg.getBytes());
				clientChannel.write(buffer);
				buffer.clear();
			} catch (IOException e) {
				System.out.println("A conex\u00e3o com o servidor foi perdida de alguma forma.");
				e.printStackTrace();
			}
		} while (!msg.equalsIgnoreCase("CLOSE"));
		close();
	}

	public void close() {
		try {
			clientChannel.close();
		} catch (IOException e) {
			System.out.println("Erro no fechamento do socket");
			e.printStackTrace();
		}
	}
}
