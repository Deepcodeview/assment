package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    if (newWarehouse == null || newWarehouse.businessUnitCode == null) {
      throw new IllegalArgumentException("Business Unit Code is required for replacement.");
    }

    Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (oldWarehouse == null) {
      throw new IllegalArgumentException(
          "Active warehouse with business unit code "
              + newWarehouse.businessUnitCode
              + " not found.");
    }

    // Location validation
    if (newWarehouse.location == null) {
      throw new IllegalArgumentException("Location is required for new warehouse.");
    }
    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location " + newWarehouse.location + " is invalid.");
    }

    // Capacity Accommodation Validation
    if (newWarehouse.capacity == null || newWarehouse.capacity < oldWarehouse.stock) {
      throw new IllegalArgumentException(
          "New warehouse capacity ("
              + newWarehouse.capacity
              + ") cannot accommodate old warehouse stock ("
              + oldWarehouse.stock
              + ").");
    }

    // Stock Matching Validation
    if (newWarehouse.stock == null || !Objects.equals(newWarehouse.stock, oldWarehouse.stock)) {
      throw new IllegalArgumentException(
          "New warehouse stock ("
              + newWarehouse.stock
              + ") must match previous warehouse stock ("
              + oldWarehouse.stock
              + ").");
    }

    // Location capacity feasibility check
    List<Warehouse> activeAtLocation = warehouseStore.getActiveByLocation(newWarehouse.location);
    int currentTotalCapacity =
        activeAtLocation.stream().mapToInt(w -> w.capacity != null ? w.capacity : 0).sum();

    if (oldWarehouse.location.equalsIgnoreCase(newWarehouse.location)) {
      currentTotalCapacity -= (oldWarehouse.capacity != null ? oldWarehouse.capacity : 0);
    }

    if (currentTotalCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException(
          "Replacement warehouse exceeds maximum location capacity ("
              + location.maxCapacity
              + ").");
    }

    // Archive current warehouse
    oldWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(oldWarehouse);

    // Create new replacement warehouse
    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}
