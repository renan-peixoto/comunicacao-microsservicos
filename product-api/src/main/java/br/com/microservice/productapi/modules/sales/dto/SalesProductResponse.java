package br.com.microservice.productapi.modules.sales.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesProductResponse {

  private List<String> salesIds;
}
