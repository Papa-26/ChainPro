package com.papaworx.cpro.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.prefs.Preferences;
import com.papaworx.cpro.MainClass;
import java.util.prefs.Preferences;

@SuppressWarnings("rawtypes")
public class D_Parameters {
	public StringProperty DB_URL;
	public StringProperty USER;
	public StringProperty PASS;
	public StringProperty DEPOSIT;
	public StringProperty STARTPERSON;
	public StringProperty CUTOFF;
	public StringProperty IMAGEVIEWER;
	public StringProperty PDFVIEWER;
	private Double scale = 1.0;
	private  Preferences prefs;
	private MainClass myMain;

	public D_Parameters(MainClass _myMain) {
		myMain = _myMain;
		prefs = myMain.getPrefs();
		DB_URL = new SimpleStringProperty(prefs.get("DB_URL", "jdbc:mariadb://192.168.2.24:3306/ChainPro"));
		USER = new SimpleStringProperty(prefs.get("USER", "Genealogist"));
		PASS = new SimpleStringProperty(prefs.get("PASS", "A1AwI17zo3EMyPY;X"));
		DEPOSIT = new SimpleStringProperty(prefs.get("DEPOSIT", "/mnt/ChainPro/ChainPro_Docs"));
		STARTPERSON = new SimpleStringProperty(prefs.get("STARTPERSON", "@I00644@"));
		CUTOFF = new SimpleStringProperty(prefs.get("CUTOFF", "1950"));
		IMAGEVIEWER = new SimpleStringProperty(prefs.get("IMAGEVIEWER", "eog"));
		PDFVIEWER = new SimpleStringProperty(prefs.get("PDFVIEWER","evince"));
	}
	
	public void setScale(Double s) {
		scale = s;
	}

	public  StringProperty getServer() {
		return DB_URL;
	}
	
	public  StringProperty getUser() {
		return USER;
	}
	
	public  StringProperty getPassword() {
		return PASS;
	}

	public  StringProperty getDeposit() {
		return DEPOSIT;
	}

	public  StringProperty getCutOff() {
		return CUTOFF;
	}
	
	public void saveURL (String s) {
		prefs.put("DB_URL", s);
	}
	
	public void saveUSER(String s) {
		prefs.put("USER",  s);
	}

	public void savePASS (String s) {
		prefs.put("PASS", s);
	}

	public void saveDEPOSIT (String s) {
		prefs.put("DEPOSIT", s);
	}

	public void saveSTARTPERSON (String s) {
		prefs.put("STARTPERSON", s);
	}

	public void saveCUTOFF (String s) {
		prefs.put("CUTOFF", s);
	}
	
	public void saveImgViewer(String s) {
		prefs.put("IMAGEVIEWER", s);
	}
	
	public void savePdfViewer(String s) {
		prefs.put("PDFVIEWER", s);
	}

	public ImageView getIV (String name) {
		InputStream is;
		Image img = null;
		is = myMain.getClass().getResourceAsStream("/com.papaworx.cpro/" + name);
		try {
			assert is != null;
			img = new Image(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageView iv = new ImageView (img);
		iv.setScaleX(scale);
		iv.setScaleY(scale);
		return iv;
	}
	
}
