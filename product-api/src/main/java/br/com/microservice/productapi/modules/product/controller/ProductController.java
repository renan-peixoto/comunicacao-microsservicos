package br.com.microservice.productapi.modules.product.controller;

import br.com.microservice.productapi.config.SuccessResponse;
import br.com.microservice.productapi.modules.product.dtos.ProductCheckStockRequest;
import br.com.microservice.productapi.modules.product.dtos.ProductRequest;
import br.com.microservice.productapi.modules.product.dtos.ProductResponse;
import br.com.microservice.productapi.modules.product.dtos.ProductSalesResponse;
import br.com.microservice.productapi.modules.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ProductResponse save(@RequestBody ProductRequest request) {
    return productService.save(request);
  }

  @GetMapping
  public List<ProductResponse> findAll() {
    return productService.findByAll();
  }

  @GetMapping("/{id}")
  public ProductResponse findById(@PathVariable Integer id) {
    return productService.findByIdResponse(id);
  }

  @GetMapping("name/{name}")
  public List<ProductResponse> findByDescription(@PathVariable String name) {
    return productService.findByName(name);
  }

  @GetMapping("supplier/{supplierId}")
  public List<ProductResponse> findBySupplierId(
    @PathVariable Integer supplierId
  ) {
    return productService.findBySupplierId(supplierId);
  }

  @GetMapping("category/{categoryId}")
  public List<ProductResponse> findByCategoryId(
    @PathVariable Integer categoryId
  ) {
    return productService.findByCategoryId(categoryId);
  }

  @PutMapping("{id}")
  public ProductResponse update(
    @RequestBody ProductRequest request,
    @PathVariable Integer id
  ) {
    return productService.update(request, id);
  }

  @DeleteMapping("{id}")
  public SuccessResponse delete(@PathVariable Integer id) {
    return productService.delete(id);
  }

  @PostMapping("check-stock")
  public SuccessResponse checkProductsStock(
    @RequestBody ProductCheckStockRequest request
  ) {
    return productService.checkProductsStock(request);
  }

  @GetMapping("{id}/sales")
  public ProductSalesResponse findProductSales(@PathVariable Integer id) {
    return productService.findProductSales(id);
  }
}
