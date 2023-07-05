package com.papaworx.cpro.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import com.papaworx.cpro.structures.GcUnit;

public class DExporter {
	// Exports GEDCOM file for descendants of index person
	// the tree maps contain elements {String old, String new}
	// Algorithm:
	// export header
	// iterate doPerson
	// resort tp
	// export INDI (NEW)
	// resort tf
	// export FAM
    private final GConnection G;
	private final TreeMap <String, String> tp;
	private final TreeMap <String, String> tf;
	private Integer personIterator;
    private Integer familyIterator;
    private String rootObject = null;
    
	public DExporter (GConnection g, String p, Integer pI, Integer fI) {
		
		String oldID;
		String newID;
		Set<String> set;
		personIterator = pI;
		familyIterator = fI;
		Iterator<String> iterator;
		G = g;
		// hook for parameter screen
		tp = new TreeMap<>();	// initial Collection of persons
		tf = new TreeMap<>(); // initial Collection of families
		TreeMap<String, String> tt = new TreeMap<>(); // resorted collection of either
		doPerson(p);
		// hook for export header
		// -- export people:
		// create inverse treemap tt to tp
		
		set = tp.keySet();
		iterator = set.iterator();
		while (iterator.hasNext()) {
			oldID = iterator.next();
			newID = tp.get(oldID);
			tt.put(newID, oldID);
		}
			
		// in the order of tt (new IDs)
		// loop through all new id's
		// using tp and tt as dictionaries
		set = tt.keySet();
		iterator = set.iterator();
		while (iterator.hasNext()) {
			newID = iterator.next();
			oldID = tt.get(newID);
			// now we export the person corresponding to this ID
			rootObject = newID;
			doUnit(oldID);
		}
		// -- export families
		// create inverse treemap tt to tf
		set.clear();
		tt.clear();
		set = tf.keySet();
		iterator = set.iterator();
		while (iterator.hasNext()) {
			oldID = iterator.next();
			newID = tf.get(oldID);
			tt.put(newID, oldID);
		}
			
		// in the order of tt (new IDs)
		// loop through all new id's
		// using tp and tt as dictionaries
		set = tt.keySet();
		iterator = set.iterator();
		while (iterator.hasNext()) {
			newID = iterator.next();
			oldID = tt.get(newID);
			// now we export the family corresponding to this ID
			rootObject = newID;
			doUnit(oldID);
		}
	}
	
	private void doPerson(String p) {
		
		String v;
		ArrayList <String> flist = new ArrayList<>();
		ArrayList <String> plist;
		// getNewPerson
		// if not in tp, put in
		if (!tp.containsKey(p)) {
			v=getNewPerson();
			tp.put(p,  v);
		}
		// for person p find all FAMS
		flist = G.getSimple(flist, p, "FAMS");
		// iterate though FAMSs
		for (String m: flist){
			if (!tf.containsKey(m)) {
				//	 getNewFamily
				v = getNewFamily();
				//   put fam in tf
				tf.put(m,  v);
			}
			// list <- nuclear parents
			plist= new ArrayList<>();
			plist = G.getSimple(plist, m, "WIFE");
			plist = G.getSimple(plist, m, "HUSB");
			for (String parent: plist) {
				if (!parent.equals(p) && !tp.containsKey(parent)) {
					v = getNewPerson();
					tp.put(parent,  v);
				}
					
			}
			// now for children
			plist.clear();
			// 	 find all CHIL in FAMS
			plist = G.getSimple(plist, m, "CHIL");
			//	 iterate through CHILs
			for (String child: plist) {
				if (!tp.containsKey(child)){
					v = getNewPerson();
					tp.put(child,  v);
					// doPerson (CHIL)
					doPerson(child);
				}
			}
		}
	}
	
	private String getNewPerson() {
		String result;
		result = String.format("@I%05d@", personIterator++);
		return result;
	}
	
	private String getNewFamily () {
		String result;
		result = String.format("@F%05d@", familyIterator++);
		return result;
	}
	
	private void doUnit (String id) {
		// exports INDI or FAM
		String sql;
		sql = "SELECT GC_NODE FROM GEDCOM WHERE GC_LEVEL = 0 AND GC_ROOT_OBJECT = '" + id + "';";
		long rootNode = G.getNode(sql, "GC_NODE");
		GcUnit line = G.getLine(rootNode);
		String output = "0 " + rootObject + " " + line.tag;
		System.out.println(output);
		doUnit(rootNode);
	}
	
	private void doUnit(long parent ) {
		GcUnit line;
		String reference;
		String output;
		Stack<Long> descendantNodes = G.getNodeStack(parent, true);
		for (Long node: descendantNodes) {
			line = G.getLine(node);
			switch (line.tag.trim()) {
				case "FAMS", "FAMC" -> reference = tf.get(line.value);
				case "HUSB", "WIFE", "CHIL" -> reference = tp.get(line.value);
				default -> {
					if (line.value == null)
						reference = "";
					else
						reference = line.value;
				}
			}
			// don't export empty dates and places
			if (line.tag.trim().equals("DATE")) {
				assert line.value != null;
				if (line.value.trim().equals("")) continue;
			}
			if (line.tag.trim().equals("PLAC")) {
				assert line.value != null;
				if (line.value.trim().equals("")) continue;
			}
			output = line.level.toString() + " " + line.tag + " " + reference;
			System.out.println(output);
			doUnit(node);
		}
	}
}
