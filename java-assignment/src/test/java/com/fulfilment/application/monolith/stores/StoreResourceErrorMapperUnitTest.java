package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

public class StoreResourceErrorMapperUnitTest {

  @Test
  public void testErrorMapper() {
    StoreResource.ErrorMapper mapper = new StoreResource.ErrorMapper();
    setField(mapper, "objectMapper", new ObjectMapper());

    Response r1 = mapper.toResponse(new WebApplicationException("Store not found", 404));
    assertEquals(404, r1.getStatus());

    Response r2 = mapper.toResponse(new RuntimeException("Generic Error"));
    assertEquals(500, r2.getStatus());

    // Null message exception branch
    Response r3 = mapper.toResponse(new NullPointerException());
    assertEquals(500, r3.getStatus());
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
}
