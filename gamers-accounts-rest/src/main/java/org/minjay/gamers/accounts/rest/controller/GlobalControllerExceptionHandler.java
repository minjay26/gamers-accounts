package org.minjay.gamers.accounts.rest.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.minjay.gamers.accounts.core.BasicErrorCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        return handleValidationException(ex.getBindingResult(), status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleValidationException(ex.getBindingResult(), status);
    }

    private ResponseEntity<Object> handleValidationException(BindingResult result, HttpStatus status) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", status.getReasonPhrase());
        if (result != null) {
            if (result.hasGlobalErrors()) {
                List<Error> globalErrors = new ArrayList<>(6);
                for (ObjectError err : result.getGlobalErrors()) {
                    Error error = new Error();
                    error.message = err.getCode();
                    globalErrors.add(error);
                }
                errorAttributes.put("errors", globalErrors);
            }
            if (result.hasFieldErrors()) {
                List<Error> fieldErrors = new ArrayList<>(6);
                for (FieldError err : result.getFieldErrors()) {
                    Error error = new Error();
                    error.field = err.getField();
                    error.rejected = err.getRejectedValue();
                    error.message = err.getCode();
                    fieldErrors.add(error);
                    errorAttributes.put("fieldErrors", fieldErrors);
                }
            }
            errorAttributes.put("message", "Validation failed for object='"
                    + result.getObjectName() + "'. Error count: " + result.getErrorCount());
        }
        return new ResponseEntity<>(errorAttributes, status);
    }

    @JsonInclude(Include.NON_EMPTY)
    static class Error {
        public String field;
        public Object rejected;
        public String message;
    }

    @ExceptionHandler(BasicErrorCodeException.class)
    public ResponseEntity<?> handleErrorCodeException(BasicErrorCodeException ex, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        errorAttributes.put("status", ex.getStatus());
        errorAttributes.put("error", status.getReasonPhrase());
        errorAttributes.put("message", ex.getMessage());
        return new ResponseEntity<>(errorAttributes, status);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class, EntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handleNotFoundException() {
    }

    @ExceptionHandler(Exception.class)
    public void handleUncaughtException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (ex instanceof AccessDeniedException) {
            throw ex;
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        logException(ex, request, status);
        String message = ex.getLocalizedMessage();
        response.sendError(status.value(), message);
    }

    private void logException(Exception ex, HttpServletRequest request, HttpStatus status) {
        if (LOGGER.isErrorEnabled() && status.value() >= 500 || LOGGER.isInfoEnabled()) {
            Marker marker = MarkerFactory.getMarker(ex.getClass().getName());
            String uri = request.getRequestURI();
            if (request.getQueryString() != null) {
                uri += '?' + request.getQueryString();
            }
            String msg = String.format("%s %s ~> %s", request.getMethod(), uri, status);
            if (status.value() >= 500) {
                LOGGER.error(marker, msg, ex);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(marker, msg, ex);
            } else {
                LOGGER.info(marker, msg);
            }
        }
    }

}
