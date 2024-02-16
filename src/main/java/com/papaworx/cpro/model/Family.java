package com.papaworx.cpro.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.papaworx.cpro.structures.GRecord;
import com.papaworx.cpro.utilities.FSource;
import com.papaworx.cpro.utilities.FStringProperty;
import com.papaworx.cpro.utilities.GConnection;

public class Family {
	
	/**
	 *  Model class for family
	 *  
	 *  @author Ralph Bloch
	 *  created Jan 4 2016
	 */
	public FStringProperty IDField;
	public FStringProperty Spouse_a;
	public FStringProperty Spouse_b;
	public FStringProperty fRemarks;
	public FStringProperty engagementDate;
	public FStringProperty engagementPlace;
	public FStringProperty marriageDate;
	public FStringProperty marriagePlace;
	public FStringProperty separationDate;
	public FStringProperty separationPlace;
	public FStringProperty divorceDate;
	public FStringProperty divorcePlace;
	private Boolean bChanged;
	public ArrayList<String> salChildren = null;
	/**
	 * 	we had a long discussion, how to handle the whole issue of modern families
	 *  and gender issues. We settled on a compromise: a family consists of one or two adults/parents of any gender,
	 *  plus any number of children from zero upwards, not necessarily biologically related
	 */
	public String sSpouse_a = null;			// sPerson ID
	public String sSpouse_b = null;			// sPersonID
	
	private List <GRecord> rList;	// MainClass set for family
	private FSource s;
	private GConnection G;
	private String fRoot = null;
	
	/**
	 * Family is one of the three types of building blocks. In a given situation we may either deal with an already existing family
	 * (i.e. it has a distinct family ID) or a new family in construction, in which case it has the identifier 'New'. At any one time, only one 'New' family may exist.
	 */
	
	public Family(GConnection g, String famID) {
		G = g;		// pointer to gConnector
	    createBase();
	    if ((famID != null) && famID.equals("NEW")) {
	    	IDField = new FStringProperty("New", "famID", this);
	    	bChanged = true;
	    	//bNew = true;
	    }
	    else
	    {
	    	fRoot = famID;
		    String sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + fRoot + "';";
		    rList = g.LoadSet(sql);	//load all individual data
		    s = new FSource(g);
		    List <GRecord> headers = rList.stream().filter(u -> u.gLevel.equals(1)).collect(Collectors.toList());
		    salChildren = new ArrayList<String>();
		    IDField =new FStringProperty(famID, "famID", this);
		    fRemarks = new FStringProperty("","fRemarks", this);
		    for (GRecord r : headers)
		    	switch (r.gTag) {
			    	case "HUSB":
			    		Spouse_a = new FStringProperty(s.getName(r.gValue), "husband", this);
			    		break;
			    	case "WIFE":
			    		Spouse_b = new FStringProperty(s.getName(r.gValue), "wife", this);
			    		break;
			    	case "CHIL":
			    		salChildren.add(s.getName(r.gValue));
			    		break;
			    	case "MARR":
			    	case "ENGA":
			    	case "SEP":
			    	case "DIV":
			    			doEvent(r.gTag, r.gID);
			    		break;
			    	case "NOTE":
			    		doRemark(r.gID);
			    		break;
			    	default:
		    	}
	    }
	    bChanged = false;
	}
	
	private void doEvent(String event, long parent) {
		String place, date;
		place = null;
		date = null;
	    List <GRecord> eData = rList.stream().filter(u -> u.gParent.equals(parent)).collect(Collectors.toList());
	    for (GRecord r : eData)
	    	switch (r.gTag) {
	    		case "PLAC":
	    				place = r.gValue;
	    			break;
	    		case "DATE":
	    			date = r.gValue;
	    			break;
    			default:
	    	}
	    switch (event) {
	    	case "ENGA":
	    		engagementDate = new FStringProperty(date, "engagementDate", this);
	    		engagementPlace = new FStringProperty(place, "engagementPlace", this);
	    		break;
	    	case "MARR":
	    		marriageDate = new FStringProperty(date, "marriageDate", this);
	    		// if date != null update root field value to year
	    		marriagePlace = new FStringProperty(place, "marriagePlace", this);
	    		break;
	    	case "SEP":
	    		separationDate = new FStringProperty(date, "separationDate", this);
	    		separationPlace = new FStringProperty(place, "separationPlace", this);
	    		break;
	    	case "DIV":
	    		divorceDate = new FStringProperty(date, "divorceDate", this);
	    		divorcePlace = new FStringProperty(place, "divorcePlace", this);
	    		break;
    		default:
	    }
	}
	
