package no.kristiania.httpserver.messages;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    private String startLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpMessage(Socket socket) throws IOException {
        startLine = readLine(socket);
        headers = readHeaders(socket);

        String contentLength = headers.get("Content-Length");
        if (contentLength != null) {
            body = readBody(socket, Integer.parseInt(contentLength));
        } else {
            body = null;
        }
    }

    public static String readLine(Socket socket) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            if (c == '\r') {
                socket.getInputStream().read();
                break;
            }
            line.append((char) c);
        }
        return line.toString();
    }

    static String readBody(Socket socket, int contentLength) throws IOException {
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) socket.getInputStream().read());
        }
        String bodyDecoded = URLDecoder.decode(body.toString(), StandardCharsets.UTF_8);
        return bodyDecoded;
    }

    static Map<String, String> readHeaders(Socket socket) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = readLine(socket)).isEmpty()) {
            int colonPos = headerLine.indexOf(':');
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();

            headers.put(headerName, headerValue);
        }
        return headers;
    }


    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}