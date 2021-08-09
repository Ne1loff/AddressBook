package com.example.demo.exception

class ApiInternalServerErrorException : RuntimeException {
    constructor(message: String, ex: Exception): super(message, ex) {}
    constructor(message: String): super(message) {}
}