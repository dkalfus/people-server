package com.amex.personServer.controller;

import java.util.Date;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 
 * Base class for controllers providing error handling functionality.
 * Controllers inheriting from this base class can send diagnostic
 * error messages to the client when they detect errors.  To do this
 * they can throw an IllegalArgumentException from any controller request handling
 * method.  This class registers an Exception handler which will extract the
 * message from the Exception and send it to the client as part of an ExceptionResponse
 * object including the message and a timestamp, with an HTTP 422 response code.
 * 
 * *Only* IllegalArgumentExceptions thrown by the controller methods
 * will be intercepted and handled this way.
 * 
 * @author David Kalfus
 *
 */
public abstract class AmexBaseController {
	
	public static class ExceptionResponse {
		private Date timestamp = new Date();
		private String message;

		public ExceptionResponse(String message) {
			this.message = message;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public String getMessage() {
			return message;
		}
	}
		
    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ExceptionResponse> errorHandler(Exception e) {
	   ExceptionResponse exceptionResponse= new ExceptionResponse(e.getMessage());
       return new ResponseEntity<> (exceptionResponse, new HttpHeaders(), 
           HttpStatus.UNPROCESSABLE_ENTITY); 
    }
}

