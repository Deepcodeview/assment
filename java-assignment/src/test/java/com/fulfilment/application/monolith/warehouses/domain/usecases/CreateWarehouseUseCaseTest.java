package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  private InMemoryWarehouseStore warehouseStore;
  private LocationResolver locationResolver;
  private CreateWarehouseUseCase createWarehouseUseCase;

  @BeforeEach
  public void setup() {
    warehouseStore = new InMemoryWarehouseStore();
    locationResolver =
        id -> "ZWOLLE-001".equalsIgnoreCase(id) ? new Location("ZWOLLE-001", 2, 100) : null;
    createWarehouseUseCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
  }

  @Test
  public void testSuccessfulWarehouseCreation() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.100";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 50;
    warehouse.stock = 10;

    assertDoesNotThrow(() -> createWarehouseUseCase.create(warehouse));
    assertEquals(1, warehouseStore.getAll().size());
  }

  @Test
  public void testDuplicateBusinessUnitCodeThrowsException() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.100";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 50;
    warehouse.stock = 10;

    createWarehouseUseCase.create(warehouse);

    Warehouse duplicate = new Warehouse();
    duplicate.businessUnitCode = "MWH.100";
    duplicate.location = "ZWOLLE-001";
    duplicate.capacity = 30;
    duplicate.stock = 5;

    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(duplicate));
  }

  @Test
  public void testInvalidLocationThrowsException() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.200";
    warehouse.location = "INVALID-LOC";
    warehouse.capacity = 50;
    warehouse.stock = 10;

    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
  }

  @Test
  public void testStockExceedsCapacityThrowsException() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.300";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 40;
    warehouse.stock = 50;

    assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
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
