package com.nodeDiagnosticInfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
DTO for Cordapp info
*/
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString

public class CorDappInfo {

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("shortName")
    private String shortName;

    @JsonProperty("targetPlatformVersion")
    private String targetPlatformVersion;

    @JsonProperty("minimumPlatformVersion")
    private String minimumPlatformVersion;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("version")
    private String version;

    @JsonProperty("licence")
    private String licence;

    @JsonProperty("jarHash")
    private JarHash jar;


    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setTargetPlatformVersion(String targetPlatformVersion) {
        this.targetPlatformVersion = targetPlatformVersion;
    }

    public void setMinimumPlatformVersion(String minimumPlatformVersion) {
        this.minimumPlatformVersion = minimumPlatformVersion;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setLicense(String licence) {
        this.licence = licence;
    }

    public void setJar(JarHash jar) {
        this.jar = jar;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

}
