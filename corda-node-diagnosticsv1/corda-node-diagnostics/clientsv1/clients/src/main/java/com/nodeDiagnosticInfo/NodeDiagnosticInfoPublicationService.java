package com.nodeDiagnosticInfo;

import com.nodeDiagnosticInfo.events.PublicationNotifier;
import com.nodeDiagnosticInfo.model.NodeDiagnosticData;
import com.nodeDiagnosticInfo.repository.NodeDiagnosticInfoRepository;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class NodeDiagnosticInfoPublicationService {

    private NodeDiagnosticInfoRepository nodeDiagnosticInfoRepository;

    private PublicationNotifier publicationNotifier;

    /**
     * Retrieve a published NodeDiagonsticInfo by the given identifier
     * @param infoIdentifier the identifier of the info
     * @return the published NodeDiagonsticInfo with that identifier, if exists
     *          otherwise an empty optional.
     */
    public Optional<NodeDiagnosticData> getNodeDiagnosticData(final String infoIdentifier) {
        return nodeDiagnosticInfoRepository.getInfo(infoIdentifier);
    }

    /**
     * Publishes the NodeDiagonsticInfo, which consists of the following processes:
     * - stores the NodeDiagonsticInfo in the datastore
     * - publishes an event for the published NodeDiagonsticInfo
     * @param info the newly published NodeDiagonsticInfo
     */
    public void publishNodeDiagnosticData(final NodeDiagnosticData info) {
        nodeDiagnosticInfoRepository.storeInfo(info);
        publicationNotifier.onNodeDiagnosticInfoPublished(info);
    }
}
