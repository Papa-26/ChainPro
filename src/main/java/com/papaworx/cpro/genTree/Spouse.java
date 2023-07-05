package com.papaworx.cpro.genTree;

import java.util.List;

import com.papaworx.cpro.utilities.GConnection;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.model.Person;

public class Spouse {

	private DescGenoType dGT = null;
	private GConnection gc = null;
	private Integer position = 0;
	private String personID;
	private boolean bPrinter;
	private List<String> children;
	private double g_X = 0;
	private double g_Y = 0;
	private D_Parameters par = null;
	private String imageName = null;
	private ImageView v = null;
	private Text t = null;
	private String familyID;
	private boolean prune = false;
	private String sText;

	public Spouse (String pid, String fid, Integer d, DescGenoType gt, Group root, double x0, double y0, boolean isMale) {
		Integer depth = d;
		dGT = gt;
		position = dGT.getN();
		Group Root = root;
		gc = dGT.getG();
		par = dGT.getParameters();
		personID = pid;
		familyID = fid;
		if (isMale)
			imageName = dGT.getFSpousalM();
		else
			imageName = dGT.getFSpousalF();
		if (personID != null) {
			Person p = new Person(gc, personID);
			sText = dGT.getBio(p);
		} else
			sText = "UNKNOWN";
		if ((personID != null) && dGT.member(personID)) {
			sText = "** " + sText;
			prune = true;
		}
		t = new Text(sText);
		t.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				handle_click();
			}
		});
		v = par.getIV(imageName);
		v.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				handle_click();
			}
		});
		bPrinter = dGT.getPrintFlag();
		if (bPrinter) {
			g_Y = dGT.getY(position);
			if( personID != null)
				dGT.addToIndex(dGT.getSortName(personID), position );
		} else {
			g_Y = dGT.getRootY() + position * 30.0;
		}
		g_X = dGT.getRootX() + depth * dGT.getX_Offset();
		double tx, ty, h1, h2;
		tx = g_X + dGT.getGap1();
		h2 = v.getBoundsInParent().getHeight();
		if (bPrinter) {
			t.setFont(dGT.getFont());
			v.setY(g_Y - h2 );
		} else
			v.setY(g_Y - 0.5 * h2);
		v.setX(tx);
		if (bPrinter)
			h1 = t.getBoundsInParent().getHeight() - 10;
		else
			h1 = - 0.9 * t.getBoundsInParent().getHeight();
		t.setY(g_Y - 0.3 * h1);
		tx = tx + v.getBoundsInLocal().getWidth() + dGT.getGap2();
		t.setX(tx);
		Root.getChildren().addAll(v, t);

		Line L1 = new Line(x0, y0, x0, g_Y);
		Line L2 = new Line(x0, g_Y, g_X + dGT.getGap1(), g_Y);
		L1.setStrokeWidth(0.4f);
		L1.setStrokeWidth(0.4f);
		L2.setStroke(Color.GRAY);
		L2.setStroke(Color.GRAY);
		Root.getChildren().addAll(L1, L2);
		if (prune)
			return;
		children = getChildren();
		if (children != null) {
			ty = g_Y;
			for (String ch: children) {
				Child child = new Child (ch, depth + 1, dGT, Root, g_X, ty);
				ty = child.getY();
			}
		}
		
	}
	
	public double getY() {
		return g_Y;
	}

	private void handle_click() {
		dGT.getController().handler(personID);
	}
	
	private List<String> getChildren() {
		String sql = "call getFChildren('" + familyID + "');";
		return gc.getRecords(sql, "GC_VALUE");
	}
}
