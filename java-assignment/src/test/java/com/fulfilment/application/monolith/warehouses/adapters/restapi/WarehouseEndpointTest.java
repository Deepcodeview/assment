package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseEndpointTest {

  @Test
  public void testSimpleListWarehouses() {
    final String path = "warehouse";

    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));
  }

  @Test
  public void testCreateWarehouseEndpoint() {
    final String path = "warehouse";

    String json = "{\"businessUnitCode\":\"MWH.999\",\"location\":\"AMSTERDAM-001\",\"capacity\":30,\"stock\":10}";

    given()
        .contentType(ContentType.JSON)
        .body(json)
        .when()
        .post(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.999"));
  }

  @Test
  public void testReplaceWarehouseEndpoint() {
    final String path = "warehouse";

    String json = "{\"businessUnitCode\":\"MWH.012\",\"location\":\"AMSTERDAM-001\",\"capacity\":60,\"stock\":5}";

    given()
        .contentType(ContentType.JSON)
        .body(json)
        .when()
        .post(path + "/MWH.012/replacement")
        .then()
        .statusCode(200)
        .body(containsString("MWH.012"));
  }

  @Test
  public void testSimpleCheckingArchivingWarehouses() {
    final String path = "warehouse";

    given().when().delete(path + "/MWH.001").then().statusCode(204);

    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(not(containsString("MWH.001")));
  }
}
