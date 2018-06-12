package edu.agh.utils;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class Functions {

    public static UnivariateFunction function1() {
        return x -> 1/x;
    }

    public static UnivariateFunction function2() {
        //funkcja "/"
        return x -> x;
    }

    public static UnivariateFunction function3() {
        //funkcja "\"
        return x -> -x + 100;
    }

    public static UnivariateFunction function4() {
        //wielomian sklejany "/\"

        double knots[] = {0.0, 50.0, 100.0};

        double f1[] = {0.0, 1.0};
        double f2[] = {100.0, -1.0};

        PolynomialFunction polynomials[] = {
                new PolynomialFunction(f1),
                new PolynomialFunction(f2)
        };

        return new PolynomialSplineFunction(knots, polynomials);
    }

}
