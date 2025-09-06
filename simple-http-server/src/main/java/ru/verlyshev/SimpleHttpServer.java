package ru.verlyshev;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start at http://localhost:8080");

            getConnection(serverSocket);
        }
    }

    private static void getConnection(ServerSocket serverSocket) throws IOException {
        while (true) {
            try (var connection = serverSocket.accept()) {
                readRequest(connection);
                writeResponse(connection);
            }
        }
    }

    private static void readRequest(Socket connection) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            System.out.println(line);
        }

        System.out.println();

    }

    private static void writeResponse(Socket connection) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        var htmlContent = "<h1>Hello world from server</h1>";

        writer.write("""
                HTTP/1.1 200 OK
                Content-type: text/html; charset=UTF-8
                Content-Length: %s
                
                %s
                """.formatted(htmlContent.length(), htmlContent));
        writer.flush();
    }
}
