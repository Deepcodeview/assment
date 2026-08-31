package com.fulfilment.application.monolith.fulfilment.domain.models;

public class ProductStoreFulfillment {

  public String businessUnitCode;
  public Long productId;
  public Long storeId;

  public ProductStoreFulfillment(String businessUnitCode, Long productId, Long storeId) {
    this.businessUnitCode = businessUnitCode;
    this.productId = productId;
    this.storeId = storeId;
  }
}
