package com.example.demo.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.ZoneId
import java.time.ZonedDateTime

@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(value = [(ApiNotFoundException::class)])
    fun handleApiNotFoundException(e: ApiNotFoundException): ResponseEntity<Any> {
        val httpStatus = HttpStatus.NOT_FOUND
        val apiException = ApiException(
            message = e.message,
            httpStatus = httpStatus,
            ZonedDateTime.now(ZoneId.of("Z"))
        )
        return ResponseEntity(apiException, httpStatus)
    }

    @ExceptionHandler(value = [(ApiBadRequestException::class)])
    fun handleApiConflictException(e: ApiBadRequestException): ResponseEntity<Any> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val apiException = ApiException(
            message = e.message,
            httpStatus = httpStatus,
            ZonedDateTime.now(ZoneId.of("Z"))
        )
        return ResponseEntity(apiException, httpStatus)
    }

    @ExceptionHandler(value = [(ApiInternalServerErrorException::class)])
    fun handleInternalServerErrorException(e: ApiInternalServerErrorException): ResponseEntity<Any> {
        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        val apiException = ApiException(
            message = e.message,
            httpStatus = httpStatus,
            ZonedDateTime.now(ZoneId.of("Z"))
        )
        return ResponseEntity(apiException, httpStatus)
    }
}