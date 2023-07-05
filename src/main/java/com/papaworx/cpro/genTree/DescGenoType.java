package com.papaworx.cpro.genTree;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.papaworx.cpro.printing.Index;
import com.papaworx.cpro.utilities.GConnection;
import javafx.scene.text.Font;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.model.Person;
import com.papaworx.cpro.controllers.DescFamilyTreeViewController;

public class DescGenoType {
	// incorporates the common genotype: C:\Users\Dad\Documents\ChainPro_J
	private final GConnection G;
	private final D_Parameters Par;
	private DescFamilyTreeViewController ftvc;
	private final boolean bPrinter;
	private final Index index;
	private Integer itemsPerPage;
	private double itemIncrement;
	private Integer N = 0;
	private final Set<String> members;
	private double scale = 1.0;
	private Font font;
	private final Font titleFont;
	private final Font footerFont;
	
	public DescGenoType(GConnection g, D_Parameters p, boolean bp) {
		// dummy constructor
		G = g;
		Par = p;
		bPrinter = bp;
		members = new HashSet<>();
		index = new Index();
		font = new Font(scale * 15.0);
		titleFont = new Font(15.0);
		footerFont = new Font (10.0);
	}
	
	public void setController(DescFamilyTreeViewController d) {
		ftvc = d;
	}
	
	public Object[] sIndex() {
		return  index.toArray();
	}
	
	public GConnection getG() {
		return G;
	}
	
	public void setScale(Double scale) {
		this.scale = scale;
		font = new Font(scale * 15.0);
	}
	
	public Font getFont() {
		return font;
	}
	
	public Font getTitleFont() {
		return titleFont;
	}
	
	public Font getFooterFont() {
		return footerFont;
	}
	
	public Double getX_Offset () {
		return 20.0;
	}

	public Double getGap1() {
		// between anchor and graphic
		return 10.0;
	}
	
	public Double getGap2() {
		// between graphic and text
		return 10.0;
	}
	
	public Double getRootX() {
		return 50.0;
	}
	
	public Double getRootY() {
		return 20.0;
	}
	
	public String getFMale() {
		return "IconMale.gif";
	}
	
	public String getFFemale() {
		return "IconFemale.gif";
	}
	
	public String getFSpousalM() {
		return "MarriedM.gif";
	}
	
	public String getFSpousalF() {
		return "MarriedF.gif";
	}

	public D_Parameters getParameters () {
		return Par;
	}
	
	public DescFamilyTreeViewController getController() {
		return ftvc;
	}
	
	public String getBio(Person p) {
		String firstName = p.getFirstName();
		String lastName = p.getLastName();
		String birthDate = p.getBirthDate();
		String personID = p.getPersonID();
		String sText = "[" + personID.trim() + "] " + firstName + " " + lastName;
		if (bPrinter) {
			String s = p.getBirth();
			if (!Objects.equals(s, ""))
				sText += ";\n" + s;
			s = p.getDeath();
			if (!Objects.equals(s, ""))
				sText += ";\n" + s;
		} else if (!Objects.equals(birthDate, ""))
			sText += " *" + p.getBirthDate();
		return sText;
	}
	
	public String getSortName (String pID) {
		Person p = new Person(G, pID);
		String firstName = p.getFirstName();
		String lastName = p.getLastName();
		return lastName + " " + firstName;
	}

	public boolean getPrintFlag() {
		return bPrinter;
	}
	
	public void addToIndex (String name, Integer l) {
		Integer p = (l + 1)/itemsPerPage + 1;
		int ll = (l + 1) % itemsPerPage + 1;
		if (p.equals(1))
			ll--;
		index.addItem(name, "page " + p + " - " + ll);
	}

	public void setPrintWidth () {
	}
	
	public void setPrintHeight(double h) {
		itemsPerPage = (int)(h - 54) / 42;
		itemIncrement = (h - 54)/itemsPerPage;
	}
	
	public double getY(Integer m) {
		int n = m + 1;
		int page = n / itemsPerPage;
		return n * itemIncrement + page * 54 + 10;
	}
	
	public Integer getN() {
		return N++;
	}
	
	public boolean member(String id) {
		if (members.contains(id))
			return true;
		members.add(id);
		return false;
	}
}
