package com.papaworx.cpro.utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FSource {
	private Connection Con;
	
	public FSource(GConnection g){
		Con = g.con();
	}

	public String getName (String fID) {
		ResultSet rs = null;
		Statement stmt = null;
		String sql = "call getIndName('" + fID + "');";
		String name = null;
		try {
	      stmt = Con.createStatement();
	      rs = stmt.executeQuery(sql);
	      if (!rs.next())
	    	  name =  null;
	      else
	    	  name = rs.getString("First");
		} catch (SQLException se) {
			se.printStackTrace();
		}
	      
	    //STEP 6: Clean-up environment
	    try {
	  	  rs.close();
		  stmt.close();
		  return name;
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  rs = null;
			  stmt = null;
		}
	    return null;
	}
}

