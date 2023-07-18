package com.papaworx.cpro.model;

import java.util.List;
import java.util.stream.*;
//import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.model.Document;
import com.papaworx.cpro.structures.GRecord;
import com.papaworx.cpro.structures.GRecord;
import com.papaworx.cpro.utilities.CpString;
import com.papaworx.cpro.utilities.CpBoolean;
import com.papaworx.cpro.utilities.GConnection;

import com.papaworx.cpro.structures.Source;


public class Person {
	public CpString personID;		// unique person identifier of the type either "New" or "@I00000@"
									// if 'New', then hasn't been saved yet
	public CpString firstName;
	public CpString lastName;
	public CpString birthDate;
	public CpString birthPlace;
	public CpString deathDate;
	public CpString deathPlace;
	public CpString pRemarks;
	public CpBoolean isMale;
	public CpBoolean Holocaust;
	private String depository;
	private List <GRecord> rList;	// MainClass set for person
	private Source s;
	private String nRoot;
	private Boolean Changed = false;
	private GConnection G;
	private ObservableList<DropLabel> l;
	//private Boolean bNew = false;
	private StringBuilder sb = null;
	
	/**
	 *  Model class for person
	 *  
	 *  @author Ralph Bloch
	 *  created Dec. 30 2015
	 */

	/*
	 * Default constructor
	 */
	
/* Constructor with initial data
 * @param root
 */

	public Person(GConnection g, String root) {		// constructor
		G = g;
	    s = new Source(G);
		switch (root) {
			case "NEW":
				createBase();
				personID.set("NEW");
				nRoot = "NEW";
				personID.smudge();
				Changed = true;
				//bNew = true;
				return;
			case "FIRST":
				nRoot = g.getStartPerson();
				break;
			default:
				nRoot=root;
				break;	
		}
		this.personID = new CpString(nRoot, "personID", this);
		depository = g.getDeposit();
	    String sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "' ORDER BY GC_NODE;";
	    rList = G.LoadSet(sql);	//load all individual data
	    if(rList.isEmpty()){
	    	createBase();
	    	return;
	    } else {
	    	
		    List <GRecord> filtered = rList.stream().filter(u -> u.gTag.equals("NAME")).collect(Collectors.toList());
		    GRecord r = null;
		    String[] names = null;
		    if(!filtered.isEmpty()){
		    	r = filtered.get(0);
		    	names = r.gValue.trim().split("/");
			    if (names.length > 1) {
			    	this.lastName = new CpString(names[1], "lastName", this);
			    	this.firstName = new CpString(names[0], "firstName", this);
			    } else {
			    	this.firstName = new CpString(r.gValue.trim(), "firstName", this);
				    this.lastName = new CpString("unknown", "lastName", this);
				}
		    }
		    this.birthDate = new CpString(getString("BIRT_DATE"), "birthDate", this);
		    this.birthPlace = new CpString(getString("BIRT_PLAC"), "birthPlace", this);
		    this.deathDate = new CpString(getString("DEAT_DATE"), "deathDate", this);
		    this.deathPlace = new CpString(getString("DEAT_PLAC"), "deathPlace", this);
		    filtered = rList.stream().filter(u -> u.gLevel.equals(1) && u.gTag.equals("NOTE")).collect(Collectors.toList());
		    if (filtered.isEmpty())
		    	this.pRemarks = new CpString(null, "pRemarks", this);
		    else {
			    GRecord gNote = filtered.get(0);
			    long parent = gNote.gID;
			    String sHead = gNote.gValue;
			    if (sHead.equals(""))
			    	this.pRemarks = new CpString(getNote(parent), "pRemarks", this);
			    else
			    	this.pRemarks = new CpString(sHead + "\n " + getNote(parent), "pRemarks", this);
		    }
		    // handle gender:
		    filtered = rList.stream().filter(u -> u.gTag.equals("SEX")).collect(Collectors.toList());
		    if (!filtered.isEmpty()) 
			    this.isMale = new CpBoolean(filtered.get(0).gValue.trim().equals("M"), "isMale", this);		//default female, if sex field missing
		    else 
		    	this.isMale = new CpBoolean(false, "isMale", this);
		    
		    // handle holocaust
		    filtered = rList.stream().filter(u -> (u.gTag.equals("CAUS") && u.gValue.equals("HOLOCAUST"))).collect(Collectors.toList());
		    Boolean bTest = filtered.isEmpty();
		    this.Holocaust = new CpBoolean(!bTest, "Holocaust", this);
	    }
	}
	
	private void createBase() {
		this.personID = new CpString("", "personID", this);
    	this.lastName = new CpString("", "lastName", this);
    	this.firstName = new CpString("", "firstName", this);
	    this.birthDate = new CpString("", "birthDate", this);
	    this.birthPlace = new CpString("", "birthPlace", this);
	    this.deathDate = new CpString("", "deathDate", this);
	    this.deathPlace = new CpString("", "deathPlace", this);
    	this.pRemarks = new CpString("", "pRemarks", this);
	    this.isMale = new CpBoolean(true, "isMale", this);		//default male
	    this.Holocaust = new CpBoolean(false, "Holocaust", this);
	}
	
