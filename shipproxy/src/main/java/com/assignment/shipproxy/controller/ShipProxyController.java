package com.assignment.shipproxy.controller;

import com.assignment.shipproxy.service.ShipProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ShipProxyController {

    private final ShipProxyService proxyService;

    @RequestMapping("/**")
    public ResponseEntity<byte[]> handle(HttpServletRequest request, @RequestBody(required = false) byte[] body) throws IOException, InterruptedException {
        byte[] response = proxyService.forwardRequest(request, body);
        return ResponseEntity.ok().body(response);
    }
}
