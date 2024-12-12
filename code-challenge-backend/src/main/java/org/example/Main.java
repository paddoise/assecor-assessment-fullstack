package org.example;

import org.example.API.API;
import org.example.DataService.DataService;
import org.example.DataService.DataSources.DataSourceCsv;
import org.example.DataService.DataSources.DataSourceSqlite;

public class Main {
    public static void main(String[] args) {
        DataSourceCsv dataSourceCsv = new DataSourceCsv();
        DataSourceSqlite dataSourceSqlite = new DataSourceSqlite();
        dataSourceSqlite.createNewTable();

        // region Create table and insert person into extra datasource
        /*Person person1 = new Person();
        person1.setName("Firstname1");
        person1.setLastname("Lastname1");
        person1.setZipcode("Zipcode1");
        person1.setCity("City1");
        person1.setColor(1);

        dataSourceSqlite.addPerson(person1);*/
        // endregion

        DataService dataService = new DataService(dataSourceCsv, dataSourceSqlite);
        API api = new API(dataService);

        //dataSourceSqlite.close();
    }
}
