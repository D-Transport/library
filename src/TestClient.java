import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import org.web3j.abi.datatypes.Type;

/**
 * Test TCP client
 * 
 * @author Mathieu Porcel & Victor Le
 */
public class TestClient {
	public static void main(String[] args) {
		try {
			TerminalConfig config = new TerminalConfig();
			SmartContract contract = new SmartContract(config.getWeb3Url(), config.getTerminalAddress(),
					config.getContractAddress());

			// Populate blockchain
			String address = config.getTerminalAddress();
			if (contract.getTerminalCount() == 0) {
				contract.register();
				contract.addCompany(config.getTerminalAddress(), "Test");
				contract.addTerminal(config.getTerminalAddress(), 42, config.getTerminalAddress());
			}

			// TCP connection
			Socket socket = new Socket(InetAddress.getLocalHost(), config.getTerminalPort());

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());

			// Send address
			out.println(address);
			out.flush();

			// Read terminal index
			long terminalIndex = Long.parseLong(in.readLine());
			if (terminalIndex != -1) {
				long price = Long.parseLong(in.readLine());
				@SuppressWarnings("rawtypes")
				List<Type> terminal = contract.getTerminal(terminalIndex);

				String terminalAddr = contract.toAddress(terminal.get(0));
				String companyAddress = contract.toAddress(terminal.get(3));

				// Show terminal / company info
				System.out.println("Price: " + price);
				System.out.println("Terminal: " + contract.getTerminal(terminalIndex));
				for (long i = 0; i < contract.getCompanyCount(); i++) {
					if (contract.toAddress(contract.getCompany(i).get(0)).equals(companyAddress)) {
						System.out.println("Company: " + contract.getCompany(i));
						break;
					}
				}

				// Validate if terminal is ok
				if (contract.getAuthorizationDate(address, terminalAddr) != 0) {
					// Debug
					System.out.println("Validation count (before): " + contract.getUser(0).get(2).getValue());
					System.out.println("Balance (before): " + contract.getBalance(address));

					// Validate
					contract.validate(terminalAddr, 0);

					// Debug
					System.out.println("Validation count (after):  " + contract.getUser(0).get(2).getValue());
					System.out.println("Balance (after):  " + contract.getBalance(address));
				}

			} else {
				System.err.println("Terminal error");
			}

			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
