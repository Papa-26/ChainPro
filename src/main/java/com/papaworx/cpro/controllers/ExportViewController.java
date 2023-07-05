package com.papaworx.cpro.controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class ExportViewController {
	
	@FXML
	private TextField personStart;
	@FXML
	private TextField familyStart;
	@FXML
	private TextField filePath;
	@FXML
	private MenuItem miFileSearch;
	@FXML
	private MenuItem miExport;

    @FXML
    private void initialize() {
    	miFileSearch.setOnAction((event) -> {getFile();});
    	miExport.setOnAction((event) -> {export();});
    }
    
	private Stage dialogStage;

     public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    
    
    private void export() {
    	
    }
    
    private void getFile() {
    	 FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("GEDCOM Files", "*.ged"));
    	 File selectedFile = fileChooser.showOpenDialog(dialogStage);
    	 if (selectedFile != null) {
    	    System.out.println(selectedFile.getName());
    	 }

    }
}
