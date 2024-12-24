package com.example.polynomialapi.controller;

import com.example.polynomialapi.model.PolynomialRequest;
import com.example.polynomialapi.model.PolynomialResponse;
import com.example.polynomialapi.service.PolynomialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/polynomial")
public class PolynomialController {

    private final PolynomialService polynomialService;

    @Autowired
    public PolynomialController(PolynomialService polynomialService) {
        this.polynomialService = polynomialService;
    }
    @PostMapping("/calculate")
    public ResponseEntity<PolynomialResponse> calculatePolynomial(@RequestBody PolynomialRequest request) {
        PolynomialResponse response = polynomialService.calculatePolynomial(request.getPolynomial());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
