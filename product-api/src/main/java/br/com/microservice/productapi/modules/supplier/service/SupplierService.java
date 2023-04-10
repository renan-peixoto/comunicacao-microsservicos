package br.com.microservice.productapi.modules.supplier.service;

import br.com.microservice.productapi.config.SuccessResponse;
import br.com.microservice.productapi.config.exception.ValidationException;
import br.com.microservice.productapi.modules.product.service.ProductService;
import br.com.microservice.productapi.modules.supplier.dtos.SupplierRequest;
import br.com.microservice.productapi.modules.supplier.dtos.SupplierResponse;
import br.com.microservice.productapi.modules.supplier.model.Supplier;
import br.com.microservice.productapi.modules.supplier.repositories.SupplierRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Lazy })
public class SupplierService {

  private final SupplierRepository supplierRepository;

  @Lazy
  private final ProductService productService;

  public List<SupplierResponse> findByAll() {
    return supplierRepository
      .findAll()
      .stream()
      .map(SupplierResponse::of)
      .collect(Collectors.toList());
  }

  public List<SupplierResponse> findByName(String name) {
    ValidateInformedData(name, "The supplier name must be informed.");
    return supplierRepository
      .findByNameIgnoreCaseContaining(name)
      .stream()
      .map(SupplierResponse::of)
      .collect(Collectors.toList());
  }

  public SupplierResponse findByIdResponse(Integer id) {
    return SupplierResponse.of(findById(id));
  }

  public Supplier findById(Integer id) {
    ValidateInformedData(id, "The supplier id must be informed.");

    return supplierRepository
      .findById(id)
      .orElseThrow(() ->
        new ValidationException("There's no supplier for the given ID.")
      );
  }

  public SupplierResponse save(SupplierRequest request) {
    validateCategoryNameInformed(request);
    var supplier = supplierRepository.save(Supplier.of(request));
    return SupplierResponse.of(supplier);
  }

  public SupplierResponse update(SupplierRequest request, Integer id) {
    validateCategoryNameInformed(request);
    var supplier = Supplier.of(request);
    supplier.setId(id);
    supplierRepository.save(supplier);
    return SupplierResponse.of(supplier);
  }

  private void validateCategoryNameInformed(SupplierRequest request) {
    if (request.getName() == null) {
      throw new ValidationException("The supplier's name was not informed.");
    }
  }

  public SuccessResponse delete(Integer id) {
    ValidateInformedData(id, "The supplier id must be informed.");
    if (productService.existisBySupplierId(id)) {
      throw new ValidationException(
        "You cannot delete this supplier because it's already defined by a product"
      );
    }
    supplierRepository.deleteById(id);
    return SuccessResponse.create("The supplier was deleted");
  }

  private void ValidateInformedData(Object data, String message) {
    if (ObjectUtils.isEmpty(data)) {
      throw new ValidationException(message);
    }
  }
}
