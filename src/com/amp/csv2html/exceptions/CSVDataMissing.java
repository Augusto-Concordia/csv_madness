//COMP 249 - Assignment #3
//Due Date: March 20th
//Written by: Augusto Mota Pinheiro (40208080) & Michaël Gugliandolo (40213419)

package com.amp.csv2html.exceptions;

/**
 * Exception thrown when missing data is found when parsing CSV files.
 */
public class CSVDataMissing extends Exception {

    /**
     * Constructs a new default exception with the default message.
     */
    public CSVDataMissing() {
        this("Error: Input row cannot be parsed due to missing information");
    }

    /**
     * Constructs a new exception with the supplied message.
     *
     * @param message Detail error message about the exception.
     */
    public CSVDataMissing(String message) {
        super(message);
    }
}
