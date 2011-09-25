import java.awt.Point;
import java.io.Serializable;


public class CommEvent implements Serializable {
	static final int EVENT_CLEAR = 1;
	static final int EVENT_MARK = 2;
	static final int EVENT_MESSAGE = 4;
	static final int EVENT_DISCONNECT = 8;
	
	public Point pos;
	public String message = null;
	public int tipo;
	
	CommEvent(int tipo, String message, Point pos) {
		this.tipo = tipo;
		if ((this.tipo & CommEvent.EVENT_MARK) > 0) {
			this.pos = pos;
		}
		if ((this.tipo & CommEvent.EVENT_MESSAGE) > 0) {
			this.message = message;
		}
	}
	
}

