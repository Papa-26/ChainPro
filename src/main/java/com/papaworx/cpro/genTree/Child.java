package com.papaworx.cpro.genTree;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.papaworx.cpro.utilities.GConnection;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.model.Person;

public class Child {

	private DescGenoType dGT = null;
	private GConnection gc = null;
	private Integer position = 0;
	private String personID;
	private boolean bPrinter;
	private boolean isMale;
	private List<String> families;
	private String spouseID;
	private String familyID;
	private double g_X = 0;
	private double g_Y = 0;
	private D_Parameters par = null;
	private String imageName = null;
	private ImageView v = null;
	private Text t = null;
	private boolean prune = false;
	
	public Child(String id, Integer d, DescGenoType gt, Group root, double x0, double y0) {
		Integer depth = d;
		String dob, sCutoff;
		Integer yob, iCutoff;
		iCutoff = 0;
		dGT = gt;
		Group Root = root;
		gc = dGT.getG();
		par = dGT.getParameters();
		sCutoff = par.getCutOff().getValue();
		iCutoff = Integer.valueOf(sCutoff);
		personID = id;
		Person p = new Person(gc, personID);
		dob = p.getBirthDate();
		yob = getYear(dob);
		if (yob > iCutoff) 
			return; 
		position = dGT.getN();
		isMale = p.isMale();
		if (isMale)
			imageName = dGT.getFMale();
		else
			imageName = dGT.getFFemale();
		String sText = dGT.getBio(p);
		if (dGT.member(personID)) {
			sText = "** " + sText;
			prune = true;
		}
		if(position.equals(0)) {
			Circle c = new Circle(0,0,0);
			Root.getChildren().add(c);
		}
			
		t = new Text(sText);
		t.setFont(dGT.getFont());
		t.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				handle_click();
			}
		});
		v = par.getIV(imageName);
		bPrinter = dGT.getPrintFlag();
		if (bPrinter) {
			t.setFont(dGT.getFont());
			dGT.addToIndex(dGT.getSortName(personID), position );
			g_Y = dGT.getY(position);
		} else {
			g_Y = dGT.getRootY() + position * 30.0;
		}
		g_X = dGT.getRootX() + depth * dGT.getX_Offset();
		v.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				handle_click();
			}
		});
		double tx, ty, h1, h2;
		tx = g_X + dGT.getGap1();
		h2 = v.getBoundsInParent().getHeight();
		if (bPrinter) {
			v.setY(g_Y - h2 );
		} else
			v.setY(g_Y - 0.5 * h2);
		v.setX(tx);
		if (bPrinter)
			h1 = t.getBoundsInParent().getHeight() - 10;
		else
			h1 = - 0.9 * t.getBoundsInParent().getHeight();
		t.setY(g_Y - 0.3 * h1);
		tx = tx + v.getBoundsInParent().getWidth() + dGT.getGap2();
		t.setX(tx);
		Root.getChildren().addAll(v, t);
		if (!depth.equals(0) && (y0 > 0)) {
			Line L1 = new Line(x0, y0, x0, g_Y);
			Line L2 = new Line(x0, g_Y, g_X + dGT.getGap1(), g_Y);
			L1.setStrokeWidth(0.4f);
			L1.setStrokeWidth(0.4f);
			L2.setStroke(Color.GRAY);
			L2.setStroke(Color.GRAY);
			Root.getChildren().addAll(L1, L2);
		} else {
			Line L3 = new Line(dGT.getRootX()- dGT.getGap1(), g_Y, dGT.getRootX() + dGT.getGap1(), g_Y);
			L3.setStroke(Color.GRAY);
			Root.getChildren().add(L3);
		}
		p = null;
		if (prune)
			return;
		families = getFams();
		if (families != null) {
			ty = g_Y;
			for (String fam: families) {
				familyID = fam;
				List<String> x = getSpouse();
				if (x.isEmpty())
					spouseID = null;
				else
					spouseID = x.get(0);
				Spouse spouse = new Spouse (spouseID, familyID, depth + 1, dGT, Root, g_X, ty, !isMale);
				ty = spouse.getY();
			}
		}
	}
	
	public double getY() {
		return g_Y;
	}

	private void handle_click() {
		dGT.getController().handler(personID);
	}
	
	private List<String> getFams () {
		String sql = "SELECT * FROM GEDCOM WHERE GC_TAG = 'FAMS' AND GC_ROOT_OBJECT = '" + personID + "';";
		return gc.getRecords(sql, "GC_VALUE");
	}
	
	private List<String> getSpouse () {
		String sql = "SELECT * FROM GEDCOM WHERE (GC_TAG = 'WIFE' OR GC_TAG = 'HUSB') AND GC_ROOT_OBJECT = '";
		sql += familyID + "' AND GC_VALUE != '" + personID + "';";
		return gc.getRecords(sql, "GC_VALUE");
	}
	
	private Integer getYear(String y) {
		Integer nY = null;
		if (y== null)
			return 0;
		Pattern pattern = Pattern.compile("(\\d{4})");
	    Matcher matcher = pattern.matcher(y);
	    String val = "";
	    if (matcher.find()) {
	        val = matcher.group(1);
	        nY = Integer.valueOf(val);
	        return nY;
	    }
	    return 0;
	}
}
