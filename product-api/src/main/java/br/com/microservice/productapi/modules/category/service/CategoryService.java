package br.com.microservice.productapi.modules.category.service;

import br.com.microservice.productapi.config.SuccessResponse;
import br.com.microservice.productapi.config.exception.ValidationException;
import br.com.microservice.productapi.modules.category.dtos.CategoryRequest;
import br.com.microservice.productapi.modules.category.dtos.CategoryResponse;
import br.com.microservice.productapi.modules.category.model.Category;
import br.com.microservice.productapi.modules.category.repositories.CategoryRepository;
import br.com.microservice.productapi.modules.product.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Lazy })
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Lazy
  private final ProductService productService;

  public CategoryResponse findByIdResponse(Integer id) {
    return CategoryResponse.of(findById(id));
  }

  public List<CategoryResponse> findByAll() {
    return categoryRepository
      .findAll()
      .stream()
      .map(CategoryResponse::of)
      .collect(Collectors.toList());
  }

  public List<CategoryResponse> findByDescription(String description) {
    ValidateInformedData(
      description,
      "The category description must be informed."
    );
    return categoryRepository
      .findByDescriptionIgnoreCaseContaining(description)
      .stream()
      .map(CategoryResponse::of)
      .collect(Collectors.toList());
  }

  public Category findById(Integer id) {
    ValidateInformedData(id, "The category id must be informed.");

    return categoryRepository
      .findById(id)
      .orElseThrow(() ->
        new ValidationException("There's no category for the given ID.")
      );
  }

  public CategoryResponse save(CategoryRequest request) {
    validateCategoryNameInformed(request);
    var category = categoryRepository.save(Category.of(request));
    return CategoryResponse.of(category);
  }

  public CategoryResponse update(CategoryRequest request, Integer id) {
    validateCategoryNameInformed(request);
    ValidateInformedData(id, "The category id must be informed.");
    var category = Category.of(request);
    category.setId(id);
    categoryRepository.save(category);
    return CategoryResponse.of(category);
  }

  private void validateCategoryNameInformed(CategoryRequest request) {
    if (request.getDescription() == null) {
      throw new ValidationException(
        "The category description was not informed."
      );
    }
  }

  public SuccessResponse delete(Integer id) {
    ValidateInformedData(id, "The category id must be informed.");
    if (productService.existisBySupplierId(id)) {
      throw new ValidationException(
        "You cannot delete this category because it's already defined by a product"
      );
    }
    categoryRepository.deleteById(id);
    return SuccessResponse.create("The category was deleted");
  }

  private void ValidateInformedData(Object data, String message) {
    if (ObjectUtils.isEmpty(data)) {
      throw new ValidationException(message);
    }
  }
}
