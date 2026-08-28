package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class DbWarehouseUnitTest {

  @Test
  public void testDbWarehouseMapping() {
    DbWarehouse empty = new DbWarehouse();
    assertNull(empty.businessUnitCode);

    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";
    w.location = "ZWOLLE-001";
    w.capacity = 100;
    w.stock = 50;
    LocalDateTime now = LocalDateTime.now();
    w.createdAt = now;
    w.archivedAt = null;

    DbWarehouse dbw = new DbWarehouse(w);
    assertEquals("MWH.001", dbw.businessUnitCode);
    assertEquals("ZWOLLE-001", dbw.location);
    assertEquals(100, dbw.capacity);
    assertEquals(50, dbw.stock);
    assertEquals(now, dbw.createdAt);
    assertNull(dbw.archivedAt);

    Warehouse back = dbw.toWarehouse();
    assertEquals("MWH.001", back.businessUnitCode);
    assertEquals("ZWOLLE-001", back.location);
    assertEquals(100, back.capacity);
    assertEquals(50, back.stock);
    assertEquals(now, back.createdAt);
  }
}
