package br.com.microservice.productapi.modules.product.dtos;

import br.com.microservice.productapi.modules.category.dtos.CategoryResponse;
import br.com.microservice.productapi.modules.product.model.Product;
import br.com.microservice.productapi.modules.supplier.dtos.SupplierResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesResponse {

  private Integer id;

  private String name;

  @JsonProperty("quantity_available")
  private Integer quantityAvailable;

  @JsonProperty("created_at")
  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  private LocalDateTime createdAt;

  private SupplierResponse supplier;

  private CategoryResponse category;

  private List<String> sales;

  public static ProductSalesResponse of(Product product, List<String> sales) {
    return ProductSalesResponse
      .builder()
      .id(product.getId())
      .name(product.getName())
      .quantityAvailable(product.getQuantityAvailable())
      .createdAt(product.getCreatedAt())
      .supplier(SupplierResponse.of(product.getSupplier()))
      .category(CategoryResponse.of(product.getCategory()))
      .sales(sales)
      .build();
  }
}
