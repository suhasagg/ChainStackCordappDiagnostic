package com.nodeDiagnosticInfo.events;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.nodeDiagnosticInfo.model.NodeDiagnosticData;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PublicationNotifier {

    private AmazonSQS sqsClient;

    private String queueName;

    /**
     * Persists a publication event for a newly published node Diagnostic Info
     * @param newNodeDiagnosticData the new NodeDiagnosticData that was just published.
     */
    public void onNodeDiagnosticInfoPublished(final NodeDiagnosticData nodeDiagnosticInfo) {
        final SendMessageRequest messageRequest =
                new SendMessageRequest(queueName, nodeDiagnosticInfo.ToJson());
        sqsClient.sendMessage(messageRequest);
    }



}
