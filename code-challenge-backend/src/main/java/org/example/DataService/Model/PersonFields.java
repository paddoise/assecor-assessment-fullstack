package org.example.DataService.Model;

public class PersonFields {
    public enum F {
        ID("id"),
        NAME("name"),
        LASTNAME("lastname"),
        ZIPCODE("zipcode"),
        CITY("city"),
        COLOR("color");

        public final String label;

        F(String label) {
            this.label = label;
        }
    }
}
