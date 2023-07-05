package com.papaworx.cpro.controllers;

import java.io.File;

import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.utilities.GConnection;
import com.papaworx.cpro.model.Document;
import com.papaworx.cpro.model.Person;

import javafx.fxml.*;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DocumentViewController {

	@FXML
	private TextField fileName;
	@FXML
	private TextField title;
	@FXML
	private TextArea comment;
	@FXML
	private MenuItem miFileSearch;
	@FXML
	private MenuItem miInternalFileSearch;
	@FXML
	private MenuItem miSaveDoc;
	@FXML
	private MenuItem miSaveGlobal;
	@FXML
	private MenuItem miDelete;
	@FXML
	private MenuItem miCancel;
	@FXML
	private ListView<DropLabel> selector;
	
	private Stage dialogStage;
	private String filePath;
	private Person p;
	private GConnection G;
	private long currentDocPointer = 0;
	
    @FXML
    private void initialize() {
    	miFileSearch.setOnAction((event) -> {getFile();});
    	miSaveDoc.setOnAction((event) -> {this.DocumentHandler();});
    	miDelete.setOnAction((event) -> {deleteDocument();});
    	miCancel.setOnAction((event) -> {this.close();});
    	comment.setWrapText(true);
		selector.setOnMouseClicked((event) ->{handleSelection();});
		miInternalFileSearch.setOnAction((event) -> {getInternalFile();});
		miSaveGlobal.setOnAction((event) -> {this.saveGlobalDoc();});
    }
    
    public void setMainApp(GConnection g) {
        G = g;
    }
    
     public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void close() {
    	dialogStage.close();
    }
    
    public void show(Person p) {
    	this.p = p;
		selector.setItems(p.getDocs());
    }
    
    private void getFile() {
    	
    	final FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Resource File");
    	final File selectedFile = fileChooser.showOpenDialog(dialogStage); 
    	filePath = selectedFile.getAbsolutePath();
    	fileName.setText(filePath);
    }
    
    private void DocumentHandler() {
    	Document doc = new Document(G, title.getText(), null, fileName.getText(), comment.getText(), false);
    	p.addDocument(doc);
    	this.close();
    }
    
    private void saveGlobalDoc() {
   	Document doc = new Document( G, title.getText(),  null,  fileName.getText(),  comment.getText(), true);
    doc.completeGlobalDocument(G);
    }

    private void handleSelection() {
    	if (selector.selectionModelProperty() == null)
    		return;
    	DropLabel selected = selector.selectionModelProperty().get().getSelectedItem();
    	if(selected == null)
    		return;
    	currentDocPointer = Long.parseLong(selected.Root);
    	Document doc = p.getDocument(currentDocPointer);
    	title.setText(doc.getTitle());
    	fileName.setText(doc.getAddress());
    	comment.setText(doc.getNote());
    }
    
    private void deleteDocument() {
    	if (currentDocPointer < 1)
    		return;
    	G.deleteNodeTree(currentDocPointer);
    	fileName.clear();
    	title.clear();
    	comment.clear();
    	close();
    }
    
    private void getInternalFile() {
    	String depository = G.getDeposit();
    	final FileChooser fileChooser = new FileChooser();
    	File initialDirectory = new File(depository);
    	fileChooser.setTitle("Open Resource File");
    	fileChooser.setInitialDirectory(initialDirectory);
    	final File selectedFile = fileChooser.showOpenDialog(dialogStage);
    	if (selectedFile != null) {
	    	filePath = selectedFile.getAbsolutePath();
	    	fileName.setText(filePath);
    	}
    	
    }
}
