package com.amp.csv2html;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner covidStatsScanner = null;
        Scanner doctorListScanner = null;

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
    }
}
