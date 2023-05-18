package ru.kovbasa.pcspdf.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StopList {

    private final static String FILE_NAME = "stop-ru.txt";

    private static List<String> stopList;

    static {
        stopList = new ArrayList<>();
        stopList.add("");

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopList.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла категорий: " + e.getMessage());
        }
    }

    private StopList() {
    }

    public static List<String> getStopList() {
        return stopList;
    }
}
