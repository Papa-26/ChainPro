package com.papaworx.cpro.structures;
import com.papaworx.cpro.structures.Source;
import com.papaworx.cpro.utilities.GConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Source {
	private Connection Con;
	private GConnection G;
	private Integer recCounter;
	public String health = "healthy";

	public Source(GConnection g){
		G = g;
		Con = G.con();
	}

	protected enum FamPart {
		Siblings, Parents, Children, Spouses, Names, BIO, DOCS, FAMNAM, GNAME, PLACE, YEAR, HOLO
	}

	protected ObservableList<DropLabel> getList (FamPart fam, String argument) {
		ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		List <DropLabel> xList = new ArrayList<DropLabel>();
		recCounter = 0;
		switch (fam) {
			case Siblings:
					sql = "call getSiblings('" + argument + "');";
				break;
			case Parents:
					sql = "call getParents('" + argument + "');";
				break;
			case Children:
					sql = "call getOChildren('" + argument + "');";
				break;
			case Spouses:
					sql = "call getSpouses('" + argument + "');";
				break;
			case Names:
					String submitName = "%/" + argument + "/";
					sql = "call getNames('" + submitName + "')";
				break;
			case BIO:
					sql = "call getBio('" + argument + "');";
				break;
			case DOCS:
					sql = "call getDocs('" + argument + "');";
				break;
			case FAMNAM:
				sql = "SELECT * FROM FAMILIES WHERE LAST LIKE '" + argument + "%';";
				break;
			case GNAME:
				sql = "call getNames('%/"+ argument.trim() + "/');";
				break;
			case PLACE:
				String sa = "%" + argument.trim() + "%";
				sql = "call getByPlace('" + sa + "')";
				break;
			case YEAR:
				sa = argument.trim();
				sql = "call getByYear('" + sa + "')";
				break;
			case HOLO:
				sql = "call getHolocaust()";
		}
		try {
	      stmt = Con.createStatement();
	      rs = stmt.executeQuery(sql);
	      while(rs.next()){
	    	  DropLabel l = new DropLabel();
	    	  switch (fam) {
	    		  case BIO:
			    		  l.setRoot(rs.getString("TAG1").trim() + "_" + rs.getString("TAG2").trim());
			    		  l.setText(rs.getString("VALUE"));
	    			  break;
	    		  case DOCS:
				    	  l.setRoot(rs.getString("ROOT"));
				    	  if (rs.getString("TEXT") != null)
				    		  l.setText(rs.getString("TEXT").trim());
	    			  break;
	    		  case Spouses:
			    	  l.setRoot(rs.getString("GC_ROOT_OBJECT"));
			    	  l.setText(rs.getString("FIRST").trim() + " (" + rs.getString("LAST") + ")" );
			    	  l.setExtra(rs.getString("EXTRA"));
	    			  break;
	    		  case FAMNAM:
	    			  l.setText(rs.getString("LAST"));
	    			  break;
	    		  case GNAME:
	    			  l.setRoot(rs.getString("GC_ROOT_OBJECT"));
	    			  l.setText(rs.getString("FIRST"));
	    			  break;
	    		  case PLACE:
	    		  case YEAR:
	    		  case HOLO:
	    			  l.setText(rs.getString("TEXT"));
	    			  l.setRoot(rs.getString("GC_ROOT_OBJECT"));
	    			  recCounter++;
	    			  break;
	    		  default:
				    	  l.setRoot(rs.getString("GC_ROOT_OBJECT"));
				    	  l.setText(rs.getString("FIRST").trim() + " (" + rs.getString("LAST") + ")");
	    			  break;
	    	  }
	    	  xList.add(l);
	      }

		} catch (SQLException se) {

			showStackTrace("Query read", se);
		}

	    //STEP 6: Clean-up environment
	    try {
	  	  rs.close();
		  stmt.close();
		  ObservableList<DropLabel> observableList = FXCollections.observableList(xList);
		  return observableList;
		} catch (SQLException e) {
			  e.printStackTrace();
		} finally {
			  rs = null;
			  stmt = null;
		}
	    return null;
	}

	public ObservableList<DropLabel> getChildren(String root) {
		return getList( FamPart.Children, root);
	}

	public ObservableList<DropLabel> getParents(String root) {
		return getList( FamPart.Parents, root);
	}

	public ObservableList<DropLabel> getSpouses(String root) {
		return getList( FamPart.Spouses, root);
	}

	public ObservableList<DropLabel> getSiblings(String root) {
		return getList( FamPart.Siblings, root);
	}

	public ObservableList<DropLabel> getNames(String root) {
		return getList( FamPart.Names, root);
	}

	public ObservableList<DropLabel> getBio(String root) {
		return getList( FamPart.BIO, root);
	}

	public ObservableList<DropLabel> getDocs(String root) {
		return getList( FamPart.DOCS, root);
	}

	public ObservableList<DropLabel> getFamNames(String arg) {
		return getList( FamPart.FAMNAM, arg);
	}

	public ObservableList<DropLabel> getFirstNames(String arg) {
		return getList( FamPart.GNAME, arg);
	}

	public ObservableList<DropLabel> getByPlace(String arg) {
		return getList( FamPart.PLACE, arg);
	}
	public ObservableList<DropLabel> getByYear(String arg) {
		return getList( FamPart.YEAR, arg);
	}
	public ObservableList<DropLabel> getHolocaust(String arg) {
		return getList( FamPart.HOLO, arg);
	}

    public void showStackTrace(String sLocation, Exception e)
    {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Exception Dialog");
    	alert.setHeaderText("Look, an Exception Dialog");
    	alert.setContentText(sLocation);


    	// Create expandable Exception.
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	String exceptionText = sw.toString();

    	Label label = new Label("The exception stacktrace was:");

    	TextArea textArea = new TextArea(exceptionText);
    	textArea.setEditable(false);
    	textArea.setWrapText(true);

    	textArea.setMaxWidth(Double.MAX_VALUE);
    	textArea.setMaxHeight(Double.MAX_VALUE);
    	GridPane.setVgrow(textArea, Priority.ALWAYS);
    	GridPane.setHgrow(textArea, Priority.ALWAYS);

    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(label, 0, 0);
    	expContent.add(textArea, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	alert.getDialogPane().setExpandableContent(expContent);

    	alert.showAndWait();
    }

    public void fixBlanks() {
    	String sql = "SELECT GC_NODE, GC_FIXED FROM SPOTTY ORDER BY GC_NODE;";
    	Statement stmt = null;
	    ResultSet rs = null;
	    Double dNode = 0D;
	    String sFixed = null;
	    Integer iCounter = 0;
		try {
			stmt = Con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			while(rs.next()){
				iCounter++;
				dNode = rs.getDouble("GC_NODE");
				sFixed = rs.getString("GC_FIXED");
				//System.out.println(dNode + ";  " + sFixed);
				sql = "UPDATE GEDCOM SET GC_VALUE = '" + sFixed + "' WHERE GC_NODE = " + dNode + ";";
				//System.out.println(sql);
			  }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      System.out.println(iCounter + " files fixed.");
    	return;
    }

}
