package com.papaworx.cpro.genTree;

import com.papaworx.cpro.controllers.AscFamilyTreeViewController;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.model.Person;
import com.papaworx.cpro.printing.Index;
import com.papaworx.cpro.utilities.GConnection;
import javafx.scene.text.Font;

public class AscGenoType {
	private final GConnection G;
	private final D_Parameters Par;
	private AscFamilyTreeViewController ftvc = null;
	private Font font;
	private final Font titleFont;
	private final Font footerFont;
	private Double scale = 1.0;
	private final boolean bPrinter;
	private final Index index;
	private Integer itemsPerPage;
	private double itemIncrement;
	//private ascFamilyTreeViewController ftvc;
	
	public AscGenoType(GConnection g, D_Parameters p, boolean bp) {
		// dummy constructor
		G = g;
		Par = p;
		font = new Font(scale * 15.0);
		titleFont = new Font(15.0);
		footerFont = new Font (10.0);
		bPrinter = bp;
		index = new Index();
	}
	
	public void setScale(Double scale) {
		this.scale = scale;
		font = new Font(scale * 15.0);
	}
	
	public void setController (AscFamilyTreeViewController a) {
		ftvc = a;
	}
	
	public GConnection getG() {
		return G;
	}
	
	public Double getX_Offset () {
		Double x_Offset = 50.0;
		return x_Offset * scale;
	}

	public Double getGap1() {
		// between anchor and graphic
		Double gap1 = 0.0;
		return gap1 * scale;
	}
	
	public Double getGap2() {
		// between graphic and text
		Double gap2 = 0.0;
		return gap2 * scale;
	}
	
	public Double getRootX() {
		Double rootX = 50.0;
		return rootX * scale;
	}
	
	public Double getRootY() {
		Double rootY = 20.0;
		return rootY * scale;
	}
	
	public String getFMale() {
		return "IconMale.gif";
	}
	
	public String getFFemale() {
		return "IconFemale.gif";
	}

	public D_Parameters getParameters () {
		return Par;
	}
	
	public AscFamilyTreeViewController getController() {
		return ftvc;
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

	public boolean getPrintFlag() {
		return bPrinter;
	}
	
	public double getY(Integer m) {
		int n = m + 1;
		int page = n / itemsPerPage;
		return n * itemIncrement + page * 54 + 10;
	}

	public void addToIndex (String name, Integer l) {
		Integer p = (l + 1)/itemsPerPage + 1;
		int ll = (l + 1) % itemsPerPage + 1;
		if (p.equals(1))
			ll--;
		index.addItem(name, "page " + p + " - " + ll);
	}
	
	public Object[] sIndex() {
		return  index.toArray();
	}
	
	public void setPrintHeight(double h) {
		itemsPerPage = (int)(h - 54) / 42;
		itemIncrement = (h - 54)/itemsPerPage;
	}
	
	public void setPrintWidth () {
	}
	
	public String getBio(Person p) {
		String firstName = p.getFirstName();
		String lastName = p.getLastName();
		String birthDate = p.getBirthDate();
		String personID = p.getPersonID();
		String sText = "[" + personID.trim() + "] " + firstName + " " + lastName;
		if (bPrinter) {
			String s = p.getBirth();
			if (s != null)
				sText += ";\n" + s;
			s = p.getDeath();
			if (s != null)
				sText += ";\n" + s;
		} else if (birthDate != null)
			sText += " *" + p.getBirthDate();
		return sText;
	}
}