	public void Change() {
		Changed = true;
	}
	
	public Boolean hasChanged() {
		return Changed;
	}

	private String getString (String pattern) {
	    l = s.getBio(personID.getValue());
	    List <DropLabel> filtered = l.stream().filter(u -> u.getRoot().equals(pattern)).collect(Collectors.toList());
	    if (!filtered.isEmpty()) { 
	    	DropLabel r = filtered.get(0);
			return r.getText();
	    } else {
	    	return null;
	    }
	}
	
	public void completePerson () {
		if(Changed) {
			G.setRoot(nRoot);
			processItem(firstName);
			processItem(lastName);
			processItem(birthDate);
			processItem(birthPlace);
			processItem(deathDate);
			processItem(deathPlace);
			processItem(pRemarks);
			processItem(isMale);
			processItem(Holocaust);
		}
		Changed = false;
	}
	
	public String getPersonID() {
		return personID.get();
	}
	
	public String getFirstName() {
		if(firstName != null)
			return firstName.get();
		else
			return null;
	}
	
	public String getLastName() {
		if (lastName != null)
			return lastName.get();
		else
			return null;
	}
	
	public String getBirthDate() {
			return birthDate.get();
	}
	
	public String getBirthPlace() {
		return birthPlace.get();
	}
	
	public String getBirth() {
		String b = "";
		
		if (birthDate != null)
			b = birthDate.get();
		if ((birthPlace != null) && (birthPlace.get().trim() != ""))
			if (b.trim() != "")
				b += "; " + birthPlace.get();
			else
				b = birthPlace.get();
		if (b != "")
			b = "b. " + b;
		return b;
	}
	
	public String getDeathDate() {
		return deathDate.get();
	}
	
	public String getDeathPlace() {
		return deathPlace.get();
	}
	
	public String getDeath() {
		String d = "";
		if (deathDate != null)
			d = deathDate.get();
		if ((deathPlace != null) && (deathPlace.get().trim() != ""))
			if (d != "")
				d += "; " + deathPlace.get();
			else
				d = deathPlace.get();
		if (d.trim() != "")
			d = "d. " + d;
		return d;
	}
	
	public ObservableList <DropLabel> getParents() {
		return s.getParents(personID.getValue());
	}
	
	public ObservableList <DropLabel> getSiblings() {
		return s.getSiblings(personID.getValue());
	}
	
	public ObservableList <DropLabel> getSpouses() {
		return s.getSpouses(personID.getValue());
	}
	
	public ObservableList <DropLabel> getChildren() {
		return s.getChildren(personID.getValue());
	}

	public ObservableList <DropLabel> getDocs() {
		return s.getDocs(personID.getValue());
	}
	
	public String getNote () {
	    List <GRecord> filtered = rList.stream().filter(u -> u.gLevel.equals(1) && u.gTag.equals("NOTE")).collect(Collectors.toList());
	    if (filtered.isEmpty())
	    	return "";
	    GRecord gNote = filtered.get(0);
	    long parent = gNote.gID;
	    return getNote(parent);
	}
	
	public String getNote (long parent) {
	    List <GRecord> lines = rList.stream().filter(u -> u.gParent.equals(parent)).collect(Collectors.toList());
	    if (lines.isEmpty()) {
	    	return "";
	    }
	    sb = new StringBuilder();
	    Boolean bFirst = true;
	    for (GRecord g : lines) {
	    	if ((g.gTag.equals("CONC")) || bFirst)
	    		sb.append(g.gValue);
	    	else if (g.gTag.equals("CONT"))
	    		sb.append("\n     " + g.gValue);
	    	bFirst= false;
	    }
	    return sb.toString();
	}
	
	public Boolean isMale () {
		if (rList == null)
			return isMale.getValue();
	    List <GRecord> filtered = rList.stream().filter(u -> u.gTag.equals("SEX")).collect(Collectors.toList());
	    if (filtered.isEmpty())
	    	return null;		//default female, if sex field missing
	    else {
	    	return filtered.get(0).gValue.trim().equals("M");
	    }
	    	
	}

	public Boolean isHolocaust () {
	    List <GRecord> filtered = rList.stream().filter(u -> u.gTag.equals("CAUS")).collect(Collectors.toList());
	    if (filtered.isEmpty())
	    	return false;
	    else
	    	return true;
	}
	
	public String getPicture(long iDocRoot) {
		long iDoc = iDocRoot;
		String sFile = null;
	    List <GRecord> filtered = rList.stream().filter(u -> u.gParent.equals(iDoc)).collect(Collectors.toList());
	    if (filtered.isEmpty()) {
	    	//JOptionPane.showMessageDialog(null, "Empty Object");
	    	Platform.exit();
	    }
		for ( GRecord r : filtered) {
				if(r.gTag.equals("FILE") || r.gTag.equals("URL")) {
					sFile = r.gValue;
				}
		}
	return sFile;
	}
	
