package br.com.microservice.productapi;

import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {

  @GetMapping("status")
  @ResponseStatus(code = HttpStatus.OK)
  public HashMap<String, Object> getApiStatus() {
    var response = new HashMap<String, Object>();

    response.put("service", "Product-API");
    response.put("status", "up");
    response.put("httpStatus", HttpStatus.OK.value());

    return response;
  }
}
