package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

  @Test
  public void testStoreCrudOperations() {
    final String path = "store";

    // 1. List all initial stores (TONSTAD, KALLAX, BESTÅ from import.sql)
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));

    // 2. Create a new store
    Store newStore = new Store("ROTTERDAM-STORE");
    newStore.quantityProductsInStock = 25;

    given()
        .contentType(ContentType.JSON)
        .body(newStore)
        .when()
        .post(path)
        .then()
        .statusCode(201)
        .body(containsString("ROTTERDAM-STORE"));

    // 3. Verify it appears in the list
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("ROTTERDAM-STORE"));
  }
}
