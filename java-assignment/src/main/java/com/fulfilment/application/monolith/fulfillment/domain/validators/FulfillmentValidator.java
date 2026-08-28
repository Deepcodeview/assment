package com.fulfilment.application.monolith.fulfillment.domain.validators;

import com.fulfilment.application.monolith.fulfillment.domain.models.ProductStoreFulfillment;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FulfillmentValidator {

  public void validateAssociation(
      String businessUnitCode,
      Long productId,
      Long storeId,
      List<ProductStoreFulfillment> fulfillments) {
    if (businessUnitCode == null || productId == null || storeId == null) {
      throw new IllegalArgumentException(
          "Business unit code, product ID, and store ID are required.");
    }

    boolean alreadyAssociated =
        fulfillments.stream()
            .anyMatch(
                f ->
                    f.businessUnitCode.equalsIgnoreCase(businessUnitCode)
                        && f.productId.equals(productId)
                        && f.storeId.equals(storeId));

    // Constraint 1: Each Product can be fulfilled by a maximum of 2 different Warehouses per Store
    long warehousesForProductAndStore =
        fulfillments.stream()
            .filter(f -> f.productId.equals(productId) && f.storeId.equals(storeId))
            .map(f -> f.businessUnitCode.toLowerCase())
            .distinct()
            .count();

    if (!alreadyAssociated && warehousesForProductAndStore >= 2) {
      throw new IllegalArgumentException(
          "Product cannot be fulfilled by more than 2 warehouses per store.");
    }

    // Constraint 2: Each Store can be fulfilled by a maximum of 3 different Warehouses
    long warehousesForStore =
        fulfillments.stream()
            .filter(f -> f.storeId.equals(storeId))
            .map(f -> f.businessUnitCode.toLowerCase())
            .distinct()
            .count();

    boolean warehouseAlreadyFulfillsStore =
        fulfillments.stream()
            .anyMatch(
                f ->
                    f.storeId.equals(storeId)
                        && f.businessUnitCode.equalsIgnoreCase(businessUnitCode));

    if (!warehouseAlreadyFulfillsStore && warehousesForStore >= 3) {
      throw new IllegalArgumentException(
          "Store cannot be fulfilled by more than 3 different warehouses.");
    }

    // Constraint 3: Each Warehouse can store maximally 5 types of Products
    long productTypesInWarehouse =
        fulfillments.stream()
            .filter(f -> f.businessUnitCode.equalsIgnoreCase(businessUnitCode))
            .map(f -> f.productId)
            .distinct()
            .count();

    boolean productAlreadyInWarehouse =
        fulfillments.stream()
            .anyMatch(
                f ->
                    f.businessUnitCode.equalsIgnoreCase(businessUnitCode)
                        && f.productId.equals(productId));

    if (!productAlreadyInWarehouse && productTypesInWarehouse >= 5) {
      throw new IllegalArgumentException("Warehouse cannot store more than 5 types of products.");
    }
  }
}
