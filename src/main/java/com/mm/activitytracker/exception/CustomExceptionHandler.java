package com.mm.activitytracker.exception;

import com.mm.user.core.exception.TokenRefreshException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public IllegalArgumentException handleIllegalArgumentException(IllegalArgumentException e) {
        return e;
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public TokenRefreshException handleTokenRefreshException(TokenRefreshException ex) {
        return ex;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExpiredJwtException handleExpiredJwtException(ExpiredJwtException e) {
        return e;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UsernameNotFoundException handleUsernameNotFoundException(UsernameNotFoundException e) {
        return e;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResourceNotFoundException handleResourceNotFoundException(ResourceNotFoundException e) {
        return e;
    }

}
