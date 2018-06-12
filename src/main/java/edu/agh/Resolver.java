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

    private List<Pair> temperatureSpecificHeatPairs;
    private List<Pair> interpolatedTemperatureSpecificHeatPairs;
    private List<Pair> temperatureEnthalpyPairs;
    private List<Pair> newTemperatureEnthalpyPairs;
    private List<Double> extraEnthalpy;

    Resolver() {
        temperatureEnthalpyPairs = new ArrayList<>();
        newTemperatureEnthalpyPairs = new ArrayList<>();
    }

    public void resolve(ResolverCallback callback) {
        new Thread(() -> {
            //here should go calculations => temperatureEnthalpyPairs
            interpolatedTemperatureSpecificHeatPairs = new LagrangeInterpolation().interpolate(temperatureSpecificHeatPairs);
            //TODO odczytywanie inputu od usera - zakresy temperatur, entalpia reakcji, rodzaj funkcji
            //TODO znalazenie podanej wartości w tablicy (obecnie gdy podamy 800 to brany jest pod uwagę element nr 800 a nie temp 800)
            //funkcje do uwzględnienia przemiany definiowane w klasie Functions
            calculateExtraEnthalpy(800, 950, 200, Functions.function4());
            calculateEnthalpy();

            callback.onResolve();

        }).start();
    }

    private void calculateEnthalpy() {

        double enthalpy;
        double newEnthalpy;

        enthalpy = interpolatedTemperatureSpecificHeatPairs.get(0).getLeftValue() *
                interpolatedTemperatureSpecificHeatPairs.get(0).getRightValue();

        temperatureEnthalpyPairs.add(new Pair(interpolatedTemperatureSpecificHeatPairs.get(0).getLeftValue(), enthalpy));
        newTemperatureEnthalpyPairs.add(new Pair(interpolatedTemperatureSpecificHeatPairs.get(0).getLeftValue(), enthalpy));

        for (int i = 1; i < interpolatedTemperatureSpecificHeatPairs.size(); i++) {

            enthalpy = (interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue() -
                    interpolatedTemperatureSpecificHeatPairs.get(i - 1).getLeftValue()) *
                    interpolatedTemperatureSpecificHeatPairs.get(i).getRightValue() +
                    temperatureEnthalpyPairs.get(i - 1).getRightValue();

            newEnthalpy = (interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue() -
                    interpolatedTemperatureSpecificHeatPairs.get(i - 1).getLeftValue()) *
                    interpolatedTemperatureSpecificHeatPairs.get(i).getRightValue() +
                    newTemperatureEnthalpyPairs.get(i - 1).getRightValue() + extraEnthalpy.get(i);

            temperatureEnthalpyPairs.add(new Pair(interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue(), enthalpy));
            newTemperatureEnthalpyPairs.add(new Pair(interpolatedTemperatureSpecificHeatPairs.get(i).getLeftValue(), newEnthalpy));

        }

    }

    private void calculateExtraEnthalpy(int lower, int upper, double reactionEnthalpy, UnivariateFunction univariateFunction) {

        //wypełniam tablice zerami tak aby osiągnęła rozmiar tablicy interpolowanej ciepła właściwego - mniej pierdolenia przy indeksach
        extraEnthalpy = new ArrayList<>(Collections.nCopies(interpolatedTemperatureSpecificHeatPairs.size(), 0.0));

        SimpsonIntegrator simpsonIntegrator = new SimpsonIntegrator();

        double area = simpsonIntegrator.integrate(SimpsonIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, univariateFunction, 1, 100);

        for (int i = lower; i < upper; i++) {

            //reskalowanie zakresu z podanego na <1, 100> na potrzeby całki
            double newLower = (i - lower) * 99.0 / (upper - lower) + 1.0;
            double newUpper = (i + 1 - lower) * 99.0 / (upper - lower) + 1.0;

            //oblioczany jest ułamek pola podwykresu - sprawdzone dla 200 cząstki dodają się do 200 z małym błędem numerycznym
            extraEnthalpy.set(i, simpsonIntegrator.integrate(SimpsonIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, univariateFunction, newLower, newUpper) / area * reactionEnthalpy);

        }

    }

    public void setData(List<Pair> temperatureSpecificHeatPairs) {
        this.temperatureSpecificHeatPairs = temperatureSpecificHeatPairs;
    }

    public List<Pair> getTemperatureSpecificHeatPairs() {
        return temperatureSpecificHeatPairs;
    }

    public List<Pair> getTemperatureEnthalpyPairs() {
        return temperatureEnthalpyPairs;
    }

    public List<Pair> getInterpolatedTemperatureSpecificHeatPairs() {
        return interpolatedTemperatureSpecificHeatPairs;
    }

    public List<Pair> getNewTemperatureEnthalpyPairs() {
        return newTemperatureEnthalpyPairs;
    }
}
