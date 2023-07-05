package com.papaworx.cpro.structures;

public class Branch {
	public String ID;
	public String FIRST;
	public String LAST;
	public String SEX;
	public String TYPE;
	public String YEAR;
	public String BIRTH;
	
	public Branch () {
		this.ID = null;
		this.FIRST = null;
		this.LAST = null;
		this.SEX = null;
		this.TYPE = null;
		this.YEAR = null;
		this.BIRTH = null;
	}
	
	public void Clear() {
		this.ID = null;
		this.FIRST = null;
		this.LAST = null;
		this.SEX = null;
		this.TYPE = null;
		this.YEAR = null;
		this.BIRTH = null;
	}
}
