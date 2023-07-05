package com.papaworx.cpro.structures;

public class DropLabel {
	public String Root = null;
	public String Text = null;
	public String Extra = null;
	
	public DropLabel (String r, String t, String e){
		Root = r;
		Text = t;
		Extra = e;
	}
	
	public DropLabel (String r, String t){
		Root = r;
		Text = t;
	}
	
	public DropLabel () {
	}
	
	public void setRoot(String root) {
		Root = root;
	}
	
	public void setText(String text) {
		Text = text;
	}
	
	public void setExtra (String extra) {
		Extra = extra;
	}
	
	public String getRoot() {
		return Root;
	}
	
	public String getText() {
		return Text;
	}
	
	public String getExtra() {
		return Extra;
	}
	
	@Override
	public String toString() {
		return Text;
	}
	
	public String toValue () {
		return Root;
	}
}
