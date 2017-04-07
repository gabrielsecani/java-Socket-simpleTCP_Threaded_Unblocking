package run;

import client.SimpleClient;

public class RunClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 9009;
		String host = "localhost";
		SimpleClient client = new SimpleClient(host,port);
		client.Connect();
		System.out.println("Fim na execu\u00e7\u00e3o do cliente.");
	}
}
