package com.fulfilment.application.monolith.fulfilment.domain.usecases;

import com.fulfilment.application.monolith.fulfilment.domain.models.ProductStoreFulfillment;
import com.fulfilment.application.monolith.fulfilment.domain.ports.AssociateFulfillmentOperation;
import com.fulfilment.application.monolith.fulfilment.domain.validators.FulfillmentValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FulfillmentService implements AssociateFulfillmentOperation {

  private final List<ProductStoreFulfillment> fulfillments = new ArrayList<>();
  private final FulfillmentValidator validator;

  @Inject
  public FulfillmentService(FulfillmentValidator validator) {
    this.validator = validator;
  }

  public FulfillmentService() {
    this(new FulfillmentValidator());
  }

  @Override
  public void associateFulfillment(String businessUnitCode, Long productId, Long storeId) {
    validator.validateAssociation(businessUnitCode, productId, storeId, fulfillments);

    boolean alreadyAssociated =
        fulfillments.stream()
            .anyMatch(
                f ->
                    f.businessUnitCode.equalsIgnoreCase(businessUnitCode)
                        && f.productId.equals(productId)
                        && f.storeId.equals(storeId));

    if (!alreadyAssociated) {
      fulfillments.add(new ProductStoreFulfillment(businessUnitCode, productId, storeId));
    }
  }

  @Override
  public List<ProductStoreFulfillment> getFulfillments() {
    return new ArrayList<>(fulfillments);
  }
}
