package no.kristiania.controllers;

import no.kristiania.httpserver.messages.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public interface HttpController {
    void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException;
}
