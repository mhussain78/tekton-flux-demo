package com.mhussain.tektonfluxdemo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CustomerControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    CustomerRepository customerRepository;


    @BeforeEach
    void setUp() throws IOException {
        Files.list(Path.of("/")).forEach(System.out::println);
        Files.list(Path.of("/root")).forEach(System.out::println);
        Files.list(Path.of("/root/.docker")).forEach(System.out::println);

        RestAssured.baseURI = "http://localhost:" + port;
        customerRepository.deleteAll();

        List<Customer> customers = List.of(
                new Customer(null, "John", "john@mail.com"),
                new Customer(null, "Dennis", "dennis@mail.com"),
                new Customer(null, "Mohamad", "mohamad@mail.com")
        );

        var saved = customerRepository.saveAll(customers);
        saved.forEach(customer -> log.info("Stored customer in database: " + customer));
    }

    @Test
    void testBootingContainer() throws IOException, InterruptedException {
        try (var container = new GenericContainer<>("mhussain78/greeting-app:0.0.1").withExposedPorts(8080).waitingFor(Wait.forHttp("/resources/greetings"))) {
            container.start();
            var client = HttpClient.newHttpClient();
            var uri = "http://" + container.getHost() + ":" + container.getFirstMappedPort() + "/resources/greetings";
            var request = HttpRequest.newBuilder(URI.create(uri)).GET().build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("The body: " + response.body());
        }
    }

//    @Test
//    void shouldGetAllCustomers() {
//        given()
//                .contentType(ContentType.JSON)
//                .when()
//                .get("/api/customers")
//                .then()
//                .statusCode(200)
//                .body(".", hasSize(3));
//    }

}