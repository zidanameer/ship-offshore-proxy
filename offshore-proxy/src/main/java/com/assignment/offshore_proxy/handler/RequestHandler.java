package com.assignment.offshore_proxy.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
@Component
public class RequestHandler {
    @Value("${default.target.port:80}")
    private int targetPort;

    public byte[] handle(byte[] rawRequest) throws IOException {
        try (Socket targetSocket = createTargetSocket(rawRequest)) {
            OutputStream targetOut = targetSocket.getOutputStream();
            targetOut.write(rawRequest);
            targetOut.flush();

            InputStream targetIn = targetSocket.getInputStream();
            ByteArrayOutputStream response = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;

            while ((read = targetIn.read(buffer)) != -1) {
                response.write(buffer, 0, read);
                if (read < 4096) break;
            }

            return response.toByteArray();
        }
    }

    private Socket createTargetSocket(byte[] rawRequest) throws IOException {
        ByteArrayInputStream requestStream = new ByteArrayInputStream(rawRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestStream));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) throw new IOException("Invalid request");

        String host = null;
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            if (line.toLowerCase().startsWith("host:")) {
                host = line.substring(5).trim();
                break;
            }
        }

        if (host == null) throw new IOException("Host header missing");

        return new Socket(host, targetPort); // Only HTTP
    }
}
