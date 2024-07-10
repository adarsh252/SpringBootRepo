package com.example.demo.Exception;


import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler{
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String,String> handleException(MethodArgumentNotValidException exception){
	
		
		Map<String,String> exceptionMap = new HashMap<>();
		
		exception.getBindingResult().getFieldErrors().forEach(errors -> {
	
			exceptionMap.put(errors.getField(), errors.getDefaultMessage());
		});
		
		return exceptionMap;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ResourceNotFoundException.class)
	public Map<String,String> handleException(ResourceNotFoundException exception){
	
		Map<String,String> exceptionMap = new HashMap<>();
		exceptionMap.put("errorMessage",exception.getMessage());
		
		return exceptionMap;
	}
}
