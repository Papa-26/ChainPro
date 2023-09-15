package com.papaworx.cpro.utilities;

public class Paragraph {

    /**
     * Paragraph encapsulates text as an array of lines.
     * By loose GEDCOM convention, a line is limited to maximal 70 characters.
     * Implicitly, the first line is preceded by a carriage return ("\n"),
     * except in the first paragraph of a NOTE.
     */

    private String[] sLines;
    private int iLines;
    private int iLineCounter = 0;
    public Paragraph(String _sPar){
        int iMaxLine = 70;
        int iP_Length = _sPar.length();
        int iCount = 0;
        int iTo = 0;
        int iFrom = 0;
        iLines = iP_Length / iMaxLine;
        int iRest = iP_Length - iLines * iMaxLine;
        if (iRest >0)
            iLines++;
        sLines = new String[iLines];
        while (iFrom < iP_Length) {
            iTo = Math.min(iFrom + iMaxLine, iP_Length);
            sLines[iCount++] = _sPar.substring(iFrom, iTo);
            iFrom = iTo;
        }
    }

    public int getNumberLines(){
        return iLines;
    }

    public String getLine(int iCount){
        return sLines[iCount];
    }
}
