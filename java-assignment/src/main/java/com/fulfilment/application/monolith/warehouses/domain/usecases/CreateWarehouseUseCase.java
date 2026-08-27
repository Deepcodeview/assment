package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public CreateWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    if (warehouse == null || warehouse.businessUnitCode == null) {
      throw new IllegalArgumentException("Business Unit Code is required.");
    }

    // 1. Business Unit Code Verification
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException(
          "Warehouse with business unit code " + warehouse.businessUnitCode + " already exists.");
    }

    // 2. Location Validation
    if (warehouse.location == null) {
      throw new IllegalArgumentException("Location is required.");
    }
    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location " + warehouse.location + " is invalid.");
    }

    // 3. Stock & Capacity Validation
    if (warehouse.capacity == null || warehouse.capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than zero.");
    }
    if (warehouse.stock == null || warehouse.stock < 0 || warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Stock cannot exceed warehouse capacity.");
    }

    // 4. Warehouse Creation Feasibility
    List<Warehouse> activeAtLocation = warehouseStore.getActiveByLocation(warehouse.location);

    if (activeAtLocation.size() >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException(
          "Maximum number of warehouses ("
              + location.maxNumberOfWarehouses
              + ") reached for location "
              + warehouse.location);
    }

    int currentTotalCapacity =
        activeAtLocation.stream().mapToInt(w -> w.capacity != null ? w.capacity : 0).sum();
    if (currentTotalCapacity + warehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException(
          "Creating warehouse exceeds maximum location capacity ("
              + location.maxCapacity
              + "). Current: "
              + currentTotalCapacity
              + ", Requested: "
              + warehouse.capacity);
    }

    // Create warehouse
    warehouseStore.create(warehouse);
  }
}
