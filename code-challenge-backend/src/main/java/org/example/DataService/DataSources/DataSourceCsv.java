package org.example.DataService.DataSources;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.example.DataService.DataSource;
import org.example.DataService.Model.Person;
import org.example.Utility.RepairLine;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataSourceCsv implements DataSource {
    private static final Logger LOGGER = Logger.getLogger(DataSourceCsv.class.getName());
    private final ArrayList<Person> persons = new ArrayList<>();

    public DataSourceCsv() {
        this.fetchData();
    }

    @Override
    public List<Person> getPersons() {
        return this.persons;
    }

    @Override
    public List<Person> getPersonsById(int id) {
        return filterPersons(person -> person.getId() == id);
    }

    @Override
    public List<Person> getPersonsByColor(int color) {
        return filterPersons(person -> person.getColor() == color);
    }

    @Override
    public boolean addPerson(Person person) {
        return false;
    }

    @Override
    public <T extends DataSource> boolean addPerson(Person person, Class<T> clazz) {
        return false;
    }

    @Override
    public int getHighestId() {
        OptionalInt maxId = persons.stream()
            .mapToInt(Person::getId)
            .max();

        return maxId.orElse(0);
    }

    private List<Person> filterPersons(Predicate<Person> indicator) {
        return persons.stream()
            .filter(indicator)
            .collect(Collectors.toList());
    }

    private void fetchData() {
        String csvFile = "src/main/resources/sample-input.csv";

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] nextLine;
            RepairLine repairLine = new RepairLine();
            int id = 1;
            int lineLength = 4;

            while ((nextLine = reader.readNext()) != null) {
                List<String> currentLine = new ArrayList<>(Arrays.asList(nextLine));

                if (!currentLine.isEmpty() && currentLine.size() < lineLength) {
                    if (currentLine.getLast().isEmpty() || currentLine.getLast().isBlank()) {
                        currentLine.removeLast();
                    }

                    repairLine.addLine(id, currentLine);
                } else {
                    this.insertPerson(id, currentLine);
                }

                if (repairLine.getLine().size() > lineLength) {
                    repairLine.reset();
                } else if (repairLine.getLine().size() == lineLength) {
                    this.insertPerson(repairLine.getId(), repairLine.getLine());
                    repairLine.reset();
                }

                id++;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not read CSV.", e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertPerson(int id, List<String> line) {
        Person person = new Person();

        person.setId(id);
        person.setName(line.getFirst().trim());
        person.setLastname(line.get(1).trim());

        String address = line.get(2).trim();

        if (address.matches("^\\d+.*")) {
            Pattern pattern = Pattern.compile("^(\\d+)(.*)$");
            Matcher matcher = pattern.matcher(address);

            if (matcher.find()) {
                String zipcode = matcher.group(1).trim();
                String city = matcher.group(2).trim();

                person.setZipcode(zipcode);
                person.setCity(city);
            }
        } else {
            person.setZipcode("");
            person.setCity(address);
        }

        try {
            person.setColor(Integer.parseInt(line.get(3).trim()));
            this.persons.add(person);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Could not add person with id " + id + ". Unable to read color value.");
        }
    }
}
