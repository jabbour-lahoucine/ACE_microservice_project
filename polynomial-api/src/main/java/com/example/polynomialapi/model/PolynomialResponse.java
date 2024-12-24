package com.example.polynomialapi.model;

import lombok.Data;
import java.util.List;

@Data
public class PolynomialResponse {
    private List<String> roots;
    private String factorization;
}