	public String getCFamily() {
	    List <GRecord> filtered = rList.stream().filter(u -> u.gTag.equals("FAMC")).collect(Collectors.toList());
		if (filtered.isEmpty())
			return "nil";
		else
			return filtered.get(0).gValue;
	}
	
	private void processItem (Object o) {
		switch (o.getClass().getName()) {
			case "Utilities.CpString":
				CpString cs = (CpString)o;
				if(cs.hasChanged()) {
					String sKey = cs.getName();
					String sName = cs.getValue();
					switch (sKey) {
					case "firstName":
					case "lastName":
						if (firstName.getValue()!= null)
							sName = firstName.getValue().trim();
						if (lastName.getValue() != null)
							sName += " /" + lastName.getValue().trim().toUpperCase() + "/";
						sKey = "Name";
						break;
					case "birthDate":
						G.dateObject(sName);
						break;
					default:
						break;
					}
				G.saveString(sKey, sName);
				cs.prime();          // reset the changed flag
			} 
				break;
			case "Utilities.cpBoolean":
				CpBoolean cb = (CpBoolean)o;
				if (cb.hasChanged()) {
					G.saveBoolean(cb.getName(), cb.getValue());
					cb.prime();          // reset the changed flag
				}
				break;
			default:
		}
	}
	
	public void addDocument(Document doc) {
		doc.completeDocument(personID.getValue());
		Changed = true;
	}
	
	public void delete() {
		String sql = "DELETE FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "';";
		G.executeSQL(sql);
		sql = "DELETE FROM GEDCOM WHERE GC_VALUE = '" + nRoot + "';";
		G.executeSQL(sql);
	}
	
	public void unlink() {
		String sql = "DELETE FROM GEDCOM WHERE GC_VALUE = '" + nRoot + "';";
		G.executeSQL(sql);
		sql = "DELETE FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "' AND GC_TAG LIKE 'FAM%';";
		G.executeSQL(sql);
	}
	
	public void setGender(Boolean sex) {
		isMale.setValue(sex);
		isMale.smudge();
		Changed = true;
	}
	
	public Boolean getGender()
	{
		return isMale.get();
	}
	
	public void addFamily(String fRoot, String type) {
		String sql = null;
		String res = null;
		if (type == null)
			return;
		switch(type) {
			case "FAMS":
				sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "' AND GC_TAG = '" + type + "' AND GC_VALUE = '" + fRoot + "';";
				break;
			case "FAMC":
				sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "' AND GC_TAG = '" + type + "';";
				break;
		}
		res = G.getString(sql, "GC_NODE");
		if (res == null) {
			// That combination does not exist, add it
			G.setRoot(nRoot);
			G.saveString(type, fRoot);
		}
		Changed = true;
	}
	
	public void setLastName (String ln) {
		lastName.set(ln);
		Changed = true;
	}
	
	public Document getDocument(long parent) {
		long iDoc = parent;
		String sForm = null;
		String Title = null;
		String Note = null;
		String File = null;
		long fileParent = 0;
		long noteParent = 0;
	    List <GRecord> filtered = rList.stream().filter(u -> u.gParent.equals(iDoc)).collect(Collectors.toList());
	    if (filtered.isEmpty())
	    	return null;
		for ( GRecord r : filtered) {
			switch (r.gTag) {
				case "TITL":
					Title = r.gValue;
					break;
				case "FILE":
				case "URL":
					fileParent = r.gID;
					File = r.gValue;
					break;
				case "NOTE":
					noteParent = r.gID;
					break;
			}
		}
		final long fp = fileParent;
		filtered = rList.stream().filter(u -> u.gParent.equals(fp)).collect(Collectors.toList());
		if (filtered.isEmpty())
			sForm = null;
		else {
			for ( GRecord r : filtered) {
				if(r.gTag.equals("FORM"))
					sForm = r.gValue;
			}
		}
		final long np = noteParent;
		filtered = rList.stream().filter(u -> u.gParent.equals(np)).collect(Collectors.toList());
		if (filtered.isEmpty())
			Note = null;
		else {
		    StringBuilder sb = new StringBuilder();
		    Boolean bFirst = true;
		    for (GRecord g : filtered) {
		    	if ((g.gTag.equals("CONC")) || bFirst)
		    		sb.append(g.gValue);
		    	else if (g.gTag.equals("CONT"))
		    		sb.append("\n     " + g.gValue);
		    	bFirst= false;
		    }
		    Note = sb.toString();
		}
		
		Document doc = new Document( G, Title, sForm, File, Note, false);
		return doc;
	}
	
	public String getDepository() {
		return depository;
	}
	
	public void setRoot(String _nRoot)
	{
		nRoot = _nRoot;
		personID = new CpString("", _nRoot, this);
		Changed = true;
	}
}
