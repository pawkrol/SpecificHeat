package edu.agh.utils;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.List;

public class LagrangeInterpolation {

    private List<Pair> data;

    public List<Pair> interpolate(List<Pair> data) {

        double[] x = new double[data.size()];
        double[] y = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            x[i] = data.get(i).getLeftValue();
            y[i] = data.get(i).getRightValue();
        }

        LinearInterpolator linearInterpolator = new LinearInterpolator();
        PolynomialSplineFunction polynomialSplineFunction = linearInterpolator.interpolate(x, y);

        List<Pair> results = new ArrayList<>();

        for (double i = data.get(0).getLeftValue(); i < data.get(data.size() - 1).getLeftValue(); i++) {
            Pair p = new Pair(i, polynomialSplineFunction.value(i));
            results.add(p);
        }

        return results;

    }

    private Pair calculateValue(double x){

        double t;
        double y = 0.0;

        for(int k = 0; k < data.size(); k++){

            t = 1.0;

            for(int j = 0; j < data.size(); j++){

                if(j != k ){
                    t = t * ( (x-data.get(j).getLeftValue() ) / (data.get(k).getLeftValue()-data.get(j).getLeftValue() ) );
                }
            }

            y += t*data.get(k).getRightValue();

        }

        return new Pair(x, y);

    }

}
