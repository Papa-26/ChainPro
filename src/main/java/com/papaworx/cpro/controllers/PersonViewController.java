package com.papaworx.cpro.controllers;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.utilities.GConnection;
import com.papaworx.cpro.model.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PersonViewController {

		@FXML
		private TextField personIDField;
		@FXML
		private TextField firstNameField;
		@FXML
		private TextField lastNameField;
		@FXML
		private TextField birthDateField;
		@FXML
		private TextField birthPlaceField;
		@FXML
		private TextField deathDateField;
		@FXML
		private TextField deathPlaceField;

		@FXML
		private ComboBox <DropLabel> parentsBox;
		@FXML
		private ComboBox <DropLabel> siblingsBox;
		@FXML
		private ComboBox <DropLabel> spousesBox;
		@FXML
		private ComboBox <DropLabel> childrenBox;
		@FXML
		private ComboBox <DropLabel> docsBox;
		
		@FXML
		private TextArea note;
		
		@FXML
		private RadioButton femaleButton;
		
		@FXML
		private RadioButton maleButton;
		
		
		@FXML
		private CheckBox holocaustCheck;
		
		@FXML
		private ImageView picture;
		
		@FXML
		private Button CFamButton;
		
		@FXML
		private Button SFamButton;

	    // Reference to the MainClass application.
		
		private String nextSpouse;
		private Boolean bJumpOnSpouse;
		private File f;
		private Person P;
		private GConnection G;
		private String displayedDocument = null;
		
		private enum FamForm {Spousal, Parental}
		
		private MainClass mainApp;

	    /*
	     * The constructor.
	     * The constructor is called before the initialize() method.
	     */
		
		
	    /*
	     * Is called by the MainClass application to give a reference back to itself.
	     * 
	     * @param mainApp
	     */
	    public void setMainApp(MainClass mainApp, GConnection g) {
	        this.mainApp = mainApp;
	        G = g;
	    }
	    
	    // Handle ComboBox events
	    
	    public void showPerson(Person p) {
	    	// ToDO: handle changed data
	    	P = p;
	    	bJumpOnSpouse = true;
	    	personIDField.textProperty().bindBidirectional(P.personID);
	    	personIDField.setEditable(false);
	    	firstNameField.textProperty().bindBidirectional(P.firstName);
	    	lastNameField.textProperty().bindBidirectional(P.lastName);
	    	birthDateField.textProperty().bindBidirectional(P.birthDate);
	    	birthPlaceField.textProperty().bindBidirectional(P.birthPlace);
	    	deathDateField.textProperty().bindBidirectional(P.deathDate);
	    	deathPlaceField.textProperty().bindBidirectional(P.deathPlace);
	    	maleButton.selectedProperty().bindBidirectional(P.isMale);
	    	femaleButton.setSelected(!maleButton.isSelected());
	    	holocaustCheck.selectedProperty().bindBidirectional(P.Holocaust);
	    	siblingsBox.setItems(p.getSiblings());
	    	siblingsBox.setEditable(false);
	    	spousesBox.setItems(p.getSpouses());
	    	spousesBox.setEditable(false);
	    	childrenBox.setItems(p.getChildren());
	    	childrenBox.setEditable(false);
	    	parentsBox.setItems(p.getParents());
	    	parentsBox.setEditable(false);
	    	parentsBox.setOnAction((event) -> {
	    		DropLabel selectedPerson = parentsBox.getSelectionModel().getSelectedItem();
	    		personSelectionHandler(selectedPerson.toValue());
	    	});
	    	siblingsBox.setOnAction((event) -> {
	    		DropLabel selectedPerson = siblingsBox.getSelectionModel().getSelectedItem();
	    		personSelectionHandler(selectedPerson.toValue());
	    	});
	    	spousesBox.setOnAction((event) -> {
	    		DropLabel selectedPerson = (DropLabel)spousesBox.getSelectionModel().getSelectedItem();
	    		nextSpouse = selectedPerson.toValue();
	    		if (bJumpOnSpouse)
	    			personSelectionHandler(nextSpouse);
	    		else {
	    			bJumpOnSpouse = true;
	    			spousesBox.setPromptText("Spouses");
	    			String sFam = selectedPerson.getExtra();
	    			showFamily(FamForm.Spousal, sFam);
	    		}
	    	});

	    	childrenBox.setOnAction((event) -> {
	    		DropLabel selectedPerson = childrenBox.getSelectionModel().getSelectedItem();
	    		personSelectionHandler(selectedPerson.toValue());
	    	});

	    	docsBox.setOnAction((event) -> {
	    		DropLabel selectedDocument = docsBox.getSelectionModel().getSelectedItem();
	    		documentSelectionHandler(p, selectedDocument.toValue());
	    	});

	    	docsBox.setItems(p.getDocs());
	    	//picture.setImage(null);
	    	note.setWrapText(true);
	    	note.textProperty().bindBidirectional(p.pRemarks);
	    	//note.appendText(p.getNote());
	    	if (!docsBox.getItems().isEmpty()) {
		    	String primaryPicture = docsBox.getItems().get(0).toValue();
		    	if (primaryPicture != null) {
			    	long iDocRoot = Long.parseLong(primaryPicture);
			    	String fn = p.getPicture(iDocRoot);
			    	displayedDocument = fn;
			    	if (fn != null) {
				    	f = new File(Paths.get(p.getDepository(), fn).toString());
				    	String ext = getFileExtension(fn).toUpperCase();
				    	switch (ext) {
					    	case "JPG":
					    	case "BMP":
					    	case "TIFF":
					    	case "GIF":
					    	case "PNG":
						    	Image image = new Image(f.toURI().toString());
						    	picture.setImage(image);
						    	break;
					    	case "PDF":
					    		placePDF();
					    		break;
				    		default:
				    			break;
				    	}
			    	}
		    	}
	    	}
	    	CFamButton.setOnAction((event) -> showFamily (FamForm.Parental, p.getCFamily()));
	    	SFamButton.setOnAction((event) -> {
	    		Integer n = spousesBox.getItems().size();
	    		if ( n > 1){
		    		bJumpOnSpouse = false;
		    		spousesBox.setPromptText("Select Spousal Family");
		    		bJumpOnSpouse = false;
	    		}
	    		else if (n == 1) {
	    			DropLabel d = spousesBox.getItems().get(0);
	    			showFamily (FamForm.Spousal, d.getExtra());
	    		}
	    	});
	    	
	    	picture.setOnMouseClicked((event) -> handleImageClick());
	    }
	    
	    private void 	handleImageClick()
	    {
	    	String docRoot = null;
	    	if (!docsBox.getItems().isEmpty()) {
	    		DropLabel selectedDocument = docsBox.getSelectionModel().getSelectedItem();
	    		if (selectedDocument == null)
	    			docRoot = docsBox.getItems().get(0).toValue();
	    		else
	    			docRoot = selectedDocument.toValue();
		    	if (docRoot != null) {
			    	String fn = displayedDocument;
			    	if (fn != null) {
				    	f = new File(Paths.get(P.getDepository(), fn).toString());
				    	String[] cmd = new String[2];
				    	String ext = getFileExtension(fn).toUpperCase();
				    	switch (ext) {
					    	case "JPG":
					    	case "BMP":
					    	case "TIFF":
					    	case "GIF":
					    	case "PNG":
						    	cmd[0] = G.getImageViewer();
						    	break;
					    	case "PDF":
					    		cmd[0] = G.getPdfViewer();
					    		break;
				    		default:
				    			mainApp.showBrowser(fn);
				    			break;
				    	}
				    	mainApp.showBrowser(f.toString());
			    		return;
			    	}
		    	}
	    	}
	    		return;		//no documents available
		}
	    
	    private void personSelectionHandler (String personID) {
	    	// ToDo: save changes and clean up
	    	// ToDo: switch to new person
	    	bJumpOnSpouse = true;
	    	mainApp.changePerson(personID);
	    }
	    
	    private void documentSelectionHandler (Person p, String docRoot) {
	    	// Load image
	    	long iDocRoot = Long.parseLong(docRoot);
	    	String fn = p.getPicture(iDocRoot);
	    	displayedDocument = fn;
	    	String ext = getFileExtension(fn).toUpperCase();
	    	switch (ext) {
		    	case "JPG":
		    	case "BMP":
		    	case "TIFF":
		    	case "GIF":
		    	case "PNG":
		    		try
		    		{
			    	f = new File(Paths.get(p.getDepository(), fn).toString());
		    		} catch (Exception e)
		    		{
		    			e.printStackTrace();
		    		}
			    	Image image = new Image(f.toURI().toString());
			    	picture.setImage(image);
			    	break;
		    	case "PDF":
		    		placePDF();
		    		break;
		    	default:				//URL
		    		mainApp.showBrowser(fn);
		    		break;
	    	}
	    }
	    
	    private void showFamily(FamForm f, String sFam) {
	    		mainApp.showFamilyViewDialog(sFam);
	    }
	    
	    private static String getFileExtension(String fileName) {
	        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
	        return fileName.substring(fileName.lastIndexOf(".")+1);
	        else return "";
	    }
	    
	    private void placePDF () {
	    	docsBox.hide();
			InputStream is = null;
			Image img = null;
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			is = cl.getResourceAsStream("Resources/pdfTag.png");
			try {
				img = new Image(is);
		    	picture.setImage(img);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public void clearFields()
	    {
	    	firstNameField.setText(null);
	    	lastNameField.setText(null);
	    	birthDateField.setText(null);
	    	birthPlaceField.setText(null);
	    	deathDateField.setText(null);
	    	deathPlaceField.setText(null);
	    	personIDField.setText(null);
	    }
}
