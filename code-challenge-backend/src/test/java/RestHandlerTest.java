import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import org.example.API.RestHandler;
import org.example.DataService.DataService;
import org.example.DataService.DataSources.DataSourceSqlite;
import org.example.DataService.Model.Person;
import org.example.Utility.Colors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class RestHandlerTest {
    private DataService dataService;
    private RestHandler restHandler;
    private HttpExchange exchange;
    private ObjectMapper objectMapper;
    private List<Person> mockPersons;

    @BeforeEach
    public void setUp() throws IOException {
        dataService = mock(DataService.class);
        restHandler = new RestHandler(dataService);
        exchange = mock(HttpExchange.class);
        objectMapper = new ObjectMapper();

        mockPersons = Arrays.asList(
                new Person(1, "John", "Doe", "12345", "City", 1),
                new Person(2, "Jane", "Smith", "54321", "Town", 2),
                new Person(3, "Sophia", "Wilson", "30303", "Star City", 3),
                new Person(4, "Liam", "Taylor", "40404", "Central City", 3),
                new Person(5, "Olivia", "Anderson", "50505", "Coast City", 4),
                new Person(6, "Noah", "Thomas", "60606", "Smallville", 4),
                new Person(7, "Ava", "Jackson", "70707", "Sunnydale", 5)
        );
    }

    @Test
    public void testHandleGetPersons() throws IOException, URISyntaxException {
        // Arrange
        when(dataService.getPersons()).thenReturn(mockPersons);
        ByteArrayOutputStream responseBody = setupExchangeForGetRequest("/persons");

        // Act
        restHandler.handle(exchange);

        // Assert
        verify(exchange).sendResponseHeaders(200, responseBody.size());

        String expectedResponseString = createExpectedResponse(mockPersons);
        String response = responseBody.toString(StandardCharsets.UTF_8);

        assertEquals(expectedResponseString, response);
    }

    @Test
    public void testHandleGetPersonsById() throws IOException, URISyntaxException {
        // Arrange
        int personId = 1;
        Person mockPerson = mockPersons.stream()
                .filter(person -> person.getId() == personId)
                .findFirst()
                .orElse(null);

        when(dataService.getPersonsById(personId)).thenReturn(Collections.singletonList(mockPerson));

        ByteArrayOutputStream responseBody = setupExchangeForGetRequest("/persons/" + personId);

        // Act
        restHandler.handle(exchange);

        // Assert
        verify(exchange).sendResponseHeaders(200, responseBody.size());

        String expectedResponseString = createExpectedResponse(Collections.singletonList(mockPerson));
        String response = responseBody.toString(StandardCharsets.UTF_8);

        assertEquals(expectedResponseString, response);
    }

    @Test
    public void testHandleGetPersonsByColor() throws IOException, URISyntaxException {
        // Arrange
        int colorId = 3;
        List<Person> filteredPersons = mockPersons.stream()
                .filter(person -> person.getColor() == colorId)
                .toList();

        when(dataService.getPersonsByColor(colorId)).thenReturn(filteredPersons);
        ByteArrayOutputStream responseBody = setupExchangeForGetRequest(
                "/persons/color/" + Colors.convertColor(colorId)
        );

        // Act
        restHandler.handle(exchange);

        // Assert
        verify(exchange).sendResponseHeaders(200, responseBody.size());

        String expectedResponseString = createExpectedResponse(filteredPersons);
        String response = responseBody.toString(StandardCharsets.UTF_8);

        assertEquals(expectedResponseString, response);
    }

    @Test
    public void testHandlePostPerson() throws IOException, URISyntaxException {
        // Arrange
        Person newPerson = new Person();

        newPerson.setName("Alice");
        newPerson.setLastname("Johnson");
        newPerson.setZipcode("12345");
        newPerson.setCity("Metropolis");
        newPerson.setColor(1);

        ObjectNode requestBodyNode = objectMapper.createObjectNode();
        requestBodyNode.put("name", newPerson.getName());
        requestBodyNode.put("lastname", newPerson.getLastname());
        requestBodyNode.put("zipcode", newPerson.getZipcode());
        requestBodyNode.put("city", newPerson.getCity());
        requestBodyNode.put("color", Colors.convertColor(newPerson.getColor()));

        String requestBody = objectMapper.writeValueAsString(requestBodyNode);

        when(dataService.addPerson(any(Person.class), eq(DataSourceSqlite.class))).thenReturn(true);
        ByteArrayOutputStream responseBody = setupExchangeForPostRequest("/person", requestBody);

        // Act
        restHandler.handle(exchange);

        // Assert
        verify(exchange).sendResponseHeaders(201, responseBody.size());

        ObjectNode expectedResponse = objectMapper.createObjectNode();
        expectedResponse.put("msg", "Person created successfully");
        String expectedResponseString = expectedResponse.toString();
        String response = responseBody.toString(StandardCharsets.UTF_8);

        assertEquals(expectedResponseString, response);
    }

    private ByteArrayOutputStream setupExchangeForGetRequest(String uri) throws URISyntaxException {
        return setupExchange("GET", uri, null);
    }

    private ByteArrayOutputStream setupExchangeForPostRequest(String uri, String requestBody) throws URISyntaxException {
        return setupExchange("POST", uri, requestBody);
    }

    private ByteArrayOutputStream setupExchange(String method, String uri, String requestBody) throws URISyntaxException {
        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getRequestURI()).thenReturn(new URI(uri));
        when(exchange.getResponseHeaders()).thenReturn(new com.sun.net.httpserver.Headers());

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(responseBody);

        if (requestBody != null) {
            when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8)));
        }

        return responseBody;
    }

    private String createExpectedResponse(List<Person> persons) throws JsonProcessingException {
        ObjectNode expectedResponse = objectMapper.createObjectNode();
        ArrayNode personsArray = expectedResponse.putArray("msg");

        for (Person person : persons) {
            ObjectNode personNode = objectMapper.createObjectNode();
            personNode.put("id", person.getId());
            personNode.put("name", person.getName());
            personNode.put("lastname", person.getLastname());
            personNode.put("zipcode", person.getZipcode());
            personNode.put("city", person.getCity());
            personNode.put("color", Colors.convertColor(person.getColor()));
            personsArray.add(personNode);
        }

        return objectMapper.writeValueAsString(expectedResponse);
    }
}
