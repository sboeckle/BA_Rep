package messenger;

public class NodeByte {
	byte[] value;
	NodeByte next;

	NodeByte(byte[] item) {
		this.value = item;
		next = null;
	}
}