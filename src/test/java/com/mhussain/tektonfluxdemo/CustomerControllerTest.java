package com.mhussain.tektonfluxdemo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

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
    void setUp() {
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
    void shouldGetAllCustomers() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/customers")
                .then()
                .statusCode(200)
                .body(".", hasSize(3));
    }

}