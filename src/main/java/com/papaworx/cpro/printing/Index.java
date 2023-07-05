package com.papaworx.cpro.printing;

import java.util.SortedSet;
import java.util.TreeSet;

public class Index {
	private SortedSet<Item> items; 
	
	public Index() {
		items = new TreeSet<Item>();
	}
	
	public void addItem (String k, String v) {
		Item i = new Item(k, v);
		items.add(i);
	}
	
	public Item getFirst() {
		return items.first();
	}
	
	public Object[] toArray() {
		return items.toArray();
	}
}
