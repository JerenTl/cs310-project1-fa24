package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap; // Added Linked HashMap library


public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {

    JsonArray section = new JsonArray(); // Creates a JsonArray for section
    LinkedHashMap course = new LinkedHashMap<>(); // A LinkedHashMap for the course names
    LinkedHashMap scheduletype = new LinkedHashMap<>(); // A LinkedHashMap for schedule type
    LinkedHashMap subject = new LinkedHashMap<>(); // A LinkedHashMap for subject

    Iterator<String[]> iterator = csv.iterator();
    String[] headerline = iterator.next(); // Reads the first line for the header and keys

    while (iterator.hasNext()) {
        String[] valueLine = iterator.next(); // Reads the next line of csv for the values
        JsonObject csvLineMap = new JsonObject(); // JsonObject to store data for each line

        // Map headers to corresponding values from valueLine
        for (int i = 0; i < headerline.length; i++) {
            csvLineMap.put(headerline[i], valueLine[i]);
        }

        // Update the schedule type LinkedHashMap
        scheduletype.put(csvLineMap.get(TYPE_COL_HEADER), csvLineMap.get(SCHEDULE_COL_HEADER));

        // Split course name for the subject LinkedHashMap
        String[] courseName = csvLineMap.get(NUM_COL_HEADER).toString().split(" ");
        subject.put(courseName[0], csvLineMap.get(SUBJECT_COL_HEADER));

        // Create a LinkedHashMap for the course and update it
        LinkedHashMap courseMap = new LinkedHashMap<>();
        courseMap.put(SUBJECTID_COL_HEADER, courseName[0]);
        courseMap.put(NUM_COL_HEADER, courseName[1]);
        courseMap.put(DESCRIPTION_COL_HEADER, csvLineMap.get(DESCRIPTION_COL_HEADER));
        int credits = Integer.parseInt(csvLineMap.get(CREDITS_COL_HEADER).toString());
        courseMap.put(CREDITS_COL_HEADER, credits);
        course.put(csvLineMap.get(NUM_COL_HEADER), courseMap);

        // Create a LinkedHashMap for the section and update it
        LinkedHashMap sectionMap = new LinkedHashMap<>();
        int crn = Integer.parseInt(csvLineMap.get(CRN_COL_HEADER).toString());
        sectionMap.put(CRN_COL_HEADER, crn);
        sectionMap.put(SUBJECTID_COL_HEADER, courseName[0]);
        sectionMap.put(NUM_COL_HEADER, courseName[1]);
        sectionMap.put(SECTION_COL_HEADER, csvLineMap.get(SECTION_COL_HEADER));
        sectionMap.put(TYPE_COL_HEADER, csvLineMap.get(TYPE_COL_HEADER));
        sectionMap.put(START_COL_HEADER, csvLineMap.get(START_COL_HEADER));
        sectionMap.put(END_COL_HEADER, csvLineMap.get(END_COL_HEADER));
        sectionMap.put(DAYS_COL_HEADER, csvLineMap.get(DAYS_COL_HEADER));
        sectionMap.put(WHERE_COL_HEADER, csvLineMap.get(WHERE_COL_HEADER));

        // Splitting instructor names by comma
        String[] instructorArray = csvLineMap.get(INSTRUCTOR_COL_HEADER).toString().split(", ");
        sectionMap.put(INSTRUCTOR_COL_HEADER, instructorArray);

        // Add section to the JsonArray
        section.add(sectionMap);
    }

    // Create the final JsonObject with all the LinkedHashMaps and JsonArray
    JsonObject object = new JsonObject();
    object.put("scheduletype", scheduletype);
    object.put("subject", subject);
    object.put("course", course);
    object.put("section", section);

    // Serialize and return the JsonObject as a formatted JSON string
    return Jsoner.serialize(object);
}
    
    public String convertJsonToCsvString(JsonObject json) {
    // Getting the JsonObject from the parameters into a new JsonObject
    JsonObject jsonObject = new JsonObject(json);

    // Extracting the four parts of the JsonObject into their own JsonObject and JsonArray for easier access
    JsonObject scheduleTypeObject = (JsonObject) jsonObject.get("scheduletype"); // Extract scheduletype into a JsonObject
    JsonObject subjectObject = (JsonObject) jsonObject.get("subject"); // Extract subject into a JsonObject
    JsonObject courseObject = (JsonObject) jsonObject.get("course"); // Extract course into a JsonObject
    JsonArray sectionArray = (JsonArray) jsonObject.get("section"); // Extract section into a JsonArray

    List<String[]> sectionList = new ArrayList<>();
    // CSV header row
    String[] header = {
        CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER, SECTION_COL_HEADER, 
        TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER, 
        WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER
    };
    sectionList.add(header);

    // Iterate through the section array to process each section
    for (int i = 0; i < sectionArray.size(); i++) {
        JsonObject section = (JsonObject) sectionArray.get(i);

        // Convert the instructor array to a single string of comma-separated names
        JsonArray instructorArray = (JsonArray) section.get(INSTRUCTOR_COL_HEADER);
        String[] instructorNames = instructorArray.toArray(new String[0]);
        String instructor = String.join(", ", instructorNames);

        // Get course details from the courseObject
        HashMap courseNames = (HashMap) courseObject.get(
            section.get(SUBJECTID_COL_HEADER) + " " + section.get(NUM_COL_HEADER)
        );

        // Create a CSV line for the section
        String[] csvline = {
            section.get(CRN_COL_HEADER).toString(),
            subjectObject.get(section.get(SUBJECTID_COL_HEADER)).toString(),
            section.get(SUBJECTID_COL_HEADER) + " " + section.get(NUM_COL_HEADER),
            courseNames.get(DESCRIPTION_COL_HEADER).toString(),
            section.get(SECTION_COL_HEADER).toString(),
            section.get(TYPE_COL_HEADER).toString(),
            courseNames.get(CREDITS_COL_HEADER).toString(),
            section.get(START_COL_HEADER).toString(),
            section.get(END_COL_HEADER).toString(),
            section.get(DAYS_COL_HEADER).toString(),
            section.get(WHERE_COL_HEADER).toString(),
            scheduleTypeObject.get(section.get(TYPE_COL_HEADER).toString()).toString(),
            instructor
        };

        sectionList.add(csvline); // Add the csv line to the list
    }

    // Use CSVWriter to write the list of sections to a CSV formatted string
    StringWriter sw = new StringWriter();
    CSVWriter csvWriter = new CSVWriter(sw, '\t', '"', '\\', "\n");
    csvWriter.writeAll(sectionList);

    // Return the CSV string
    return sw.toString();
}


    public JsonObject getJson() {

        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
         
    }
    
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}