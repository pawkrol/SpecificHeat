package edu.agh;

import edu.agh.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileProcessor {

    public List<Pair<Double, Double>> getDataFromFile(File file) {
        Path filePath = file.toPath();
        List<Pair<Double, Double>> pairs = new ArrayList<>();

        try (Stream<String> stream = Files.lines(filePath)){
            stream.forEach(line -> {
                String[] words = line.split(" ");
                if (!isNumeric(words[0]) || words.length != 2) return;

                double temperature = Double.parseDouble(words[0]);
                double specificHeat = Double.parseDouble(words[1]);

                Pair<Double, Double> pair = new Pair<>(temperature, specificHeat);
                pairs.add(pair);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pairs;
    }

    private boolean isNumeric(String string) {
        return Pattern.matches("([0-9]*)\\.([0-9]*)", string);
    }
}
