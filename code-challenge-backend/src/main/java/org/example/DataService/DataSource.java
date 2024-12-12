package org.example.DataService;

import org.example.DataService.Model.Person;

import java.util.List;

public interface DataSource {
    List<Person> getPersons();
    List<Person> getPersonsById(int id);
    List<Person> getPersonsByColor(int color);
    boolean addPerson(Person person);
    <T extends DataSource> boolean addPerson(Person person, Class<T> clazz);
    int getHighestId();
}
