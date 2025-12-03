package azrou.app.service;

import azrou.app.model.entity.Group;
import azrou.app.model.entity.Student;
import azrou.app.repo.GroupRepository;
import azrou.app.repo.StudentRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvImportService {
    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    public CsvImportService(StudentRepository studentRepository, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    public ImportResult importStudents(File csvFile) {
        ImportResult result = new ImportResult();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.toLowerCase().startsWith("cin")) {
                    continue; // Skip header
                }

                String[] parts = line.split(",");
                if (parts.length < 3) {
                    result.addError("Line " + lineNumber + ": Insufficient data");
                    continue;
                }

                try {
                    String cin = parts[0].trim();
                    String fullName = parts[1].trim();
                    String groupName = parts[2].trim();
                    String phone = parts.length > 3 ? parts[3].trim() : "";
                    String dobStr = parts.length > 4 ? parts[4].trim() : "";
                    String qualifications = parts.length > 5 ? parts[5].trim() : "";

                    if (cin.isEmpty() || fullName.isEmpty() || groupName.isEmpty()) {
                        result.addError("Line " + lineNumber + ": Missing required fields (CIN, Name, Group)");
                        continue;
                    }

                    // Check if student exists
                    if (studentRepository.findByCin(cin).isPresent()) {
                        result.addSkipped("Line " + lineNumber + ": Student with CIN " + cin + " already exists");
                        continue;
                    }

                    // Find or create group
                    Group group = groupRepository.findByName(groupName).orElseGet(() -> {
                        Group newGroup = new Group();
                        newGroup.setName(groupName);
                        newGroup.setDescription("Imported from CSV");
                        newGroup.setCapacity(30); // Default
                        groupRepository.save(newGroup);
                        result.addCreatedGroup(groupName);
                        return newGroup;
                    });

                    Student student = new Student();
                    student.setCin(cin);
                    student.setFullName(fullName);
                    student.setGroup(group);
                    student.setPhone(phone);
                    student.setQualifications(qualifications);

                    if (!dobStr.isEmpty()) {
                        try {
                            student.setDateOfBirth(LocalDate.parse(dobStr, DateTimeFormatter.ISO_LOCAL_DATE));
                        } catch (Exception e) {
                            logger.warn("Invalid date format for student {}: {}", fullName, dobStr);
                        }
                    }

                    studentRepository.save(student);
                    result.incrementImported();

                } catch (Exception e) {
                    logger.error("Error processing line " + lineNumber, e);
                    result.addError("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read CSV file", e);
            result.addError("File error: " + e.getMessage());
        }

        return result;
    }

    public static class ImportResult {
        private int importedCount = 0;
        private final List<String> errors = new ArrayList<>();
        private final List<String> skipped = new ArrayList<>();
        private final List<String> createdGroups = new ArrayList<>();

        public void incrementImported() {
            importedCount++;
        }

        public void addError(String error) {
            errors.add(error);
        }

        public void addSkipped(String msg) {
            skipped.add(msg);
        }

        public void addCreatedGroup(String name) {
            if (!createdGroups.contains(name))
                createdGroups.add(name);
        }

        public int getImportedCount() {
            return importedCount;
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<String> getSkipped() {
            return skipped;
        }

        public List<String> getCreatedGroups() {
            return createdGroups;
        }
    }
}
