package br.com.microservice.productapi.modules.supplier.repositories;

import br.com.microservice.productapi.modules.supplier.model.Supplier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
  List<Supplier> findByNameIgnoreCaseContaining(String name);
}
