package br.com.microservice.productapi.modules.product.model;

import br.com.microservice.productapi.modules.category.model.Category;
import br.com.microservice.productapi.modules.product.dtos.ProductRequest;
import br.com.microservice.productapi.modules.supplier.model.Supplier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PRODUCT")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "NAME", nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "FK_SUPPLIER", nullable = false)
  private Supplier supplier;

  @ManyToOne
  @JoinColumn(name = "FK_CATEGORY", nullable = false)
  private Category category;

  @Column(name = "QUANTITY_AVAILABLE", nullable = false)
  private Integer quantityAvailable;

  @Column(name = "CREATED_AT", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void PrePersist() {
    createdAt = LocalDateTime.now();
  }

  public static Product of(
    ProductRequest request,
    Supplier supplier,
    Category category
  ) {
    return Product
      .builder()
      .name(request.getName())
      .quantityAvailable(request.getQuantityAvailable())
      .supplier(supplier)
      .category(category)
      .build();
  }

  public void updateStock(Integer quantity) {
    quantityAvailable = quantityAvailable - quantity;
  }
}
