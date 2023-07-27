package com.rosspaffett.mattercraft;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class MatterbridgeApiClient {
    private static final int CONNECT_TIMEOUT = 30_000;
    private static final String JSON_MIME_TYPE = "application/json";
    public static final int READ_TIMEOUT = 5_000;
    private static final String SEND_CHAT_MESSAGE_PATH = "/api/message";
    private static final String STREAM_CHAT_MESSAGES_PATH = "/api/stream";

    private final String apiToken;
    private final String gateway;
    private final String baseUrl;

    public MatterbridgeApiClient(String baseUrl, String gateway, String apiToken) {
        this.apiToken = apiToken;
        this.gateway = gateway;
        this.baseUrl = baseUrl;
    }

    private URL apiUrl(String path) throws MalformedURLException {
        return new URL(this.baseUrl + path);
    }

    private String authorizationHeader() {
        return "Bearer " + this.apiToken;
    }

    private String readLines(InputStream stream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    public void sendChatMessage(ChatMessage message) throws IOException, MatterbridgeApiErrorException {
        HttpURLConnection connection = sendChatMessageConnection();
        String requestBody = new SendMessageRequestBody(this.gateway, message.getUsername(),
            message.getText()).toJson();

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBodyBytes, 0, requestBodyBytes.length);
        }

        if (connection.getResponseCode() >= 400) {
            String responseBody = readLines(connection.getErrorStream());
            throw new MatterbridgeApiErrorException(connection.getResponseCode(), responseBody);
        }
    }

    private HttpURLConnection sendChatMessageConnection() throws IOException {
        URL url = apiUrl(SEND_CHAT_MESSAGE_PATH);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Accept", JSON_MIME_TYPE);
        connection.setRequestProperty("Authorization", authorizationHeader());
        connection.setRequestProperty("Content-Type", JSON_MIME_TYPE);

        connection.setDoOutput(true);

        return connection;
    }

    public HttpURLConnection streamMessagesConnection() throws IOException {
        URL url = apiUrl(STREAM_CHAT_MESSAGES_PATH);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", JSON_MIME_TYPE);
        connection.setRequestProperty("Authorization", authorizationHeader());
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        return connection;
    }

    public static class MatterbridgeApiErrorException extends Exception {
        private final int responseCode;

        public MatterbridgeApiErrorException(int responseCode, String responseBody) {
            super(responseBody);
            this.responseCode = responseCode;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String toString() {
            return "Matterbridge responded with HTTP " + getResponseCode() + ": " + getMessage();
        }
    }
}
