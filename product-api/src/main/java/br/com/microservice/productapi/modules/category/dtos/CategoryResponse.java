package br.com.microservice.productapi.modules.category.dtos;

import br.com.microservice.productapi.modules.category.model.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CategoryResponse {

  private Integer id;
  private String description;

/**
 * This Java function creates a CategoryResponse object by copying properties from a Category object.
 * 
 * @param category An object of the Category class that contains information about a category.
 * @return An instance of the CategoryResponse class is being returned.
 */
  public static CategoryResponse of(Category category) {
    var response = new CategoryResponse();
    BeanUtils.copyProperties(category, response);
    return response;
  }
}
