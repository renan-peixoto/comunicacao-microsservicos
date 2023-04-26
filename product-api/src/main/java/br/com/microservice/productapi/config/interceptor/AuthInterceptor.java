package br.com.microservice.productapi.config.interceptor;

import br.com.microservice.productapi.modules.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

  private static final String AUTHORIZATION = "Authorization";
  private static final String TRANSACTION_ID = "transactionid";

  @Autowired
  private JwtService jwtService;

  @Override
  public boolean preHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler
  ) throws Exception {
    if (isOptions(request)) {
      return true;
    }

    if (ObjectUtils.isEmpty(request.getHeader(TRANSACTION_ID))) {
      throw new ValidationException("The transactionid header id required.");
    }

    var authorization = request.getHeader(AUTHORIZATION);
    jwtService.validateAuthorization(authorization);
    request.setAttribute("serviceid", UUID.randomUUID().toString());
    return true;
  }

  private boolean isOptions(HttpServletRequest request) {
    return HttpMethod.OPTIONS.name().equals(request.getMethod());
  }
}
