package com.assignment.shipproxy.service;

import com.assignment.shipproxy.connection.ConnectionManager;
import com.assignment.shipproxy.model.ProxyRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
@Service
@RequiredArgsConstructor
public class ShipProxyService {
    @Autowired
    private ConnectionManager connectionManager;

    public byte[] forwardRequest(HttpServletRequest request, byte[] body) throws IOException, InterruptedException {
        ByteArrayOutputStream rawRequest = new ByteArrayOutputStream();

        rawRequest.write((request.getMethod() + " " + request.getRequestURI() + " HTTP/1.1\r\n").getBytes());

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            rawRequest.write((name + ": " + request.getHeader(name) + "\r\n").getBytes());
        }
        rawRequest.write("\r\n".getBytes());

        if (body != null) rawRequest.write(body);

        ProxyRequest proxyRequest = new ProxyRequest(rawRequest.toByteArray());

        return connectionManager.sendRequest(proxyRequest);
    }
}
