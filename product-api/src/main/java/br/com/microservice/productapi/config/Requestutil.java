package br.com.microservice.productapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Requestutil {

  public static HttpServletRequest getCurrentRequest() {
    try {
      return (
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()
      ).getRequest();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new ValidationException(
        "The current request could not be proccessed."
      );
    }
  }
}
