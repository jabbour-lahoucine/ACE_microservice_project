package com.example.polynomialapi.service;

import com.example.polynomialapi.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PolynomialService {
    private final WebClient webClient;

    @Autowired
    public PolynomialService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    public PolynomialResponse calculatePolynomial(String polynomial) {
        PolynomialResponse response = new PolynomialResponse();
        try {
            // 1. Get Coefficients from the Coefficient Service
            CoefficientRequest coefficientRequest = new CoefficientRequest();
            coefficientRequest.setPolynomial(polynomial);
            CoefficientResponse coefficientResponse = webClient.post()
                    .uri("http://gateway-service/api/coefficients/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(coefficientRequest)
                    .retrieve()
                    .bodyToMono(CoefficientResponse.class)
                    .block();

            if (coefficientResponse == null || coefficientResponse.getCoefficients() == null) {
                response.setRoots(java.util.List.of("Error calculating coefficients."));
                response.setFactorization("Error calculating coefficients");
                return response;
            }
            double[] coefficients = coefficientResponse.getCoefficients();
            //2. Get Roots from Root Service
            RootsRequest rootsRequest = new RootsRequest();
            rootsRequest.setCoefficients(coefficients);
            RootsResponse rootsResponse = webClient.post()
                    .uri("http://gateway-service/api/roots/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(rootsRequest)
                    .retrieve()
                    .bodyToMono(RootsResponse.class)
                    .block();
            if (rootsResponse == null || rootsResponse.getRoots() == null) {
                response.setRoots(java.util.List.of("Error calculating roots."));
                response.setFactorization("Error calculating roots");
                return response;
            }
            response.setRoots(rootsResponse.getRoots());

            // 3. Get Factorization from Factorization Service (Optional)
            FactorizationRequest factorizationRequest = new FactorizationRequest();
            factorizationRequest.setCoefficients(coefficients);
            FactorizationResponse factorizationResponse = webClient.post()
                    .uri("http://gateway-service/api/factorization/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(factorizationRequest)
                    .retrieve()
                    .bodyToMono(FactorizationResponse.class)
                    .block();
            if(factorizationResponse != null){
                response.setFactorization(factorizationResponse.getFactorization());
            }
        }catch (IllegalArgumentException e) {
            response.setRoots(java.util.List.of("Error: Invalid polynomial format"));
            response.setFactorization(e.getMessage());
        } catch (Exception e) {
            response.setRoots(java.util.List.of("Error: An error occurred during processing."));
            response.setFactorization(e.getMessage());
        }
        return response;
    }
}
