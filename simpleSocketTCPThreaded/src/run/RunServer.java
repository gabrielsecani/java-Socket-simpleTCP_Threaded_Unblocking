package run;

import server.SimpleThreadedServer;

public class RunServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 9009;
		if (args.length>0) {
			String sPort = args[0];
			try {
				port = Integer.parseInt(sPort);
				if (port<1024) {
					System.out.println("Porta informada < 1024. Assumindo porta 9009.");
					port = 9009;
				}
			} catch (NumberFormatException e) {
				System.out.println("Erro na porta informada: "+sPort);
				System.out.println("Assumindo porta 9009.");
				port = 9009;
			}
		} else {
			System.out.println("Assumindo porta "+port+".");
		}
		
		SimpleThreadedServer server = new SimpleThreadedServer(port);
		server.open();
		server.handleRequest();
	}

}
