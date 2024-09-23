package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        
        ClassSchedule schedule = new ClassSchedule();
        
        try {
            
            // Get CSV/JSON Data
            
            List<String[]> csvOriginal = schedule.getCsv();
            JsonObject jsonOriginal = schedule.getJson();
            
            // Print Total Sections Found in CSV and JSON Data (should be equal)
            
            System.out.println("Sections Found (CSV): " + (csvOriginal.size() - 1));
            
            JsonArray sections = (JsonArray)jsonOriginal.get("section");
            System.out.println("Sections Found (JSON): " + sections.size());
            
            
            // Convert CSV data to JSON and print the result
            String jsonStringFromCsv = schedule.convertCsvToJsonString(csvOriginal); // Convert CSV to JSON string
            System.out.println("\nConverted JSON from CSV:");
            System.out.println(jsonStringFromCsv); // Print the converted JSON string

            // Convert JSON data back to CSV and print the result
            String csvStringFromJson = schedule.convertJsonToCsvString(jsonOriginal); // Convert JSON back to CSV string
            System.out.println("\nConverted CSV from JSON:");
            System.out.println(csvStringFromJson); // Print the converted CSV string

        }
        catch (Exception e) { e.printStackTrace(); }
            
    }
    
}