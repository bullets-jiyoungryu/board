package kr.co.bullets.board.exception;

import kr.co.bullets.board.model.error.ClientErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ClientErrorException.class)
  public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
    return new ResponseEntity<>(
        new ClientErrorResponse(e.getStatus(), e.getMessage()), e.getStatus());
  }
}
