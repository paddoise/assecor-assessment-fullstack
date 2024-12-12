package org.example.DataService.Model;

public class Person {
    private int id;
    private String name;
    private String lastname;
    private String zipcode;
    private String city;
    private int color;

    public Person() { }

    public Person(int id, String name, String lastname, String zipcode, String city, int color) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.zipcode = zipcode;
        this.city = city;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
