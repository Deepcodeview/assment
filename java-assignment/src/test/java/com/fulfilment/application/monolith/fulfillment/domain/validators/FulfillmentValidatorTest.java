package com.fulfilment.application.monolith.fulfillment.domain.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.fulfillment.domain.models.ProductStoreFulfillment;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FulfillmentValidatorTest {

  private FulfillmentValidator validator;
  private List<ProductStoreFulfillment> fulfillments;

  @BeforeEach
  public void setup() {
    validator = new FulfillmentValidator();
    fulfillments = new ArrayList<>();
  }

  @Test
  public void testNullParameters() {
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation(null, 1L, 10L, fulfillments));
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.001", null, 10L, fulfillments));
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.001", 1L, null, fulfillments));
  }

  @Test
  public void testValidAssociationAndReassociation() {
    assertDoesNotThrow(
        () -> validator.validateAssociation("MWH.001", 1L, 10L, fulfillments));

    fulfillments.add(new ProductStoreFulfillment("MWH.001", 1L, 10L));

    // Re-association of same tuple should pass
    assertDoesNotThrow(
        () -> validator.validateAssociation("MWH.001", 1L, 10L, fulfillments));
  }

  @Test
  public void testConstraint1Max2WarehousesPerProductPerStore() {
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 1L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.002", 1L, 10L));

    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.003", 1L, 10L, fulfillments));
  }

  @Test
  public void testConstraint2Max3WarehousesPerStore() {
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 1L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.002", 2L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.003", 3L, 10L));

    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.004", 4L, 10L, fulfillments));
  }

  @Test
  public void testConstraint3Max5ProductTypesPerWarehouse() {
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 1L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 2L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 3L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 4L, 10L));
    fulfillments.add(new ProductStoreFulfillment("MWH.001", 5L, 10L));

    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAssociation("MWH.001", 6L, 10L, fulfillments));
  }
}
