package com.assignment.shipproxy.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProxyRequest {
    private byte[] rawHttpRequest;
}
