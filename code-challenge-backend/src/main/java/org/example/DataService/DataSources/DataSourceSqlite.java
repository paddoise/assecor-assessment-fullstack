package org.example.DataService.DataSources;

import org.example.DataService.DataSource;
import org.example.DataService.Model.Person;
import org.example.DataService.Model.PersonFields;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSourceSqlite implements DataSource {
    private static final Logger LOGGER = Logger.getLogger(DataSourceSqlite.class.getName());
    private Connection connection;
    private final String tableName = "persons";

    public DataSourceSqlite() {
        this.connection = this.connect();
    }

    public void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS persons (\n"
            + " " + PersonFields.F.ID.label + " integer PRIMARY KEY,\n"
            + " " + PersonFields.F.NAME.label + " text NOT NULL,\n"
            + " " + PersonFields.F.LASTNAME.label + " text NOT NULL,\n"
            + " " + PersonFields.F.ZIPCODE.label + " text NOT NULL,\n"
            + " " + PersonFields.F.CITY.label + " text NOT NULL,\n"
            + " " + PersonFields.F.COLOR.label + " integer NOT NULL\n"
            + ");";

        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sql);
            LOGGER.log(Level.INFO, "Table created successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not create table.", e);
        }
    }

    public Connection connect() {
        String url = "jdbc:sqlite:persons.db";

        try {
            this.connection = DriverManager.getConnection(url);
            LOGGER.log(Level.INFO, "Connection to SQLite has been established.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not establish connection to SQLite.", e);
        }
        return this.connection;
    }

    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not close connection to SQLite.", e);
        }
    }

    @Override
    public List<Person> getPersons() {
        String sql = "SELECT * FROM " + this.tableName;
        List<Person> persons = new ArrayList<>();

        try (Statement stmt = this.connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                persons.add(createPersonFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not retrieve persons.", e);
        }

        return persons;
    }

    @Override
    public List<Person> getPersonsById(int id) {
        String sql = "SELECT * FROM " + this.tableName + " WHERE " + PersonFields.F.ID.label + " = ?";
        return getPersonsByQuery(sql, id);
    }

    @Override
    public List<Person> getPersonsByColor(int color) {
        String sql = "SELECT * FROM " + this.tableName + " WHERE " + PersonFields.F.COLOR.label + " = ?";
        return getPersonsByQuery(sql, color);
    }

    @Override
    public boolean addPerson(Person person) {
        String sql = "INSERT INTO " + this.tableName + "("
            + PersonFields.F.ID.label + ","
            + PersonFields.F.NAME.label + ","
            + PersonFields.F.LASTNAME.label + ","
            + PersonFields.F.ZIPCODE.label + ","
            + PersonFields.F.CITY.label + ","
            + PersonFields.F.COLOR.label
            + ") VALUES(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, person.getId());
            pstmt.setString(2, person.getName());
            pstmt.setString(3, person.getLastname());
            pstmt.setString(4, person.getZipcode());
            pstmt.setString(5, person.getCity());
            pstmt.setInt(6, person.getColor());
            pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "Person inserted successfully.");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not insert person.", e);
            return false;
        }
    }

    @Override
    public <T extends DataSource> boolean addPerson(Person person, Class<T> clazz) {
        return false;
    }

    @Override
    public int getHighestId() {
        String sql = "SELECT MAX(" + PersonFields.F.ID.label + ") FROM " + this.tableName;
        int highestId = 0;

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                highestId = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not retrieve highest ID from database.", e);
        }

        return highestId;
    }

    private List<Person> getPersonsByQuery(String sql, int parameter) {
        List<Person> persons = new ArrayList<>();

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, parameter);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    persons.add(createPersonFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not retrieve persons by query.", e);
        }

        return persons;
    }

    private Person createPersonFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt(PersonFields.F.ID.label);
        String name = rs.getString(PersonFields.F.NAME.label);
        String lastname = rs.getString(PersonFields.F.LASTNAME.label);
        String zipcode = rs.getString(PersonFields.F.ZIPCODE.label);
        String city = rs.getString(PersonFields.F.CITY.label);
        int color = rs.getInt(PersonFields.F.COLOR.label);

        return new Person(id, name, lastname, zipcode, city, color);
    }
}
