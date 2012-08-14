package messenger;

public class QueueByteArray {
	public NodeByte head, tail;
	
	public QueueByteArray() {
		head = null;
		tail = null;
	}

	public boolean empty() {
		return (head == null);
	}

	public byte[] get() {
		byte[] v = head.value;
		head = head.next;
		return v;
	}

	public void put(byte[] item) {
		NodeByte t = tail;
		tail = new NodeByte(item);
		if (empty())
			head = tail;
		else
			t.next = tail;
	}
}