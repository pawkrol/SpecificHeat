package edu.agh;

import edu.agh.utils.Functions;
import edu.agh.utils.LagrangeInterpolation;
import edu.agh.utils.Pair;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Resolver {

    private List<Pair<Double, Double>> temperatureSpecificHeatPairs;
    private List<Pair<Double, Double>> interpolatedTemperatureSpecificHeatPairs;
    private List<Pair<Double, Double>> temperatureEnthalpyPairs;
    private List<Pair<Double, Double>> newTemperatureEnthalpyPairs;
    private List<Double> extraEnthalpy;

    private double t1;
    private double t2;
    private double enthalpy;
    private UnivariateFunction function;

    public void resolve(ResolverCallback callback) {
        temperatureEnthalpyPairs = new ArrayList<>();
        newTemperatureEnthalpyPairs = new ArrayList<>();

        new Thread(() -> {
            interpolatedTemperatureSpecificHeatPairs = new LagrangeInterpolation().interpolate(temperatureSpecificHeatPairs);
            calculateExtraEnthalpy(t1, t2, enthalpy, function);
            calculateEnthalpy();

            callback.onResolve();
        }).start();
    }

    private void calculateEnthalpy() {
        double enthalpy;
        double newEnthalpy;

        enthalpy = interpolatedTemperatureSpecificHeatPairs.get(0).getLeftValue() *
                interpolatedTemperatureSpecificHeatPairs.get(0).getRightValue();

        temperatureEnthalpyPairs.add(new Pair<>(interpolatedTemperatureSpecificHeatPairs.get(0).getLeftValue(), enthalpy));
        newTemperatureEnthalpyPairs.add(new Pair<>(interpolatedTemperatureSpecificHeatPairs.get(0).getLeftValue(), enthalpy));

        for (int i = 1; i < interpolatedTemperatureSpecificHeatPairs.size(); i++) {
            enthalpy = (interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue() -
                    interpolatedTemperatureSpecificHeatPairs.get(i - 1).getLeftValue()) *
                    interpolatedTemperatureSpecificHeatPairs.get(i).getRightValue() +
                    temperatureEnthalpyPairs.get(i - 1).getRightValue();

            newEnthalpy = (interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue() -
                    interpolatedTemperatureSpecificHeatPairs.get(i - 1).getLeftValue()) *
                    interpolatedTemperatureSpecificHeatPairs.get(i).getRightValue() +
                    newTemperatureEnthalpyPairs.get(i - 1).getRightValue() + extraEnthalpy.get(i);

            temperatureEnthalpyPairs.add(new Pair<>(interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue(), enthalpy));
            newTemperatureEnthalpyPairs.add(new Pair<>(interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue(), newEnthalpy));
        }
    }

    private void calculateExtraEnthalpy(double t1, double t2, double reactionEnthalpy, UnivariateFunction univariateFunction) {
        //wypełniam tablice zerami tak aby osiągnęła rozmiar tablicy interpolowanej ciepła właściwego
        extraEnthalpy = new ArrayList<>(Collections.nCopies(interpolatedTemperatureSpecificHeatPairs.size(), 0.0));

        SimpsonIntegrator simpsonIntegrator = new SimpsonIntegrator();
        double area = simpsonIntegrator.integrate(SimpsonIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, univariateFunction, 1, 100);

        int lowerTempIndex = findExactOrClosestIndexByLeftValue(interpolatedTemperatureSpecificHeatPairs, t1);
        int upperTempIndex = findExactOrClosestIndexByLeftValue(interpolatedTemperatureSpecificHeatPairs, t2);

        for (int i = lowerTempIndex; i < upperTempIndex; i++) {
            //reskalowanie zakresu z podanego na <1, 100> na potrzeby całki
            double newLower = (i - lowerTempIndex) * 99.0 / (upperTempIndex - lowerTempIndex) + 1.0;
            double newUpper = (i + 1 - lowerTempIndex) * 99.0 / (upperTempIndex - lowerTempIndex) + 1.0;

            //oblioczany jest ułamek pola podwykresu - sprawdzone dla 200 cząstki dodają się do 200 z małym błędem numerycznym
            extraEnthalpy.set(i,
                    simpsonIntegrator.integrate(
                            SimpsonIntegrator.DEFAULT_MAX_ITERATIONS_COUNT,
                            univariateFunction,
                            newLower,
                            newUpper) / area * reactionEnthalpy
            );
        }
    }

    public void setData(List<Pair<Double, Double>> temperatureSpecificHeatPairs) {
        this.temperatureSpecificHeatPairs = temperatureSpecificHeatPairs;
    }

    public void setT1(double t1) {
        this.t1 = t1;
    }

    public void setT2(double t2) {
        this.t2 = t2;
    }

    public void setEnthalpy(double enthalpy) {
        this.enthalpy = enthalpy;
    }

    public void setFunction(UnivariateFunction function) {
        this.function = function;
    }

    public List<Pair<Double, Double>> getTemperatureSpecificHeatPairs() {
        return temperatureSpecificHeatPairs;
    }

    public List<Pair<Double, Double>> getTemperatureEnthalpyPairs() {
        return temperatureEnthalpyPairs;
    }

    public List<Pair<Double, Double>> getInterpolatedTemperatureSpecificHeatPairs() {
        return interpolatedTemperatureSpecificHeatPairs;
    }

    public List<Pair<Double, Double>> getNewTemperatureEnthalpyPairs() {
        return newTemperatureEnthalpyPairs;
    }

    private int findExactOrClosestIndexByLeftValue(List<Pair<Double, Double>> list, double leftValue) {
        int i = 0;
        for (Pair p: list) {
            double listLeftValue = (double) p.getLeftValue();
            if (listLeftValue == leftValue) {
                return i;
            }

            if (listLeftValue > leftValue) {
                if ((listLeftValue - leftValue) < 50) {
                    return i;
                } else {
                    return -1;
                }
            }

            i++;
        }

        return -1;
    }
}
