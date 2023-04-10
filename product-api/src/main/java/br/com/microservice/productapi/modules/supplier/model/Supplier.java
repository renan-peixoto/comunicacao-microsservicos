package br.com.microservice.productapi.modules.supplier.model;

import br.com.microservice.productapi.modules.supplier.dtos.SupplierRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SUPPLIER")
public class Supplier {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer id;

  @Column(name = "NAME", nullable = false)
  private String name;

  public static Supplier of(SupplierRequest request) {
    var supplier = new Supplier();
    BeanUtils.copyProperties(request, supplier);
    return supplier;
  }
}
