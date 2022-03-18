//COMP 249 - Assignment #3
//Due Date: March 20th
//Written by: Augusto Mota Pinheiro (40208080) & Michaël Gugliandolo (40213419)

package com.amp.csv2html;

import com.amp.csv2html.exceptions.CSVAttributeMissing;
import com.amp.csv2html.exceptions.CSVDataMissing;

import java.awt.Desktop;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * Driver class that reads 2 csv files and converts them into 2 html files, while dealing with any exceptions that may arise. It also appends any exceptions to a log file.
 * At the end, it prompts the user to open one of the html files
 */
public class Main {
    /**
     * Driver method that runs the project
     *
     * @param args Optional arguments for driver method
     */
    public static void main(String[] args) {
        Scanner covidStatsScanner = null; //Scanner to read the file covidStatistics.csv
        Scanner doctorListScanner = null; //Scanner to read the file doctorList.csv

        FileWriter exceptionsWriter = null; //FileWriter to write the file Exceptions.log
        FileWriter covidStatsWriter = null; //FileWriter to write the file covidStatistics.html
        FileWriter doctorListWriter = null; //FileWriter to write the file doctorList.html

        File exceptionsFile = null; //The file Exceptions.log
        File covidFile = null; //The file covidStatistics.html
        File doctorFile = null; //The file doctorList.html

        //Attempt to open covidStatistics.csv
        try {
            covidStatsScanner = new Scanner(new File("input/covidStatistics.csv"));
        } catch (FileNotFoundException e) { //File doesn't exist
            System.out.println("Could not open input file covidStatistics.csv for reading.");
            System.out.println("Please check that the file exists and is readable. This program will terminate after closing any opened files.");

            System.exit(-1);
        }

        //Attempt to open doctorList.csv
        try {
            doctorListScanner = new Scanner(new File("input/doctorList.csv"));
        } catch (FileNotFoundException e) { //File doesn't exist
            System.out.println("Could not open input file doctorList.csv for reading.");
            System.out.println("Please check that the file exists and is readable. This program will terminate after closing any opened files.");

            covidStatsScanner.close(); //Close open file

            System.exit(-1);
        }
        //Both files have been opened successfully


        //Open or create Exceptions.log
        try {
            exceptionsFile = new File("output/Exceptions.log");
            exceptionsWriter = new FileWriter(exceptionsFile, true);
        } catch (IOException e) {
            System.out.println("Could not open nor create file Exceptions.log for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log from the \"output\" folder.");

            if (exceptionsFile.delete()) System.out.println("Deletion successful."); //Delete file
            else System.out.println("Failed to delete Exceptions.log.");

            covidStatsScanner.close(); //Close open files
            doctorListScanner.close();

            System.exit(-1);
        }
        //Create covidStatistics.html
        try {
            covidFile = new File("output/covidStatistics.html");
            covidStatsWriter = new FileWriter(covidFile);
        } catch (IOException e) {
            System.out.println("Could not open file covidStatistics.html for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log and covidStatistics.html from the \"output\" folder.");

            if (exceptionsFile.delete() && covidFile.delete()) System.out.println("Deletion successful."); //Delete files
            else System.out.println("Failed to delete Exceptions.log and/or covidStatistics.html.");

            covidStatsScanner.close(); //Close open files
            doctorListScanner.close();

            System.exit(-1);
        }
        //Create doctorList.html
        try {
            doctorFile = new File("output/doctorList.html");
            doctorListWriter = new FileWriter(doctorFile);
        } catch (IOException e) {
            System.out.println("Could not open file doctorList.html for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log, covidStatistics.html and doctorList.html from the \"output\" folder.");

            if (exceptionsFile.delete() && covidFile.delete() && doctorFile.delete()) System.out.println("Deletion successful."); //Delete files
            else System.out.println("Failed to delete Exceptions.log, covidStatistics.html and/or doctorList.html.");

            covidStatsScanner.close(); //Close open files
            doctorListScanner.close();

            System.exit(-1);
        }
        //All 3 files to be written to have been created or opened successfully

        //Convert covidStatistics.csv to html
        try {
            ConvertCSVtoHTML(covidStatsScanner, covidStatsWriter, exceptionsWriter, "covidStatistics.csv");
        } catch (CSVAttributeMissing e) { //Missing attribute
            if (covidFile.delete()) System.out.println("covidStatistics.html deletion successful."); //Delete file
            else System.out.println("Failed to delete covidStatistics.html.");

            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException when converting covidStatistics.csv to html.");
            e.printStackTrace();
        } finally {
            covidStatsScanner.close(); //Close open file
        }
        //Convert doctorList.csv to html
        try {
            ConvertCSVtoHTML(doctorListScanner, doctorListWriter, exceptionsWriter, "doctorList.csv");
        } catch (CSVAttributeMissing e) { //Missing attribute
            if (doctorFile.delete()) System.out.println("doctorList.html deletion successful."); //Delete file
            else System.out.println("Failed to delete doctorList.html.");

            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException when converting doctorList.csv to html.");
            e.printStackTrace();
        } finally {
            doctorListScanner.close(); //Close open file
        }

        try {
            exceptionsWriter.close(); //Close open file
        } catch (IOException e) {
            System.out.println("There was an IOException when closing the file writer for Exceptions.log");
            e.printStackTrace();
        }
        System.out.println("Both csv files have been read and converted to html! You can find the html files at " + (new File("output").getAbsolutePath()));


        //Prompt user to open a html file

        Scanner keyInput = new Scanner(System.in); //Scanner for user input
        BufferedReader fileReader; //Reader the html file that the user wants
        String answer; //Name of the file that the user wants
        int chances = 2; //Total number of tries that the user has to write the correct name
        boolean isBobRoss = false; //True if the user adds 'Bob Ross!' after the name of the file, false if not. If true, open the requested file in the default browser

        do {
            if (chances == 1) System.out.println("This is your last chance!");
            System.out.println("Which html file to you want to look at? (covidStatistics.html or doctorList.html)"); //Prompt user
            answer = keyInput.nextLine(); //Read user's answer

            if (answer.endsWith("Bob Ross!")) { //Check if user added 'Bob Ross!' after the file name
                isBobRoss = true;
                answer = answer.replace("Bob Ross!", ""); //Remove the 'Bob Ross!'
                System.out.println("Bob Ross!");
            }

            try { //Try to open the file and display its content
                fileReader = new BufferedReader(new FileReader("output/" + answer));

                chances = 0; //if we reached this line, then the file exists

                if (isBobRoss) { //Open html file in default browser
                    File htmlFile = new File("output/" + answer);
                    Desktop.getDesktop().browse(htmlFile.toURI());
                }

                String line; //Current line read from the file
                while ((line = fileReader.readLine()) != null) { //Check if we're at the end of the file
                    System.out.println(line);
                }

            } catch (FileNotFoundException e) { //User has entered a wrong name

                chances--; //Decrement chances remaining
                System.out.print("Html file \"" + answer + "\" doesn't exist. ");
                if (chances >= 1) System.out.println("You have " + chances + " chance" + ((chances > 1) ? "s left." : " left.")); //Indicate how many chances left
                else System.out.println("Terminating program."); //It was the last chance

            } catch (IOException e) {
                System.out.println("IOException when printing out the content of " + answer + ".");
                e.printStackTrace();
            }

        } while (chances > 0); //Repeat while there are chances remaining

        System.out.println("\nThank you for using our program!");
    }

