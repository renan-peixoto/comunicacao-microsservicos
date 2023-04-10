package br.com.microservice.productapi.modules.category.controller;

import br.com.microservice.productapi.config.SuccessResponse;
import br.com.microservice.productapi.modules.category.dtos.CategoryRequest;
import br.com.microservice.productapi.modules.category.dtos.CategoryResponse;
import br.com.microservice.productapi.modules.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor(onConstructor_ = { @Lazy })
public class CategoryController {

  @Lazy
  private final CategoryService categoryService;

  @PostMapping
  public CategoryResponse save(@RequestBody CategoryRequest request) {
    return categoryService.save(request);
  }

  @GetMapping
  public List<CategoryResponse> findAll() {
    return categoryService.findByAll();
  }

  @GetMapping("/{id}")
  public CategoryResponse findById(@PathVariable Integer id) {
    return categoryService.findByIdResponse(id);
  }

  @GetMapping("description/{description}")
  public List<CategoryResponse> findByDescription(
    @PathVariable String description
  ) {
    return categoryService.findByDescription(description);
  }

  @DeleteMapping("{id}")
  public SuccessResponse delete(@PathVariable Integer id) {
    return categoryService.delete(id);
  }

  @PutMapping("{id}")
  public CategoryResponse update(
    @RequestBody CategoryRequest request,
    @PathVariable Integer id
  ) {
    return categoryService.update(request, id);
  }
}
