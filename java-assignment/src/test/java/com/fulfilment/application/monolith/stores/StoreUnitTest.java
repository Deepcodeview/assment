package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class StoreUnitTest {

  @Test
  public void testStoreConstructorsAndFields() {
    Store empty = new Store();
    assertNull(empty.name);
    assertEquals(0, empty.quantityProductsInStock);

    Store s = new Store("AMSTERDAM-STORE");
    assertEquals("AMSTERDAM-STORE", s.name);
    s.quantityProductsInStock = 50;
    assertEquals(50, s.quantityProductsInStock);
  }

  @Test
  public void testStoreEvents() {
    Store s = new Store("ROTTERDAM-STORE");
    StoreCreatedEvent createdEvent = new StoreCreatedEvent(s);
    assertEquals(s, createdEvent.store());

    StoreUpdatedEvent updatedEvent = new StoreUpdatedEvent(s);
    assertEquals(s, updatedEvent.store());
  }
}
