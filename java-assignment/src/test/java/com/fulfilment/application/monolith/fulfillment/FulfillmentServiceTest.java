package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FulfillmentServiceTest {

  private FulfillmentService fulfillmentService;

  @BeforeEach
  public void setup() {
    fulfillmentService = new FulfillmentService();
  }

  @Test
  public void testSuccessfulAssociation() {
    assertDoesNotThrow(() -> fulfillmentService.associateFulfillment("MWH.001", 1L, 10L));
  }

  @Test
  public void testConstraint1Max2WarehousesPerProductPerStore() {
    fulfillmentService.associateFulfillment("MWH.001", 1L, 10L);
    fulfillmentService.associateFulfillment("MWH.002", 1L, 10L);

    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.associateFulfillment("MWH.003", 1L, 10L));
  }

  @Test
  public void testConstraint2Max3WarehousesPerStore() {
    fulfillmentService.associateFulfillment("MWH.001", 1L, 10L);
    fulfillmentService.associateFulfillment("MWH.002", 2L, 10L);
    fulfillmentService.associateFulfillment("MWH.003", 3L, 10L);

    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.associateFulfillment("MWH.004", 4L, 10L));
  }

  @Test
  public void testConstraint3Max5ProductTypesPerWarehouse() {
    fulfillmentService.associateFulfillment("MWH.001", 1L, 10L);
    fulfillmentService.associateFulfillment("MWH.001", 2L, 10L);
    fulfillmentService.associateFulfillment("MWH.001", 3L, 10L);
    fulfillmentService.associateFulfillment("MWH.001", 4L, 10L);
    fulfillmentService.associateFulfillment("MWH.001", 5L, 10L);

    assertThrows(
        IllegalArgumentException.class,
        () -> fulfillmentService.associateFulfillment("MWH.001", 6L, 10L));
  }
}
