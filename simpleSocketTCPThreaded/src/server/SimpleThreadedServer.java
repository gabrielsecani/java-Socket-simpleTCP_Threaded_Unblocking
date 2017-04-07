package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SimpleServer Implementa��o de um servidor TCP simples para ilustrar os
 * fundamentos de sistemas distribu�dos para os alunos da FIAP.
 * 
 * @author Fabian Martins
 **/
public class SimpleThreadedServer {

	private ServerSocket socket;
	private int port;

	public SimpleThreadedServer() {
		this.port = 9009;
	}

	public SimpleThreadedServer(int port) {
		this.port = port;
	}

	/**
	 * Abre o socket e define o timeout.
	 */
	public void open() {
		try {
			socket = new ServerSocket(port);
			System.out.println("Servidor ativo e aguardando conex�o.");

		} catch (IOException e) {
			System.out.println("Erro criando o socket na porta:" + port);
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que determina o in�cio de atendimento � uma requisi��o.
	 */
	public void handleRequest() {
		for(;;) {
			if ((socket != null) && (socket.isBound())) {
				try {
					Socket clientSocket = socket.accept();
					clientSocket.setSoTimeout(60000);
					System.out.println("Cliente conectado:"
							+ clientSocket.getInetAddress().getHostAddress()
							+ ":" + clientSocket.getPort());
					Thread worker = new Thread(new Worker(clientSocket));
					worker.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
