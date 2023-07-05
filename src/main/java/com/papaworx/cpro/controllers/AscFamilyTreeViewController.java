package com.papaworx.cpro.controllers;

import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.genTree.Parent;
import com.papaworx.cpro.genTree.AscGenoType;
import com.papaworx.cpro.utilities.GConnection;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.model.Person;

public class AscFamilyTreeViewController {
	private Stage dialogStage;
	private MainClass mainApp;
	private D_Parameters par;
	private GConnection G;

	@FXML
	private MenuItem miClose;

	@FXML
	private ScrollPane screen;
	
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

    public void showTree(Person p) {
    	AscGenoType aGT = new AscGenoType(G, screen, par, false);
    	aGT.setController(this);
    	String rootID = p.getPersonID();
    	Parent root = new Parent(rootID, 0, 0, aGT, null );
    	miClose.setOnAction((event) -> close());
    	screen.setContent(root.getRoot());
    }
    
    public void handler( String personID) {
    	mainApp.changePerson(personID);
    	close();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void close() {
    	dialogStage.close();
    }
}
