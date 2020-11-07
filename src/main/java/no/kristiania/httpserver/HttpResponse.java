package no.kristiania.httpserver;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse extends HttpMessage{
    private String startLine;
    private Map<String, String> headers;
    private String body;

    public HttpResponse(Socket socket) throws IOException {
        startLine = readLine(socket);
        headers = readHeaders(socket);

        String contentLength = headers.get("Content-Length");
        if (contentLength != null) {
            body = readBody(socket, Integer.parseInt(contentLength));
        } else {
            body = null;
        }
    }


    public HttpResponse(String statusCodeAndMessage){
        startLine = "HTTP/1.1 " + statusCodeAndMessage;
        headers = new HashMap<>();
        headers.put("Content-Length", "0");
        headers.put("Connection", "close");
    }
    public HttpResponse(String statusCodeAndMessage, String body){
            startLine = "HTTP/1.1 " + statusCodeAndMessage;
            headers = new HashMap<>();
            headers.put("Content-Length", String.valueOf(body.length()));
            headers.put("Connection", "close");
            this.body = body;
    }

    public void redirect(String redirLocation){
        startLine = "HTTP/1.1 302 Redirect";
        headers.put("Location", "/" + redirLocation);
    }
    public void setContentType(String contentType){
        headers.put("Content-Type", contentType);
    }


    /*public HttpResponse() {
        headers = new HashMap<>();
        this.body = null;
    }*/

    public void write(Socket clientSocket) throws IOException {
        clientSocket.getOutputStream().write((startLine + "\r\n").getBytes());
        for (String headerName : headers.keySet()) {
            clientSocket.getOutputStream().write((headerName + ": " + headers.get(headerName) + "\r\n").getBytes());
        }
        clientSocket.getOutputStream().write(("\r\n").getBytes());
        if (body != null) {
            clientSocket.getOutputStream().write(body.getBytes());
        }
    }
}
