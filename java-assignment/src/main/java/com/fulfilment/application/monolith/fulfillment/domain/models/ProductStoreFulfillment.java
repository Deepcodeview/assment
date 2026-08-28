package com.fulfilment.application.monolith.fulfillment.domain.models;

public class ProductStoreFulfillment {
  public Long id;
  public String businessUnitCode;
  public Long productId;
  public Long storeId;

  public ProductStoreFulfillment() {}

  public ProductStoreFulfillment(String businessUnitCode, Long productId, Long storeId) {
    this.businessUnitCode = businessUnitCode;
    this.productId = productId;
    this.storeId = storeId;
  }
}
