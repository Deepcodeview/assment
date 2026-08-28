package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreResourceCdiTest {

  @Inject StoreResource storeResource;

  @Test
  public void testStoreResourceDirectCdiInvocations() {
    // 1. GET all
    List<Store> initialStores = storeResource.get();
    assertNotNull(initialStores);

    // 2. GET single existing (TONSTAD has id 1 from import.sql)
    Store s1 = storeResource.getSingle(1L);
    assertNotNull(s1);

    // 3. GET single non-existent (404 branch)
    assertThrows(WebApplicationException.class, () -> storeResource.getSingle(9999L));

    // 4. POST create valid
    Store newStore = new Store("CDI-STORE");
    newStore.quantityProductsInStock = 15;
    Response createResp = storeResource.create(newStore);
    assertEquals(201, createResp.getStatus());

    // 5. POST create invalid (id set - 422 branch)
    Store invalidStore = new Store("INVALID-ID-STORE");
    invalidStore.id = 77L;
    assertThrows(WebApplicationException.class, () -> storeResource.create(invalidStore));

    // 6. PUT update valid
    Store updateDto = new Store("TONSTAD-CDI-UPDATED");
    updateDto.quantityProductsInStock = 100;
    Store updatedStore = storeResource.update(1L, updateDto);
    assertEquals("TONSTAD-CDI-UPDATED", updatedStore.name);

    // 7. PUT update null name (422 branch)
    Store nullNameDto = new Store(null);
    assertThrows(WebApplicationException.class, () -> storeResource.update(1L, nullNameDto));

    // 8. PUT update non-existent (404 branch)
    assertThrows(WebApplicationException.class, () -> storeResource.update(9999L, updateDto));

    // 9. PATCH update valid
    Store patchDto = new Store("TONSTAD-CDI-PATCHED");
    patchDto.quantityProductsInStock = 200;
    Store patchedStore = storeResource.patch(1L, patchDto);
    assertEquals("TONSTAD-CDI-PATCHED", patchedStore.name);

    // 10. PATCH update null name (422 branch)
    assertThrows(WebApplicationException.class, () -> storeResource.patch(1L, nullNameDto));

    // 11. PATCH update non-existent (404 branch)
    assertThrows(WebApplicationException.class, () -> storeResource.patch(9999L, patchDto));

    // 12. DELETE valid (created store)
    Response deleteResp = storeResource.delete(newStore.id);
    assertEquals(204, deleteResp.getStatus());

    // 13. DELETE non-existent (404 branch)
    assertThrows(WebApplicationException.class, () -> storeResource.delete(9999L));
  }
}
