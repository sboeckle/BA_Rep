package messenger;

public class QueueString {

public NodeString head, tail;
	
	public QueueString() {
		head = null;
		tail = null;
	}

	public boolean empty() {
		return (head == null);
	}

	public String get() {
		String v = head.value;
		head = head.next;
		return v;
	}

	public void put(String item) {
		NodeString t = tail;
		tail = new NodeString(item);
		if (empty())
			head = tail;
		else
			t.next = tail;
	}
	
}
