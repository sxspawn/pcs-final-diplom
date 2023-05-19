package ru.kovbasa.pcspdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import com.google.gson.Gson;

import ru.kovbasa.pcspdf.model.PageEntry;
import ru.kovbasa.pcspdf.search.BooleanSearchEngine;
import ru.kovbasa.pcspdf.search.SearchEngine;

public class Main {

    public static void main(String[] args) {
        SearchEngine searchEngine = new BooleanSearchEngine(new File("pdfs"));
        Gson gson = new Gson();

        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            System.out.println("Сервер запущен!");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String inputLine = in.readLine();
                    System.out.println("Request: '" + inputLine + "'");

                    Set<PageEntry> result = searchEngine.search(inputLine);

                    Writer out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    out.write(gson.toJson(result));
                    out.flush();
                } catch (Exception e) {
                    System.err.println("Ошибка при обработке запроса: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}