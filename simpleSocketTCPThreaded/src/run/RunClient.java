package run;

import client.SimpleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 9009;
//		String host = null;
//
//		if (args.length < 2) {
//			System.out
//					.println("java -jar simpleSocketTCPClient <ipServidor> [<porta>]");
//			System.exit(0);
//		} else {
//			host = args[0];
//			if (args.length < 2) {
//				// porta não informada
//				System.out
//						.println("Porta remota nao informada. Assumindo porta 9009 em "
//								+ host + ".");
//			} else {
//				String sPort = args[1];
//				try {
//					port = Integer.parseInt(sPort);
//					if (port < 1024) {
//						System.out
//								.println("Porta informada < 1024. Assumindo porta 9009.");
//						port = 9009;
//					}
//				} catch (NumberFormatException e) {
//					System.out.println("Erro na porta informada: " + sPort);
//					System.out.println("Assumindo porta 9009.");
//					port = 9009;
//				}
//			}
//		}		
		
		String comando = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		SimpleClient client = new SimpleClient(port);
		client.Connect();
		if (client.isConnected()) {
		do {
			try {
				System.out.print("Entre com um comando:");
				comando = in.readLine();
				client.SendMessage(comando);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!comando.trim().equalsIgnoreCase("CLOSE"));
		client.close();
		}
		else System.out.println("Falha - Cliente não conectado.");
	}
}
