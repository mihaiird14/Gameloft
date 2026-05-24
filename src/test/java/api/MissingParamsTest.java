package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class MissingParamsTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://play.ludigames.com";
    }

    @Test
    @DisplayName("TC-API-08: category.html without catId param")
    public void testCategoryPageWithoutParam() {
        Response response = given()
                .when()
                .get("/category.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-08 /category.html (no param) Results ===");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response time: " + response.time() + "ms");;

        assertNotEquals(500, response.statusCode(),
                "/category.html without catId should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s, got: " + response.time() + "ms");
        String body = response.getBody().asString();
        assertTrue(body.length() > 100,
                "Response body should not be empty when catId is missing");
    }

    @Test
    @DisplayName("TC-API-09: game.html without pID param")
    public void testGamePageWithoutParam() {
        Response response = given()
                .when()
                .get("/game.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-09 /game.html (no param) Results ===");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response time: " + response.time() + "ms");

        assertNotEquals(500, response.statusCode(),
                "/game.html without pID should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s, got: " + response.time() + "ms");

        String body = response.getBody().asString();
        assertTrue(body.length() > 100,
                "Response body should not be empty when pID is missing");
    }

    @Test
    @DisplayName("TC-API-10: search.html without search param")
    public void testSearchPageWithoutParam() {
        Response response = given()
                .when()
                .get("/search.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-10 /search.html (no param) Results ===");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response time: " + response.time() + "ms");

        assertNotEquals(500, response.statusCode(),
                "/search.html without search param should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s, got: " + response.time() + "ms");

        String body = response.getBody().asString();
        assertTrue(body.length() > 100,
                "Response body should not be empty when search param is missing");
    }

    @Test
    @DisplayName("TC-API-11: hw-detail.html without hwpr param")
    public void testNewsDetailWithoutParam() {
        Response response = given()
                .when()
                .get("/hw-detail.html")
                .then()
                .extract().response();

        System.out.println("=== TC-API-11 /hw-detail.html (no param) Results ===");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response time: " + response.time() + "ms");

        assertNotEquals(500, response.statusCode(),
                "/hw-detail.html without hwpr should not cause server error");
        assertTrue(response.time() < 5000,
                "Server should respond within 5s, got: " + response.time() + "ms");
    }
}