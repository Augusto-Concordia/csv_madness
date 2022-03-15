package com.amp.csv2html.exceptions;

/**
 * Exception thrown when missing data is found when parsing CSV files.
 */
public class CSVDataMissing extends Exception {
    public CSVDataMissing()
    {
        this("Error: Input row cannot be parsed due to missing information");
    }

    public CSVDataMissing(String message)
    {
        super(message);
    }
}
