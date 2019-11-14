package com.nodeDiagnosticInfo.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/*
DTO for NodeDiagnosticData
There is field for every data point present in CordApp Info
*/
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString

public class NodeDiagnosticData {

    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("version")
    private String version;

    @JsonProperty("revision")
    private String revision;

    @JsonProperty("platformVersion")
    private String platformVersion;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("cordapps")
    private List<CorDappInfo> cordapps;

    @JsonProperty("Id")
    private String Id;

	public String getId() {
        return Id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getRevision() {
        return revision;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public String getVendor() {
        return vendor;
    }

    public List<CorDappInfo> getCordapps() {
        return cordapps;
    }

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void setId(String id) {
        Id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setCordapps(List<CorDappInfo> cordapps) {
        this.cordapps = cordapps;
    }

    /**
     * De-serialises NodeDiagnosticsInfo from a json payload
     *
     * @param json the json payload to be deserialised
     * @return the equivalent NodeDiagnostics object
     * <p>
     * Note: method throws an IllegalArgumentException, if the json was not of the right format
     */
    public static NodeDiagnosticData FromJson(final String json) {
        try {
            System.out.println(json);
            NodeDiagnosticData datav1 = OBJECT_MAPPER.readValue(json, NodeDiagnosticData.class);
            return datav1;
        } catch (JsonParseException | JsonMappingException e) {
            throw new IllegalArgumentException("Info de-serialisation failed: " + json, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialises NodeDiagnosticInfo to a json payload
     *
     * @return the equivalent json string
     */
    public String ToJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

