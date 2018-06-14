package edu.agh;

import edu.agh.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void saveToFile(File file, List<Pair<Double, Double>> list, String... titles) {
        List<String> lines = new ArrayList<>();

        String title = String.join(";", titles);
        lines.add(title);

        list.forEach(pair -> {
            lines.add(pair.getLeftValue() + ";" + pair.getRightValue());
        });

        try {
            Files.write(Paths.get(file.getPath()), lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isNumeric(String string) {
        return Pattern.matches("([0-9]*)\\.([0-9]*)", string);
    }
}
