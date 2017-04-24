import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Terminal implementation for DTransport
 * 
 * TODO change TCP to NFC
 * 
 * @author Mathieu Porcel & Victor Le
 */
public class DTransport implements Runnable {

	private TerminalConfig config;

	private ServerSocket serverSocket;
	private SmartContract contract;

	public DTransport() {
		try {
			config = new TerminalConfig();
			contract = new SmartContract(config.getWeb3Url(), config.getTerminalAddress(), config.getContractAddress());

			// Termianl TCP implementation
			serverSocket = new ServerSocket(config.getTerminalPort());
			Thread thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (true) {
				// Accept client
				Socket socket = serverSocket.accept();
				System.out.println(socket.toString());

				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream());

					// Get user address
					String userAddr = in.readLine();
					System.out.println("User address: " + userAddr);

					// Call smart contract
					if (contract.giveAuthorization(userAddr)) {
						System.out.println("Authorized");
						// Send born ID
						out.println(config.getTerminalId());
						out.println(config.getPrice());
					} else {
						System.err.println("Error");
						out.println("-1");
					}
					out.flush();

					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new DTransport();
	}
}
