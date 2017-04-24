import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Load config file
 * 
 * @author Mathieu Porcel & Victor Le
 */
public class TerminalConfig {

	private Properties properties;

	public TerminalConfig() throws IOException {
		properties = new Properties();
		properties.load(new FileInputStream("config.properties"));
	}

	public String getWeb3Url() {
		return properties.get("web3_url").toString();
	}

	public int getTerminalId() {
		return Integer.parseInt(properties.get("terminal_id").toString());
	}

	public String getTerminalAddress() {
		return properties.get("terminal_address").toString();
	}

	public String getContractAddress() {
		return properties.get("contract_address").toString();
	}

	public int getTerminalPort() {
		return Integer.parseInt(properties.get("terminal_port").toString());
	}

	public long getPrice() {
		return Long.parseLong(properties.get("price").toString());
	}
}
