package com.papaworx.cpro.printing;

import com.papaworx.cpro.genTree.Child;
import com.papaworx.cpro.genTree.Parent;
import com.papaworx.cpro.genTree.AscGenoType;
import com.papaworx.cpro.genTree.DescGenoType;
import com.papaworx.cpro.utilities.GConnection;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.model.Person;

public class TreePrinter {
	private GConnection G;
	private D_Parameters par = null;
	private Double nodeHeight = 0.0;
	public enum treeType {ascend, descend};
	private String title;
	private AscGenoType aGT;
	private DescGenoType dGT;
	private PrinterJob job = null;
	private double printHeight = 0;
	private double printWidth = 0;
	private PageLayout pl = null;
	private treeType t;
	
	public TreePrinter(GConnection g) {
		G = g;
		par = G.getPar();
	}
	
	public void print (Person p, treeType _t) {
		Group n = null;
		t = _t;
    	String rootID = p.getPersonID();
		switch (t) {
			case ascend:				// constructs ascending node
		    	aGT = new AscGenoType(G, par, true );
				job = PrinterJob.createPrinterJob();
				if (job != null && job.showPrintDialog(null)){
					if (job.showPageSetupDialog(null)) {
						pl = job.getJobSettings().getPageLayout();
						printHeight = pl.getPrintableHeight();
						aGT.setPrintHeight(printHeight);
						printWidth = pl.getPrintableWidth();
						aGT.setPrintWidth();
					}
				}
		    	aGT.setScale(0.5);
		    	par.setScale(0.8);
		    	Parent root = new Parent(rootID, 0, 0, aGT, null );
		    	String name = p.getFirstName() + " " + p.getLastName();
		    	title = "Ahnentafel for " + name; 
		    	n = root.getRoot();
				break;
			case descend:				// constructs descending node
		    	dGT = new DescGenoType(G, par, true );
				job = PrinterJob.createPrinterJob();
				if (job != null && job.showPrintDialog(null)){
					if (job.showPageSetupDialog(null)) {
						pl = job.getJobSettings().getPageLayout();
						printHeight = pl.getPrintableHeight();
						dGT.setPrintHeight(printHeight);
						printWidth = pl.getPrintableWidth();
						dGT.setPrintWidth();
					}
				}
		    	dGT.setScale(0.5);
		    	par.setScale(0.8);
		    	Group Root = new Group();
		    	new Child(rootID, 0, dGT, Root, 0, 0 );
		    	name = p.getFirstName() + " " + p.getLastName();
		    	title = "Descendants of " + name; 
		    	n = Root;
				break;
		}
		render (n);
	}

	private void render(Group n) {
		// renders node n to the printer
		Font ft = null, ff = null;
		Object[] index = null;
		boolean success = false;
		switch (t) {
			case ascend: 	ft = aGT.getTitleFont();
							ff = aGT.getFooterFont();
						    index = aGT.sIndex();
				break;
			case descend: 	ft = dGT.getTitleFont();
							ff = dGT.getFooterFont();
						    index = dGT.sIndex();
				break;
		}
		Text tFooter = null;
		Text t1 = null;
		Text t2 = null;
		String footer;
		Double scroll = 0.0;
		double dTemp;
		VBox container = new VBox();
		container.getChildren().add(n);
		nodeHeight = n.getLayoutBounds().getHeight();
		Text t = new Text(title);
		t.setFont(ft);
		double titleWidth = t.getBoundsInParent().getWidth();
		double xOffset = (printWidth - titleWidth)/2;
		t.setX(xOffset);
		t.setY(10.0);
		n.getChildren().add(t);
		Integer page = 1;
		while (scroll < nodeHeight + 30) {
			footer = title + "; page " + page;
			tFooter = new Text(footer);
			tFooter.setFont(ff);
			tFooter.setX(printWidth - tFooter.getBoundsInParent().getWidth());
			tFooter.setY(scroll + printHeight - 20.0);
			n.getChildren().add(tFooter);
			success = job.printPage(pl, container);
			scroll += printHeight;
			n.setTranslateY(-scroll);
			page++;
			//System.out.println(scroll.toString());
		}
		container = null;
	    double lineIncrement = 15.0;
	    Integer linesPerPage = (int) (printHeight/lineIncrement);
	    Integer lc = 0;
	    Integer cc = 0;
	    Integer plc = 0;
	    double dWidth = 0;
	    n = new Group();
	    Font indexFont = new Font(8.0);
	    for (Object o: index) {
	    	Item i = (Item)o;
	    	dTemp = plc * lineIncrement + 30;
	    	t1 = new Text(i.getKey());
	    	t1.setFont(indexFont);
	    	t2 = new Text(i.getValue());
	    	t2.setFont(indexFont);
	    	t1.setY(dTemp);
	    	t2.setY(dTemp);
	    	dWidth = t2.getBoundsInParent().getWidth();
	    	if (cc.equals(0)) {
	    		t1.setX(0.0);
	    		dTemp = printWidth / 2 - dWidth - 40;
	    		t2.setX(dTemp);
	    	} else {
	    		t1.setX(printWidth/2);
	    		dTemp = printWidth - dWidth;
	    		t2.setX(dTemp);
	    	}
	    	n.getChildren().addAll(t1, t2);
	    	lc++;
	    	cc = (lc / linesPerPage) % 2;
	    	plc = lc % linesPerPage;
	    	if(plc.equals(0) && cc.equals(0)) {
				success = job.printPage(pl, n);
	    		n = new Group();
	    	}
	    }
		success = job.printPage(pl, n);
	    if (success) {
	        job.endJob();
	    }
	}

}
