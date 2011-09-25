import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommClient extends CommBasic {
	
	private String enderecoServidor;
	private int portaServidor;
	//protected Socket sock;
	
	public CommClient(String enderecoServidor, int portaServidor) throws UnknownHostException, IOException {
		this.enderecoServidor = enderecoServidor;
		this.portaServidor = portaServidor;
		this.connect();
	}
	
	public CommClient(String enderecoServidor) throws UnknownHostException, IOException {
		this.enderecoServidor = enderecoServidor;
		this.portaServidor = 6996;
		this.connect();
	}
	
	public CommClient() throws UnknownHostException, IOException {
		this.enderecoServidor = "127.0.0.1";
		this.portaServidor = 6996;	
		this.connect();
	}
	
	private void connect() throws UnknownHostException, IOException {
		this.sock = new Socket(this.enderecoServidor, this.portaServidor);
		
		/* timeout para a thread */
		this.sock.setSoTimeout(CommBasic.readTimeout);
		
		this.setInput(this.sock.getInputStream());
		this.setOutput(this.sock.getOutputStream());
		
		/* iniciar a thread */
		this.start();
	}
}

