import com.papaworx.cpro.MainClass;
import com.papaworx.cpro.model.*;
import com.papaworx.cpro.controllers.*;
import com.papaworx.cpro.structures.*;
import com.papaworx.cpro.genTree.*;
import com.papaworx.cpro.printing.*;
import com.papaworx.cpro.utilities.*;


module com.papaworx.cpro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires java.prefs;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    uses MainClass;
    uses RootLayoutController;
     uses AscFamilyTreeViewController;
    uses DescFamilyTreeViewController;
    uses DetailViewController;
    uses DocumentViewController;
    uses ExportViewController;
    uses FamilyViewController;
    uses FinderViewController;
    uses PersonViewController;
    uses PreferenceViewController;
    uses AscGenoType;
    uses Child;
    uses DescGenoType;
    uses Parent;
    uses Spouse;
    uses D_Parameters;
    uses Document;
    uses Family;
    uses Person;
    uses Index;
    uses Item;
    uses TreePrinter;
    uses Branch;
    uses DropLabel;
    uses FamGender;
    uses GcUnit;
    uses GRecord;
    uses Member;
    uses Source;
    uses CpBoolean;
    uses CpString;
    uses Diagnostics;
    uses FSource;
    uses FStringProperty;
    uses GConnection;

    opens com.papaworx.cpro to javafx.fxml;
    exports com.papaworx.cpro;
    exports com.papaworx.cpro.controllers;
    opens com.papaworx.cpro.controllers to javafx.fxml;
}