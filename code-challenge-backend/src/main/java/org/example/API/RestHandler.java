package org.example.API;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpStatus;
import org.example.DataService.*;
import org.example.DataService.DataSources.DataSourceSqlite;
import org.example.DataService.Model.Person;
import org.example.DataService.Model.PersonAndValidation;
import org.example.DataService.Model.PersonFields;
import org.example.Utility.Colors;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestHandler implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(RestHandler.class.getName());
    private final DataService dataService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestHandler(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String encodedUrl = exchange.getRequestURI().toString();
        String url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
        String key = "msg";

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "http://localhost:4200");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        ResponseAndRCode responseAndCode = switch (requestMethod.toUpperCase()) {
            case "GET" -> handleGet(url, key);
            case "POST" -> handlePost(url, key, exchange);
            case "OPTIONS" -> handleOptions(key);
            default -> handleDefault(key);
        };

        ObjectNode response = responseAndCode.getResponse();
        int rCode = responseAndCode.getRCode();
        String responseString = response.toString();
        byte[] responseBytes = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(rCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private ResponseAndRCode handleGet(String url, String key) {
        ObjectNode response = objectMapper.createObjectNode();
        int rCode = HttpStatus.SC_NOT_FOUND;

        if (url.matches("/persons")) {
            // /persons

            List<Person> persons = this.dataService.getPersons();
            response.set(key, this.personListToJsonArray(persons));
            rCode = HttpStatus.SC_OK;
        } else if (url.matches("/persons/\\d+")) {
            // /persons/{id}

            String id = url.substring("/persons/".length());

            try {
                List<Person> persons = dataService.getPersonsById(Integer.parseInt(id));
                response.set(key, personListToJsonArray(persons));
                rCode = HttpStatus.SC_OK;
            } catch (NumberFormatException e) {
                response.put(key, "Could not convert id to number");
                rCode = HttpStatus.SC_BAD_REQUEST;
            }
        } else if (url.matches("/persons/color/.+")) {
            // /persons/color/{color}

            String colorStr = url.substring("/persons/color/".length());
            int colorId = Colors.convertColor(colorStr);
            List<Person> persons = dataService.getPersonsByColor(colorId);
            response.set(key, personListToJsonArray(persons));
            rCode = HttpStatus.SC_OK;
        } else {
            response.put(key, "Invalid endpoint");
        }

        return new ResponseAndRCode(response, rCode);
    }

    private ResponseAndRCode handlePost(String url, String key, HttpExchange exchange) {
        ObjectNode response = objectMapper.createObjectNode();
        int rCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;

        if (url.matches("/person")) {
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                PersonAndValidation personAndValidation = jsonToPerson(requestBody);

                if (!personAndValidation.isValid()) {
                    response.put("msg", "Could not read person from JSON");
                } else {
                    boolean success = dataService.addPerson(personAndValidation.getPerson(), DataSourceSqlite.class);

                    if (success) {
                        response.put(key, "Person created successfully");
                        rCode = HttpStatus.SC_CREATED;
                    } else {
                        response.put(key, "Could not create new person");
                    }
                }
            } catch (IOException e) {
                response.put(key, "Invalid JSON format");
                rCode = HttpStatus.SC_BAD_REQUEST;
            }
        } else {
            response.put(key, "Invalid endpoint for POST");
            rCode = HttpStatus.SC_BAD_REQUEST;
        }

        return new ResponseAndRCode(response, rCode);
    }

    private ResponseAndRCode handleOptions(String key) {
        ObjectNode response = objectMapper.createObjectNode();
        int rCode = HttpStatus.SC_OK;

        response.put(key, "OK");
        return new ResponseAndRCode(response, rCode);
    }

    private ResponseAndRCode handleDefault(String key) {
        ObjectNode response = objectMapper.createObjectNode();
        int rCode = HttpStatus.SC_METHOD_NOT_ALLOWED;

        response.put(key, "Unsupported request method");
        return new ResponseAndRCode(response, rCode);
    }

    private PersonAndValidation jsonToPerson(String personJsonStr) {
        try {
            JsonNode personJson = objectMapper.readTree(personJsonStr);
            Person person = new Person();

            person.setName(personJson.get(PersonFields.F.NAME.label).asText());
            person.setLastname(personJson.get(PersonFields.F.LASTNAME.label).asText());
            person.setZipcode(personJson.get(PersonFields.F.ZIPCODE.label).asText());
            person.setCity(personJson.get(PersonFields.F.CITY.label).asText());
            person.setColor(Colors.convertColor(personJson.get(PersonFields.F.COLOR.label).asText()));

            return new PersonAndValidation(person,true);
        } catch (JsonProcessingException | NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Could not retrieve or convert necessary values from JSON.", e);
            return new PersonAndValidation(false);
        }
    }

    private ArrayNode personListToJsonArray(List<Person> persons) {
        ArrayNode personsJsonArray = objectMapper.createArrayNode();

        persons.stream().map(person -> {
            ObjectNode personJson = objectMapper.valueToTree(person);
            String colorName = Colors.convertColor(person.getColor());
            personJson.put("color", colorName);
            return personJson;
        }).forEach(personsJsonArray::add);

        return personsJsonArray;
    }
}
