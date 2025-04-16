package com.assignment.offshore_proxy.connection;

import com.assignment.offshore_proxy.handler.RequestHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
@Slf4j
@Component
@RequiredArgsConstructor
public class TcpConnection {
    @Value("${tcp.server.port:9010}")
    private int tcpPort;

    private final RequestHandler requestHandler;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
                log.info(String.format("Offshore TCP server started on port %s", tcpPort));

                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection from ship: " + clientSocket.getRemoteSocketAddress());

                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                while (true) {
                    byte[] sizeBytes = in.readNBytes(4);
                    if (sizeBytes.length < 4) break;

                    int size = bytesToInt(sizeBytes);
                    byte[] requestData = in.readNBytes(size);

                    byte[] responseData = requestHandler.handle(requestData);
                    out.write(intToBytes(responseData.length));
                    out.write(responseData);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >> 24), (byte) (value >> 16),
                (byte) (value >> 8), (byte) value
        };
    }

    private int bytesToInt(byte[] b) {
        return (b[0] & 0xFF) << 24 |
                (b[1] & 0xFF) << 16 |
                (b[2] & 0xFF) << 8 |
                (b[3] & 0xFF);
    }
}
