package com.papaworx.cpro.genTree;

import java.util.List;
import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.utilities.GConnection;
import com.papaworx.cpro.model.Person;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import com.papaworx.cpro.model.D_Parameters;

public class Parent {
	private Parent father = null;
	private String fatherID = null;
	private Parent mother = null;
	private Group Root = null;
	private Person p;
	private String motherID = null;
	private String personID = null;
	private String firstName = null;
	private String lastName = null;
	private Boolean bSex;
	private Integer depth = 0;
	private Integer position = 0;
	private Integer positionTotal = 0;
	private AscGenoType GT;
	private GConnection G;
	private Double g_X = (double)0;
	private Double g_Y = (double)0;
	private String sText = null;
	private D_Parameters par = null;
	private ImageView v = null;
	private Text t = null;
	private String imageName = null;
	
	
	public Parent (String id, Integer pos0, Integer d, AscGenoType gt, Group root) {
		personID = id.trim();
		positionTotal = pos0;
		GT = gt;
		G = GT.getG();
		par = GT.getParameters();
		depth = d + 1;
		Root = root;
		boolean bPrinter = GT.getPrintFlag();
		
		//set root node to link all others to.
		if (depth.equals(1)) {
			Root = new Group();
		}
		
		// derive String parameters from database
		p = new Person(G, personID);
		sText = GT.getBio(p);
		firstName = p.getFirstName();
		lastName = p.getLastName();
		bSex = p.isMale();
		if (bSex)
			imageName = GT.getFMale();
		else
			imageName = GT.getFFemale();
		List<DropLabel> list = G.parentGender(personID);
		if(!list.isEmpty()) {
			for ( DropLabel item: list) {
				switch (item.Extra) {
					case "F":
						motherID = item.Text;
						break;
					case "M":
						fatherID = item.Text;
						break;
				}
			}
		}
		if (fatherID != null) {
			father = new Parent (fatherID, positionTotal, depth, GT, Root);
			positionTotal = father.getPosTotal() + 1;
		}
		p = null;
		position = positionTotal;
		if (bPrinter) {
			String sortName = lastName + " " + firstName;
			GT.addToIndex(sortName, position );
		}
		if (motherID != null) {
			mother = new Parent (motherID, positionTotal + 1, depth, GT, Root);
			positionTotal = mother.getPosTotal();
		}
		// now draw item
		g_X = GT.getRootX() + depth * GT.getX_Offset();
		if (bPrinter)
			g_Y = GT.getY(position);
		else
			g_Y = GT.getRootY() + position * 30.0;
		
		t = new Text (sText);
		t.setFont(GT.getFont());
		t.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				handle_click();
			}
		});
		Double x, x1, y, h1, h2;
		x = g_X + GT.getGap1();
		v = par.getIV(imageName);
		if (bPrinter)
			h1 = t.getBoundsInParent().getHeight() - 10;
		else
			h1 = - 0.9 * t.getBoundsInParent().getHeight();
		t.setY(g_Y - 0.3 * h1);
		x1 = x + v.getBoundsInLocal().getWidth() + GT.getGap2();
		t.setX(x1);
		v.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				handle_click();
			}
		});
		h2 = v.getBoundsInParent().getHeight();
		if (bPrinter) {
			v.setY(g_Y - h2 );
		} else
			v.setY(g_Y - 0.5 * h2);
		v.setX(x);
		Line L1 = null;
		Line L2 = null;
		Root.getChildren().addAll(v, t);
		if (father!= null) {
			x = father.getX();
			y = father.getY();
			L1 = new Line(g_X - 10, g_Y, g_X - 10, y);
			L2 = new Line(g_X - 10, y, x, y);
			L1.setStrokeWidth(0.4f);
			L1.setStrokeWidth(0.4f);
			L2.setStroke(Color.GRAY);
			L2.setStroke(Color.GRAY);
			Root.getChildren().addAll(L1, L2);
		}
		if (mother!= null) {
			x = mother.getX();
			y = mother.getY();
			L1 = new Line(g_X - 10, g_Y, g_X - 10, y);
			L2 = new Line(g_X - 10, y, x, y);
			L1.setStrokeWidth(0.4f);
			L1.setStrokeWidth(0.4f);
			L2.setStroke(Color.GRAY);
			L2.setStroke(Color.GRAY);
			Root.getChildren().addAll(L1, L2);
		}
		if (depth.equals(1)) {
			Line L3 = new Line(10, g_Y, g_X, g_Y);
			L3.setStroke(Color.GRAY);
			L3.setStroke(Color.GRAY);
			Root.getChildren().add(L3);
		}
	}
	
	public Integer getPosTotal() {
		return positionTotal;
	}

	private void handle_click() {
		GT.getController().handler(personID);
	}
	
	public Group getRoot() {
		return Root;
	}
	
	public Double getX() {
		return g_X;
	}
	
	public Double getY() {
		return g_Y;
	}
}
