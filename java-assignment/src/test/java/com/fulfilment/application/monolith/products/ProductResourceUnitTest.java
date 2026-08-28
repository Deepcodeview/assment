package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductResourceUnitTest {

  private ProductResource resource;
  private MockProductRepository repo;

  @BeforeEach
  public void setup() {
    resource = new ProductResource();
    repo = new MockProductRepository();
    setField(resource, "productRepository", repo);
  }

  @Test
  public void testGet() {
    Product p = new Product("P1");
    repo.persist(p);
    List<Product> list = resource.get();
    assertEquals(1, list.size());
  }

  @Test
  public void testGetSingle() {
    Product p = new Product("P1");
    p.id = 1L;
    repo.persist(p);

    Product found = resource.getSingle(1L);
    assertEquals("P1", found.name);

    assertThrows(WebApplicationException.class, () -> resource.getSingle(99L));
  }

  @Test
  public void testCreate() {
    Product pValid = new Product("P1");
    Response res = resource.create(pValid);
    assertEquals(201, res.getStatus());

    Product pInvalid = new Product("P2");
    pInvalid.id = 10L;
    assertThrows(WebApplicationException.class, () -> resource.create(pInvalid));
  }

  @Test
  public void testUpdate() {
    Product p = new Product("P1");
    p.id = 1L;
    repo.persist(p);

    Product updateDto = new Product("P1-Updated");
    Product updated = resource.update(1L, updateDto);
    assertEquals("P1-Updated", updated.name);

    Product nullNameDto = new Product();
    assertThrows(WebApplicationException.class, () -> resource.update(1L, nullNameDto));

    assertThrows(WebApplicationException.class, () -> resource.update(99L, updateDto));
  }

  @Test
  public void testDelete() {
    Product p = new Product("P1");
    p.id = 1L;
    repo.persist(p);

    Response res = resource.delete(1L);
    assertEquals(204, res.getStatus());

    assertThrows(WebApplicationException.class, () -> resource.delete(99L));
  }

  @Test
  public void testErrorMapper() {
    ProductResource.ErrorMapper mapper = new ProductResource.ErrorMapper();
    setField(mapper, "objectMapper", new ObjectMapper());

    Response r1 = mapper.toResponse(new WebApplicationException("Not found", 404));
    assertEquals(404, r1.getStatus());

    Response r2 = mapper.toResponse(new RuntimeException("Server error"));
    assertEquals(500, r2.getStatus());
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

  static class MockProductRepository extends ProductRepository {
    private final List<Product> list = new ArrayList<>();

    @Override
    public List<Product> listAll(io.quarkus.panache.common.Sort sort) {
      return list;
    }

    @Override
    public Product findById(Long id) {
      return list.stream().filter(p -> id.equals(p.id)).findFirst().orElse(null);
    }

    @Override
    public void persist(Product entity) {
      if (entity.id == null) {
        entity.id = (long) (list.size() + 1);
      }
      if (!list.contains(entity)) {
        list.add(entity);
      }
    }

    @Override
    public void delete(Product entity) {
      list.remove(entity);
    }
  }
}
