package com.papaworx.cpro.genTree;

import com.papaworx.cpro.printing.Index;
import com.papaworx.cpro.utilities.GConnection;
import javafx.scene.Node;
import javafx.scene.text.Font;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.model.Person;
import com.papaworx.cpro.controllers.AscFamilyTreeViewController;

public class AscGenoType {
	private GConnection G;
	private D_Parameters Par;
	private Node SC;
	private Double x_Offset = 50.0;
	private Double y_Offset = 80.0;
	private Double gap1 = 0.0;	// between anchor and graphic
	private Double gap2 = 0.0;	// between graphic and text
	private Double rootX = 50.0;
	private Double rootY = 20.0;
	private String fMale = "IconMale.gif";
	private String fFemale = "IconFemale.gif";
	private AscFamilyTreeViewController ftvc = null;
	private Font font = null;
	private Font titleFont = null;
	private Font footerFont = null;
	private Double scale = 1.0;
	private Double Overlap = 30.0;
	private boolean bPrinter = false;
	private Integer page = 0;
	private Index index = null;
	@SuppressWarnings("unused")
	private double printWidth = 0;
	@SuppressWarnings("unused")
	private double printHeight = 0;
	private Integer itemsPerPage;
	private double itemIncrement;
	//private ascFamilyTreeViewController ftvc;
	
	public AscGenoType(GConnection g, Node sc, D_Parameters p, boolean bp) {
		// dummy constructor
		G = g;
		SC = sc;
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
		return x_Offset * scale;
	}
	
	public Double getY_Offset () {
		return y_Offset * scale;
	}
	
	public Double getGap1() {
		return gap1 * scale;
	}
	
	public Double getGap2() {
		return gap2 * scale;
	}
	
	public Double getRootX() {
		return rootX * scale;
	}
	
	public Double getRootY() {
		return rootY * scale;
	}
	
	public String getFMale() {
		return fMale;
	}
	
	public String getFFemale() {
		return fFemale;
	}
	
	public Node getScreen() {
		return SC;
	}
	
	public D_Parameters getParameters () {
		return Par;
	}
	
	public AscFamilyTreeViewController getController() {
		return ftvc;
	}

	public Double getScale() {
		return scale;
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
	
	public Double getOverlap() {
		return Overlap;
	}
	
	public boolean getPrintFlag() {
		return bPrinter;
	}
	
	public double getY(Integer m) {
		Integer n = m + 1;
		page = n / itemsPerPage;
		double result = n * itemIncrement + page * 54 + 10;
		return result;
	}
	
	public Integer getPage() {
		return page + 1;
	}
	
	public void addToIndex (String name, Integer l) {
		Integer p = (l + 1)/itemsPerPage + 1;
		Integer ll = (l + 1) % itemsPerPage + 1;
		if (p.equals(1))
			ll--;
		index.addItem(name, "page " + p.toString() + " - " + ll.toString());
	}
	
	public Object[] sIndex() {
		return  index.toArray();
	}
	
	public void setPrintHeight(double h) {
		printHeight = h;
		itemsPerPage = (int)(h - 54) / 42;
		itemIncrement = (h - 54)/itemsPerPage;
	}
	
	public void setPrintWidth (double w) {
		printWidth = w;
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
