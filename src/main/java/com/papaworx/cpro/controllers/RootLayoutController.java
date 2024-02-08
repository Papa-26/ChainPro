package com.papaworx.cpro.controllers;

import com.papaworx.cpro.structures.Relative;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.papaworx.cpro.MainClass;

public class RootLayoutController {

    @FXML
    private MenuItem miExit;
    @FXML
    private MenuItem miFindByName;
    @FXML
    private MenuItem miFindByYear;
    @FXML
    private MenuItem miFindByID;
    @FXML
    private MenuItem miFindByPlace;
    @FXML
    private MenuItem miFindHolocaust;
    @FXML
    private MenuItem miAddDocument;
    @FXML
    private MenuItem miDeletePresent;
    @FXML
    private MenuItem miUnlinkPresent;
    @FXML
    private MenuItem miAddPerson;
    @FXML
    private MenuItem miAddDaughter;
    @FXML
    private MenuItem miAddSon;
    @FXML
    private MenuItem miAddSister;
    @FXML
    private MenuItem miAddBrother;
    @FXML
    private MenuItem miAddMother;
    @FXML
    private MenuItem miAddFather;
    @FXML
    private MenuItem miAddSpouse;
    @FXML
    private MenuItem miService;
    @FXML
    private MenuItem miFixSex;
    @FXML
    private MenuItem miTestParents;		// actually, Diagnostics
    @FXML
    private MenuItem miDescendingTree;
    @FXML
    private MenuItem miAscendingTree;
    @FXML
    private MenuItem miTestPrinter;
    @FXML
    private MenuItem miClean;
    @FXML
    private MenuItem miPrintAscending;
    @FXML
    private MenuItem miPrintDescending;
    @FXML
    private MenuItem miExportGEDCOM;
    @FXML
    private MenuItem miFixBlank;
    @FXML
    private Pane bg;

    // Reference to the MainClass application.

    private Stage stage;
    private MainClass mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    @FXML
    private void initialize(){
        miExit.setOnAction((event) -> {mainApp.cleanExit();});
        miFindByID.setOnAction((event) -> {mainApp.findPerson("ID");});
        miFindByName.setOnAction((event) -> {mainApp.findPerson("Name");});
        miFindByYear.setOnAction((event) -> {mainApp.findPerson("Year");});
        miFindByID.setOnAction((event) -> {mainApp.findPerson("ID");});
        miFindByPlace.setOnAction((event) -> {mainApp.findPerson("Place");});
        miFindHolocaust.setOnAction((event) -> {mainApp.findPerson("Holocaust");});
        miAddDocument.setOnAction((event) -> {mainApp.addDocument();});
        miAddPerson.setOnAction((event) -> {mainApp.createRelative(Relative.NONE, null);});
        miAddDaughter.setOnAction((event) -> {mainApp.createRelative(Relative.DAUGHTER, false);});
        miAddSon.setOnAction((event) -> {mainApp.createRelative(Relative.SON, true);});
        miAddSister.setOnAction((event) -> {mainApp.createRelative(Relative.SISTER, false);});
        miAddBrother.setOnAction((event) -> {mainApp.createRelative(Relative.BROTHER, true);});
        miAddMother.setOnAction((event) -> {mainApp.createRelative(Relative.MOTHER, false);});
        miAddFather.setOnAction((event) -> {mainApp.createRelative(Relative.FATHER, true);});
        miAddSpouse.setOnAction((event) -> {mainApp.createRelative(Relative.SPOUSE, null);});
        miDeletePresent.setOnAction((event) -> {mainApp.deletePerson();});
        miUnlinkPresent.setOnAction((event) -> {mainApp.unlinkPerson();});
        miService.setOnAction((event) -> {mainApp.preferences("");});
        miDescendingTree.setOnAction((event) -> {mainApp.showDescTree();});
        miAscendingTree.setOnAction((event) -> {mainApp.showAscTree();});
        miFixSex.setOnAction((event) -> {mainApp.handleGender();});
        miTestParents.setOnAction((event) -> {handleDiagnostics();});
        miPrintAscending.setOnAction((event) -> {mainApp.printAscending();});
        miPrintDescending.setOnAction((event) -> {mainApp.printDescending();});
        miExportGEDCOM.setOnAction((event) -> {mainApp.export();});
    }

    public void setStage (Stage s) {
        stage = s;
    }

    public void setMainApp(MainClass _mainApp) {
        mainApp = _mainApp;
    }
    private void handleDiagnostics(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Diagnostic output file");
        File f = fc.showSaveDialog(stage);
        if (f != null) {
            PrintStream ps = null;
            try {
                ps = new PrintStream(f);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //mainApp.Diagnostics(ps);
        }
    }
}