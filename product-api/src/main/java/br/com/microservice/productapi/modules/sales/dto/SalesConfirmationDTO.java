package br.com.microservice.productapi.modules.sales.dto;

import br.com.microservice.productapi.modules.sales.enums.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesConfirmationDTO {

  private String salesId;

  private SalesStatus status;

  private String transactionid;
}