    /**
     * Convert a csv file into a html table. First line of csv file is the title, second line is the attributes, last line can be a note if it starts with "Note:",
     * and all the other lines contain data. Each column is separated by a comma.
     *
     * @param fileReader       Scanner to read the csv file
     * @param fileWriter       FileWriter to write the html file
     * @param exceptionsWriter FileWriter to write the Exceptions.log file
     * @param fileName         Name of the csv file
     * @throws CSVAttributeMissing Missing attribute in csv file
     * @throws IOException         Exception when writing to html file or to Exceptions.log
     */
    private static void ConvertCSVtoHTML(Scanner fileReader, FileWriter fileWriter, FileWriter exceptionsWriter, String fileName) throws CSVAttributeMissing, IOException {

        fileWriter.write("""
                <!DOCTYPE html>
                <html>
                <style>
                table {font-family: arial, sans-serif;border-collapse: collapse;}
                td, th {border: 1px solid #000000;text-align: left;padding: 8px;}
                tr:nth-child(even) {background-color: #dddddd;}
                span{font-size: small}
                </style>
                <body>

                <table>

                """);

        String[] line = fileReader.nextLine().split(","); //Contains the current row of the csv file
        String[] attributes; //Contains attributes
        int lineCounter = 1; //Counts the number of lines in the csv file

        fileWriter.write("<caption>" + line[0] + "</caption>\n"); //Title

        attributes = fileReader.nextLine().split(","); //Attributes
        lineCounter++;

        writeAttributes(fileWriter, exceptionsWriter, attributes, fileName);

        //Convert all the data rows into html
        while (fileReader.hasNextLine()) { //Repeat while there's another line

            line = fileReader.nextLine().split(",");
            lineCounter++;

            if (!fileReader.hasNextLine() && line[0].startsWith("Note:")) { //Last line and it's a note
                fileWriter.write("</table>\n<span>" + line[0] + "</span>\n");
            } else {

                try { //Verify if the data row is missing a data
                    verifyDataRow(line, attributes.length);
                } catch (CSVDataMissing e) { //Write in console, in Exceptions.log, and skip the rest of this iteration to skip the row
                    System.out.println("Missing data in \"" + fileName + "\" for attribute \"" + attributes[Integer.parseInt(e.getMessage())] + "\" at line " + lineCounter + ".");
                    exceptionsWriter.write("Missing data in \"" + fileName + "\" for attribute \"" + attributes[Integer.parseInt(e.getMessage())] + "\" at line " + lineCounter + ".\n");
                    continue;
                }

                fileWriter.write("<tr>\n");

                for (String data : line) {
                    fileWriter.write("<td>" + data + "</td>\n");
                }

                fileWriter.write("</tr>\n");

                if (!fileReader.hasNextLine()) fileWriter.write("</table>"); //Last line of table
            }
        }

        fileWriter.write("</body>\n</html>");

        fileWriter.close(); //Close file writer
    }