	public ObservableList<String> getChildren() {
		// TODO: from person IDs to names
		return FXCollections.observableList(salChildren);
	}
	
	public String getOtherSpouse(String _sPersonID)
	{
		// returns either zero for congenitally single parent families or at most one partner
		String sOtherPerson = null;
		return sOtherPerson;
	}
	
	public Boolean addSpouse(String _sPersonID)
	{
		// makes sure that there are at most two spouses in a given family
		if ((sSpouse_a != null) && (sSpouse_b != null))
			return false;			// can't have more than two parent/adults in a family
		else if (sSpouse_a == null)
			sSpouse_a = _sPersonID;
		else
			sSpouse_b = _sPersonID;
		bChanged = true;
		return true;
	}
	
	private void doRemark (long parent) {
		    List <GRecord> lines = rList.stream().filter(u -> u.gParent.equals(parent)).collect(Collectors.toList());
		    if (lines.isEmpty()) {
		    	fRemarks.set("");
		    }
		    StringBuilder sb = new StringBuilder();
		    Boolean bFirst = true;
		    for (GRecord g : lines) {
		    	if ((g.gTag.equals("CONC")) || bFirst)
		    		sb.append(g.gValue);
		    	else if (g.gTag.equals("CONT"))
		    		sb.append("\n     " + g.gValue);
		    	bFirst= false;
		    }
	    	fRemarks = new FStringProperty(sb.toString(), "fRemarks", this);
	}


	public Boolean hasChanged (){
		return bChanged;
	}

	public void Change(Boolean b){
		bChanged = b;
	}
	
	public void complete() {
		if (bChanged) {
			//System.out.println("Completing family " + fRoot);
			G.setRoot(fRoot);
			processItem(Spouse_a);
			processItem(Spouse_b);
			processItem(fRemarks);
			processItem(engagementDate);
			processItem(engagementPlace);
			processItem(marriageDate);
			processItem(marriagePlace);
			processItem(separationDate);
			processItem(separationPlace);
			processItem(divorceDate);
			processItem(divorcePlace);
		}
		bChanged = false;
	}
	
	private void processItem (Object o) {
		String sKey, sValue;
		//System.out.println("Processing " + ((FStringProperty)o).getName());
		FStringProperty fp = (FStringProperty)o;
		if(fp.hasChanged()) {
			sKey = fp.getName();
			sValue = fp.getValue();
			if (sKey.equals("marriageDate"))
				G.dateObject(sValue);
			G.saveString(sKey, sValue);
			fp.prime();          // reset the changed flag
		}
	}
	
	private void createBase() {
    	this.Spouse_a = new FStringProperty(null, "spouse_a", this);
    	this.Spouse_b = new FStringProperty(null, "spouse_b", this);
	    this.fRemarks = new FStringProperty(null, "fRemarks", this);
	    this.engagementDate = new FStringProperty(null, "engagementDate", this);
	    this.engagementPlace = new FStringProperty(null, "engagementPlace", this);
	    this.marriageDate = new FStringProperty(null, "marriageDate", this);
    	this.marriagePlace = new FStringProperty(null, "marriagePlace", this);
	    this.separationDate = new FStringProperty(null, "separationDate", this);
	    this.separationPlace = new FStringProperty(null, "separationPlace", this);
	    this.divorceDate = new FStringProperty(null, "divorceDate", this);
    	this.divorcePlace = new FStringProperty(null, "divorcePlace", this);
	}
	
	public void addPerson (String pRoot, String type) {
		bChanged = true;
		String sql = null;
		String res = null;
		if (type == null)
			return;
		if(type.equals("CHIL"))
			sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + fRoot + "' AND GC_TAG = '" + type + "' AND GC_VALUE = '" + pRoot + "';";
		else
			sql = "SELECT * FROM GEDCOM WHERE GC_ROOT_OBJECT = '" + fRoot + "' AND GC_TAG = '" + type + "';";
		res = G.getString(sql, "GC_NODE");
		if (res == null) {
			// only if no child with same identity or parent of same sex exists
			G.setRoot(fRoot);
			G.saveString(type, pRoot);
		}
	}
	
	public String getRoot() {
		return IDField.getValue();
	}	
}
