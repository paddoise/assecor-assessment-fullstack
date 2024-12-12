package org.example.API;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ResponseAndRCode {
    private final ObjectNode response;
    private final int rCode;

    public ResponseAndRCode(ObjectNode response, int rCode) {
        this.response = response;
        this.rCode = rCode;
    }

    public ObjectNode getResponse() {
        return response;
    }

    public int getRCode() {
        return rCode;
    }
}
