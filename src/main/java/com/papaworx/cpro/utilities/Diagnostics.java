package com.papaworx.cpro.utilities;

import java.io.PrintStream;
import java.util.List;

import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.structures.GRecord;
import javafx.scene.Cursor;

public class Diagnostics {
	private GConnection G;
	private PrintStream PS;
	private MainClass mainApp;
	private Integer iErrorCount = 0;
	
	public Diagnostics(GConnection g, MainClass mainApp) {
		G = g;
		this.mainApp = mainApp;
	}

	public void process( PrintStream ps) {
		PS = ps;
		String pRoot = null;
		String fRoot = null;
		String type = null;
		String sTemp = null;
		String sql = null;
		long node = 0;

		mainApp.setCursor(Cursor.WAIT);

		// phantom families
		sql = "SELECT * FROM GEDCOM WHERE GC_LEVEL = 0 AND GC_TAG = 'FAM';";
		List <GRecord> list = G.LoadSet(sql);
		PS.println("************ phantom families");
		for (GRecord r1: list) {
			fRoot = r1.gRoot;
			sql = "SELECT COUNT(GC_NODE) FROM GEDCOM WHERE (GC_TAG = 'HUSB' OR GC_TAG = 'WIFE' OR GC_TAG = 'CHIL') AND GC_ROOT_OBJECT = '" + fRoot + "';";
			if (G.getInteger(sql) < 2)
				PS.println("Family " + fRoot + "is trivial.");
		}		
		
		sql = "SELECT * FROM GEDCOM WHERE GC_LEVEL = 0 AND GC_TAG = 'INDI';";
		list = G.LoadSet(sql);
		
		// test for ghosts
		PS.println("************ ghosts");
		for (GRecord r1: list) {
			sql = "SELECT * FROM GEDCOM WHERE GC_TAG = 'NAME' AND GC_PARENT = " + r1.gID +";";
			if(G.getNode(sql, "GC_NODE") < 1) {
				pRoot = r1.gRoot;
				PS.println("Person " + pRoot + "has no name.");
			}
		}		
		
		// test for missing gender record
		PS.println("************ missing gender records");
		for (GRecord r1: list) {
			node = r1.gID;
			pRoot = r1.gRoot;
			sql = "SELECT GC_NODE FROM GEDCOM WHERE GC_TAG = 'SEX' AND GC_PARENT = " + node + ";";
			if (G.getNode(sql, "GC_NODE") < 0) {
				PS.println("Person " + pRoot + " has no gender record.");
				iErrorCount++;
			}
		}
			
		// test for unmatched roles
		PS.println("************ unmatched roles");
		sql = "SELECT * FROM GEDCOM WHERE (GC_TAG = 'WIFE') OR (GC_TAG = 'HUSB') OR (GC_TAG = 'CHIL')";
		list = G.LoadSet(sql);
		for (GRecord r2: list) {
			pRoot = r2.gValue;
			type = r2.gTag;
			if (type.equals("CHIL"))
				type = "FAMC";
			else
				type = "FAMS";
			sql = "SELECT * FROM GEDCOM WHERE GC_TAG = '" + type + "' AND GC_ROOT_OBJECT = '" + pRoot + "';";
			node = G.getNode(sql, "GC_NODE");
			//System.out.println("Person " + pRoot + "; node " + node);
			if (node <1) {
				fRoot = r2.gRoot;
				PS.println("No " + type + "for person " + pRoot + " and family " + fRoot);
				iErrorCount++;
			}
				
		}
		
		// test for unmatched spousals
		PS.println("************ unmatched spousals");
		sql = "SELECT * FROM GEDCOM WHERE (GC_TAG = 'FAMS') OR (GC_TAG = 'FAMC')";
		list = G.LoadSet(sql);
		for (GRecord r3 : list) {
			fRoot = r3.gValue;
			type = r3.gTag;
			pRoot = r3.gRoot;
			sql = "SELECT * FROM GEDCOM WHERE GC_TAG = 'SEX' AND GC_ROOT_OBJECT = '" + pRoot + "';";
			
			sTemp = G.getString(sql, "GC_VALUE");
			if (sTemp != null) {
				Boolean bSex = sTemp.equals("M");
				if (type.equals("FAMC"))
					type = "CHIL";
				else if (bSex)
					type = "HUSB";
				else
					type = "WIFE";
				sql = "SELECT * FROM GEDCOM WHERE GC_TAG = '" + type + "' AND GC_ROOT_OBJECT = '" + fRoot + "';";
				node = G.getNode(sql, "GC_NODE");
				if(node < 0) {
					PS.println("No spouse for family role for person " + pRoot + "in family " + fRoot);
					iErrorCount++;
				}
			}
		}
		
		// test for polygamy
		PS.println("************ polygamy");
		sql = "SELECT GC_ROOT_OBJECT, COUNT(GC_NODE) as cnt FROM GEDCOM WHERE GC_TAG = 'WIFE' GROUP BY GC_ROOT_OBJECT HAVING cnt>1;";
		List<String> rList = G.getRecords(sql, "GC_ROOT_OBJECT");
		for (String fr: rList) {
			PS.println("Multiple wives in family " + fr);
			iErrorCount++;
		}
		
		sql = "SELECT GC_ROOT_OBJECT, COUNT(GC_NODE) as cnt FROM GEDCOM WHERE GC_TAG = 'HUSB' GROUP BY GC_ROOT_OBJECT HAVING cnt>1;";
		rList = G.getRecords(sql, "GC_ROOT_OBJECT");
		for (String fr: rList) {
			PS.println("Multiple husbands in family " + fr);
			iErrorCount++;
		}
		
		// test for more than one set of parents
		PS.println("************ multiple parent sets");
		sql = "SELECT GC_ROOT_OBJECT, COUNT(GC_NODE) as cnt FROM GEDCOM WHERE GC_TAG = 'FAMC' GROUP BY GC_ROOT_OBJECT HAVING cnt>1;";
		rList = G.getRecords(sql, "GC_ROOT_OBJECT");
		for (String fr: rList) {
			PS.println("Multiple parents for person " + fr);
			iErrorCount++;
		}
		// done
		if (iErrorCount.equals(0))
			PS.println("No errors detected.");
		else
			PS.println("Done!");
		PS.close();
		mainApp.setCursor(Cursor.DEFAULT);
		System.out.println("Output file closed.");
	}
	
}

