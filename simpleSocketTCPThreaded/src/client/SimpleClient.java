package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleClient {
	private Socket clientSocket;
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 9009;
	private String host;
	private int port;
	private PrintWriter out = null;

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
			clientSocket = new Socket(host,port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			System.out.println("Servidor não encontrado.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro na preparação para ler o socket.");
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	public void SendMessage(String message) {
		out.println(message);
	}

	public void close() {
		try {
			clientSocket.shutdownOutput();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Erro no fechamento do socket");
			e.printStackTrace();
		}
	}
}
