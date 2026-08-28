package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WarehouseResourceImplUnitTest {

  private WarehouseResourceImpl resource;
  private MockWarehouseStore store;

  @BeforeEach
  public void setup() {
    resource = new WarehouseResourceImpl();
    store = new MockWarehouseStore();

    CreateWarehouseOperation createOp =
        w -> {
          if ("EXISTING".equals(w.businessUnitCode)) {
            throw new IllegalArgumentException("Already exists");
          }
          store.create(w);
        };

    ReplaceWarehouseOperation replaceOp =
        w -> {
          if ("FAIL_REPLACE".equals(w.businessUnitCode)) {
            throw new IllegalArgumentException("Replace failed");
          }
        };

    ArchiveWarehouseOperation archiveOp =
        w -> {
          if ("FAIL_ARCHIVE".equals(w.businessUnitCode)) {
            throw new IllegalArgumentException("Archive failed");
          }
        };

    // Inject using reflection or package-private setter if needed, or field access
    setField(resource, "warehouseStore", store);
    setField(resource, "createWarehouseOperation", createOp);
    setField(resource, "replaceWarehouseOperation", replaceOp);
    setField(resource, "archiveWarehouseOperation", archiveOp);
  }

  @Test
  public void testListAllWarehousesUnits() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";
    w.location = "ZWOLLE-001";
    w.capacity = 50;
    w.stock = 10;
    store.create(w);

    var list = resource.listAllWarehousesUnits();
    assertEquals(1, list.size());
    assertEquals("MWH.001", list.get(0).getBusinessUnitCode());
  }

  @Test
  public void testCreateANewWarehouseUnitSuccess() {
    com.warehouse.api.beans.Warehouse dto = new com.warehouse.api.beans.Warehouse();
    dto.setBusinessUnitCode("MWH.002");
    dto.setLocation("ZWOLLE-001");
    dto.setCapacity(50);
    dto.setStock(10);

    var res = resource.createANewWarehouseUnit(dto);
    assertNotNull(res);
    assertEquals("MWH.002", res.getBusinessUnitCode());
  }

  @Test
  public void testCreateANewWarehouseUnitFailure() {
    com.warehouse.api.beans.Warehouse dto = new com.warehouse.api.beans.Warehouse();
    dto.setBusinessUnitCode("EXISTING");

    assertThrows(
        WebApplicationException.class, () -> resource.createANewWarehouseUnit(dto));
  }

  @Test
  public void testGetAWarehouseUnitByID() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";
    store.create(w);

    var res = resource.getAWarehouseUnitByID("MWH.001");
    assertNotNull(res);

    assertThrows(
        WebApplicationException.class,
        () -> resource.getAWarehouseUnitByID("NON_EXISTENT"));
  }

  @Test
  public void testArchiveAWarehouseUnitByID() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";
    store.create(w);

    resource.archiveAWarehouseUnitByID("MWH.001");

    assertThrows(
        WebApplicationException.class,
        () -> resource.archiveAWarehouseUnitByID("NON_EXISTENT"));

    Warehouse wFail = new Warehouse();
    wFail.businessUnitCode = "FAIL_ARCHIVE";
    store.create(wFail);
    assertThrows(
        WebApplicationException.class,
        () -> resource.archiveAWarehouseUnitByID("FAIL_ARCHIVE"));
  }

  @Test
  public void testReplaceTheCurrentActiveWarehouse() {
    com.warehouse.api.beans.Warehouse dto = new com.warehouse.api.beans.Warehouse();
    dto.setBusinessUnitCode("MWH.001");
    dto.setLocation("ZWOLLE-001");
    dto.setCapacity(60);
    dto.setStock(10);

    var res = resource.replaceTheCurrentActiveWarehouse("MWH.001", dto);
    assertNotNull(res);

    dto.setBusinessUnitCode("FAIL_REPLACE");
    assertThrows(
        WebApplicationException.class,
        () -> resource.replaceTheCurrentActiveWarehouse("FAIL_REPLACE", dto));
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      var field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static class MockWarehouseStore implements WarehouseStore {
    private final List<Warehouse> warehouses = new ArrayList<>();

    @Override
    public List<Warehouse> getAll() {
      return warehouses;
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
          .filter(w -> buCode.equalsIgnoreCase(w.businessUnitCode))
          .findFirst()
          .orElse(null);
    }

    @Override
    public List<Warehouse> getActiveByLocation(String location) {
      return warehouses;
    }
  }
}
