package com.fulfilment.application.monolith.fulfilment.domain.ports;

import com.fulfilment.application.monolith.fulfilment.domain.models.ProductStoreFulfillment;
import java.util.List;

public interface AssociateFulfillmentOperation {

  void associateFulfillment(String businessUnitCode, Long productId, Long storeId);

  List<ProductStoreFulfillment> getFulfillments();
}
