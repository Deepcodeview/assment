package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  @Inject
  public ArchiveWarehouseUseCase(
      WarehouseStore warehouseStore, WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this(warehouseStore, new WarehouseValidator());
  }

  @Override
  public void archive(Warehouse warehouse) {
    Warehouse existing = warehouseValidator.validateForArchive(warehouse, warehouseStore);
    existing.archivedAt = LocalDateTime.now();
    warehouseStore.update(existing);
  }
}

