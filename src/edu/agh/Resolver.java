package edu.agh;

import edu.agh.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class Resolver {

    private List<Pair> temperatureSpecificHeatPairs;
    private List<Pair> temperatureEnthalpyPairs;

    Resolver() {
        temperatureEnthalpyPairs = new ArrayList<>();
    }

    public void resolve(ResolverCallback callback) {
        new Thread(() -> {
            //here should go calculations => temperatureEnthalpyPairs

            callback.onResolve();
        }).start();
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
}
