package br.com.microservice.productapi.modules.product.repositories;

import br.com.microservice.productapi.modules.product.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
  List<Product> findByNameIgnoreCaseContaining(String name);
  List<Product> findByCategoryId(Integer name);
  List<Product> findBySupplierId(Integer name);

  Boolean existsByCategoryId(Integer id);
  Boolean existsBySupplierId(Integer id);
}
