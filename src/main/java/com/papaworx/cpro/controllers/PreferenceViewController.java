package com.papaworx.cpro.controllers;

import java.io.File;

import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.model.D_Parameters;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

//import com.papaworx.cpro.model.*;

public class PreferenceViewController {
	@FXML
	private TextField db_url;
	@FXML
	private TextField user;
	@FXML
	private TextField pass;
	@FXML
	private TextField deposit;
	@FXML
	private TextField startPerson;
	@FXML
	private TextField cutoff;
	@FXML
	private TextField imageViewer;
	@FXML
	private TextField pdfViewer;
	@FXML
	private TextField wrdViewer;
	@FXML
	private MenuItem miDepositLocation;
	@FXML
	private MenuItem miSave;
	@FXML
	private MenuItem miCancel;
	@FXML
	private MenuItem miExit;
	@FXML
	private CheckBox cbVerbose;
	
	private Stage dialogStage;
	//private model.Parameters P;
	private MainClass mainApp;
	private Boolean bVerbose;

	private D_Parameters P;

    @FXML 
    private void initialize() {
    }

    public void setMainApp(MainClass _mainApp, D_Parameters par, Scene sc) {
        P = par;
        mainApp = _mainApp;
        // Add observable list data to the table
    }
    
	public void showPreference (String sMessage){
		//startup code
		db_url.textProperty().bindBidirectional(P.DB_URL);
		user.textProperty().bindBidirectional(P.USER);
		deposit.textProperty().bindBidirectional(P.DEPOSIT);
		pass.textProperty().bindBidirectional(P.PASS);
		startPerson.textProperty().bindBidirectional(P.STARTPERSON);
		cutoff.textProperty().bindBidirectional(P.CUTOFF);
		imageViewer.textProperty().bindBidirectional(P.IMAGEVIEWER);
		pdfViewer.textProperty().bindBidirectional(P.PDFVIEWER);
		miExit.setVisible(true);
		miExit.setOnAction((event) -> {System.exit(1);});
		miCancel.setOnAction((event) ->{close();});
		miSave.setOnAction((event) ->{handleSave();});
		miDepositLocation.setOnAction((event) ->{handleDeposit();});
		bVerbose = mainApp.getVerbosity();
		cbVerbose.setSelected(bVerbose);
	}	

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    private void close() {
    	dialogStage.close();
    }
    
    private void handleDeposit() {
    	DirectoryChooser dC = new DirectoryChooser();
    	dC.setTitle("Set Location of Document Files");
    	File filePath = dC.showDialog(dialogStage);
    	deposit.setText(filePath.toString());  	
    }
    
    private void handleSave() {
    	P.saveURL(db_url.getText());
    	P.saveUSER(user.getText());
    	P.savePASS(pass.getText());
    	P.saveSTARTPERSON(startPerson.getText());
    	P.saveCUTOFF(cutoff.getText());
    	P.saveDEPOSIT(deposit.getText());
    	P.saveImgViewer(imageViewer.getText());
    	P.savePdfViewer(pdfViewer.getText());
    	bVerbose = cbVerbose.isSelected();
    	mainApp.setVerbosity(bVerbose);
    	dialogStage.close();
    }
}
