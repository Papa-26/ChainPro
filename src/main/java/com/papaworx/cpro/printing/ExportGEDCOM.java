package com.papaworx.cpro.printing;

import com.papaworx.cpro.utilities.GConnection;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExportGEDCOM {
    FileWriter fw;
    File fOutput;
    Boolean bAsc;
    GConnection gc;
    ArrayList[] gcRecords;

    public ExportGEDCOM(GConnection _gc, Stage _myStage, Boolean _bAsc, String _sID){
        gc = _gc;
        Stage myStage = _myStage;
        bAsc = _bAsc;
        String sStartPerson = _sID;
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Save GEDCOM File");
            fOutput = fc.showSaveDialog(myStage);
            if (!fOutput.equals(null))
                fw = new FileWriter(fOutput);
        } catch (Exception e){
            //TODO handle exception
        }

    }

    public void doParents (String sID, String sFAM){

    }

    public void doChildren (String sID, String sFAM){

    }

    public void doPerson (String sID, String sFAM){

    }

    public void doFamily(String sID, String sFAM){

    }

    public void doDocument(String sDOC){

    }

    public void dump() {
        String SQL = "SELECT * FROM GEDCOM;";
        ArrayList<String> gcRecords = gc.getRecords(SQL);
        Object sRecord;
        for (String s : gcRecords)
            try {
                fw.write(s + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                e = null;
            }

    }
}
