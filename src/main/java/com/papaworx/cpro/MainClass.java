package com.papaworx.cpro;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.papaworx.cpro.model.Family;
import com.papaworx.cpro.model.Person;
import com.papaworx.cpro.model.D_Parameters;
import com.papaworx.cpro.controllers.DescFamilyTreeViewController;
import com.papaworx.cpro.controllers.FamilyViewController;
import com.papaworx.cpro.controllers.FinderViewController;
import com.papaworx.cpro.controllers.PersonViewController;
import com.papaworx.cpro.controllers.PreferenceViewController;
import com.papaworx.cpro.controllers.DocumentViewController;
import com.papaworx.cpro.controllers.RootLayoutController;
import com.papaworx.cpro.controllers.AscFamilyTreeViewController;
import com.papaworx.cpro.controllers.DetailViewController;
import com.papaworx.cpro.utilities.GConnection;
import com.papaworx.cpro.structures.DropLabel;
import com.papaworx.cpro.structures.Relative;
import com.papaworx.cpro.utilities.Diagnostics;
import com.papaworx.cpro.printing.TreePrinter;
import com.papaworx.cpro.printing.TreePrinter.treeType;
import com.papaworx.cpro.printing.ExportGEDCOM;

@SuppressWarnings("ClassEscapesDefinedScope")
public class MainClass extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private Person p;	// original person
    private Person p2;	// added person
    private Family f;
    private D_Parameters par;
    private String nextPerson;
    private String currentPerson;
    private GConnection g;
    private DropLabel returnSelection;
    private AnchorPane personView;
    private Boolean bShow = true;
    private Scene scene;
    private String type1 = null;
    private String type2 = null;
    private String type3 = null;
    private String type4 = null;
    private Boolean bExtend = false;
    private String familyRoot = null;
    private String firstPersonID = null;
    private Boolean bAbort = false;
    private Boolean bVerbosity = false;
    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/RootLayout.fxml"));
        rootLayout = fxmlLoader.load();
        RootLayoutController controller = fxmlLoader.getController();
        scene = new Scene(rootLayout);
        primaryStage.setTitle("ChainPro_M 2.3.5");
        primaryStage.setScene(scene);
        controller.setMainApp(this);
        par = new D_Parameters(this);
        g = new GConnection(this, par, primaryStage);
        g.Load();
        p = new Person(g, "FIRST");
        showPersonView(p);
        primaryStage.show();
    }

     public static void main(String[] args) {

        launch();
    }


    public String findPerson(String name) {
        AnchorPane page = null;
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/FinderView.fxml"));
        try {
            page = loader.load();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("Find");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        assert page != null;
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        nextPerson = "empty";

        // Set the person into the controller.
        FinderViewController controller = loader.getController();
        controller.setMainApp(this, scene);
        controller.setDialogStage(dialogStage);
        controller.search(g, name);

        // Show the dialog and wait until the user closes i
        dialogStage.showAndWait();
        if (dialogStage.isShowing()){
            dialogStage.close();
        }
        if (nextPerson.trim().length() > 8)
        {
            nextPerson = null;
            System.out.println("Abnormal search.");
        }
        p = new Person(g, nextPerson);
        personView.setDisable(false);
        bShow = true;
        this.showPersonView(p);

        return nextPerson;
    }

    public void cleanExit() {
        if ((p != null) && (p.hasChanged()) || ((f != null) && f.hasChanged())){
            Alert aBox = new Alert(AlertType.CONFIRMATION, "Do you want to save changes before exiting?");
            aBox.showAndWait().ifPresent(response -> {
                if (response != ButtonType.OK)
                    return;
                p.completePerson();
            });
        }
        System.exit(0);
    }

    public void showStackTrace(String sLocation, Exception e)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(sLocation);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(e.getMessage());

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }


    public void preferences(String sMessage)  {
        // handles preferences and backup
        // First display view
        AnchorPane page = null;
        FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/PreferenceView.fxml"));
       try {
            page = loader.load();
        } catch (Exception e){
            e.printStackTrace();
        }

        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("Preference");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        assert page != null;
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        PreferenceViewController controller = loader.getController();
        controller.setMainApp(this, par, scene);
        controller.setDialogStage(dialogStage);
        controller.showPreference (sMessage);

        // Show the dialog and wait until the user closes i
        dialogStage.showAndWait();
        if (dialogStage.isShowing()){
            dialogStage.close();
        }
    }
    public void setVerbosity (Boolean vb)
    {
        bVerbosity = vb;
    }

    public Boolean getVerbosity()
    {
        return bVerbosity;
    }

    public D_Parameters getPar() {
        return par;
    }

    public void showPersonView(Person p) {
        // Load person overview.
        currentPerson = p.getPersonID();
        FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/PersonView.fxml"));
        try {
            personView = loader.load();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Set person overview into the center of root layout.
        rootLayout.setCenter(personView);

        // Give the controller access to the main app.
        PersonViewController controller = loader.getController();
        controller.clearFields();
        controller.setMainApp(this, g);
         controller.showPerson(p);
    }

    public void setCursor(Cursor c) {
        scene.setCursor(c);
    }

    public void showBrowser (String _url)
    {
        getHostServices().showDocument(_url);
    }

    public void showFamilyViewDialog(String f) {
        AnchorPane page = null;
            // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/FamilyView.fxml"));
        try {
            page = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Family");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        assert page != null;
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        FamilyViewController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        Family F = new Family(g, f);
        controller.showFamily(F);

        // Show the dialog and wait until the user closes i
        dialogStage.showAndWait();
     }

    public void selectionReturn (DropLabel r) {
        returnSelection = r;
    }

    public void update() {
        if (bExtend)
            completeEntry();		// got to 'completeEntry(), i.e. complete family extension
        else						// this is an ordinary record update
        {
            if (p != null)
                p.setChange(true);
            assert p != null;
            p.completePerson();
            if (f != null)
                f.complete();
            p = new Person(g, currentPerson);
            showPersonView(p);
        }
    }

    public void completeEntry()
    {
        /*
         * complement to createRelative.
         * Persist new person and family connections.
         * i.e. 2 persons and 1 family has to be updated
         */
        Family f = null;
        String sPerson_ID_1 = null;
        if ( p != null)
            sPerson_ID_1 = p.getPersonID();
        String sPerson_ID_2 = null;
        String sFamily_ID ;
        bExtend = false;
        // first: determine if new person ID has to be created. If yes, create it
        if (Objects.equals(p2.personID.get(), "NEW"))
        {
            sPerson_ID_2 = g.getNextPersonID();
            p2.setRoot(sPerson_ID_2);
            p2.setChange(true);
        }
        // second: determine if new family ID has to be created. If yes, create it
        if (Objects.equals(familyRoot, "NEW"))
        {
            sFamily_ID = g.getNextFamilyID();
        } else
            sFamily_ID = familyRoot;
        f = new Family( g, sFamily_ID);
        p2.addFamily(sFamily_ID, type1);
        p.addFamily(sFamily_ID, type1);
        p.setChange(true);

        if(sFamily_ID != null) {
            //f = new Family(g, sFamily_ID);
            f.addPerson(sPerson_ID_2, type4);
            f.addPerson(sPerson_ID_1, type2);
            p2.addFamily(sFamily_ID, type3);
            p2.setChange(true);
            f.complete();
        }

        if (p != null)
            p.completePerson();
        p2.completePerson();
        assert sPerson_ID_2 != null;
        p = new Person(g, sPerson_ID_2);
        bShow = true;
        personView.setDisable(false);
        showPersonView(p);
    }

    public void setNext (String nextPerson) {
        this.nextPerson = nextPerson;
    }

    public void changePerson (String personID) {
        if (p.hasChanged()){
            Alert aBox = new Alert(AlertType.CONFIRMATION, "Are you sure you want to save changes?");
            aBox.showAndWait().ifPresent(response -> {
                if (response != ButtonType.OK)
                    return;
                p.completePerson();
            });
        }
        Person p2 = new Person(g, personID);
        if (bShow) {
            p = p2;
            showPersonView(p);
        }
    }
    public void addDocument() {
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/DocumentView.fxml"));
        AnchorPane page = null;
        try {
            page = loader.load();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("Find");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        assert page != null;
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        nextPerson = "empty";

        // Set the person into the controller.
        DocumentViewController controller = loader.getController();
        controller.setMainApp( g);
        controller.setDialogStage(dialogStage);
        controller.show(p);

        // Show the dialog and wait until the user closes i
        dialogStage.showAndWait();
        if (dialogStage.isShowing()){
            dialogStage.close();
        }
    }
    public void createRelative (Relative r, Boolean _gender) {
        bExtend = true;
        Boolean Gender2 = _gender;			// gender of new person
        Boolean gender = p.isMale();				// now gender of first person
        String secondPersonID = null;
        String label;
        // Roles:
        type1 = null;	// p in p
        type2 = null;	// p in f
        type3 = null;	// p2 in p2
        type4 = null;	// p2 in f

        DropLabel  r1 = null;
        familyRoot = null;
        p2 = null;		//set second person to null;
        f = null;		//set linking family to null
        DropLabel d;
        List<DropLabel> list = new ArrayList<>();
        ObservableList<DropLabel> options;
        bShow = false;

        if (p.hasChanged()){
            Alert aBox = new Alert(AlertType.CONFIRMATION, "Do you want to save changes before continuing?");
            aBox.showAndWait().ifPresent(response -> {
                if (response != ButtonType.OK)
                    return;
                p.completePerson();
            });
        }
        if(r.equals(Relative.NONE)) {
            personView.setDisable(false);
            bShow = true;
            p2 = new Person(g, "NEW");
            firstPersonID = null;
            p = null;
            this.showPersonView(p2);
            return;
        }
        else
            firstPersonID = p.getPersonID();

        d = new DropLabel(null, "a new person.", "NEW");
        list.add(d);
        d = new DropLabel(null, "an existing person by Name.", "Name");
        list.add(d);
        d = new DropLabel(null, "an existing person by ID.", "ID");
        list.add(d);
        label = "  Whom would you like to add?";
        options = FXCollections.observableList(list);
        personView.setDisable(true);
        r1 = getDetail(label, options);
        if(r1 == null) {
            personView.setDisable(false);
            return;
        }
        if (r1.Extra.equals("NEW")) {
            // handle new person
            p2 = new Person(g, "NEW");
            p2.setGender(true);
        } else {
            // find existing person
            bShow = false;
            nextPerson = findPerson(r1.Extra);
            //System.out.println("findPerson 2: " + nextPerson);
            if (Objects.equals(secondPersonID = nextPerson, "empty"))
                return;
            p2 = new Person(g, secondPersonID);
            if (p2.isMale() != Gender2)
            {
                Alert aBox = new Alert(AlertType.CONFIRMATION, "That person has the wrong gender!");
                aBox.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.CANCEL)
                        bAbort = true;
                });
                if (bAbort)
                {
                    bShow = true;
                    personView.setDisable(false);
                    this.showPersonView(p);
                    return;
                }
            }
        }
        switch (r) {
            case SON, DAUGHTER -> {
                type1 = "FAMS";
                if (gender) {
                    type2 = "HUSB";
                    p2.setLastName(p.getLastName());
                } else
                    type2 = "WIFE";
                type3 = "FAMC";
                type4 = "CHIL";
            }
            case BROTHER, SISTER -> {
                type1 = "FAMC";
                type2 = "CHIL";
                type3 = "FAMC";
                type4 = "CHIL";
                p2.setLastName(p.getLastName());
            }
            case FATHER, MOTHER -> {
                type1 = "FAMC";
                type2 = "CHIL";
                type3 = "FAMS";
                if (Gender2) {
                    type4 = "HUSB";
                    p2.setLastName(p.getLastName());
                } else
                    type4 = "WIFE";
            }
            case SPOUSE -> {
                bExtend = true;
                type1 = "FAMS";
                type3 = "FAMS";
                if (gender) {
                    type2 = "HUSB";
                    type4 = "WIFE";
                } else {
                    type2 = "WIFE";
                    type4 = "HUSB";
                }
            }
            default -> {
            }
        }

        switch(r){
            case BROTHER, FATHER, SON:
                p2.setGender(true);
                break;
            case SISTER, MOTHER, DAUGHTER:
                p2.setGender(false);
                break;
            case SPOUSE:
                p2.setGender(!p.isMale());
                break;
            default:
                p2.setGender(true);
                break;
        }

        List<DropLabel> list1 = g.getFamilyMembers(firstPersonID, type1);
        List<DropLabel> list2 = g.getFamilyMembers(secondPersonID, type2);
        // join the two lists
        list1.addAll(list2);

        if (list1.isEmpty())
            familyRoot = "NEW";
        else {
            List <DropLabel> filtered = list1.stream().filter(u -> u.Root.equals(firstPersonID)).toList();
            if (!filtered.isEmpty()) {
                String fn = p.getFirstName();
                Alert aBox = new Alert(AlertType.INFORMATION, fn + " is already in that family!");
                aBox.showAndWait().ifPresent(response -> {
                });
                bShow = true;
                personView.setDisable(false);
                return;
            }
            d = new DropLabel(null, "None", "NONE");
            list1.add(d);
            label = "  Who is already in this family?";
            options = FXCollections.observableList(list1);
            personView.setDisable(true);
            r1 = getDetail(label, options);
            if(r1 == null) {
                bShow = true;
                personView.setDisable(false);
                return;
            }
            if (r1.Extra.equals("NONE"))
                familyRoot = "NEW";
            else
                familyRoot = r1.Root;
        }
        personView.setDisable(false);
        bShow = true;
        try {
            this.showPersonView(p2);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    private DropLabel getDetail(String label, ObservableList<DropLabel> options) {
        // Load the fxml file and create a new stage for the popup dialog.
         try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClass.class.getResource("/com.papaworx.cpro/layouts/detailView.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();

            dialogStage.setTitle("Detail");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            DetailViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            controller.showDetail (label, options);

            // Show the dialog and wait until the user closes i
            dialogStage.showAndWait();
            if (dialogStage.isShowing()){
                dialogStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnSelection;
    }
    public void deletePerson() {
        Alert aBox = new Alert(AlertType.CONFIRMATION, "Are you sure you want permanently delete this person?");
        aBox.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK)
                return;
            p.delete();
        });
        p = new Person(g, "FIRST");
        showPersonView(p);
    }
    public void unlinkPerson() {
        Alert aBox = new Alert(AlertType.CONFIRMATION, "Are you sure you want permanently unlink this person?");
        aBox.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK)
                return;
            p.unlink();
        });
        showPersonView(p);
    }
    public void showDescTree() {
        // handles preferences and backup
        // First display view
        FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/descFamilyTreeView.fxml"));
        AnchorPane page = null;
        try {
            page = loader.load();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("Family Tree");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        assert page != null;
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        DescFamilyTreeViewController controller = loader.getController();
        controller.setMainApp(this, g);
        controller.setDialogStage(dialogStage);
        controller.showTree(p);

        // Show the dialog and wait until the user closes i
        dialogStage.showAndWait();
        if (dialogStage.isShowing()){
            dialogStage.close();
        }
    }
    public void showAscTree() {
        // handles preferences and backup
        // First display view
        AnchorPane page = null;
        FXMLLoader loader = new FXMLLoader(MainClass.class.getResource("/com.papaworx.cpro/layouts/ascFamilyTreeView.fxml"));
        try {
            page = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("Family Tree");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        assert page != null;
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        AscFamilyTreeViewController controller = loader.getController();
        controller.setMainApp(this, g);
        controller.setDialogStage(dialogStage);
        controller.showTree(p);

        // Show the dialog and wait until the user closes i
        dialogStage.showAndWait();
        if (dialogStage.isShowing()){
            dialogStage.close();
        }
}
    public void handleGender() {
        g.handleGender();
    }
    public void Diagnostics(PrintStream f) {
        Diagnostics diag = new Diagnostics(g, this);
        diag.process(f);
    }
    public void printAscending() {
        TreePrinter tP = new TreePrinter(g);
        tP.print(p, treeType.ascend);
    }

    public void printDescending() {
        TreePrinter tP = new TreePrinter(g);
        tP.print(p, treeType.descend);
    }

    public void showInfo (String sInfo)
    {
        if (bVerbosity)
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText(sInfo);

            alert.showAndWait();
        }
    }

     public void export () {
       ExportGEDCOM exporter = new ExportGEDCOM(g, primaryStage, true, null);
        exporter.dump();
    }

    public Preferences getPrefs(){
        return prefs;
    }

}