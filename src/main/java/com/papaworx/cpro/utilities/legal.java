package com.papaworx.cpro.utilities;

import java.util.ArrayList;
import java.util.List;
import com.papaworx.cpro.structures.DropLabel;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class legal {
	
	private legal() {
		// this is a static utility class to test for the legality of specific family connections
		// all methods need to be static and not depend on class fields
		// each method return 'true' if the relation is legal
		// each method needs be handed a gConnection, the roots of the two persons an potential genders
	}
	
	public static Boolean legalChild (GConnection g, String child, String family){
		//System.out.println(child + "; " + family);
    	List<DropLabel> list = new ArrayList<DropLabel>();
		list = g.relations(child.trim(), "FAMC", "CHIL");
		Alert alert = null;
		if (!list.isEmpty()) {
			// the child already is in another FAMC
			if (!list.get(0).getExtra().equals(family)) {
				alert = new Alert(AlertType.INFORMATION, "The person is already in another family!");
				alert.showAndWait().ifPresent(response -> {});
				return false;
			}
		}

		return true;		// dummy
	}

	public static Boolean legalParent (GConnection g, String child, String family) {
		// test if already has parent of a
		return true;	//dummy
	}
}
