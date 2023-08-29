package com.papaworx.cpro.controllers;

import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.genTree.Child;
import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.utilities.GConnection;
import com.papaworx.cpro.genTree.DescGenoType;
import com.papaworx.cpro.model.Person;
import javafx.scene.SnapshotParameters;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;


public class DescFamilyTreeViewController {
	
	private Stage dialogStage;
	private MainClass mainApp;
	private D_Parameters par;
	private GConnection G;
	private Group root;

	@FXML
	private Button miClose;

	@FXML
	private ScrollPane screen;

	@FXML private MenuItem miPrint;
	@FXML
	public void initialize() {
    	// dummy
	}
	
    public void setMainApp(MainClass mainApp, GConnection g) {
    	G = g;
    	this.mainApp = mainApp;
    	par = mainApp.getPar();
        // Add observable list data to the table
    }
    
    public void showTree(Person p)
    {
    	DescGenoType dGT = new DescGenoType(G, par, false);
    	dGT.setController(this);
    	root = new Group();
    	String rootID = p.getPersonID();
    	new Child(rootID, 0, dGT, root, 50, 20 );
    	miClose.setOnAction((event) -> close());
    	screen.setContent(root);
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void close() {
    	dialogStage.close();
    }
	
    public void handler( String personID) {
    	mainApp.changePerson(personID);
    	close();
    }

	//TODO print DescFamilyTree
}
