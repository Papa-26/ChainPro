package com.papaworx.cpro.printing;

public class Item implements Comparable<Item> {

	private String key;
	private String value;
	
	public Item(String k, String v) {
		key = k;
		value = v;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}

	@Override
	public int compareTo(Item i) {
		return key.compareTo(i.key);
	}
	
}
