import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;

public class CommServer extends CommBasic {
	private int porta;
	//protected ServerSocket sock;
	
	public CommServer(int porta) throws IOException {
		this.porta = porta;
		this.listen();
	}
	
	public CommServer() throws IOException {
		this.porta = 6996;
		this.listen();
	}

	private void listen() throws IOException {
		ServerSocket serv = new ServerSocket(this.porta);
		serv.setSoTimeout(5000);
		this.sock = null;
		try {
			this.sock = serv.accept();
		} catch (InterruptedIOException e) {
			System.err.println("Ninguem conectou em 5 segundos");
			throw e;
		}
		System.err.println("Server: cliente conectado");
		
		/* timeout para a thread */
		this.sock.setSoTimeout(CommBasic.readTimeout);
		
		this.setInput(this.sock.getInputStream());
		this.setOutput(this.sock.getOutputStream());
		
		/* iniciar a thread */
		this.start();
		serv.close();
	}
}

