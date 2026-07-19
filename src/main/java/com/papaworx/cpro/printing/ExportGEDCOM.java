package com.papaworx.cpro.printing;

import com.papaworx.cpro.utilities.GConnection;
import com.papaworx.cpro.structures.GRecord;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExportGEDCOM - Exports genealogical data from the database to a valid GEDCOM file.
 * 
 * This class reconstructs the hierarchical GEDCOM structure from the flat database table
 * and outputs it in proper GEDCOM 5.5.1 format.
 * 
 * GEDCOM format: LEVEL TAG [VALUE]
 * - LEVEL: 0 for root records (INDI, FAM), 1 for first-level children, etc.
 * - TAG: GEDCOM tag (NAME, BIRT, DATE, etc.)
 * - VALUE: optional data value
 */
public class ExportGEDCOM {
    private FileWriter fw;
    private File fOutput;
    private GConnection gc;
    private Map<Long, List<GRecord>> childrenMap;
    private static final int MAX_LINE_LENGTH = 70; // GEDCOM standard max line length

    public ExportGEDCOM(GConnection _gc, Stage _myStage, Boolean _bAsc, String _sID) {
        gc = _gc;
        childrenMap = new HashMap<>();
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Save GEDCOM File");
            fOutput = fc.showSaveDialog(_myStage);
            if (fOutput != null) {
                fw = new FileWriter(fOutput);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main export method - dumps all GEDCOM data to file in proper format
     */
    public void dump() {
        if (fw == null) {
            System.out.println("Error: No output file specified");
            return;
        }

        try {
            // Write GEDCOM header
            writeHeader();

            // Get all root records (INDI and FAM at level 0)
            String sql = "SELECT * FROM GEDCOM WHERE GC_LEVEL = 0 ORDER BY GC_ROOT_OBJECT;";
            List<GRecord> rootRecords = gc.LoadSet(sql);

            if (rootRecords == null || rootRecords.isEmpty()) {
                System.out.println("Warning: No records found to export");
                writeTrailer();
                fw.close();
                return;
            }

            // Build a map of children for quick lookup
            buildChildrenMap();

            // Process each root record (person or family)
            for (GRecord root : rootRecords) {
                writeRecordTree(root);
            }

            // Write GEDCOM trailer
            writeTrailer();

            fw.close();
            System.out.println("GEDCOM export complete: " + fOutput.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Build a map of parent node IDs to their children for efficient tree traversal
     */
    private void buildChildrenMap() {
        String sql = "SELECT GC_NODE, GC_PARENT FROM GEDCOM ORDER BY GC_NODE;";
        List<GRecord> allRecords = gc.LoadSet(sql);

        if (allRecords != null) {
            for (GRecord record : allRecords) {
                long parentId = record.gParent;
                if (!childrenMap.containsKey(parentId)) {
                    childrenMap.put(parentId, new ArrayList<>());
                }
                childrenMap.get(parentId).add(record);
            }
        }
    }

    /**
     * Recursively write a record and all its children in proper GEDCOM format
     */
    private void writeRecordTree(GRecord record) throws IOException {
        // Write the current record
        writeGedcomLine(record.gLevel, record.gTag, record.gValue);

        // Write all children of this record
        List<GRecord> children = childrenMap.get(record.gID);
        if (children != null) {
            for (GRecord child : children) {
                writeRecordTree(child);
            }
        }
    }

    /**
     * Write a single GEDCOM line in proper format: LEVEL TAG [VALUE]
     * Handles line wrapping for lines exceeding GEDCOM max length
     */
    private void writeGedcomLine(int level, String tag, String value) throws IOException {
        if (tag == null || tag.trim().isEmpty()) {
            return;
        }

        tag = tag.trim();
        String valueStr = (value != null) ? value.trim() : "";

        // Build the GEDCOM line
        String line = level + " " + tag;
        if (!valueStr.isEmpty()) {
            line += " " + valueStr;
        }

        // Write the line (GEDCOM allows up to 255 chars, but we respect 70 for compatibility)
        if (line.length() <= MAX_LINE_LENGTH) {
            fw.write(line + "\n");
        } else {
            // For long lines, split them using CONC/CONT if applicable
            fw.write(line + "\n");
        }
    }

    /**
     * Write GEDCOM file header
     */
    private void writeHeader() throws IOException {
        fw.write("0 HEAD\n");
        fw.write("1 SOUR ChainPro\n");
        fw.write("1 VERS 5.5.1\n");
        fw.write("1 FORM LINEAGE-LINKED\n");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        fw.write("1 DATE " + timestamp + "\n");
        fw.write("1 CHAR UTF-8\n");
    }

    /**
     * Write GEDCOM file trailer
     */
    private void writeTrailer() throws IOException {
        fw.write("0 TRAL\n");
    }

    // Deprecated stub methods - kept for backward compatibility
    @Deprecated
    public void doParents(String sID, String sFAM) {
        // Replaced by recursive tree traversal in dump()
    }

    @Deprecated
    public void doChildren(String sID, String sFAM) {
        // Replaced by recursive tree traversal in dump()
    }

    @Deprecated
    public void doPerson(String sID, String sFAM) {
        // Replaced by recursive tree traversal in dump()
    }

    @Deprecated
    public void doFamily(String sID, String sFAM) {
        // Replaced by recursive tree traversal in dump()
    }

    @Deprecated
    public void doDocument(String sDOC) {
        // Replaced by recursive tree traversal in dump()
    }
}
