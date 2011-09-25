public interface CommListener {
	public void clearAll(CommEvent e);
	public void markPosition(CommEvent e);
	public void newMessage(CommEvent e);
	public void disconnected(CommEvent ev);
}

