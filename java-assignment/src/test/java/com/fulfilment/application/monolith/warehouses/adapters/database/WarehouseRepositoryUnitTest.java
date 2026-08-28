package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WarehouseRepositoryUnitTest {

  private WarehouseRepository repository;

  @BeforeEach
  public void setup() {
    repository = spy(new WarehouseRepository());
  }

  @Test
  public void testGetAll() {
    DbWarehouse dbw = new DbWarehouse();
    dbw.businessUnitCode = "MWH.001";
    doReturn(List.of(dbw)).when(repository).list("archivedAt is null");

    List<Warehouse> res = repository.getAll();
    assertEquals(1, res.size());
    assertEquals("MWH.001", res.get(0).businessUnitCode);
  }

  @Test
  public void testCreate() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";
    w.location = "ZWOLLE-001";
    w.capacity = 100;
    w.stock = 10;

    doNothing().when(repository).persist(any(DbWarehouse.class));

    repository.create(w);
    assertNotNull(w.createdAt);
    verify(repository).persist(any(DbWarehouse.class));
  }

  @Test
  public void testUpdateActiveFound() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";
    w.location = "ZWOLLE-001";
    w.capacity = 100;
    w.stock = 10;

    DbWarehouse dbw = new DbWarehouse(w);
    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> queryMock = mock(PanacheQuery.class);
    doReturn(dbw).when(queryMock).firstResult();

    doReturn(queryMock)
        .when(repository)
        .find(eq("businessUnitCode = ?1 and archivedAt is null"), eq("MWH.001"));
    doNothing().when(repository).persist(any(DbWarehouse.class));

    repository.update(w);
    verify(repository).persist(any(DbWarehouse.class));
  }

  @Test
  public void testUpdateFallbackArchivedFound() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";

    DbWarehouse dbw = new DbWarehouse(w);
    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> queryMockNull = mock(PanacheQuery.class);
    doReturn(null).when(queryMockNull).firstResult();

    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> queryMockFound = mock(PanacheQuery.class);
    doReturn(dbw).when(queryMockFound).firstResult();

    doReturn(queryMockNull)
        .when(repository)
        .find(eq("businessUnitCode = ?1 and archivedAt is null"), eq("MWH.001"));
    doReturn(queryMockFound).when(repository).find(eq("businessUnitCode = ?1"), eq("MWH.001"));
    doNothing().when(repository).persist(any(DbWarehouse.class));

    repository.update(w);
    verify(repository).persist(any(DbWarehouse.class));
  }

  @Test
  public void testUpdateNotFound() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";

    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> queryMockNull = mock(PanacheQuery.class);
    doReturn(null).when(queryMockNull).firstResult();

    doReturn(queryMockNull)
        .when(repository)
        .find(eq("businessUnitCode = ?1 and archivedAt is null"), eq("MWH.001"));
    doReturn(queryMockNull).when(repository).find(eq("businessUnitCode = ?1"), eq("MWH.001"));

    repository.update(w);
    verify(repository, never()).persist(any(DbWarehouse.class));
  }

  @Test
  public void testRemoveFoundAndNotFound() {
    Warehouse w = new Warehouse();
    w.businessUnitCode = "MWH.001";

    DbWarehouse dbw = new DbWarehouse(w);
    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> queryMock = mock(PanacheQuery.class);
    doReturn(dbw).when(queryMock).firstResult();

    doReturn(queryMock).when(repository).find(eq("businessUnitCode = ?1"), eq("MWH.001"));
    doNothing().when(repository).delete(any(DbWarehouse.class));

    repository.remove(w);
    verify(repository).delete(any(DbWarehouse.class));

    // Not found branch
    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> queryMockNull = mock(PanacheQuery.class);
    doReturn(null).when(queryMockNull).firstResult();
    doReturn(queryMockNull).when(repository).find(eq("businessUnitCode = ?1"), eq("MWH.999"));
    Warehouse w999 = new Warehouse();
    w999.businessUnitCode = "MWH.999";

    repository.remove(w999);
  }

  @Test
  public void testFindByBusinessUnitCode() {
    DbWarehouse dbw = new DbWarehouse();
    dbw.businessUnitCode = "MWH.001";

    @SuppressWarnings("unchecked")
    PanacheQuery<DbWarehouse> queryMock = mock(PanacheQuery.class);
    doReturn(dbw).when(queryMock).firstResult();

    doReturn(queryMock)
        .when(repository)
        .find(eq("businessUnitCode = ?1 and archivedAt is null"), eq("MWH.001"));

    Warehouse res = repository.findByBusinessUnitCode("MWH.001");
    assertNotNull(res);
    assertEquals("MWH.001", res.businessUnitCode);

    doReturn(queryMock)
        .when(repository)
        .find(eq("businessUnitCode = ?1 and archivedAt is null"), eq("NOT_FOUND"));
    doReturn(null).when(queryMock).firstResult();
    assertNull(repository.findByBusinessUnitCode("NOT_FOUND"));
  }

  @Test
  public void testGetActiveByLocation() {
    DbWarehouse dbw = new DbWarehouse();
    dbw.location = "ZWOLLE-001";

    doReturn(List.of(dbw))
        .when(repository)
        .list(eq("location = ?1 and archivedAt is null"), eq("ZWOLLE-001"));

    List<Warehouse> res = repository.getActiveByLocation("ZWOLLE-001");
    assertEquals(1, res.size());
    assertEquals("ZWOLLE-001", res.get(0).location);
  }
}
