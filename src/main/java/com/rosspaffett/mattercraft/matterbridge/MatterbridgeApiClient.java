package com.rosspaffett.mattercraft.matterbridge;

import com.google.gson.Gson;
import com.rosspaffett.mattercraft.ChatMessage;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class MatterbridgeApiClient {
    private static final String GET_UNREAD_CHAT_MESSAGES_PATH = "/api/messages";
    public static final Gson GSON = new Gson();
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String SEND_CHAT_MESSAGE_PATH = "/api/message";

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

    public ChatMessage[] getUnreadChatMessages() throws IOException, MatterbridgeApiErrorException {
        HttpURLConnection connection = getUnreadChatMessagesConnection();
        String responseBody;

        if (connection.getResponseCode() >= 400) {
            responseBody = readLines(connection.getErrorStream());
            throw new MatterbridgeApiErrorException(connection.getResponseCode(), responseBody);
        }

        responseBody = readLines(connection.getInputStream());
        return GSON.fromJson(responseBody, (Type)ChatMessage[].class);
    }

    private HttpURLConnection getUnreadChatMessagesConnection() throws IOException {
        URL url = apiUrl(GET_UNREAD_CHAT_MESSAGES_PATH);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty("Accept", JSON_MIME_TYPE);
        connection.setRequestProperty("Authorization", authorizationHeader());

        return connection;
    }

    private String readLines(InputStream stream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    public void sendChatMessage(ChatMessage message) throws IOException {
        HttpURLConnection connection = sendChatMessageConnection();
        String requestBody = new SendMessageRequestBody(this.gateway, message.getUsername(),
            message.getText()).toJson();

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBodyBytes = requestBody.getBytes("UTF-8");
            outputStream.write(requestBodyBytes, 0, requestBody.length());
        }

        connection.disconnect();
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

    public class MatterbridgeApiErrorException extends Exception {
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
