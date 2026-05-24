package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class SearchSpecialCharsTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://play.ludigames.com";
    }

    @Test
    @DisplayName("TC-API-01: Search with XSS")
    public void testSearchXssInput() {
        String xssInput = "<script>alert(1)</script>";

        Response response = given()
                .queryParam("search", xssInput)
                .when()
                .get("/search.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-01 XSS Results ===");
        System.out.println("Input: " + xssInput);
        System.out.println("Status code: " + response.statusCode());


        assertNotEquals(500, response.statusCode(),
                "XSS input should not cause server error");

        assertTrue(response.time() < 5000,
                "Server should respond within 5s for XSS input, got: " + response.time() + "ms");

        String body = response.getBody().asString();
        assertFalse(body.contains("<script>alert(1)</script>"),
                "Server should sanitize XSS input, not reflect it directly in response");
    }

    @Test
    @DisplayName("TC-API-02: Search with SQL injection")
    public void testSearchSqlInjection() {
        String sqlInput = "' OR '1'='1";

        Response response = given()
                .queryParam("search", sqlInput)
                .when()
                .get("/search.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-02 SQL Injection Results ===");
        System.out.println("Input: " + sqlInput);
        System.out.println("Status code: " + response.statusCode());

        assertNotEquals(500, response.statusCode(),
                "SQL injection input should not cause server error");

        assertTrue(response.time() < 5000,
                "Server should respond within 5s for SQL injection input, got: " + response.time() + "ms");
    }

    @Test
    @DisplayName("TC-API-03: Search with null string")
    public void testSearchNullString() {
        Response response = given()
                .queryParam("search", "null")
                .when()
                .get("/search.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-03 Null String Results ===");
        System.out.println("Status code: " + response.statusCode());

        assertNotEquals(500, response.statusCode(),
                "Search for 'null' string should not cause server error");
    }
}