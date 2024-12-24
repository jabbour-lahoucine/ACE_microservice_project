package com.example.rootsservice.service;

import com.example.rootsservice.model.RootsResponse;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RootsService {
    public RootsResponse calculateRoots(double[] coefficients) {
        RootsResponse response = new RootsResponse();

        try {
            log.info("Processing coefficients: {}", Arrays.toString(coefficients));

            if (coefficients == null || coefficients.length == 0) {
                response.setRoots(List.of("Invalid polynomial coefficients"));
                return response;
            }

            // Reverse the array since it's in opposite order
            double[] reversedCoefficients = reverseArray(coefficients);
            log.info("Reversed coefficients: {}", Arrays.toString(reversedCoefficients));

            List<String> roots = findRoots(reversedCoefficients);
            log.info("Calculated roots: {}", roots);

            response.setRoots(roots);

        } catch (Exception e) {
            log.error("Error calculating roots", e);
            response.setRoots(List.of("Error occurred while calculating roots: " + e.getMessage()));
        }

        return response;
    }

    private double[] reverseArray(double[] coefficients) {
        double[] reversed = new double[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            reversed[i] = coefficients[coefficients.length - 1 - i];
        }
        return reversed;
    }

    private List<String> findRoots(double[] coefficients) {
        try {
            LaguerreSolver solver = new LaguerreSolver(1.0e-6, 100);
            Complex[] complexRoots = solver.solveAllComplex(coefficients, 0);
            return formatRoots(complexRoots);
        } catch (Exception e) {
            log.error("Error finding roots", e);
            return List.of("Error: Unable to find roots - " + e.getMessage());
        }
    }

    private List<String> formatRoots(Complex[] complexRoots) {
        List<String> roots = new ArrayList<>();
        double threshold = 1e-10;

        for (Complex complex : complexRoots) {
            if (Math.abs(complex.getImaginary()) < threshold) {
                roots.add(String.format("%.4f", complex.getReal()));
            } else {
                String sign = complex.getImaginary() > 0 ? "+" : "";
                roots.add(String.format("%.4f %s %.4fi",
                        complex.getReal(),
                        sign,
                        complex.getImaginary()));
            }
        }
        return roots;
    }
}
