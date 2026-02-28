package me.longbow122.demojavabackend.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ControllerAdvice(basePackages = {
	"me.longbow122.demojavabackend.controller"
})
@ComponentScan(basePackages = {
	"me.longbow122.demojavabackend.controller",
	"me.longbow122.demojavabackend.service"
})
public class ControllerExceptionHandler {

	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<Map<String, String>> handleEntityExistsException(EntityExistsException exception) {
		return new ResponseEntity<>(Collections.singletonMap("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException exception) {
		return new ResponseEntity<>(Collections.singletonMap("error", exception.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException exception) {
		return new ResponseEntity<>(Collections.singletonMap("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		//* Get all the violations that might have been caught against the API, so we can check against one of them in testing.
		//* Something like this is done when we have something that may have been caught by the controller validation, and we need to check against it.
		List<String> errorMapping = new ArrayList<>();
		exception.getBindingResult().getAllErrors().forEach(error ->
			errorMapping.add(error.getDefaultMessage()));
		return new ResponseEntity<>(Collections.singletonMap("violations", errorMapping), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
		return new ResponseEntity<>(Collections.singletonMap("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
	}
}
