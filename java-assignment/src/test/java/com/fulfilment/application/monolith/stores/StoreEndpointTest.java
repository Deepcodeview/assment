package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

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

    // 2. GET single store by ID
    given()
        .when()
        .get(path + "/1")
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD"));

    // 3. GET single non-existent store (404)
    given()
        .when()
        .get(path + "/99999")
        .then()
        .statusCode(404);

    // 4. Create a new store
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

    // 5. Create with ID invalid (422)
    Store invalidStore = new Store("INVALID-STORE");
    invalidStore.id = 999L;
    given()
        .contentType(ContentType.JSON)
        .body(invalidStore)
        .when()
        .post(path)
        .then()
        .statusCode(422);

    // 6. PUT update store
    Store updateStore = new Store("TONSTAD-UPDATED");
    updateStore.quantityProductsInStock = 50;

    given()
        .contentType(ContentType.JSON)
        .body(updateStore)
        .when()
        .put(path + "/1")
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD-UPDATED"));

    // 7. PUT with null name (422)
    Store nullNameStore = new Store(null);
    given()
        .contentType(ContentType.JSON)
        .body(nullNameStore)
        .when()
        .put(path + "/1")
        .then()
        .statusCode(422);

    // 8. PUT non-existent store (404)
    given()
        .contentType(ContentType.JSON)
        .body(updateStore)
        .when()
        .put(path + "/99999")
        .then()
        .statusCode(404);

    // 9. PATCH store
    Store patchStore = new Store("TONSTAD-PATCHED");
    patchStore.quantityProductsInStock = 75;

    given()
        .contentType(ContentType.JSON)
        .body(patchStore)
        .when()
        .patch(path + "/1")
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD-PATCHED"));

    // 10. PATCH non-existent store (404)
    given()
        .contentType(ContentType.JSON)
        .body(patchStore)
        .when()
        .patch(path + "/99999")
        .then()
        .statusCode(404);

    // 11. DELETE store
    given()
        .when()
        .delete(path + "/3")
        .then()
        .statusCode(204);

    // 12. DELETE non-existent store (404)
    given()
        .when()
        .delete(path + "/99999")
        .then()
        .statusCode(404);
  }
}

