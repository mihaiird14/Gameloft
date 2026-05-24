package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class SearchLongStringTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://play.ludigames.com";
    }

    @Test
    @DisplayName("TC-API-04: Search with 500 character string")
    public void testSearch500CharsString() {
        String longInput = "a".repeat(500);

        Response response = given()
                .queryParam("search", longInput)
                .when()
                .get("/search.html")
                .then()
                .extract().response();
        //some data
        System.out.println("=== TC-API-04 Long String (500 chars) Results ===");
        System.out.println("Input length: " + longInput.length() + " chars");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response time: " + response.time() + "ms");

        assertNotEquals(500, response.statusCode(),
                "500-char search input should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s for long input, got: " + response.time() + "ms");
    }

    @Test
    @DisplayName("TC-API-05: Search with 1000 character string")
    public void testSearch1000CharsString() {
        String longInput = "a".repeat(1000);

        Response response = given()
                .queryParam("search", longInput)
                .when()
                .get("/search.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-05 Long String (1000 chars) Results ===");
        System.out.println("Input length: " + longInput.length() + " chars");
        System.out.println("Status code: " + response.statusCode());

        assertNotEquals(500, response.statusCode(),
                "1000-char search input should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s for very long input, got: " + response.time() + "ms");
    }

}