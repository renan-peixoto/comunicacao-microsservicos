package br.com.microservice.productapi.modules.product.service;

import br.com.microservice.productapi.config.SuccessResponse;
import br.com.microservice.productapi.config.exception.ValidationException;
import br.com.microservice.productapi.modules.category.service.CategoryService;
import br.com.microservice.productapi.modules.product.dtos.ProductRequest;
import br.com.microservice.productapi.modules.product.dtos.ProductResponse;
import br.com.microservice.productapi.modules.product.model.Product;
import br.com.microservice.productapi.modules.product.repositories.ProductRepository;
import br.com.microservice.productapi.modules.supplier.service.SupplierService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ProductService {

  private static final Integer ZERO = 0;

  private final ProductRepository productRepository;
  private final CategoryService categoryService;
  private final SupplierService supplierService;

  public List<ProductResponse> findByAll() {
    return productRepository
      .findAll()
      .stream()
      .map(ProductResponse::of)
      .collect(Collectors.toList());
  }

  public List<ProductResponse> findByName(String name) {
    if (name == null) {
      throw new ValidationException("The product name must be informed.");
    }
    return productRepository
      .findByNameIgnoreCaseContaining(name)
      .stream()
      .map(ProductResponse::of)
      .collect(Collectors.toList());
  }

  public List<ProductResponse> findBySupplierId(Integer supplierId) {
    if (supplierId == null) {
      throw new ValidationException(
        "The product's supplier id must be informed."
      );
    }
    return productRepository
      .findBySupplierId(supplierId)
      .stream()
      .map(ProductResponse::of)
      .collect(Collectors.toList());
  }

  public List<ProductResponse> findByCategoryId(Integer categoryId) {
    if (categoryId == null) {
      throw new ValidationException(
        "The product's category id must be informed."
      );
    }
    return productRepository
      .findByCategoryId(categoryId)
      .stream()
      .map(ProductResponse::of)
      .collect(Collectors.toList());
  }

  public ProductResponse findByIdResponse(Integer id) {
    return ProductResponse.of(findById(id));
  }

  public Product findById(Integer id) {
    if (ObjectUtils.isEmpty(id)) {
      throw new ValidationException("The product id must be informed.");
    }

    return productRepository
      .findById(id)
      .orElseThrow(() ->
        new ValidationException("There's no product for the given ID.")
      );
  }

  public ProductResponse save(ProductRequest request) {
    validateProductDataInformed(request);
    validadeCategoryAndSupplierIdInformed(request);

    var category = categoryService.findById(request.getCategoryId());
    var supplier = supplierService.findById(request.getSupplierId());

    var product = productRepository.save(
      Product.of(request, supplier, category)
    );
    return ProductResponse.of(product);
  }

  public ProductResponse update(ProductRequest request, Integer Id) {
    validateProductDataInformed(request);
    ValidateInformedData(Id, "The product id must be informed.");
    validadeCategoryAndSupplierIdInformed(request);

    var category = categoryService.findById(request.getCategoryId());
    var supplier = supplierService.findById(request.getSupplierId());

    var product = Product.of(request, supplier, category);
    product.setId(Id);
    productRepository.save(product);
    return ProductResponse.of(product);
  }

  private void validateProductDataInformed(ProductRequest request) {
    if (request.getName() == null) {
      throw new ValidationException("The product's name was not informed.");
    }
    if (request.getQuantityAvailable() == null) {
      throw new ValidationException("The product's quantity was not informed.");
    }
    if (request.getQuantityAvailable() <= ZERO) {
      throw new ValidationException(
        "The quantity should not be less or equal to zero."
      );
    }
  }

  private void validadeCategoryAndSupplierIdInformed(ProductRequest request) {
    boolean isCategoryIdNull = request.getCategoryId() == null;
    boolean isSupplierIdNull = request.getSupplierId() == null;
    if (isCategoryIdNull) {
      throw new ValidationException("The category ID was not informed.");
    } else if (isSupplierIdNull) {
      throw new ValidationException("The supplier ID was not informed.");
    }
  }

  public Boolean existisByCategoryId(Integer categoryId) {
    return productRepository.existsByCategoryId(categoryId);
  }

  public Boolean existisBySupplierId(Integer supplierId) {
    return productRepository.existsBySupplierId(supplierId);
  }

  public SuccessResponse delete(Integer id) {
    ValidateInformedData(id, "The product id must be informed.");

    productRepository.deleteById(id);
    return SuccessResponse.create("The product was deleted");
  }

  private void ValidateInformedData(Object data, String message) {
    if (ObjectUtils.isEmpty(data)) {
      throw new ValidationException(message);
    }
  }
}
