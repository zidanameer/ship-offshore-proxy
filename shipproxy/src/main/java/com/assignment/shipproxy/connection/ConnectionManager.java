package com.assignment.shipproxy.connection;

import com.assignment.shipproxy.model.ProxyRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
@Slf4j
@Component
public class ConnectionManager {
    @Value("${offshore.host:localhost}")
    private String offshoreHost;

    @Value("${offshore.port:9000}")
    private int offshorePort;
    private Socket socket;
    private OutputStream out;
    private InputStream in;

    private final BlockingQueue<ProxyRequest> requestQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<byte[]> responseQueue = new LinkedBlockingQueue<>();

    private boolean connected = false;

    private synchronized void connectIfNeeded() throws IOException {
        if (!connected) {
            while (true) {
                try {
                    socket = new Socket(offshoreHost, offshorePort);
                    out = socket.getOutputStream();
                    in = socket.getInputStream();
                    connected = true;
                    break;
                } catch (IOException e) {
                    log.error("Failed to connect to offshore proxy. Retrying");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {}
                }
            }

            new Thread(() -> {
                while (true) {
                    try {
                        ProxyRequest request = requestQueue.take();
                        synchronized (out) {
                            out.write(intToBytes(request.getRawHttpRequest().length));
                            out.write(request.getRawHttpRequest());
                            out.flush();

                            byte[] sizeBytes = in.readNBytes(4);
                            int size = bytesToInt(sizeBytes);
                            byte[] response = in.readNBytes(size);
                            responseQueue.put(response);
                        }
                    } catch (Exception e) {
                        log.debug(e.toString());
                    }
                }
            }).start();
        }
    }
    public byte[] sendRequest(ProxyRequest request) throws InterruptedException, IOException {
        connectIfNeeded();
        requestQueue.put(request);
        return responseQueue.take();
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
