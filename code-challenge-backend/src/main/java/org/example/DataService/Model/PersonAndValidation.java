package org.example.DataService.Model;

public class PersonAndValidation {
    private Person person;
    private final boolean isValid;

    public PersonAndValidation(boolean isValid) {
        this.isValid = isValid;
    }

    public PersonAndValidation(Person person, boolean isValid) {
        this.person = person;
        this.isValid = isValid;
    }

    public Person getPerson() {
        return person;
    }

    public boolean isValid() {
        return isValid;
    }

}
