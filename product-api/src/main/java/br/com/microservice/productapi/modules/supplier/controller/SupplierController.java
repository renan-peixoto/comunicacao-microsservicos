package br.com.microservice.productapi.modules.supplier.controller;

import br.com.microservice.productapi.config.SuccessResponse;
import br.com.microservice.productapi.modules.supplier.dtos.SupplierRequest;
import br.com.microservice.productapi.modules.supplier.dtos.SupplierResponse;
import br.com.microservice.productapi.modules.supplier.service.SupplierService;
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
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;

  @PostMapping
  public SupplierResponse save(@RequestBody SupplierRequest request) {
    return supplierService.save(request);
  }

  @GetMapping
  public List<SupplierResponse> findAll() {
    return supplierService.findByAll();
  }

  @GetMapping("/{id}")
  public SupplierResponse findById(@PathVariable Integer id) {
    return supplierService.findByIdResponse(id);
  }

  @GetMapping("name/{name}")
  public List<SupplierResponse> findByDescription(@PathVariable String name) {
    return supplierService.findByName(name);
  }

  @DeleteMapping("{id}")
  public SuccessResponse delete(@PathVariable Integer id) {
    return supplierService.delete(id);
  }

  @PutMapping("{id}")
  public SupplierResponse update(
    @RequestBody SupplierRequest request,
    @PathVariable Integer id
  ) {
    return supplierService.update(request, id);
  }
}
