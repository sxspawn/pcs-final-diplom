package ru.kovbasa.pcspdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("localhost", 8989)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String request = "бизнес";

            out.write(request + "\n");
            out.flush();

            String response = in.readLine();
            System.out.println(response);
            System.out.println();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}