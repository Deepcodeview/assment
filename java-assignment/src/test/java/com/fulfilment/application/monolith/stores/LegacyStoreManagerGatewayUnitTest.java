package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class LegacyStoreManagerGatewayUnitTest {

  @Test
  public void testLegacyStoreManagerGatewayEvents() {
    LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();
    Store store = new Store("TEST-STORE");
    store.quantityProductsInStock = 10;

    assertDoesNotThrow(() -> gateway.onStoreCreated(new StoreCreatedEvent(store)));
    assertDoesNotThrow(() -> gateway.onStoreUpdated(new StoreUpdatedEvent(store)));
    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
  }
}
