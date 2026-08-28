package com.fulfilment.application.monolith.warehouses.domain.validators;

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

public class WarehouseValidatorTest {

  private WarehouseValidator validator;
  private InMemoryWarehouseStore warehouseStore;
  private LocationResolver locationResolver;

  @BeforeEach
  public void setup() {
    validator = new WarehouseValidator();
    warehouseStore = new InMemoryWarehouseStore();
    locationResolver =
        id -> {
          if ("ZWOLLE-001".equalsIgnoreCase(id)) {
            return new Location("ZWOLLE-001", 2, 100);
          }
          if ("AMSTERDAM-001".equalsIgnoreCase(id)) {
            return new Location("AMSTERDAM-001", 5, 200);
          }
          return null;
        };
  }

  @Test
  public void testValidateForCreateAllBranches() {
    // Null warehouse
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(null, warehouseStore, locationResolver));

    // Null businessUnitCode
    Warehouse w = new Warehouse();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(w, warehouseStore, locationResolver));

    // Existing BU Code
    Warehouse existing = new Warehouse();
    existing.businessUnitCode = "MWH.001";
    existing.location = "ZWOLLE-001";
    existing.capacity = 30;
    existing.stock = 10;
    warehouseStore.create(existing);

    Warehouse duplicateBu = new Warehouse();
    duplicateBu.businessUnitCode = "MWH.001";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(duplicateBu, warehouseStore, locationResolver));

    // Null location
    Warehouse wNoLoc = new Warehouse();
    wNoLoc.businessUnitCode = "MWH.002";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wNoLoc, warehouseStore, locationResolver));

    // Invalid location
    Warehouse wInvalidLoc = new Warehouse();
    wInvalidLoc.businessUnitCode = "MWH.002";
    wInvalidLoc.location = "UNKNOWN";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wInvalidLoc, warehouseStore, locationResolver));

    // Null capacity
    Warehouse wNullCap = new Warehouse();
    wNullCap.businessUnitCode = "MWH.002";
    wNullCap.location = "ZWOLLE-001";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wNullCap, warehouseStore, locationResolver));

    // Capacity <= 0
    Warehouse wZeroCap = new Warehouse();
    wZeroCap.businessUnitCode = "MWH.002";
    wZeroCap.location = "ZWOLLE-001";
    wZeroCap.capacity = 0;
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wZeroCap, warehouseStore, locationResolver));

    // Null stock
    Warehouse wNullStock = new Warehouse();
    wNullStock.businessUnitCode = "MWH.002";
    wNullStock.location = "ZWOLLE-001";
    wNullStock.capacity = 50;
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wNullStock, warehouseStore, locationResolver));

    // Stock < 0
    Warehouse wNegStock = new Warehouse();
    wNegStock.businessUnitCode = "MWH.002";
    wNegStock.location = "ZWOLLE-001";
    wNegStock.capacity = 50;
    wNegStock.stock = -5;
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wNegStock, warehouseStore, locationResolver));

    // Stock > Capacity
    Warehouse wExceedStock = new Warehouse();
    wExceedStock.businessUnitCode = "MWH.002";
    wExceedStock.location = "ZWOLLE-001";
    wExceedStock.capacity = 50;
    wExceedStock.stock = 60;
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wExceedStock, warehouseStore, locationResolver));

    // Capacity overflow at location
    Warehouse wOverflow = new Warehouse();
    wOverflow.businessUnitCode = "MWH.002";
    wOverflow.location = "ZWOLLE-001";
    wOverflow.capacity = 80; // 30 (existing) + 80 = 110 > 100 max
    wOverflow.stock = 10;
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForCreate(wOverflow, warehouseStore, locationResolver));

    // Valid create
    Warehouse wValid = new Warehouse();
    wValid.businessUnitCode = "MWH.002";
    wValid.location = "ZWOLLE-001";
    wValid.capacity = 50; // 30 + 50 = 80 <= 100
    wValid.stock = 10;
    assertDoesNotThrow(
        () -> validator.validateForCreate(wValid, warehouseStore, locationResolver));
  }

  @Test
  public void testValidateForReplaceAllBranches() {
    // Null newWarehouse / BU code
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(null, warehouseStore, locationResolver));

    Warehouse newW = new Warehouse();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newW, warehouseStore, locationResolver));

    // Old warehouse not found
    newW.businessUnitCode = "NON_EXISTENT";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newW, warehouseStore, locationResolver));

    // Setup active warehouse
    Warehouse oldW = new Warehouse();
    oldW.businessUnitCode = "MWH.001";
    oldW.location = "ZWOLLE-001";
    oldW.capacity = 50;
    oldW.stock = 20;
    warehouseStore.create(oldW);

    // Null new location
    Warehouse newNoLoc = new Warehouse();
    newNoLoc.businessUnitCode = "MWH.001";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newNoLoc, warehouseStore, locationResolver));

    // Invalid new location
    Warehouse newInvalidLoc = new Warehouse();
    newInvalidLoc.businessUnitCode = "MWH.001";
    newInvalidLoc.location = "INVALID_LOC";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newInvalidLoc, warehouseStore, locationResolver));

    // Null capacity
    Warehouse newNullCap = new Warehouse();
    newNullCap.businessUnitCode = "MWH.001";
    newNullCap.location = "ZWOLLE-001";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newNullCap, warehouseStore, locationResolver));

    // New capacity < old stock
    Warehouse newLowCap = new Warehouse();
    newLowCap.businessUnitCode = "MWH.001";
    newLowCap.location = "ZWOLLE-001";
    newLowCap.capacity = 10; // 10 < 20
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newLowCap, warehouseStore, locationResolver));

    // Null stock
    Warehouse newNullStock = new Warehouse();
    newNullStock.businessUnitCode = "MWH.001";
    newNullStock.location = "ZWOLLE-001";
    newNullStock.capacity = 60;
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newNullStock, warehouseStore, locationResolver));

    // Stock mismatch
    Warehouse newStockMismatch = new Warehouse();
    newStockMismatch.businessUnitCode = "MWH.001";
    newStockMismatch.location = "ZWOLLE-001";
    newStockMismatch.capacity = 60;
    newStockMismatch.stock = 25; // 25 != 20
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newStockMismatch, warehouseStore, locationResolver));

    // Location capacity overflow on replacement at same location
    Warehouse newOverflowSameLoc = new Warehouse();
    newOverflowSameLoc.businessUnitCode = "MWH.001";
    newOverflowSameLoc.location = "ZWOLLE-001";
    newOverflowSameLoc.capacity = 150; // 150 > 100
    newOverflowSameLoc.stock = 20;
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForReplace(newOverflowSameLoc, warehouseStore, locationResolver));

    // Valid replace at same location
    Warehouse newValidSameLoc = new Warehouse();
    newValidSameLoc.businessUnitCode = "MWH.001";
    newValidSameLoc.location = "ZWOLLE-001";
    newValidSameLoc.capacity = 70;
    newValidSameLoc.stock = 20;
    Warehouse res =
        validator.validateForReplace(newValidSameLoc, warehouseStore, locationResolver);
    assertNotNull(res);
    assertEquals("MWH.001", res.businessUnitCode);

    // Valid replace at DIFFERENT location (AMSTERDAM-001)
    Warehouse newValidDiffLoc = new Warehouse();
    newValidDiffLoc.businessUnitCode = "MWH.001";
    newValidDiffLoc.location = "AMSTERDAM-001";
    newValidDiffLoc.capacity = 80;
    newValidDiffLoc.stock = 20;
    assertDoesNotThrow(
        () -> validator.validateForReplace(newValidDiffLoc, warehouseStore, locationResolver));
  }

  @Test
  public void testValidateForArchiveAllBranches() {
    assertThrows(
        IllegalArgumentException.class, () -> validator.validateForArchive(null, warehouseStore));

    Warehouse wNullBu = new Warehouse();
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForArchive(wNullBu, warehouseStore));

    Warehouse wNotFound = new Warehouse();
    wNotFound.businessUnitCode = "NOT_FOUND";
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateForArchive(wNotFound, warehouseStore));

    Warehouse active = new Warehouse();
    active.businessUnitCode = "MWH.001";
    warehouseStore.create(active);

    Warehouse found = validator.validateForArchive(active, warehouseStore);
    assertNotNull(found);
    assertEquals("MWH.001", found.businessUnitCode);
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
