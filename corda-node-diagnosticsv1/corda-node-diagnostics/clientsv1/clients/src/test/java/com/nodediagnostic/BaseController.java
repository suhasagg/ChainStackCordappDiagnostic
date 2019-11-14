package com.nodediagnostic.controller;

import com.nodediagnostic.grpc.catalog.nodeDiagnosticDataIdentifier;
import com.nodediagnostic.monitoring.cloudwatch.CloudwatchMetricsEmitter;
import com.nodediagnostic.nodeDiagnosticDatas.nodeDiagnosticDataPublicationService;
import com.nodediagnostic.NodeDiagnosticInfo.model.NodeDiagnosticData;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Date;
import java.util.Optional;

@Controller
public class BaseController extends com.nodediagnostic.grpc.catalog.CatalogServiceGrpc.CatalogServiceImplBase {

    private static final String SERVICE_NAMESPACE = "NodeDiagnosticInfoService";

    private static final String ENDPOINT_LATENCY_METRIC_NAME_TEMPLATE = "%s-Latency";
    private static final String ENDPOINT_REQUESTS_METRIC_NAME_TEMPLATE = "%s-Requests";
    private static final String NodeDiagnosticInfo_EXISTS_METRIC_NAME = "nodeDiagnosticDataExists";

    private static final String GET_NodeDiagnosticInfo_ENDPOINT_NAME = "GetNodeDiagnosticInfo";
    private static final String PUBLISH_NodeDiagnosticInfo_ENDPOINT_NAME = "PublishNodeDiagnosticInfo";

    @Autowired
    private NodeDiagnosticInfoPublicationService NodeDiagnosticInfoPublicationService;

    @Autowired
    private CloudwatchMetricsEmitter metricsEmitter;

    /**
     * Method used to start the gRPC server.
     * @throws IOException
     */
    public void init() throws IOException {
        Server server = NettyServerBuilder
                .forAddress(new InetSocketAddress("127.0.0.1", 9001))
                .addService(this)
                .build();
        server.start();
    }

    @RequestMapping(value = "health-check",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> healthCheck(ModelMap model) {
        return new ResponseEntity<>("I'm alive", HttpStatus.OK);
    }

    @RequestMapping(value = {"/getNodeDiagnosticData"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getnodeDiagnosticData(ModelMap model) {
        emitEndpointRequest(GET_NodeDiagnosticInfo_ENDPOINT_NAME);
        final long startTimestamp = System.currentTimeMillis();

        final Optional<NodeDiagnosticInfo> nodeDiagnosticData = nodeDiagnosticDataPublicationService.getnodeDiagnosticData(nodeDiagnosticDataIdentifier);
        if (!nodeDiagnosticData.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        final String jsonResponse = nodeDiagnosticData.get().toJson();

        final long endTimestamp = System.currentTimeMillis();
        emitLatencyMetric(GET_NodeDiagnosticInfo_ENDPOINT_NAME, endTimestamp - startTimestamp);

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @RequestMapping(value = {"/publishNodeDiagnosticData"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> publishnodeDiagnosticData(@RequestBody String jsonPayload) {
        emitEndpointRequest(PUBLISH_NodeDiagnosticInfo_ENDPOINT_NAME);
        final long startTimestamp = System.currentTimeMillis();
        try {
            final NodeDiagnosticData nodeDiagnosticData = NodeDiagnosticData.fromJson(jsonPayload);
            nodeDiagnosticDataPublicationService.publishnodeDiagnosticData(nodeDiagnosticData);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch(nodeDiagnosticDataIdentifierExistsException e) {
            metricsEmitter.emitMetric(SERVICE_NAMESPACE, nodeDiagnosticData_EXISTS_METRIC_NAME, 1.0);
            final String jsonMessage = String.format("{ message: \"%s\" }", e.getMessage());
            return new ResponseEntity<>(jsonMessage, HttpStatus.BAD_REQUEST);
        } finally {
            final long endTimestamp = System.currentTimeMillis();
            emitLatencyMetric(PUBLISH_nodeDiagnosticData_ENDPOINT_NAME, endTimestamp - startTimestamp);
        }
    }

    /* RPC calls */

    @Override
    public void publishnodeDiagnosticData(final com.nodediagnostic.NodeDiagnosticInfo.model.NodeDiagnosticData requestnodeDiagnosticData,
                            final StreamObserver<com.nodediagnostic.grpc.catalog.Empty> responseObserver) {
        final NodeDiagnosticData nodeDiagnosticData = new nodeDiagnosticData(
                requestnodeDiagnosticData.getId(),
                requestnodeDiagnosticData.getAuthorId(),
                new Date(requestnodeDiagnosticData.getReleaseDate()),
                requestnodeDiagnosticData.getDurationInSeconds(),
                requestnodeDiagnosticData.getArtifactUri());

        try {
            nodeDiagnosticDataPublicationService.publishnodeDiagnosticData(nodeDiagnosticData);

            responseObserver.onCompleted();
        } catch(nodeDiagnosticDataIdentifierExistsException exception) {
            responseObserver.onError(exception);
        }
    }

    private void emitLatencyMetric(final String endpointName, final long value) {
        final String metricName = String.format(ENDPOINT_LATENCY_METRIC_NAME_TEMPLATE, endpointName);
        metricsEmitter.emitMetric(SERVICE_NAMESPACE, metricName, value);
    }

    private void emitEndpointRequest(final String endpointName) {
        final String metricName = String.format(ENDPOINT_REQUESTS_METRIC_NAME_TEMPLATE, endpointName);
        metricsEmitter.emitMetric(SERVICE_NAMESPACE, metricName, 1.0);
    }

}