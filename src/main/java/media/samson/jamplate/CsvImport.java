package media.samson.jamplate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles importing and parsing CSV files with headers.
 * Assumes the first line of the CSV file contains field names.
 */
public class CsvImport {
    private final List<String> headers;
    private final List<Map<String, String>> records;

    /**
     * Creates a new CsvImport instance from a CSV file.
     *
     * @param file The CSV file to read
     * @throws IOException If there's an error reading the file
     * @throws IllegalArgumentException If the file is empty or missing headers
     */
    public CsvImport(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("CSV file does not exist: " + file.getAbsolutePath());
        }
        if (file.length() == 0) {
            throw new IllegalArgumentException("CSV file is empty: " + file.getAbsolutePath());
        }

        try (Reader reader = new FileReader(file);
             CSVParser csvParser = CSVFormat.DEFAULT
                 .withFirstRecordAsHeader()
                 .withTrim()
                 .withIgnoreEmptyLines(true)
                 .parse(reader)) {

            // Get headers from parser
            this.headers = new ArrayList<>(csvParser.getHeaderNames());
            if (headers.isEmpty()) {
                throw new IllegalArgumentException("CSV file has no headers");
            }

            // Parse records into maps
            this.records = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String header : headers) {
                    row.put(header, record.get(header));
                }
                records.add(row);
            }

            if (records.isEmpty()) {
                throw new IllegalArgumentException("CSV file contains headers but no data");
            }
        }
    }

    /**
     * Gets the field names (headers) from the CSV file.
     *
     * @return An unmodifiable list of field names
     */
    public List<String> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    /**
     * Gets all records from the CSV file.
     * Each record is a map of field name to value.
     *
     * @return An unmodifiable list of records
     */
    public List<Map<String, String>> getRecords() {
        return Collections.unmodifiableList(records);
    }

    /**
     * Gets a specific record by index.
     *
     * @param index The index of the record to get
     * @return An unmodifiable map representing the record
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Map<String, String> getRecord(int index) {
        return Collections.unmodifiableMap(records.get(index));
    }

    /**
     * Gets the number of records in the CSV file (excluding headers).
     *
     * @return The number of records
     */
    public int getRecordCount() {
        return records.size();
    }

    /**
     * Gets all values for a specific field name.
     *
     * @param fieldName The name of the field to get values for
     * @return An unmodifiable list of values for the field
     * @throws IllegalArgumentException if the field name doesn't exist
     */
    public List<String> getFieldValues(String fieldName) {
        if (!headers.contains(fieldName)) {
            throw new IllegalArgumentException("Field name not found: " + fieldName);
        }

        List<String> values = new ArrayList<>();
        for (Map<String, String> record : records) {
            values.add(record.get(fieldName));
        }
        return Collections.unmodifiableList(values);
    }

    /**
     * Validates that all required fields are present in the CSV.
     *
     * @param requiredFields The list of required field names
     * @throws IllegalArgumentException if any required fields are missing
     */
    public void validateRequiredFields(List<String> requiredFields) {
        List<String> missingFields = new ArrayList<>();
        for (String field : requiredFields) {
            if (!headers.contains(field)) {
                missingFields.add(field);
            }
        }
        
        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException(
                "Required fields missing from CSV: " + String.join(", ", missingFields)
            );
        }
    }
}