    /**
     * Convert attributes from csv file to html file.
     *
     * @param fileWriter       FileWriter to write the html file
     * @param exceptionsWriter FileWriter to write the Exceptions.log file
     * @param attributes       String array where each index contains one attribute
     * @param fileName         Name of the csv file
     * @throws CSVAttributeMissing Missing attribute in csv file
     * @throws IOException         Exception when writing to html file or to Exceptions.log
     */
    private static void writeAttributes(FileWriter fileWriter, FileWriter exceptionsWriter, String[] attributes, String fileName) throws CSVAttributeMissing, IOException {
        fileWriter.write("<tr>\n");

        for (String attribute : attributes) { //Convert each attribute to html
            if (attribute.isBlank()) { //Write in console, in Exceptions.log, close file writer and throw exception
                System.out.println("ERROR: Missing attribute in \"" + fileName + "\". File has not been converted to html.");
                exceptionsWriter.write("ERROR: Missing attribute in \"" + fileName + "\". File has not been converted to html.\n");
                fileWriter.close();
                throw new CSVAttributeMissing("ERROR: Missing attribute in \"" + fileName + "\". File has not been converted to html.");
            }
            fileWriter.write("<th>" + attribute + "</th>\n");
        }

        fileWriter.write("</tr>\n");
    }

    /**
     * Check if any of the data are blanks, or if there are less datas than attributes
     *
     * @param line         Current data line to verify
     * @param nbAttributes Number of attributes
     * @throws CSVDataMissing Missing data in csv file
     */
    private static void verifyDataRow(String[] line, int nbAttributes) throws CSVDataMissing {

        for (int i = 0; i < nbAttributes; i++) {
            if (i >= line.length) throw new CSVDataMissing(String.valueOf(i)); //Csv file doesn't have enough commas
            else if (line[i].isBlank()) throw new CSVDataMissing(String.valueOf(i)); //Data is black
        }
    }


    /**
     * Finds all csv files in "input" folder and puts them in an array
     *
     * @return Array of all the csv files in "input" folder
     */
    private static File[] findAllCSV() {
        try {
            File directory = new File("input");

            if (!directory.isDirectory()) return null;

            File[] files = directory.listFiles();
            File[] csvFiles = new File[Objects.requireNonNull(files).length];
            int counter = 0;

            for (File file : files) {
                if (file.getName().endsWith(".csv")) {
                    csvFiles[counter] = file;
                    counter++;
                }
            }

            File[] csvFilesResized = new File[counter];
            System.arraycopy(csvFiles, 0, csvFilesResized, 0, counter);

            return csvFilesResized;

        } catch (Exception e) {
            System.out.println("Exception in findAllCSV method\n");
            e.printStackTrace();
            return null;
        }
    }
}
