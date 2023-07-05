package com.papaworx.cpro.structures;

public class FamGender {
	private String pID;
	private String role;
	
	public FamGender (String id, String rl) {
		pID= id;
		role = rl;
	}
	
	public String getID() {
		return pID;
	}
	
	public String getRole() {
		return role;
	}
}
