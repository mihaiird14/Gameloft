package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryEdgeCasesTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://play.ludigames.com";
    }

    @Test
    @DisplayName("TC-API-06: Category ID = 0")
    public void testCategoryIdZero() {
        Response response = given()
                .when()
                .get("/category.html?catId=0")
                .then()
                .extract().response();

        System.out.println("=== TC-API-06 catId=0 Results ===");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response time: " + response.time() + "ms");

        assertNotEquals(500, response.statusCode(),
                "catId=0 should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s, got: " + response.time() + "ms");
    }

    @Test
    @DisplayName("TC-API-07: Category ID negative")
    public void testCategoryIdNegative() {
        Response response = given()
                .when()
                .get("/category.html?catId=-1")
                .then()
                .extract().response();

        System.out.println("=== TC-API-09 catId=-1 Results ===");
        System.out.println("Status code: " + response.statusCode());


        assertNotEquals(500, response.statusCode(),
                "catId=-1 should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s, got: " + response.time() + "ms");
    }

    @Test
    @DisplayName("TC-API-07: Category ID as text")
    public void testCategoryIdText() {
        Response response = given()
                .when()
                .get("/category.html?catId=abc")
                .then()
                .extract().response();

        System.out.println("=== TC-API-07 catId=abc Results ===");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response time: " + response.time() + "ms");

        assertNotEquals(500, response.statusCode(),
                "catId=abc should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s, got: " + response.time() + "ms");
    }
}