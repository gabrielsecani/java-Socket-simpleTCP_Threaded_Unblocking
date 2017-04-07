package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Vector;

/**
 * SimpleUnblockingServer Implementacao de um servidor TCP simples para ilustrar
 * os fundamentos de sistemas distribuidos para os alunos da FIAP.
 * 
 * Esta classe implementa um simples serviço de recepção de mensagem.
 * 
 * @author Fabian Martins
 **/
public class SimpleUnblockingServer {

	private ServerSocketChannel serverChannel;
	private Selector selector;
	private int port;
	
	//Ordena o fechamento do programa;
	private boolean shutdownRequested = false;
	
	public SimpleUnblockingServer() {
		this.port = 9009;
	}

	public SimpleUnblockingServer(int port) {
		this.port = port;
	}
	
	/**
	 * Executa o servidor.
	 */
	public void run() {
		this.open();
		this.handleRequest();
		System.exit(0);
	}

	/**
	 * Abre o socket para recepção de comunicações
	 */
	private void open() {
		try {
			// Obtém um canal para um server Socket 
			serverChannel = ServerSocketChannel.open();
			// Define como não bloqueante
			serverChannel.configureBlocking(false);
			// Vincula o servidor ao localhost e porta definida
			serverChannel.socket().bind(
					new InetSocketAddress("localhost", port));
			System.out.println("Servidor ativo e aguardando conex\u00E3o.");
			// Cria um novo selector
			selector = Selector.open();
			// Registra o canal do servidor no Selector criado
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			System.out.println("Erro criando o socket na porta:" + port);
			e.printStackTrace();
		}
	}

	/**
	 * Função que implementa um loop "infinito" para monitorar conexões de rede e 
	 * para tratar os pacotes recebidos. 
	 */
	private void handleRequest() {
		Vector<SelectionKey> keys = new Vector<SelectionKey>();
		SelectionKey key;

		while (!shutdownRequested) {
			try {
				//System.out.println((new Date()).toString()+" - selecionando keys");
				Thread.sleep(1000);
				selector.selectNow();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/*
			 * Usamos nas próximas duas linhas um artifício para obter 
			 * as "keys" pois esta coleção não é "thread safe"
			 */
			keys.removeAllElements();
			keys.addAll(selector.keys());
			Iterator<SelectionKey> i = keys.iterator();
			while (i.hasNext()) {
				key = i.next();
				if (key.isAcceptable()) {
					acceptNewClient();
					continue;
				}
				if (key.isReadable()) {
					//System.out.println(key.);
					handleClientRequest(key);
					continue;
				}
				i.remove();
			}
		}
		close();
	}

	/**
	 * Trata a condição onde uma "key" se refere a um pedido de nova conexão
	 */
	private void acceptNewClient() {
		try {
			SocketChannel client;
			client = serverChannel.accept();
			if (client != null) {
				client.configureBlocking(false);
				client.register(selector, SelectionKey.OP_READ);
				Socket clientSocket = client.socket(); // Esta linha existe apenas para pegar os dados do cliente
				System.out.println("Cliente conectado:"
						+ clientSocket.getInetAddress().getHostAddress() + ":"
						+ clientSocket.getPort());
			}
		} catch (IOException e) {
			System.out.println("Erro aceitando uma conex\u00E3o.");
			e.printStackTrace();
		}
	}

	/**
	 * Trata a condição onde uma "key" se refere a um dado enviado pelo cliente
	 * @param key
	 */
	private void handleClientRequest(SelectionKey key) {
		int readed = -1;
		try {
			SocketChannel client = (SocketChannel) key.channel();
			Socket clientSocket = client.socket();
			int BUFFER_SIZE = 64;
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
			while ((readed = client.read(buffer)) > 0) {
				System.out.println("Recebendo requisi\u00e7\u00e3o de:"
						+ clientSocket.getInetAddress().getHostAddress() + ":"
						+ clientSocket.getPort() 
						+" -- " + readed + " bytes");
				handleData(buffer);
				buffer.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Trata os dados recebidos
	 * @param buffer
	 */
	private void handleData(ByteBuffer buffer) {
		buffer.flip();
		Charset charset = Charset.forName("ISO-8859-1");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer;
		String msg;
		try {
			charBuffer = decoder.decode(buffer);
			msg = charBuffer.toString();
			System.out.println("Dado recebido:" + msg);
			if (msg.trim().equalsIgnoreCase("CLOSE")) shutdownRequested = true;
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
	}

	private void close() {
		try {
			System.out.println("Solicita\u00e7\u00E3o de t\u00E9rmino. Fechando o servidor.");
			serverChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
