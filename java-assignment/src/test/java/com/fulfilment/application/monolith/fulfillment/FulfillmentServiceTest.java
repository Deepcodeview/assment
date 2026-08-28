package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.fulfillment.domain.models.ProductStoreFulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.usecases.FulfillmentService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FulfillmentServiceTest {

  private FulfillmentService fulfillmentService;

  @BeforeEach
  public void setup() {
    fulfillmentService = new FulfillmentService();
  }

  @Test
  public void testAssociateFulfillmentAndBranches() {
    // 1. First association
    fulfillmentService.associateFulfillment("MWH.001", 1L, 10L);
    List<ProductStoreFulfillment> list = fulfillmentService.getFulfillments();
    assertEquals(1, list.size());

    // 2. Already associated (matches all 3 conditions: buCode, productId, storeId)
    fulfillmentService.associateFulfillment("MWH.001", 1L, 10L);
    assertEquals(1, fulfillmentService.getFulfillments().size());

    // 3. Different buCode (buCode branch false)
    fulfillmentService.associateFulfillment("MWH.002", 1L, 10L);
    assertEquals(2, fulfillmentService.getFulfillments().size());

    // 4. Same buCode, different productId (productId branch false)
    fulfillmentService.associateFulfillment("MWH.001", 2L, 10L);
    assertEquals(3, fulfillmentService.getFulfillments().size());

    // 5. Same buCode, same productId, different storeId (storeId branch false)
    fulfillmentService.associateFulfillment("MWH.001", 1L, 20L);
    assertEquals(4, fulfillmentService.getFulfillments().size());
  }
}
