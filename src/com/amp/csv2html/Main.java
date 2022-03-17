//COMP 249 - Assignment #3
//Due Date: March 20th
//Written by: Augusto Mota Pinheiro (40208080) & Michaël Gugliandolo (40213419)

package com.amp.csv2html;

import com.amp.csv2html.exceptions.CSVAttributeMissing;
import com.amp.csv2html.exceptions.CSVDataMissing;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner covidStatsScanner = null;
        Scanner doctorListScanner = null;

        FileWriter exceptionsWriter = null;
        FileWriter covidStatsWriter = null;
        FileWriter doctorListWriter = null;

        File exceptionsFile = null;
        File covidFile = null;
        File doctorFile = null;

        try {
            covidStatsScanner = new Scanner(new File("input/covidStatistics.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("Could not open input file covidStatistics.csv for reading.");
            System.out.println("Please check that the file exists and is readable. This program will terminate after closing any opened files");

            System.exit(-1);
        }

        try {
            doctorListScanner = new Scanner(new File("input/doctorList.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("Could not open input file doctorList.csv for reading.");
            System.out.println("Please check that the file exists and is readable. This program will terminate after closing any opened files");

            covidStatsScanner.close();

            System.exit(-1);
        }
        //Both files have been opened successfully


        try {
            exceptionsFile = new File("output/Exceptions.log");
            exceptionsWriter = new FileWriter(exceptionsFile, true);
        } catch (IOException e) {
            System.out.println("Could not open nor create file Exceptions.log for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log from the \"output\" folder.");

            if (exceptionsFile.delete()) System.out.println("Deletion successful.");

            covidStatsScanner.close();
            doctorListScanner.close();

            System.exit(-1);
        }

        try {
            covidFile = new File("output/covidStatistics.html");
            covidStatsWriter = new FileWriter(covidFile);
        } catch (IOException e) {
            System.out.println("Could not open file covidStatistics.html for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log and covidStatistics.html from the \"output\" folder.");

            if (exceptionsFile.delete() && covidFile.delete()) System.out.println("Deletion successful.");

            covidStatsScanner.close();
            doctorListScanner.close();

            System.exit(-1);
        }
        try {
            doctorFile = new File("output/doctorList.html");
            doctorListWriter = new FileWriter(doctorFile);
        } catch (IOException e) {
            System.out.println("Could not open file doctorList.html for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log, covidStatistics.html and doctorList.html from the \"output\" folder.");

            if (exceptionsFile.delete() && covidFile.delete() && doctorFile.delete()) System.out.println("Deletion successful.");

            covidStatsScanner.close();
            doctorListScanner.close();

            System.exit(-1);
        }
        //All 3 files to be written to have been created or opened successfully

        try {
            ConvertCSVtoHTML(covidStatsScanner, covidStatsWriter, exceptionsWriter, "covidStatistics.csv");
        } catch (CSVAttributeMissing e) {
            if (covidFile.delete()) System.out.println("covidStatistics.html deletion successful.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            covidStatsScanner.close();
        }
        try {
            ConvertCSVtoHTML(doctorListScanner, doctorListWriter, exceptionsWriter, "doctorList.csv");
        } catch (CSVAttributeMissing e) {
            if (doctorFile.delete()) System.out.println("doctorList.csv deletion successful.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            doctorListScanner.close();
        }

        try {
            exceptionsWriter.close();
        } catch (IOException e) {
            System.out.println("There was an IOException when closing the file writer for Exceptions.log");
            e.printStackTrace();
        }
        //todo when we prompt user to enter name of file, if he enters Bob Ross!, we attempt to open a file on his default browser?
    }

    /**
     * Finds all csv files in "input" folder and puts them in an array
     *
     * @return Array of all the csv files in "input" folder
     */
    private static File[] findAllCSV() { //todo ok, I just realized this is useless, I thought they wanted to make the project expandable but then they want us to hardcode the 2 files to read...
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
            System.arraycopy(csvFiles, 0, csvFilesResized, 0, counter); //todo omg this would've been so much simpler with lists

            return csvFilesResized;

        } catch (Exception e) { //todo is this try catch necessary? Idk if creating a file is dangerous or not
            System.out.println("Exception in findAllCSV method\n");
            e.printStackTrace();
            return null;
        }
    }

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

        String[] line = fileReader.nextLine().split(",");
        String[] attributes;
        int lineCounter = 1;

        fileWriter.write("<caption>" + line[0] + "</caption>\n");

        attributes = fileReader.nextLine().split(",");
        lineCounter++;

        writeAttributes(fileWriter, exceptionsWriter, attributes, fileName);


        while (fileReader.hasNextLine()) {
            line = fileReader.nextLine().split(",");
            lineCounter++;

            if (!fileReader.hasNextLine() && line[0].startsWith("Note:")) { //Last line and it's a note
                fileWriter.write("</table>\n<span>" + line[0] + "</span>\n");
            } else {

                try {
                    verifyDataRow(line);
                } catch (CSVDataMissing e) {
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

        fileWriter.close();
    }

    private static void writeAttributes(FileWriter fileWriter, FileWriter exceptionsWriter, String[] attributes, String fileName) throws CSVAttributeMissing, IOException {
        fileWriter.write("<tr>\n");

        for (String attribute : attributes) {
            if (attribute.isBlank()) {
                System.out.println("ERROR: Missing attribute in \"" + fileName + "\". File has not been converted to html.");
                exceptionsWriter.write("ERROR: Missing attribute in \"" + fileName + "\". File has not been converted to html.\n");
                fileWriter.close();
                throw new CSVAttributeMissing("ERROR: Missing attribute in \"" + fileName + "\". File has not been converted to html.");
            }
            fileWriter.write("<th>" + attribute + "</th>\n");
        }

        fileWriter.write("</tr>\n");
    }

    private static void verifyDataRow(String[] line) throws CSVDataMissing {
        for (int i = 0; i < line.length; i++) {
            if (line[i].isBlank()) throw new CSVDataMissing(String.valueOf(i));
        }
    }
}
