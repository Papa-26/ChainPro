package com.papaworx.cpro.controllers;

import java.util.List;

import javafx.collections.ObservableList;
import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.structures.DropLabel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class DetailViewController {

	@FXML
	private Label instruction;
	@FXML
	private ListView<DropLabel> selection;
	
	private MainClass mainApp;
	private Stage dialogStage;
	
	public void showDetail (String label, List<DropLabel> options){
		mainApp.selectionReturn(null);
		instruction.setText(label);
		selection.setItems((ObservableList<DropLabel>)options);
		selection.setOnMouseClicked((event) -> handleSelection());
	}
    @FXML 
    private void initialize() {
    }
    
    public void setMainApp(MainClass mainApp) {
        this.mainApp = mainApp;
        // Add observable list data to the table
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    private void close() {
    	dialogStage.close();
    }
    
    private void handleSelection() {
    	DropLabel selected = selection.selectionModelProperty().get().getSelectedItem();
    	mainApp.selectionReturn(selected);
    	this.close();
    }
}
