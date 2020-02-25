package com.icm.sna.vo;

import lombok.Data;

@Data
public class Edge {
    private String origin;
    private String destination;

    public Edge(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }
}
