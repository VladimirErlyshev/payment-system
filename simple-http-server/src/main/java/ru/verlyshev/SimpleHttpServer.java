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
    private static final String STATIC_DIR = "static";
    private static final Path STATIC_PATH = Paths.get(STATIC_DIR).toAbsolutePath();

    public static void main(String[] args) throws IOException {
        try (var serverSocket = new ServerSocket(PORT)) {
            getConnection(serverSocket);
        }
    }

    private static void getConnection(ServerSocket serverSocket) throws IOException {
        while (true) {
            try (var connection = serverSocket.accept()) {
                var requestedFile = readRequest(connection);
                writeResponse(connection, requestedFile);
            }
        }
    }

    private static String readRequest(Socket connection) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        var requestedFile = "";

        String requestLine = bufferedReader.readLine();
        if (requestLine != null) {
            System.out.println("Request Line: " + requestLine);
            requestedFile = extractFileFromRequestLine(requestLine);
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                System.out.println("Header: " + line);
            }

            System.out.println("Requested file: " + requestedFile);
            System.out.println();
        }

        return requestedFile;
    }

    private static String extractFileFromRequestLine(String requestLine) {
        StringTokenizer tokenizer = new StringTokenizer(requestLine);

        if (tokenizer.countTokens() >= 2) {
            String method = tokenizer.nextToken();
            String urlPath = tokenizer.nextToken();

            System.out.println("HTTP method: " + method);
            System.out.println("URL: " + urlPath);

            return extractLastSegment(urlPath);
        }

        return "";
    }

    private static String extractLastSegment(String urlPath) {
        if (urlPath.equals("/") || urlPath.isEmpty()) {
            return "";
        }

        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1);
        }

        int lastSlashIndex = urlPath.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return urlPath.substring(lastSlashIndex + 1);
        }

        return urlPath;
    }

    private static void writeResponse(Socket connection, String requestedFile) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        var filePath = STATIC_PATH.resolve(requestedFile);

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            sendFileContent(writer, filePath, requestedFile);
        } else {
            send404Response(writer, requestedFile);
        }
    }

    private static void sendFileContent(BufferedWriter writer, Path filePath, String fileName) throws IOException {
        var fileContent = Files.readString(filePath);
        var contentType = getContentType(fileName);
        var fileSizeInBytes = Files.size(filePath);
        writer.write("""
                HTTP/1.1 200 OK
                Content-Type: %s; charset=UTF-8
                Content-Length: %s
                
                %s
                """.formatted(contentType, fileSizeInBytes, fileContent));

        System.out.println("File served successfully: " + fileName + " (" + fileSizeInBytes + " bytes)");
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
                Content-Length: %s
                Server: SimpleHttpServer/1.0
                
                %s
                """.formatted(notFoundHtml.length(), notFoundHtml));

        System.out.println("✗ File not found: " + requestedFile);
        writer.flush();
    }

    private static String getContentType(String fileName) {
        String lowerCase = fileName.toLowerCase();

        if (lowerCase.endsWith(".html") || lowerCase.endsWith(".htm")) {
            return "text/html";
        } else if (lowerCase.endsWith(".css")) {
            return "text/css";
        } else if (lowerCase.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerCase.endsWith(".json")) {
            return "application/json";
        } else if (lowerCase.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerCase.endsWith(".png")) {
            return "image/png";
        } else if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCase.endsWith(".gif")) {
            return "image/gif";
        }

        return "application/octet-stream";
    }
}
