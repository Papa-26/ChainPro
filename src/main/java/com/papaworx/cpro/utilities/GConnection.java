package com.papaworx.cpro.utilities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.structures.Branch;
import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.structures.FamGender;
import com.papaworx.cpro.structures.GRecord;
import com.papaworx.cpro.structures.GcUnit;
import com.papaworx.cpro.utilities.Paragraph;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.controllers.PreferenceViewController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.*;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class GConnection {

	private Connection Con;
	private String Root;
	private Stack<Long> nodes;
	private HashMap<String, String> Dictionary;
	private Pattern pattern;
	private MainClass mainApp;
	public D_Parameters Par;
	public long time = 0;
	private Stage primaryStage;
	private Boolean bHasChanged = false;
	private String[]sKeys;

	public GConnection(MainClass _mainApp, D_Parameters par, Stage _stage) {
		mainApp = _mainApp;
		primaryStage = _stage;
		Par = par;
		Dictionary = new HashMap<String, String>();
		Dictionary.put("personID", "INDI");
		Dictionary.put("firstName", "INDI.NAME");
		Dictionary.put("Name", "INDI.NAME");
		Dictionary.put("lastName", "INDI.NAME");
		Dictionary.put("birthDate", "INDI.BIRT.DATE");
		Dictionary.put("birthPlace", "INDI.BIRT.PLAC");
		Dictionary.put("deathDate", "INDI.DEAT.DATE");
		Dictionary.put("deathPlace", "INDI.DEAT.PLAC");
		Dictionary.put("pRemarks", "INDI.NOTE");
		Dictionary.put("isMale", "INDI.SEX");
		Dictionary.put("Holocaust", "INDI.DEAT.CAUS");
		Dictionary.put("FAMC", "INDI.FAMC");
		Dictionary.put("FAMS", "INDI.FAMS");
		Dictionary.put("pMedia", "OBJE");
		Dictionary.put("pMediaTitle", "TITL");
		Dictionary.put("pMediaFile", "FILE");
		Dictionary.put("pMediaForm", "FORM");
		Dictionary.put("pMediaNote", "NOTE");
		Dictionary.put("FAM", "FAM");
		Dictionary.put("HUSB", "FAM.HUSB");
		Dictionary.put("WIFE", "FAM.WIFE");
		Dictionary.put("CHIL", "FAM.CHIL");
		Dictionary.put("fRemarks", "FAM.NOTE");
		Dictionary.put("engagementDate", "FAM.ENGA.DATE");
		Dictionary.put("engagementPlace", "FAM.ENGA.PLAC");
		Dictionary.put("marriageDate", "FAM.MARR.DATE");
		Dictionary.put("marriagePlace", "FAM.MARR.PLAC");
		Dictionary.put("separationDate", "FAM.SEP.DATE");
		Dictionary.put("separationPlace", "FAM.SEP.PLAC");
		Dictionary.put("divorceDate", "FAM.DIV.DATE");
		Dictionary.put("divorcePlace", "FAM.DIV.PLAC");
		Dictionary.put("Media", "OBJE");
		Dictionary.put("MediaTitle", "TITL");
		Dictionary.put("MediaFile", "FILE");
		Dictionary.put("MediaForm", "FILE.FORM");
		Dictionary.put("MediaNote", "NOTE");

		sKeys = new String[7];
		sKeys[0] = "GC_NODE";
		sKeys[1] = "GC_PARENT";
		sKeys[2] = "GC_ROOT_OBJECT";
		sKeys[3] = "GC_LEVEL";
		sKeys[4] = "GC_PRIMARY";
		sKeys[5] = "GC_TAG";
		sKeys[6] = "GC_VALUE";

	  	pattern = Pattern.compile("[1|2][0-9]{3}");

		Properties properties = new Properties();
		properties.setProperty("user", Par.getUser().getValue());
		properties.setProperty("password", Par.getPassword().getValue());
		properties.setProperty("useSSL", "false");

		if(Con != null) return;
		while (Con == null) {
			try {
				//Class.forName("org.mariadb.jdbc.Connection");
				Con = DriverManager.getConnection(Par.getServer().getValue(), properties);
			} catch (Exception e) {
				mainApp.preferences("Message");
			}
		}
	}

	public void setRoot (String root) {
		// set current root
		Root = root;
	}
	public Connection con () {
		return Con;
	}

	public String getStartPerson() {
		String s = Par.STARTPERSON.getValue();
		return s;
	}

	public String getServer() {
		return Par.getServer().getValue();
	}

	public String getDeposit() {
		return Par.getDeposit().getValue();
	}

	public String getCutOff() {
		return Par.getCutOff().getValue();
	}

	public String getImageViewer() {
		return Par.IMAGEVIEWER.getValueSafe();
	}

	public String getPdfViewer() {
		return Par.PDFVIEWER.getValueSafe();
	}

	public void Load() {
		String sql = "call makeFamilies();";
		Statement stmt = null;
	  try {
		      stmt = Con.createStatement();
		      stmt.executeQuery(sql);
	      }
      catch (SQLException se) {
    	  se.printStackTrace();
    	  mainApp.preferences(se.getMessage());
      }
      try {
		  stmt.close();
	  } catch (SQLException e) {
		  e.printStackTrace();
	  } finally {
		  	stmt = null;
	  }
	}
	public void saveString (String key, String value) {
		String sql;
		long node = 0;
		long parent = 0;
		Integer stepCount;
		String lastStep = null;
		// saves string to GEDCOM
		String path = Dictionary.get(key);
		if(path==null)
			System.out.println("Save error; key = " + key + ", Value = " + value);
		String [] steps = path.split("\\.");		// period needs to be escaped
		stepCount = 0;
		node = 0;
		for (String step : steps) {
			parent = node;
			if (step.equals("FAMC") || step.equals("FAMS") || step.equals("SOUR") || step.equals("CHIL"))
				sql = "SELECT GC_NODE FROM GEDCOM WHERE ((GC_ROOT_OBJECT = '" + Root + "') AND (GC_TAG = '" + step + "') AND (GC_PARENT = " + parent + ") AND (GC_VALUE = '" + value + "'));";
			else
				sql = "SELECT GC_NODE FROM GEDCOM WHERE ((GC_ROOT_OBJECT = '" + Root + "') AND (GC_TAG = '" + step + "') AND (GC_PARENT = " + parent + "));";
			node = getNode(sql, "GC_NODE");
			if (node <=0) {
				// node does not exist and has to be created
				node = getNextNode();
				sql = "INSERT INTO GEDCOM (GC_NODE, GC_PARENT, GC_ROOT_OBJECT, GC_LEVEL, GC_PRIMARY, GC_TAG, GC_VALUE) ";
				sql += "VALUE(" + node + ", " + parent + ", '" + Root + "', " + stepCount + ", " + 0;
				sql += ", '" + step + "', NULL);";
				executeSQL(sql);
			}	// else, just carry on
			lastStep = step;
			stepCount++;
		}
		if (lastStep.equals("NOTE")) {		// note has changed
			storeNote(node, stepCount, value);
		} else {						// anything but note has changed
			sql = "UPDATE GEDCOM SET GC_VALUE = '" + value + "' WHERE GC_NODE = " + node + ";";
			executeSQL(sql);
		}
	}

	public long saveObjectString (String key, String value, Integer level,  long oNode){
		String sql;
		// saves string to GEDCOM
		String path = Dictionary.get(key);
		if(path==null)
		System.out.println("Save error; key = " + key + ", Value = " + value);
		long parent;
		if (oNode == 0) {
			sql = "SELECT GC_NODE FROM GEDCOM WHERE ((GC_ROOT_OBJECT = '" + Root + "') AND (GC_LEVEL = '" + 0 + "'));";
			parent = getNode(sql, "GC_NODE");
		} else
			parent = oNode;
		long node = getNextNode();
		sql = "INSERT INTO GEDCOM (GC_NODE, GC_PARENT, GC_ROOT_OBJECT, GC_LEVEL, GC_PRIMARY, GC_TAG) ";
		sql += "VALUE(" + node + ", " + parent + ", '" + Root + "', " + level + ", " + 0;
		sql += ", '" + path + "');";
		executeSQL(sql);
		if (key.equals("NOTE")) {		// note has changed
			storeNote(node, level, value);
		} else {						// anything but note has changed
			sql = "UPDATE GEDCOM SET GC_VALUE = '" + value + "' WHERE GC_NODE = " + node + ";";
			executeSQL(sql);
		}
		return node;
	}

	public long getNode(String sql, String key) {
		/*
			allowed keys:
			"GC_NODE" to get current node;
			"GC_PARENT" to get parent node.
		 */
		ResultSet rs = null;
		Statement stmt = null;
		long node = -1;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
				if(!rs.next())
					node = -1;
				else
					node = rs.getLong(key);

		} catch (SQLException se) {
			se.printStackTrace();
		}

	    //Clean-up environment
	    try {
	  	  rs.close();
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  rs = null;
			  stmt = null;
		}
		return node;
	}

	public long getNextNode() {
		String sql = "SELECT MAX(GC_NODE) AS MAXN FROM GEDCOM;";
		return getNode(sql, "MAXN") + 1;
	}

	public void executeSQL(String sql) {
	    //System.out.println(sql);
		Statement stmt = null;
		try {
		      stmt = Con.createStatement();
		      stmt.executeUpdate(sql);
		      // no return expected

		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
	}

	public Stack<Long> getNodeStack(long parent, Boolean mode){
		// mode = true: ASC
		// mode = false: DESC
		Statement stmt = null;
		ResultSet rs = null;
		Stack<Long> lifo = new Stack<Long>();
		String sql = "SELECT GC_NODE FROM GEDCOM WHERE GC_PARENT = " + parent + " ORDER BY GC_NODE";
		if (mode)
			sql += ";";
		else
			sql += " desc;";
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      while (rs.next() )
		    	  lifo.push(rs.getLong("GC_NODE"));
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
	    return lifo;
	}

	public void storeNote(long lNode, Integer level, String prose) {
		int parent = 0;
		String sTag1 = "CONT";
		String sTag2 = "CONC";
		String sTag = sTag1;
		String sLine = null;
		int iLines = 0;
		Statement stmnt = null;
		ResultSet rs = null;

		nodes = getNodeStack(lNode, false);	// save old nodes
		deleteNodeTree(lNode);

		String[] sParagraphs = prose.split("\n");
		int iParaCount = sParagraphs.length;
		for (String s : sParagraphs){
			Paragraph Splitter = new Paragraph(s);
			iLines = Splitter.getNumberLines();
			for (int i = 0; i < iLines; i++) {
				sLine = Splitter.getLine(i);
				saveLine(lNode, sTag,  level, sLine);
				sTag = sTag2;
			}
			sTag = sTag1;
		}
	}
	public void storeNote2(long parent, String root, Integer level, String prose) {
		//System.out.println("Note: " + prose);
		String tLine;
		String[] lines = prose.split("\n");
		String tag, regex, sql;
		Integer iPointer, iLength, iUpper;
		long node;
		nodes = getNodeStack(parent, false);
		setRoot(root);
		for (String line : lines) {
			line = line.replaceAll("\r", "");
			regex = "['\"\\\\]";
			line = line.replaceAll("^\\s+", "");
			line = line.replaceAll(regex, "`");
			iLength = line.length();
			tag = "CONT";
			iPointer = 0;
			while (iPointer < iLength) {
				iUpper = iPointer + 64;
				if (iUpper >iLength)
					iUpper = iLength;
				tLine = line.substring(iPointer, iUpper);
				saveLine( parent,tag,  level, tLine);
				iPointer += 64;
				tag = "CONC";
			}
		}
		// no we have to clean up unused note nodes
		while (!nodes.empty()) {
			node = (long)nodes.pop();
			sql = "DELETE FROM GEDCOM WHERE GC_NODE = " + node + ";";
			executeSQL(sql);
		}

		if (prose.equals("")){
			sql = "DELETE FROM GEDCOM WHERE GC_NODE = " + parent + ";";
			executeSQL(sql);
		}
	}

    private void saveLine(long parent, String tag, Integer level, String value) {
		// The parent is the node of the 'NOTE' line
		// text in value is cleaned up (quotations) (starting with either a 'CONT' line)
		// if necessary broken up into maximum 64 char chunks with 'CONC' tag
		String sql;
		long node;
		if (!nodes.empty())
			node = (long) nodes.pop();
		else
			node = getNextNode();
		if (value == null){
			sql = "INSERT INTO GEDCOM (GC_NODE, GC_PARENT, GC_ROOT_OBJECT, GC_LEVEL, GC_PRIMARY, GC_TAG)";
			sql += " VALUES(" + node + ", " + parent + ", '" + Root + "', " + level + ", 0, '" + tag + "');";
		} else {
			sql = "INSERT INTO GEDCOM (GC_NODE, GC_PARENT, GC_ROOT_OBJECT, GC_LEVEL, GC_PRIMARY, GC_TAG, GC_VALUE)";
			sql += " VALUES(" + node + ", " + parent + ", '" + Root + "', " + level + ", 0, '" + tag + "', '" + value + "');";
		}
		executeSQL(sql);
    }

	public List <GRecord> 	LoadSet (String sql) {
		List<GRecord> xList = new ArrayList<GRecord>();
		ResultSet rs = null;
		Statement stmt = null;
		  try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      while(rs.next()){
		    	  GRecord r = new GRecord();
		    	  r.gID = rs.getLong("GC_NODE");
		    	  r.gParent = rs.getLong("GC_PARENT");
		    	  r.gRoot = rs.getString("GC_ROOT_OBJECT");
		    	  r.gPrimary = rs.getInt("GC_PRIMARY");
		    	  r.gTag = rs.getString("GC_TAG").trim();
		    	  r.gLevel = rs.getInt("GC_LEVEL");
		    	  if (rs.getObject("GC_VALUE") != null)
		    		  r.gValue = rs.getString("GC_VALUE");
		    	  else
	    			  r.gValue = "";
		    	  r.gClean = true;
		    	  xList.add(r);
		      }
	      } catch (SQLException se) {
	    	  se.printStackTrace();
	      }

	      //STEP 6: Clean-up environment
	      try {
	    	  rs.close();
			  stmt.close();
			  return xList;
		  } catch (SQLException e) {
			  e.printStackTrace();
		  } finally {
			  	rs = null;
			  	stmt = null;
		  }
	      return null;
	}

	public void saveBoolean(String key, Boolean value) {
		// saves boolean to GEDCOM
		// convert to string
		String actualValue = null;
		switch (key) {
		case "Holocaust":
			if(value)
				actualValue = "HOLOCAUST";
			else
				actualValue = null;
			break;
		case "isMale":
			if (value)
				actualValue = "M";
			else
				actualValue = "F";
			break;
		}
		saveString(key, actualValue);
	}

	public String getNextPersonID() {
		String sql = "SELECT max(GC_ROOT_OBJECT) AS maxKey FROM ChainPro.GEDCOM WHERE GC_ROOT_OBJECT LIKE '@I%';";
		String result = getString(sql, "maxKey");
		Integer iMax = Integer.parseInt(result.substring(2, 7));
		result = String.format("@I%05d@", ++iMax);
		return result;
	}

	public String getNextFamilyID() {
		String sql = "SELECT max(GC_ROOT_OBJECT) AS maxKey FROM ChainPro.GEDCOM WHERE GC_ROOT_OBJECT LIKE '@F%';";
		String result = getString(sql, "maxKey");
		Integer iMax = Integer.parseInt(result.substring(2, 7));
		result = String.format("@F%05d@", ++iMax);
		return result;
	}

	public String getNextMultimediaRoot() {
		String sql = "SELECT max(GC_ROOT_OBJECT) AS maxKey FROM ChainPro.GEDCOM WHERE GC_ROOT_OBJECT LIKE '@M%';";
		String result = getString(sql, "maxKey");
		Integer iMax = Integer.parseInt(result.substring(2, 7));
		result = String.format("@M%05d@", ++iMax);
		return result;
	}

	public String getString(String sql, String key) {
		Statement stmt = null;
		ResultSet rs;
		String result = null;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      if(rs.next())
		    	  result = rs.getString(key);
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
		return result;
	}

	public List<DropLabel> relations (String root, String r1, String r2) {
		List<DropLabel> list = new ArrayList<DropLabel>();
		DropLabel item;
		String sql = "call getRelation('" + root +  "', '" + r1 + "', '" + r2 + "');";
		//System.out.println(sql);
		Statement stmt = null;
		ResultSet rs = null;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  item = new DropLabel();
		    	  item.Root = root;
		    	  item.Text = rs.getString("NAME");
		    	  item.Extra = rs.getString("EXTRA");
		    	  list.add(item);
		      }
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}

		return list;
	}

	public List<DropLabel> relatives (String sql){
		List<DropLabel> list = new ArrayList<DropLabel>();
		DropLabel item;
		//System.out.println(sql);
		Statement stmt = null;
		ResultSet rs = null;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  item = new DropLabel();
		    	  item.Root = rs.getString("EXTRA");
		    	  item.Text = rs.getString("TEXT");
		    	  item.Extra = rs.getString("VALUE");
		    	  list.add(item);
		      }
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
		return list;

	}

	public List<DropLabel> parentGender (String root) {
		String sql = "call getParentGender('" + root +  "');";
		return relatives(sql);
	}

	public void dateObject (String date) {
		String sYear = null;
		String  sql = null;
		Matcher matcher = pattern.matcher(date);
		while (matcher.find())
			sYear = matcher.group();
		sql = "SELECT * FROM GEDCOM WHERE GC_LEVEL = 0 AND GC_ROOT_OBJECT ='" + Root + "';";
		long node = getNode(sql, "GC_NODE");
		sql = "UPDATE GEDCOM SET GC_VALUE = '" + sYear + "' WHERE GC_NODE = " + node + ";";
		executeSQL(sql);
	}

	public void timer (String title) {
		long t = System.currentTimeMillis();
		System.out.println(title + ": " + (t-time));
		time = t;
	}

	public void time0() {
		time = System.currentTimeMillis();
	}

	public long legacyPMax() {
		String sql = "SELECT MAX(SUBSTRING(GC_ROOT_OBJECT, 3,5) + 1) AS MAXP FROM ChainPro.GEDCOM WHERE GC_ROOT_OBJECT LIKE '@I%';";
		return Long.parseLong(getString(sql, "MAXP"));
	}

	public List<DropLabel> getFamilyMembers(String root, String type) {
		String sql = "call getFamilyMembers('" + root + "', '" + type + "');";
		return relatives(sql);
	}

	public Connection getConnection() {
		return Con;
	}

	public List<String> getNodes(String root) {
		ResultSet rs = null;
		Statement stmt = null;
		List<String> list = new ArrayList<String>();
		String sql = "call getNodes ('" + root + "');";
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		while (rs.next())
			list.add(rs.getString("FAMID"));
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    //Clean-up environment
	    try {
	  	  rs.close();
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  rs = null;
			  stmt = null;
		}
		return list;
	}

	public List<Branch> getBranch(String root) {
		ResultSet rs = null;
		Statement stmt = null;
		List<Branch> list = new ArrayList<Branch>();
		String sql = "call getBranch ('" + root + "');";
		Branch b = null;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
			while (rs.next()) {
				b  = new Branch();
				b.ID = rs.getString("ID").trim();
				b.FIRST = rs.getString("FIRST").trim();
				b.LAST = rs.getString("LAST").trim();
				b.TYPE = rs.getString("TYPE").trim();
				b.SEX = rs.getString("SEX").trim();
				b.YEAR = rs.getString("YEAR").trim();
				list.add(b);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    //Clean-up environment
	    try {
	  	  rs.close();
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  rs = null;
			  stmt = null;
		}
		return list;
	}

	public String getBDate(String root) {
		ResultSet rs = null;
		String result = null;
		Statement stmt = null;
		String sql = "call getBDate ('" + root + "');";
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      if (rs.next())
		    	  result = rs.getString("GC_VALUE");
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    //Clean-up environment
	    try {
	  	  rs.close();
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  rs = null;
			  stmt = null;
		}
		return result;
	}

	public void handleGender() {
		List<FamGender> list = genderPattern();
		String tSex = null;
		String sql, result;
		String root = null;
		long node = -1;
		long parent = -1;

		for (FamGender item: list) {
			root = item.getID();
			switch (item.getRole().trim()) {
			case "HUSB":
				tSex = "M";
				break;
			case "WIFE":
				tSex = "F";
				break;
			}
			sql = "SELECT GC_VALUE FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + root + "' AND GC_TAG = 'SEX';";
			result = getString(sql, "GC_VALUE");
			if (result == null) {
				// missing SEX record
				node = getNextNode();
				sql = "SELECT GC_NODE FROM GEDCOM WHERE GC_LEVEL = 0 AND GC_ROOT_OBJECT = '" + root + "';" ;
				parent = getNode(sql, "GC_NODE");
				sql = "INSERT INTO GEDCOM (GC_NODE, GC_PARENT, GC_ROOT_OBJECT, GC_LEVEL, GC_PRIMARY, GC_TAG, GC_VALUE)";
				sql += " VALUES (" + node + ", " + parent + ", '" + root +"', 1, 0, 'SEX', + '" + tSex + "');";
				executeSQL(sql);
			} else if (!result.equals(tSex)){
				// wrong gender assignment
				sql = "SELECT GC_NODE FROM GEDCOM WHERE GC_TAG = 'SEX' AND GC_ROOT_OBJECT = '" + root + "';";
				node = getNode(sql, "GC_NODE");
				sql = "UPDATE GEDCOM SET GC_VALUE = '" + tSex + "' WHERE GC_NODE = " + node + ";";
				executeSQL(sql);
			}
		}
	}

	public List<FamGender> genderPattern (){
		List<FamGender> list = new ArrayList<FamGender>();
		FamGender item;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "call getGenderPattern()";
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  item = new FamGender(rs.getString("person"), rs.getString("role"));
		    	  list.add(item);
		      }
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
		return list;
	}

	public List<String> getRecords(String sql, String key) {
		List<String> list = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs = null;
		String item = null;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  item = rs.getString(key);
		    	  list.add(item);
		      }
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
		return list;
	}

	public ArrayList<String> getRecords(String sql) {
		ArrayList<String> list = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs = null;
		String item = null;
		try {
			stmt = Con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				item = rs.getString(sKeys[0]);
				for (int i = 0; i < 7; i++)
					item += " " + rs.getString(sKeys[i]);
				list.add(item);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		// Clean-up environment
		try {
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			stmt = null;
		}
		return list;
	}


	public void deleteNodeTree (long parent) {
		// deletes the whole descendant tree of a parent node
		// very dangerous
		String sql = null;
		Alert aBox = new Alert(AlertType.CONFIRMATION, "Are you sure you want to proceed with the deletion?");
		aBox.showAndWait().ifPresent(response -> {
	     if (response != ButtonType.OK)
	    	 return;
		});
		deleteBranch(parent);
		sql = "DELETE FROM GEDCOM WHERE GC_PARENT = " + parent + ";";
		executeSQL(sql);
	}

	private void deleteBranch(long parent) {
		long current = 0;
		String sql = null;
		Stack<Long> stack = getNodeStack(parent, false);
		while(!stack.isEmpty()){
			current = stack.pop();
			deleteBranch(current);
			sql = "DELETE FROM GEDCOM WHERE GC_NODE = " + current + ";";
			executeSQL(sql);
		}
	}

	public Integer getInteger (String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery(sql);
		      if (rs == null)
		    	  return 0;
		      if (rs.next())
		    	  return rs.getInt(1);
		      else
		    	  return 0;
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
	    return 0;
	}

	public D_Parameters getPar() {
		return Par;
	}

	public ArrayList<String> getSimple(ArrayList<String> list, String p, String type) {
		Statement stmt = null;
		ResultSet rs = null;
		String item = null;
		try {
		      stmt = Con.createStatement();
		      rs = stmt.executeQuery("call getSimple('" + p + "', '" + type + "')");
		      while (rs.next()) {
		    	  item = rs.getString("GC_VALUE");
		    	  list.add(item);
		      }
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
		return list;
	}

	public GcUnit getLine(Long node) {
		Statement stmt = null;
		ResultSet rs = null;
		GcUnit result = new GcUnit();
		String sql = "SELECT GC_LEVEL, GC_TAG, GC_VALUE FROM GEDCOM WHERE GC_NODE = " + node.toString() + ";";
		try {
		    stmt = Con.createStatement();
		    rs = stmt.executeQuery(sql);
		    while (rs.next()) {
		    	  result.level = rs.getInt("GC_LEVEL");
		    	  result.tag = rs.getString("GC_TAG");
		    	  result.value = rs.getString("GC_VALUE");
		      }
		} catch (SQLException se) {
			se.printStackTrace();
		}

	    // Clean-up environment
	    try {
		  stmt.close();
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  stmt = null;
		}
		return result;
	}

	public void preferences(String sMessage)  {
		// handles preferences and backup
		// First display view
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainClass.class.getResource("/view/PreferenceView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();

			dialogStage.setTitle("Preference");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			PreferenceViewController controller = loader.getController();
			controller.setMainApp(mainApp, Par, scene);
			controller.setDialogStage(dialogStage);
			controller.showPreference (sMessage);

			// Show the dialog and wait until the user closes i
			dialogStage.showAndWait();
			if (dialogStage.isShowing()){
				dialogStage.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
