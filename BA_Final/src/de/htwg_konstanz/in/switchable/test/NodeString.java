package de.htwg_konstanz.in.switchable.test;

public class NodeString {
	String value;
	NodeString next;

	NodeString(String item) {
		this.value = item;
		next = null;
	}
}
