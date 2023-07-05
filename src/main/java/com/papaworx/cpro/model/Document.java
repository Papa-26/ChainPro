package com.papaworx.cpro.model;

import java.nio.file.*;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.papaworx.cpro.utilities.GConnection;

public class Document {
	private long DocParent;		// node number of object record - level 1 if in person
								// or level 0 if global object
	private long AddressID;		// node number of location record - level 2
	private long NoteID;		// node number of note record - level 2
	private long TitleID;		// node number of title record
	private String sForm = null;
	private String Title;
	private String Address;
	private String Note;
	private List<String> Lines;
	private GConnection G;
	boolean bGlobal;

	public Document(GConnection g, String title, String form, String path, String note, boolean bGlobal) {
		G = g;
		Title = title;
		sForm = form;
		Address = path;
		Note = note;
		this.bGlobal = bGlobal;
	}

	public Document(GConnection g, long docParent, boolean bGlobal) {
		G = g;
		this.bGlobal = bGlobal;
		DocParent = docParent;
	}

	public long getParent () {
		return DocParent;
	}

	public String getTitle() {
		return Title;
	}

	public String getAddress () {
		return Address;
	}

	public List<String> getLines() {
		return Lines;
	}

	public long getAddressID() {
		return AddressID;
	}

	public long getTitleID() {
		return TitleID;
	}

	public long getNoteID(){
		return NoteID;
	}

	public String getForm() {
		return sForm;
	}

	public String getNote() {
		return Note;
	}

	public void ClearLines() {
		Lines = null;
	}

	public void addLine (String line) {
		Lines.add(line);
	}

	public void setTitle (String title) {
		Title = title;
	}

	public void setAddress (String address) {
		Address = address;
	}

	public String completeGlobalDocument (GConnection g) {
		G = g;
		// create media root
		String mediaRoot = G.getNextMultimediaRoot();
		G.setRoot(mediaRoot);
		G.saveString("Media", mediaRoot);
		// save document
		G.saveString("MediaTitle", Title);
		String ss = transferFile(Address);
		G.saveString("MediaFile", ss);
		sForm = getForm(ss);
		G.saveString("MediaForm", sForm);
		G.saveString("MediaNote", Note);
		// update media root source
		return mediaRoot;
	}

	public void completeDocument(String root) {
		long oNode, oNode0;
		String ss = null;
		G.setRoot(root);
		oNode0 = G.saveObjectString("pMedia", null, 1, 0);
		G.saveObjectString("pMediaTitle", Title, 2, oNode0);
		if ((Address.indexOf("http") >= 0) || (Address.indexOf("HTTP") >= 0))
			ss = Address;
		else
			ss = transferFile(Address);
		oNode = G.saveObjectString("pMediaFile", ss, 2, oNode0);
		sForm = getForm(ss);
		oNode = G.saveObjectString("pMediaForm", sForm, 3, oNode);
		oNode = G.saveObjectString("pMediaNote", Note, 2, oNode0);
	}

	private String getForm (String fileName) {
		String[] lines = fileName.toString().split("\\.");
		Integer ll = lines.length;
		String ext = lines[ll-1];
		String form = null;
		switch(ext) {
			case "jpg":
			case "jpeg":
			case "png":
			case "gif":
			case "bmp":
				form = "IMG";
				break;
			case "html":
			case "php":
				form = "URL";
				break;
			case "DOC":
			case "DOCX":
				form = "WORD";
				break;
			case "PDF":
				form = "PDF";
		}
		return form;
	}

	private String transferFile(String address) {
		String depository = G.getDeposit();
		String pathSeparator = File.separator;
		Path source = Paths.get(Address);
		String ss = source.getFileName().toString().replace(' ', '_');
		Path target = Paths.get(depository + pathSeparator + ss);
		try {
			Files.copy(source, target, REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ss;
	}
}
