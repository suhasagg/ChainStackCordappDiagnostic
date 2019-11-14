package com.nodeDiagnosticInfo.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.nodeDiagnosticInfo.model.NodeDiagnosticData;
import com.nodeDiagnosticInfo.repository.NodeDiagnosticInfoRepository;
import com.nodeDiagnosticInfo.model.CorDappInfo;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Date;

@DynamoDBTable(tableName = "nodediagnosticinfo")
@Getter
@Setter
public class DynamoDBNodeDiagnosticInfoItem {

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public List<CorDappInfo> getCordapps() {
		return cordapps;
	}

	public void setCordapps(List<CorDappInfo> cordapps) {
		this.cordapps = cordapps;
	}

	@DynamoDBHashKey(attributeName = "id")
    private String Id;

    @DynamoDBAttribute(attributeName = "nodeId")
    private String nodeId;

    @DynamoDBAttribute(attributeName = "timestamp")
    private String timestamp;

    @DynamoDBAttribute(attributeName = "version")
    private String version;

    @DynamoDBAttribute(attributeName = "revision")
    private String revision;

    @DynamoDBAttribute(attributeName = "platformVersion")
    private String platformVersion;

    @DynamoDBAttribute(attributeName = "vendor")
    private String vendor;

    @DynamoDBAttribute(attributeName = "cordapps")
    private List<CorDappInfo> cordapps;


    public static DynamoDBNodeDiagnosticInfoItem fromInfo(final NodeDiagnosticData nodeDiagnosticInfo) {
        final DynamoDBNodeDiagnosticInfoItem dynamoDBNodeInfoItem = new DynamoDBNodeDiagnosticInfoItem();
        dynamoDBNodeInfoItem.setId(nodeDiagnosticInfo.getId());
        dynamoDBNodeInfoItem.setNodeId(nodeDiagnosticInfo.getNodeId());
        dynamoDBNodeInfoItem.setPlatformVersion(nodeDiagnosticInfo.getPlatformVersion());
        dynamoDBNodeInfoItem.setRevision(nodeDiagnosticInfo.getRevision());
        dynamoDBNodeInfoItem.setTimestamp(new Date().toString());
        dynamoDBNodeInfoItem.setVendor(nodeDiagnosticInfo.getVendor());
        dynamoDBNodeInfoItem.setVersion(nodeDiagnosticInfo.getVersion());
      
        return dynamoDBNodeInfoItem;
    }
    
    
    public NodeDiagnosticData toInfo() {
        return new NodeDiagnosticData();
    }
                
    
}
