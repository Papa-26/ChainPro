package com.papaworx.cpro.controllers;

import java.util.stream.Collectors;

import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.utilities.GConnection;
import com.papaworx.cpro.structures.Source;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class FinderViewController {

    private MainClass mainApp;
	private Stage dialogStage;
	private Boolean bFirstPass = true;
	private Source s;
	private ObservableList<DropLabel> alpha;
	private ObservableList<DropLabel> beta = null;
	private String type;
	private Scene scene;

	protected enum FamPart {
		Siblings, Parents, Children, Spouses, Names, BIO, DOCS, FAMNAM, GNAME, PLACE, YEAR, HOLO
	}

	@FXML
	private Label title;
	@FXML
	private TextField term;
	@FXML
	private ListView <DropLabel> choice;
	@FXML
	private Button search;

	@FXML
	private void handleMouseClick(MouseEvent arg0) {
		DropLabel result = choice.getSelectionModel().getSelectedItem();
		if (result==null)
			return;
		String id;
		switch (type) {
			case "Name":
				if(!bFirstPass)
				{
					String sID = result.Root;
					mainApp.setNext(sID);
					this.close();
				}
				id = result.Text;
				alpha = (ObservableList<DropLabel>)s.getNames(id);
				choice.setItems(null);
				choice.setItems((ObservableList<DropLabel>)alpha);
				choice.refresh();
				term.setText("");
				term.requestFocus();;
				scene.setCursor(Cursor.DEFAULT);
				bFirstPass = false;
				break;
		case "Place":
		case "Year":
				id = result.getRoot();
				mainApp.setNext(id);
				this.close();
 			break;
		case "Holocaust":
			id = result.getRoot();
			mainApp.setNext(id);
			this.close();
			break;
		}
	}

    // Reference to the MainClass application.

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    @FXML
    private void initialize() {
    }

    public void setMainApp(MainClass mainApp, Scene sc) {
        this.mainApp = mainApp;
        this.scene = sc;
        // Add observable list data to the table
    }

     public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void close() {
    	dialogStage.close();
    }

	public void search(GConnection g, String type) {
		// search view
		this.type = type;
    	s = new Source(g);
    	switch (type) {
			case "Name":
				search.setVisible(false);
				scene.setCursor(Cursor.WAIT);
				if (bFirstPass)
					alpha = s.getFamNames("");
				title.setText("Find by Name");
				choice.setItems((ObservableList<DropLabel>) alpha);
				choice.refresh();
				term.setFocusTraversable(true);
				term.textProperty().addListener(new ChangeListener<String>(){
					@Override
					public void changed(ObservableValue<? extends String> observable,
										String oldValue, String newValue) {
						if (!newValue.equals("") && (bFirstPass)) {
							alpha = s.getFamNames(newValue);
						} else {
							alpha = Filter(alpha, newValue);
						}
						choice.setItems((ObservableList<DropLabel>)alpha);
						choice.refresh();
					}
				});
				scene.setCursor(Cursor.DEFAULT);
				break;
    	case "ID":
    		term.setVisible(true);
    		choice.setVisible(false);
    		search.setVisible(true);
    		title.setText("Search by ID number");
    		search.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
        			String id = term.getText();
    				mainApp.setNext(id);
    				close();
                }
            });
    		break;
    	case "Place":
    		term.setVisible(true);
    		choice.setVisible(false);
    		search.setVisible(true);
    		title.setText("Search by Place");
    		scene.setCursor(Cursor.WAIT);
    		search.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    String place = term.getText();
                    choice.setVisible(true);
                    search.setVisible(false);
                    alpha = s.getByPlace(place);
                    choice.setItems((ObservableList<DropLabel>)alpha);
                }
            });
    		scene.setCursor(Cursor.DEFAULT);
    		break;
    	case "Year":
    		term.setVisible(true);
    		choice.setVisible(false);
    		search.setVisible(true);
    		title.setText("Search by Year");
    		scene.setCursor(Cursor.WAIT);
    		search.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    String year = term.getText();
                    choice.setVisible(true);
                    search.setVisible(false);
                    alpha = s.getByYear(year);
                    choice.setItems((ObservableList<DropLabel>)alpha);
                }
            });
    		scene.setCursor(Cursor.DEFAULT);
    		break;
    	case "Holocaust":
    		term.setVisible(true);
    		choice.setVisible(true);
    		search.setVisible(false);
    		title.setText("Search Holocaust");
    		scene.setCursor(Cursor.WAIT);
    		alpha = s.getHolocaust("");
	    	choice.setItems((ObservableList<DropLabel>)alpha);
	    	term.setFocusTraversable(true);
	    	term.textProperty().addListener(new ChangeListener<String>(){
	    		@Override
	    		public void changed(ObservableValue<? extends String> observable,
	    									String oldValue, String newValue) {
					ObservableList<DropLabel> filtered = FXCollections.observableList(alpha.stream().filter(u->u.Text.toUpperCase().startsWith(newValue.toUpperCase())).collect(Collectors.toList()));
					choice.setItems(filtered);
	    	    }
	    	});
	    	scene.setCursor(Cursor.DEFAULT);
    		break;
    	}
	}

	ObservableList<DropLabel> Filter (ObservableList<DropLabel> _alpha, String _term){
		beta = FXCollections.observableArrayList();
		if (_term.isEmpty())
			return _alpha;
		_term = _term.toLowerCase();
		String text = null;
		for (DropLabel a : _alpha){
			text = a.getText().toLowerCase();
			if (text.indexOf(_term)==0)
				beta.add(a);
		}
		return beta;
	}
}
