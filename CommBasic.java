import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Classe basica de comunicacao
 */
public class CommBasic extends Thread{
	static int readTimeout = 2000;
	
	private InputStream input;
	private OutputStream output;
	private boolean end = false;
	protected Socket sock;
	
	private List<CommListener> eventListeners;
	
	public void setInput(InputStream input) {
		this.input = input;
	}
	public void setOutput(OutputStream output) {
		this.output = output;
	}
	public void sendEvent(CommEvent e) throws IOException {
		ObjectOutputStream oout = new ObjectOutputStream(this.output);
		oout.writeObject(e);
		oout.flush();
	}
	private CommEvent recvEvent() throws IOException {
		ObjectInputStream oin = new ObjectInputStream(this.input);
		CommEvent ev = null;
		try {
			ev = (CommEvent) oin.readObject();
		} catch (ClassNotFoundException e) {
			System.err.println("Nao achou a classe?");
			//e.printStackTrace();
		}
		return ev;
	}
	
	public void addCommEventListener(CommListener e) {
		
		if (this.eventListeners == null) {
			this.eventListeners = new ArrayList<CommListener>();
		}
		this.eventListeners.add(e);
	}
	
	public void run() {
		
		while (! this.end) {
			CommEvent ev = null;
			try {
				ev = recvEvent();
				//System.err.println("Mensagem recebida: " + message);
				
			} catch (InterruptedIOException e) {
				//System.err.println("timeout");
				// timeout no socket
			} catch (IOException e) {
				System.err.println("Conexao fechada pela outra ponta");
				//e.printStackTrace();
				ev = new CommEvent(CommEvent.EVENT_DISCONNECT, null, null);
				this.setEnd();
			}
			if (ev != null) {
				Iterator<CommListener> i = this.eventListeners.iterator();
				while(i.hasNext()) {
					CommListener l = i.next();
					if ((ev.tipo & CommEvent.EVENT_CLEAR) > 0)
						l.clearAll(ev);
					if ((ev.tipo & CommEvent.EVENT_MARK) > 0)
						l.markPosition(ev);
					if ((ev.tipo & CommEvent.EVENT_MESSAGE) > 0)
						l.newMessage(ev);
					if ((ev.tipo & CommEvent.EVENT_DISCONNECT) > 0) 
						l.disconnected(ev);
				}
			}
		}
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.err.println("SAI");
	}
	synchronized public void setEnd() {
		this.end = true;
	}
}

