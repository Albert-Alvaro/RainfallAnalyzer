package com.example.rainfallanalyzerandvisualizer;

import textio.TextIO;
import org.apache.commons.csv.*;
import java.io.FileReader;
import java.io.Reader;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CP2406 Assignment Albert Alvaro (14328932)
 * Rainfall Data Analyzer - Alpha Version
 * This Java class is able to analyze CSV from a local folder and will output an analyzed CSV file to a specified folder
 */
public class RainfallAnalyzer {
    public static void main(String[] args) {
        System.out.println("This program can read in any rainfall data in a csv format and analyze it");
        System.out.println("The output will be the maximum, minimum, and total rainfall taken from the csv file");
        System.out.println("Enter 0 to end the program.");

        String filename;

        while (true){
            try{
                filename = getFilename();
                if(filename == null){
                    System.out.println("Thank you and goodbye");
                    break;
                }
                ArrayList<String> analyzedRainfallData = analyzeRainfallData(filename);
                saveRainfallData(analyzedRainfallData, filename);
                System.out.println("Records from "+filename+" were succesfully analyzed");
            } catch(Exception e){
                System.out.println("Error: ");
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * This method is used to write the processed data into a specified CSV file
     * @param processedData is an ArrayList containing the processed data in a string data type
     * @param filename is the name of the file where the data will be saved in string data type
     */
    private static void saveRainfallData(ArrayList<String> processedData, String filename){
        TextIO.writeFile(getSavePath(filename));
        TextIO.putln("year,month,total,minimum,maximum");

        for (String record : processedData){
            TextIO.putln(record);
        }
    }

    /**
     * This is the main method which will be used to process the raw data from a CSV file into processed data
     * @param filename is the the name of the file which will contain the raw data to be processed, it is in string data type
     * @return This method returns an array list filled with the processed data in string format
     * @throws Exception Two exceptions can be thrown from this method, on ewhen the file is empty and another when the date
     * format is wrong.
     * */
    private static ArrayList<String> analyzeRainfallData(String filename) throws Exception {

        File file = new File("src/main/resources/raw_rainfalldata/" + filename);
        //Check if file is empty
        if (file.length() == 0) {
            throw new Exception("This is an empty file, it is invalid.");
        }
        Reader reader = new FileReader("src/main/resources/raw_rainfalldata/" + filename);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);

        int year;
        int month;
        int day;
        double rainfall;
        int currentYear = 0;
        int currentMonth = 1;
        double monthlyTotalRainfall = 0.0;
        double minRainfall = Double.POSITIVE_INFINITY;
        double maxRainfall = 0.0;
        ArrayList<String> rainfallData = new ArrayList<>();

        for (CSVRecord record : records) {
            String stringYear = record.get("Year");
            String stringMonth = record.get("Month");
            String stringDay = record.get("Day");
            String rainfallAmount = record.get("Rainfall amount (millimetres)");

            year = Integer.parseInt(stringYear);
            month = Integer.parseInt(stringMonth);
            day = Integer.parseInt(stringDay);

            //Check date format, throw new NumberFormatException if date format is invalid
            if ((month < 1 || month > 12) || (day < 1 || day > 31)) {
                System.out.println("Error invalid date format.");
                throw new NumberFormatException("Date input out of range");
            }

            //Check rainfall for rainfall amount, if empty, assume 0
            rainfall = Objects.equals(rainfallAmount, "") ? 0 : Double.parseDouble(rainfallAmount);

            // Save data and reset minimum, maximum, and total rainfall values for the next month
            if (month != currentMonth) {
                //Check if it is the first year before saving the data
                rainfallData.add(writeCurrentData(monthlyTotalRainfall,minRainfall,maxRainfall,currentMonth,currentYear == 0 ? year : currentYear));
                currentYear = year;
                currentMonth = month;
                monthlyTotalRainfall = 0.0;
                maxRainfall = 0.0;
                minRainfall = Double.POSITIVE_INFINITY;
            }
            monthlyTotalRainfall += rainfall;
            if (rainfall > maxRainfall){
                maxRainfall = rainfall;
            }
            if (rainfall < minRainfall){
                minRainfall = rainfall;
            }
        }
        rainfallData.add(writeCurrentData(monthlyTotalRainfall,minRainfall,maxRainfall,currentMonth,currentYear));
        return rainfallData;

    }

    /**
     * This method is used to get the path for the new file in which the processed data is going to be saved in
     * @param filename is the filename of the file which was processed
     * @return This method returns the path to the new save file for the processed data in string format
     */
    private static String getSavePath(String filename){
        String[] filenameParts = filename.trim().split("\\.");
        return "src/main/resources/analyzed_rainfalldata/" + filenameParts[0] + "_analyzed.csv";
    }

    /**
     * This method is used to combine all of the already processed data into one coherent string which can be
     * saved within a CSV file
     * @param monthlyTotal is the total amount of rainfall for a month
     * @param minRainfall is the minimum amount of rainfall for a month
     * @param maxRainfall is the maximum amount of rainfall for a month
     * @param month is the month in which the data was recorded
     * @param year is the year in which the data is recorded
     * @return This method returns a new string which contains all of the processed rainfall data
     */
    private static String writeCurrentData(double monthlyTotal, double minRainfall, double maxRainfall, int month, int year) {
        return String.format("%d,%d,%1.2f,%1.2f,%1.2f", year, month, monthlyTotal, minRainfall, maxRainfall);
    }

    /**
     * This method is used to get the filename that the user wants to process, it will create a list of all of the available files
     * which can be processed contained within a raw rainfall data folder in the resources folder, the user can enter a number from the
     * displayed list and the method will return the corresponding filename, the user can also enter a 0, which will return a null, which
     * will cause the program to end.
     * @return This method will return the name of the file to be processed in string format
     */
    private static String getFilename(){
        System.out.println("The files available to be chosen are: ");
        File file = new File("src/main/resources/raw_rainfalldata");
        String[] pathNames = file.list();

        assert pathNames != null;
        for (int i = 0; i < pathNames.length; i++) {
            System.out.println((i + 1) + " : " + pathNames[i]);
        }
        System.out.println("Enter the number besides the file to be analyzed: ");

        int fileNumber;
        String filename;
        while(true){
            //Check if the selected file is valid
            try{
                fileNumber = TextIO.getInt();
                if (fileNumber == 0){
                    return null;
                }
                filename = pathNames[fileNumber-1];
                break;
            }catch(ArrayIndexOutOfBoundsException e){
                System.out.println("Number chosen not found, please try another number");
            }
        }
        return filename;
    }
}
