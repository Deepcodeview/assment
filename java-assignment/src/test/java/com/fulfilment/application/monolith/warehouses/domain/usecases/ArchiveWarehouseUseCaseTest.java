package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

  private InMemoryWarehouseStore warehouseStore;
  private ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @BeforeEach
  public void setup() {
    warehouseStore = new InMemoryWarehouseStore();
    archiveWarehouseUseCase = new ArchiveWarehouseUseCase(warehouseStore);

    Warehouse initial = new Warehouse();
    initial.businessUnitCode = "MWH.001";
    initial.location = "ZWOLLE-001";
    initial.capacity = 50;
    initial.stock = 10;
    warehouseStore.create(initial);
  }

  @Test
  public void testArchiveExistingWarehouse() {
    Warehouse warehouseToArchive = new Warehouse();
    warehouseToArchive.businessUnitCode = "MWH.001";

    assertDoesNotThrow(() -> archiveWarehouseUseCase.archive(warehouseToArchive));
    assertNull(warehouseStore.findByBusinessUnitCode("MWH.001"));
  }

  @Test
  public void testArchiveNonExistingWarehouseThrowsException() {
    Warehouse warehouseToArchive = new Warehouse();
    warehouseToArchive.businessUnitCode = "UNKNOWN.999";

    assertThrows(
        IllegalArgumentException.class, () -> archiveWarehouseUseCase.archive(warehouseToArchive));
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
