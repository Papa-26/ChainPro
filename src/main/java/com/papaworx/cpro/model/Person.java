package com.papaworx.cpro.model;

import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.structures.GRecord;
import com.papaworx.cpro.structures.Source;
import com.papaworx.cpro.utilities.CpBoolean;
import com.papaworx.cpro.utilities.CpString;
import com.papaworx.cpro.utilities.GConnection;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
	private final Source s;
	private String nRoot;
	//private Boolean Changed = false;
	private final GConnection G;

	private Boolean bChanged = false;

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
			case "NEW" -> {
				createBase();
				personID.set("NEW");
				nRoot = "NEW";
				personID.smudge();
				return;
			}
			case "FIRST" -> nRoot = g.getStartPerson();
			default -> nRoot = root;
		}
		this.personID = new CpString(nRoot, "personID", this);
		depository = g.getDeposit();
	    String sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "' ORDER BY GC_NODE;";
	    rList = G.LoadSet(sql);	//load all individual data
	    if(rList.isEmpty()){
	    	createBase();
		} else {
	    	
		    List <GRecord> filtered = rList.stream().filter(u -> u.gTag.equals("NAME")).collect(Collectors.toList());
		    GRecord r;
		    String[] names;
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
		    boolean bTest = filtered.isEmpty();
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
	
	private String getString (String pattern) {
		ObservableList<DropLabel> l = s.getBio(personID.getValue());
	    List <DropLabel> filtered = l.stream().filter(u -> u.getRoot().equals(pattern)).toList();
	    if (!filtered.isEmpty()) { 
	    	DropLabel r = filtered.get(0);
			return r.getText();
	    } else {
	    	return null;
	    }
	}
	
	public void completePerson () {
		if(bChanged) {
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

	public String getBirth() {
		String b = "";
		
		if (birthDate != null)
			b = birthDate.get();
		if ((birthPlace != null) && (!birthPlace.get().trim().equals("")))
			if (!b.trim().equals(""))
				b += "; " + birthPlace.get();
			else
				b = birthPlace.get();
		if (!Objects.equals(b, ""))
			b = "b. " + b;
		return b;
	}

	public String getDeath() {
		String d = "";
		if (deathDate != null)
			d = deathDate.get();
		if ((deathPlace != null) && (!deathPlace.get().trim().equals("")))
			if (!Objects.equals(d, ""))
				d += "; " + deathPlace.get();
			else
				d = deathPlace.get();
		if (!d.trim().equals(""))
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

	public String getNote (long parent) {
	    List <GRecord> lines = rList.stream().filter(u -> u.gParent.equals(parent)).toList();
	    if (lines.isEmpty()) {
	    	return "";
	    }
		//private Boolean bNew = false;
		StringBuilder sb = new StringBuilder();
	    boolean bFirst = true;
	    for (GRecord g : lines) {
	    	if ((g.gTag.equals("CONC")) || bFirst)
	    		sb.append(g.gValue);
	    	else if (g.gTag.equals("CONT"))
	    		sb.append("\n     ").append(g.gValue);
	    	bFirst= false;
	    }
	    return sb.toString();
	}
	
	public Boolean isMale () {
		if (rList == null)
			return isMale.getValue();
	    List <GRecord> filtered = rList.stream().filter(u -> u.gTag.equals("SEX")).toList();
	    if (filtered.isEmpty())
	    	return null;		//default female, if sex field missing
	    else {
	    	return filtered.get(0).gValue.trim().equals("M");
	    }
	    	
	}

	public String getPicture(long iDocRoot) {
		String sFile = null;
	    List <GRecord> filtered = rList.stream().filter(u -> u.gParent.equals(iDocRoot)).toList();
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
	    List <GRecord> filtered = rList.stream().filter(u -> u.gTag.equals("FAMC")).toList();
		if (filtered.isEmpty())
			return "nil";
		else
			return filtered.get(0).gValue;
	}
	
	private void processItem (Object o) {
		switch (o.getClass().getName()) {
			case "com.papaworx.cpro.utilities.CpString" -> {
				CpString cs = (CpString) o;
				if (cs.hasChanged()) {
					String sKey = cs.getName();
					String sName = cs.getValue();
					switch (sKey) {
						case "firstName", "lastName" -> {
							if (firstName.getValue() != null)
								sName = firstName.getValue().trim();
							if (lastName.getValue() != null)
								sName += " /" + lastName.getValue().trim().toUpperCase() + "/";
							sKey = "Name";
						}
						case "birthDate" -> G.dateObject(sName);
						default -> {
						}
					}
					G.saveString(sKey, sName);
					cs.prime();          // reset the changed flag
				}
			}
			case "com.papaworx.cpro.utilities.cpBoolean" -> {
				CpBoolean cb = (CpBoolean) o;
				if (cb.hasChanged()) {
					G.saveBoolean(cb.getName(), cb.getValue());
					cb.prime();          // reset the changed flag
				}
			}
			default -> {
			}
		}
	}
	
	public void addDocument(Document doc) {
		doc.completeDocument(personID.getValue());
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
	}

	public void addFamily(String fRoot, String type) {
		String sql = null;
		String res;
		if (type == null)
			return;
		switch (type) {
			case "FAMS" ->
					sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "' AND GC_TAG = '" + type + "' AND GC_VALUE = '" + fRoot + "';";
			case "FAMC" ->
					sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + nRoot + "' AND GC_TAG = '" + type + "';";
		}
		res = G.getString(sql, "GC_NODE");
		if (res == null) {
			// That combination does not exist, add it
			G.setRoot(nRoot);
			G.saveString(type, fRoot);
		}
	}
	
	public void setLastName (String ln) {
		lastName.set(ln);
	}
	
	public Document getDocument(long parent) {
		String sForm = null;
		String Title = null;
		String Note;
		String File = null;
		long fileParent = 0;
		long noteParent = 0;
	    List <GRecord> filtered = rList.stream().filter(u -> u.gParent.equals(parent)).collect(Collectors.toList());
	    if (filtered.isEmpty())
	    	return null;
		for ( GRecord r : filtered) {
			switch (r.gTag) {
				case "TITL" -> Title = r.gValue;
				case "FILE", "URL" -> {
					fileParent = r.gID;
					File = r.gValue;
				}
				case "NOTE" -> noteParent = r.gID;
			}
		}
		final long fp = fileParent;
		filtered = rList.stream().filter(u -> u.gParent.equals(fp)).collect(Collectors.toList());
		if (!filtered.isEmpty()) {
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
		    boolean bFirst = true;
		    for (GRecord g : filtered) {
		    	if ((g.gTag.equals("CONC")) || bFirst)
		    		sb.append(g.gValue);
		    	else if (g.gTag.equals("CONT"))
		    		sb.append("\n     ").append(g.gValue);
		    	bFirst= false;
		    }
		    Note = sb.toString();
		}

		return new Document( G, Title, sForm, File, Note, false);
	}
	
	public String getDepository() {
		return depository;
	}
	
	public void setRoot(String _nRoot)
	{
		nRoot = _nRoot;
		personID = new CpString("", _nRoot, this);
	}

	public Boolean hasChanged (){
		return bChanged;
	}

	public void setChange(Boolean b){
		bChanged = b;
	}
}
