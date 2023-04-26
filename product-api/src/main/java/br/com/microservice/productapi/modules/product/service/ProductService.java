package br.com.microservice.productapi.modules.product.service;

import br.com.microservice.productapi.config.RequestUtil;
import br.com.microservice.productapi.config.SuccessResponse;
import br.com.microservice.productapi.config.exception.ValidationException;
import br.com.microservice.productapi.modules.category.service.CategoryService;
import br.com.microservice.productapi.modules.product.dtos.ProductCheckStockRequest;
import br.com.microservice.productapi.modules.product.dtos.ProductQuantityDTO;
import br.com.microservice.productapi.modules.product.dtos.ProductRequest;
import br.com.microservice.productapi.modules.product.dtos.ProductResponse;
import br.com.microservice.productapi.modules.product.dtos.ProductSalesResponse;
import br.com.microservice.productapi.modules.product.dtos.ProductStockDTO;
import br.com.microservice.productapi.modules.product.model.Product;
import br.com.microservice.productapi.modules.product.repositories.ProductRepository;
import br.com.microservice.productapi.modules.sales.client.SalesClient;
import br.com.microservice.productapi.modules.sales.dto.SalesConfirmationDTO;
import br.com.microservice.productapi.modules.sales.dto.SalesProductResponse;
import br.com.microservice.productapi.modules.sales.enums.SalesStatus;
import br.com.microservice.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.microservice.productapi.modules.supplier.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

  private static final Integer ZERO = 0;
  private static final String SERVICE_ID = "serviceid";
  private static final String TRANSACTION_ID = "transactionid";
  private static final String AUTHORIZATION = "Authorization";

  private final ProductRepository productRepository;
  private final CategoryService categoryService;
  private final SupplierService supplierService;
  private final SalesConfirmationSender salesConfirmationSender;
  private final SalesClient salesClient;
  private final ObjectMapper objectMapper;

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

  public void updateProductStock(ProductStockDTO product) {
    try {
      validateStockUpdateData(product);
      updateStock(product);
    } catch (Exception e) {
      log.error(
        "Error while trying to update stock for message whith error {}",
        e.getMessage(),
        e
      );

      var rejectedMessage = new SalesConfirmationDTO(
        product.getSalesId(),
        SalesStatus.REJECTED,
        product.getTransactionid()
      );
      salesConfirmationSender.sendSalesConfirmation(rejectedMessage);
    }
  }

  // The `validateStockUpdateData` method is validating the data received for updating the stock of a
  // product. It checks if the `ProductStockDTO` object and its `salesId` attribute are not null or
  // empty, and if the `products` list is not empty. It also checks if each `salesProduct` object in
  // the `products` list has a non-null `quantity` and `productId` attribute. If any of these
  // conditions are not met, a `ValidationException` is thrown.
  @Transactional
  private void validateStockUpdateData(ProductStockDTO product) {
    if (
      ObjectUtils.isEmpty(product) || ObjectUtils.isEmpty(product.getSalesId())
    ) {
      throw new ValidationException(
        "The product data and the sales ID must be informed"
      );
    }
    if (ObjectUtils.isEmpty(product.getProducts())) {
      throw new ValidationException("The sale's product must be informed.");
    }
    product
      .getProducts()
      .forEach(salesProduct -> {
        if (
          ObjectUtils.isEmpty(salesProduct.getQuantity()) ||
          ObjectUtils.isEmpty(salesProduct.getProductId())
        ) {
          throw new ValidationException(
            "The product ID and the quantity must be informed."
          );
        }
      });
  }

  @Transactional
  private void updateStock(ProductStockDTO product) {
    var productForUpdate = new ArrayList<Product>();
    product
      .getProducts()
      .forEach(salesProduct -> {
        var existingProduct = findById(salesProduct.getProductId());
        validateQuantityInStock(salesProduct, existingProduct);
        existingProduct.updateStock(salesProduct.getQuantity());
        productForUpdate.add(existingProduct);
      });

    if (!ObjectUtils.isEmpty(productForUpdate)) {
      productRepository.saveAll(productForUpdate);
      var approvedMessage = new SalesConfirmationDTO(
        product.getSalesId(),
        SalesStatus.APPROVED,
        product.getTransactionid()
      );
      salesConfirmationSender.sendSalesConfirmation(approvedMessage);
    }
  }

  private void validateQuantityInStock(
    ProductQuantityDTO salesProduct,
    Product existingProduct
  ) {
    if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
      throw new ValidationException(
        String.format("The product %s is out of stock", existingProduct.getId())
      );
    }
  }

  public ProductSalesResponse findProductSales(Integer id) {
    var product = findById(id);
    try {
      var sales = getSalesProductId(product.getId());
      return ProductSalesResponse.of(product, sales.getSalesIds());
    } catch (Exception e) {
      throw new ValidationException(
        "There was an error trying to get the product's sales."
      );
    }
  }

  private SalesProductResponse getSalesProductId(Integer productId) {
    try {
      var currentRequest = RequestUtil.getCurrentRequest();
      var token = currentRequest.getHeader(AUTHORIZATION);
      var transactionid = currentRequest.getHeader(TRANSACTION_ID);
      var serviceid = currentRequest.getAttribute(SERVICE_ID);
      log.info(
        "Sending Get request to orders by productId with data {} | [transactionID: {} | serviceID: {}]",
        productId,
        transactionid,
        serviceid
      );
      var response = salesClient
        .findSalesByProductId(productId, token, transactionid)
        .orElseThrow(() ->
          new ValidationException("The sales was not found by this product.")
        );
      log.info(
        "Receiving response from orders by productId with data {} | [transactionID: {} | serviceID: {}]",
        objectMapper.writeValueAsString(response),
        transactionid,
        serviceid
      );
      return response;
    } catch (Exception ex) {
      throw new ValidationException("The sales could not be found");
    }
  }

  public SuccessResponse checkProductsStock(ProductCheckStockRequest request) {
    try {
      var currentRequest = RequestUtil.getCurrentRequest();
      var transactionid = currentRequest.getHeader(TRANSACTION_ID);
      var serviceid = currentRequest.getAttribute(SERVICE_ID);
      log.info(
        "Request to POST product stock with data {} | [transactionID: {} | serviceID: {}]",
        objectMapper.writeValueAsString(request),
        transactionid,
        serviceid
      );
      if (
        ObjectUtils.isEmpty(request) ||
        ObjectUtils.isEmpty(request.getProducts())
      ) {
        throw new ValidationException(
          "The request data and products must be informed."
        );
      }
      request.getProducts().forEach(this::validateStock);
      var response = SuccessResponse.create("The stock is Ok.");
      log.info(
        "Response to POST product stock with data {} | [transactionID: {} | serviceID: {}]",
        objectMapper.writeValueAsString(response),
        transactionid,
        serviceid
      );
      return response;
    } catch (Exception ex) {
      throw new ValidationException(ex.getMessage());
    }
  }

  private void validateStock(ProductQuantityDTO productQuantity) {
    if (
      ObjectUtils.isEmpty(productQuantity.getProductId()) ||
      ObjectUtils.isEmpty(productQuantity.getQuantity())
    ) {
      throw new ValidationException("Product ID and quantity must be informed");
    }
    var product = findById(productQuantity.getProductId());

    if (productQuantity.getQuantity() > product.getQuantityAvailable()) {
      throw new ValidationException(
        String.format("The product %s is out of stock.", product.getId())
      );
    }
  }
}
