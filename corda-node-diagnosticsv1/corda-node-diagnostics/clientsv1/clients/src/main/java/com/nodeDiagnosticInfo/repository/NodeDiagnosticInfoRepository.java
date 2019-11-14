package com.nodeDiagnosticInfo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.nodeDiagnosticInfo.model.NodeDiagnosticData;
import com.nodeDiagnosticInfo.repository.dynamodb.DynamoDBNodeDiagnosticInfoItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NodeDiagnosticInfoRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public NodeDiagnosticInfoRepository(final DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Optional<NodeDiagnosticData> getInfo(final String nodeDiagnosticInfoIdentifier) {
        DynamoDBNodeDiagnosticInfoItem nodeDiagnosticItem = dynamoDBMapper.load(DynamoDBNodeDiagnosticInfoItem.class, nodeDiagnosticInfoIdentifier);

        if (nodeDiagnosticItem == null) {
            return Optional.empty();
        }

        return Optional.of(nodeDiagnosticItem.toInfo());
    }

    public void storeInfo(final NodeDiagnosticData info) {
        final DynamoDBNodeDiagnosticInfoItem dynamoDBNodeInfoItem = DynamoDBNodeDiagnosticInfoItem.fromInfo(info);
        final DynamoDBSaveExpression dynamoDBSaveExpression = getInfoIdDoesNotExistExpression();

        try {
            dynamoDBMapper.save(dynamoDBNodeInfoItem, dynamoDBSaveExpression);
        } catch (ConditionalCheckFailedException e) {
            throw new InfoIdentifierExistsException(info.getId());
        }
    }

    private DynamoDBSaveExpression getInfoIdDoesNotExistExpression() {
        DynamoDBSaveExpression samePartitionIdExistsExpression = new DynamoDBSaveExpression();
        Map<String, ExpectedAttributeValue> expected = new HashMap<>();
        expected.put("id", new ExpectedAttributeValue(false));
        samePartitionIdExistsExpression.setExpected(expected);

        return samePartitionIdExistsExpression;
    }
    
   
}
