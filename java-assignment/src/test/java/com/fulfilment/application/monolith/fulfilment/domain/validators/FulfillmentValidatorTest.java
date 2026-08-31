package com.fulfilment.application.monolith.fulfilment.domain.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.fulfilment.domain.models.ProductStoreFulfillment;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FulfillmentValidatorTest {

  private FulfillmentValidator validator;

  @BeforeEach
  public void setup() {
    validator = new FulfillmentValidator();
  }

  @Test
  public void testNullInputsThrowException() {
    List<ProductStoreFulfillment> emptyList = new ArrayList<>();

    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation(null, 1L, 1L, emptyList));
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.001", null, 1L, emptyList));
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.001", 1L, null, emptyList));
  }

  @Test
  public void testConstraint1Max2WarehousesPerProductPerStore() {
    List<ProductStoreFulfillment> fulfillments = new ArrayList<>();
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 1L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.002", 1L, 10L));

    // Trying to add a 3rd warehouse for product 1 at store 10 should fail
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.003", 1L, 10L, fulfillments));

    // Re-associating an existing warehouse shouldn't throw
    assertDoesNotThrow(() -> validator.validateAssociation("MWH.001", 1L, 10L, fulfillments));
  }

  @Test
  public void testConstraint2Max3WarehousesPerStore() {
    List<ProductStoreFulfillment> fulfillments = new ArrayList<>();
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 1L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.002", 2L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.003", 3L, 10L));

    // Trying to introduce a 4th warehouse for store 10 should fail
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.004", 4L, 10L, fulfillments));
  }

  @Test
  public void testConstraint3Max5ProductsPerWarehouse() {
    List<ProductStoreFulfillment> fulfillments = new ArrayList<>();
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 1L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 2L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 3L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 4L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 5L, 10L));

    // Trying to add a 6th product to MWH.001 should fail
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.001", 6L, 10L, fulfillments));
  }
}
