package com.rosspaffett.mattercraft.matterbridge;

import com.rosspaffett.mattercraft.ChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MatterbridgeApiClient {
    private static final String JSON_MIME_TYPE = "application/json";
    private static final Logger LOGGER = LogManager.getLogger();
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

    public void sendChatMessage(ChatMessage message) throws IOException {
        HttpURLConnection connection = sendChatMessageConnection();
        String requestBody = new SendMessageRequestBody(this.gateway, message.getUsername(),
            message.getBody()).toJson();

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBodyBytes = requestBody.getBytes("UTF-8");
            outputStream.write(requestBodyBytes, 0, requestBody.length());
        }

        LOGGER.debug("> POST " + connection.getURL() + ": " + requestBody);
        LOGGER.debug("< HTTP " + connection.getResponseCode());
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
}
