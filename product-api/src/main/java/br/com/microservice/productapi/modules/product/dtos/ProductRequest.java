package br.com.microservice.productapi.modules.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductRequest {

  private String name;

  @JsonProperty("quantity_available")
  private Integer quantityAvailable;

  private Integer supplierId;
  private Integer categoryId;
}
