package org.example.DataService;

import org.example.DataService.Model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class DataService implements DataSource {
    private final List<DataSource> dataSources;

    public DataService(DataSource... dataSources) {
        this.dataSources = new ArrayList<>(Arrays.asList(dataSources));
    }

    @Override
    public List<Person> getPersons() {
        return combineLists(DataSource::getPersons);
    }

    @Override
    public List<Person> getPersonsById(int id) {
        return combineLists(dataSource -> dataSource.getPersonsById(id));
    }

    @Override
    public List<Person> getPersonsByColor(int color) {
        return combineLists(dataSource -> dataSource.getPersonsByColor(color));
    }

    @Override
    public boolean addPerson(Person person) {
        return false;
    }

    @Override
    public <T extends DataSource> boolean addPerson(Person person, Class<T> clazz) {
        if (!isValidPerson(person)) {
            return false;
        }

        for (DataSource dataSource : dataSources) {
            if (clazz.isInstance(dataSource)) {
                int id = this.createUniqueId();
                person.setId(id);
                return dataSource.addPerson(person);
            }
        }

        return false;
    }

    @Override
    public int getHighestId() {
        return dataSources.stream()
            .mapToInt(DataSource::getHighestId)
            .max()
            .orElse(0);
    }

    private int createUniqueId() {
        return this.getHighestId() + 1;
    }

    private List<Person> combineLists(Function<DataSource, List<Person>> function) {
        List<Person> persons = new ArrayList<>();

        for (DataSource dataSource : dataSources) {
            List<Person> test = function.apply(dataSource);

            persons.addAll(test);
        }

        return persons;
    }

    private boolean isValidPerson(Person person) {
        return person.getName() != null && !person.getName().isEmpty() &&
                person.getLastname() != null && !person.getLastname().isEmpty() &&
                person.getZipcode() != null && !person.getZipcode().isEmpty() &&
                person.getCity() != null && !person.getCity().isEmpty() &&
                person.getColor() >= 0;
    }
}
