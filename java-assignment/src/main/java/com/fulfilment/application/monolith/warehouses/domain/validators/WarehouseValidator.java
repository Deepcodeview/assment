package com.fulfilment.application.monolith.warehouses.domain.validators;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class WarehouseValidator {

  public void validateForCreate(
      Warehouse warehouse, WarehouseStore warehouseStore, LocationResolver locationResolver) {
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
  }

  public Warehouse validateForReplace(
      Warehouse newWarehouse, WarehouseStore warehouseStore, LocationResolver locationResolver) {
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

    return oldWarehouse;
  }

  public Warehouse validateForArchive(Warehouse warehouse, WarehouseStore warehouseStore) {
    if (warehouse == null || warehouse.businessUnitCode == null) {
      throw new IllegalArgumentException("Warehouse business unit code is required.");
    }

    Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
    if (existing == null) {
      throw new IllegalArgumentException(
          "Warehouse with code " + warehouse.businessUnitCode + " not found or already archived.");
    }

    return existing;
  }
}
