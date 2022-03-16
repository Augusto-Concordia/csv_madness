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

        File exceptions = null;
        File covid = null;
        File doctor = null;

        try {
            exceptions = new File("output/Exceptions.log");
            exceptionsWriter = new FileWriter(exceptions, true);
        } catch (IOException e) {
            System.out.println("Could not open nor create file Exceptions.log for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log from the \"output\" folder.");

            if (exceptions.delete()) System.out.println("Deletion successful.");

            covidStatsScanner.close();
            doctorListScanner.close();

            System.exit(-1);
        }

        try {
            covid = new File("output/covidStatistics.html");
            covidStatsWriter = new FileWriter(covid);
        } catch (IOException e) {
            System.out.println("Could not open file covidStatistics.html for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log and covidStatistics.html from the \"output\" folder.");

            if (exceptions.delete() && covid.delete()) System.out.println("Deletion successful.");

            covidStatsScanner.close();
            doctorListScanner.close();

            System.exit(-1);
        }
        try {
            doctor = new File("output/doctorList.html");
            doctorListWriter = new FileWriter(doctor);
        } catch (IOException e) {
            System.out.println("Could not open file doctorList.html for writing.");

            e.printStackTrace();

            System.out.println("This program will delete Exceptions.log, covidStatistics.html and doctorList.html from the \"output\" folder.");

            if (exceptions.delete() && covid.delete() && doctor.delete()) System.out.println("Deletion successful.");

            covidStatsScanner.close();
            doctorListScanner.close();

            System.exit(-1);
        }
        //All 3 files to be written to have been created or opened successfully

        try {
            ConvertCSVtoHTML(covidStatsScanner, covidStatsWriter, exceptionsWriter);
        } catch (CSVAttributeMissing e) {
            e.printStackTrace();
        } catch (CSVDataMissing e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ConvertCSVtoHTML(doctorListScanner, doctorListWriter, exceptionsWriter);
        } catch (CSVAttributeMissing e) {
            e.printStackTrace();
        } catch (CSVDataMissing e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private static void ConvertCSVtoHTML(Scanner fileReader, FileWriter fileWriter, FileWriter exceptionsWriter) throws CSVAttributeMissing, CSVDataMissing, IOException {

        final int NB_ATTRIBUTES = 4;

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

        fileWriter.write("<caption>" + line[0] + "</caption>");

        line = fileReader.nextLine().split(",");
        fileWriter.write("<tr>");

        for (String attribute : line) {
            if (attribute.isBlank()) throw new CSVAttributeMissing(); //todo on ne connait pas le nom du fichier par le scanner, donc on va ecrire le msg personnalise dans main
            fileWriter.write("<th>" + attribute + "</th>\n");
        }

        while (fileReader.hasNextLine())
        {
            line = fileReader.nextLine().split(",");
            fileWriter.write("<tr>");

            for (String attribute : line) {
                if (attribute.isBlank()) throw new CSVAttributeMissing(); //todo on ne connait pas le nom du fichier par le scanner, donc on va ecrire le msg personnalise dans main
                fileWriter.write("<th>" + attribute + "</th>\n");
            }

            fileWriter.write("</tr>\n");
        }

        fileWriter.write("</table>\n </body>\n </html>");

        fileWriter.close();
    }
}
