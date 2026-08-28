package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ProductUnitTest {

  @Test
  public void testProductEntityFields() {
    Product p1 = new Product();
    assertNull(p1.name);

    Product p2 = new Product("TEST-PRODUCT");
    assertEquals("TEST-PRODUCT", p2.name);
    p2.description = "Test desc";
    p2.price = BigDecimal.TEN;
    p2.stock = 100;

    assertEquals("Test desc", p2.description);
    assertEquals(BigDecimal.TEN, p2.price);
    assertEquals(100, p2.stock);
  }
}
