package com.papaworx.cpro.controllers;

	import javafx.fxml.FXML;
	import javafx.scene.control.ListView;
	import javafx.scene.control.MenuItem;
	import javafx.scene.control.TextArea;
	import javafx.scene.control.TextField;
	import javafx.stage.Stage;
	import com.papaworx.cpro.model.Family;

	public class FamilyViewController {
		@FXML
		private TextField familyIDField;
		@FXML
		private TextField spouse_aField;
		@FXML
		private TextField spouse_bField;
		@FXML
		private TextField engagementDateField;
		@FXML
		private TextField marriageDateField;
		@FXML
		private TextField separationDateField;
		@FXML
		private TextField divorceDateField;
		@FXML
		private TextField engagementPlaceField;
		@FXML
		private TextField marriagePlaceField;
		@FXML
		private TextField separationPlaceField;
		@FXML
		private TextField divorcePlaceField;
		@FXML
		private ListView <String> childrenListView;
		@FXML
		private TextArea remarksArea;
		@FXML
		private MenuItem saveMenu;
		@FXML
		private MenuItem cancelMenu;
		
		Family f;

	    // Reference to the MainClass application.
		private Stage dialogStage;

	    /**
	     * The constructor.
	     * The constructor is called before the initialize() method.
	     */

	    /**
	     * Initializes the controller class. This method is automatically called
	     * after the fxml file has been loaded.
	     */
	    @FXML
	    private void initialize() {
			cancelMenu.setOnAction((event) -> {exitClean();});
			saveMenu.setOnAction((event) -> {saveHandler();});
	    }

		public void showFamily (Family F){
			f = F;
			familyIDField.textProperty().bindBidirectional(f.IDField);
			spouse_aField.textProperty().bindBidirectional(f.Spouse_a);
			spouse_bField.textProperty().bindBidirectional(f.Spouse_b);
			engagementDateField.textProperty().bindBidirectional(f.engagementDate);
			engagementPlaceField.textProperty().bindBidirectional(f.engagementPlace);
			marriageDateField.textProperty().bindBidirectional(f.marriageDate);
			marriagePlaceField.textProperty().bindBidirectional(f.marriagePlace);
			separationDateField.textProperty().bindBidirectional(f.separationDate);
			separationPlaceField.textProperty().bindBidirectional(f.separationPlace);
			divorceDateField.textProperty().bindBidirectional(f.divorceDate);
			divorcePlaceField.textProperty().bindBidirectional(f.divorcePlace);
			remarksArea.textProperty().bindBidirectional(f.fRemarks);
			remarksArea.setWrapText(true);
			childrenListView.setItems( f.getChildren());
		}

	    /**
	     * Is called by the MainClass application to give a reference back to itself.
	     * 
	     * @param mainApp
	     */

	    public void setDialogStage(Stage dialogStage) {
	        this.dialogStage = dialogStage;
	    }
	    
	    public void close() {
	    	dialogStage.close();
	    }
	    
	    private void exitClean() {
	    	//System.out.println("Closing"); 
	    	close();
	    }
	    
	    private void saveHandler() {
	    	if (f.hasChanged())
	    	{
	    		f.complete();
	    	}
    		close();
	    }
	    
	}
	

