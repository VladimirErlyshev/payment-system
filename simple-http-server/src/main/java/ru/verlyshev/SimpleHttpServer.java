package ru.verlyshev;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class SimpleHttpServer {
    private static final int PORT = 8080;
    private static final String STATIC_DIR = "simple-http-server/static";
    private static final Path STATIC_PATH = Paths.get(STATIC_DIR).toAbsolutePath();

    public static void main(String[] args) throws IOException {
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start at http://localhost:8080");

            setConnection(serverSocket);
        }
    }

    private static void setConnection(ServerSocket serverSocket) throws IOException {
        while (true) {
            try (var connection = serverSocket.accept()) {
                var requestedFile = "";
                try {
                    requestedFile = getFilePathFromRequest(connection);
                } catch (Exception e) {
                    System.out.printf("Error: %s%n", e.getMessage());
                }
                var filePath = STATIC_PATH.resolve(requestedFile);
                var fileContent = readFileContent(filePath);
                writeResponse(connection, fileContent, filePath, requestedFile);
            }
        }
    }

    private static String getFilePathFromRequest(Socket connection) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        var filePath = "";

        var startLine = bufferedReader.readLine();
        if (startLine != null) {
            System.out.println("Start Line: " + startLine);
            filePath = extractFileNameFromRequestLine(startLine);

            System.out.println("Requested file: " + filePath);
            System.out.println();

            return filePath;
        }

        throw new IllegalStateException("Request start line is invalid");
    }

    private static String extractFileNameFromRequestLine(String requestLine) {
        StringTokenizer tokenizer = new StringTokenizer(requestLine);

        if (tokenizer.countTokens() >= 2) {
            String method = tokenizer.nextToken();
            String urlPath = tokenizer.nextToken();

            System.out.println("HTTP method: " + method);
            System.out.println("URL: " + urlPath);

            return extractLastSegment(urlPath);
        }

        throw new IllegalArgumentException("Bad request");
    }

    private static String extractLastSegment(String urlPath) {
        var separator = "/";

        int lastSlashIndex = urlPath.lastIndexOf(separator);
        if (lastSlashIndex != -1) {
            return urlPath.substring(lastSlashIndex + 1);
        }

        throw new IllegalArgumentException("URL path is invalid");
    }

    private static void writeResponse(Socket connection, String fileContent, Path filePath, String requestedFile) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        if (fileContent != null) {
            sendSuccessResponse(writer, fileContent, filePath, requestedFile);
        } else {
            send404Response(writer, requestedFile);
        }
    }

    private static String readFileContent(Path filePath) throws IOException {
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            return Files.readString(filePath);
        } else {
            return null;
        }
    }

    private static void sendSuccessResponse(BufferedWriter writer, String fileContent, Path filePath, String fileName) throws IOException {
        var contentType = getContentType(fileName);
        var fileSizeInBytes = Files.size(filePath);

        writer.write("""
                HTTP/1.1 200 OK
                Content-Type: %s; charset=UTF-8
                Content-Length: %s
                
                %s
                """.formatted(contentType, fileSizeInBytes, fileContent));

        System.out.printf("File served successfully: %s (%d bytes)%n", fileName, fileSizeInBytes);
        writer.flush();
    }

    private static void send404Response(BufferedWriter writer, String requestedFile) throws IOException {
        String notFoundHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>404 - File Not Found</title>
                </head>
                <body>
                    <h1 class="error">404 - File Not Found</h1>
                </body>
                </html>
                """;

        writer.write("""
                HTTP/1.1 404 Not Found
                Content-Type: text/html; charset=UTF-8
                Server: SimpleHttpServer/1.0
                
                %s
                """.formatted(notFoundHtml));

        System.out.println("✗ File not found: " + requestedFile);
        writer.flush();
    }

    private static String getContentType(String fileName) {
        var lowerCase = fileName.toLowerCase();

        if (lowerCase.endsWith(".html") || lowerCase.endsWith(".htm")) {
            return "text/html";
        } else if (lowerCase.endsWith(".css")) {
            return "text/css";
        } else if (lowerCase.endsWith(".json")) {
            return "application/json";
        }
        return "application/octet-stream";
    }
}

