package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Worker implements Runnable {

	private Socket clientSocket;

	public Worker(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		boolean isRunning = true;
		String message = "";
		do {
			try {
				if (in.ready()) {
					message = in.readLine();
					System.out.println("Mensagem recebida de ["+getClientAddress()+"]:"+message);
				}
				if (message.equalsIgnoreCase("CLOSE"))
					isRunning = false;
				Thread.sleep(500);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// turn this thread available to execute again.
			Thread.yield();
			
			/* sleep, make this thread sleep and just
			 * entering on scheduler just after a
			 * time specified in mili seconds.
			 */
			try{
				Thread.sleep(10);
			}catch (Exception e) {
				Thread.yield();
			}
			
		} while (isRunning);
		close();
	}
	
	private String getClientAddress() {
		String result = clientSocket.getInetAddress().getHostName()+":"+clientSocket.getPort();
		return result;
	}

	private void close() {
		if ((clientSocket != null) && !clientSocket.isConnected()) {
			try {
				clientSocket.shutdownOutput();
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
