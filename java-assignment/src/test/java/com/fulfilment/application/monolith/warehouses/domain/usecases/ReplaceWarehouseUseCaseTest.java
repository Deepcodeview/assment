package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

  private InMemoryWarehouseStore warehouseStore;
  private LocationResolver locationResolver;
  private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @BeforeEach
  public void setup() {
    warehouseStore = new InMemoryWarehouseStore();
    locationResolver =
        id -> "ZWOLLE-001".equalsIgnoreCase(id) ? new Location("ZWOLLE-001", 2, 100) : null;
    replaceWarehouseUseCase = new ReplaceWarehouseUseCase(warehouseStore, locationResolver);

    // Populate an initial active warehouse
    Warehouse initial = new Warehouse();
    initial.businessUnitCode = "MWH.001";
    initial.location = "ZWOLLE-001";
    initial.capacity = 50;
    initial.stock = 20;
    warehouseStore.create(initial);
  }

  @Test
  public void testSuccessfulWarehouseReplacement() {
    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = "MWH.001";
    replacement.location = "ZWOLLE-001";
    replacement.capacity = 60;
    replacement.stock = 20; // Must match old stock

    assertDoesNotThrow(() -> replaceWarehouseUseCase.replace(replacement));

    Warehouse active = warehouseStore.findByBusinessUnitCode("MWH.001");
    assertNotNull(active);
    assertEquals(60, active.capacity);
  }

  @Test
  public void testReplacementFailsWhenStockMismatch() {
    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = "MWH.001";
    replacement.location = "ZWOLLE-001";
    replacement.capacity = 60;
    replacement.stock = 25; // Mismatch with old stock 20

    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(replacement));
  }

  @Test
  public void testReplacementFailsWhenCapacityCannotAccommodateStock() {
    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = "MWH.001";
    replacement.location = "ZWOLLE-001";
    replacement.capacity = 15; // Capacity 15 < stock 20
    replacement.stock = 20;

    assertThrows(
        IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(replacement));
  }

  static class InMemoryWarehouseStore implements WarehouseStore {
    private final List<Warehouse> warehouses = new ArrayList<>();

    @Override
    public List<Warehouse> getAll() {
      return warehouses.stream().filter(w -> w.archivedAt == null).toList();
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {}

    @Override
    public void remove(Warehouse warehouse) {}

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return warehouses.stream()
          .filter(w -> buCode.equalsIgnoreCase(w.businessUnitCode) && w.archivedAt == null)
          .findFirst()
          .orElse(null);
    }

    @Override
    public List<Warehouse> getActiveByLocation(String location) {
      return warehouses.stream()
          .filter(w -> location.equalsIgnoreCase(w.location) && w.archivedAt == null)
          .toList();
    }
  }
}
