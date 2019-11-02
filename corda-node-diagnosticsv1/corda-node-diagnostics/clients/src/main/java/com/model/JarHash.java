package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
DTO for jarhash present in Cordapp info
*/

public class JarHash {

    @JsonProperty("offset")
    private String offset;

    @JsonProperty("size")
    private String size;

    @JsonProperty("bytes")
    private String bytes;

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }
}
