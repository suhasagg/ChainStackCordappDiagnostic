package com.nodeDiagnosticInfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
DTO for jarhash present in Cordapp info
*/

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString

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
