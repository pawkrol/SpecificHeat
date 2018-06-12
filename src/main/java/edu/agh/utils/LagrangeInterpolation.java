package edu.agh.utils;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.List;

public class LagrangeInterpolation {

    public List<Pair<Double, Double>> interpolate(List<Pair<Double, Double>> data) {
        double[] x = new double[data.size()];
        double[] y = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            x[i] = data.get(i).getLeftValue();
            y[i] = data.get(i).getRightValue();
        }

        LinearInterpolator linearInterpolator = new LinearInterpolator();
        PolynomialSplineFunction polynomialSplineFunction = linearInterpolator.interpolate(x, y);

        List<Pair<Double, Double>> results = new ArrayList<>();

        for (double i = data.get(0).getLeftValue(); i < data.get(data.size() - 1).getLeftValue(); i++) {
            Pair<Double, Double> p = new Pair<>(i, polynomialSplineFunction.value(i));
            results.add(p);
        }

        return results;
    }

}
