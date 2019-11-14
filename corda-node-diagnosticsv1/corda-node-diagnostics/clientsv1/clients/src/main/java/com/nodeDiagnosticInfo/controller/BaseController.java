package com.nodeDiagnosticInfo.controller;

import com.nodeDiagnosticInfo.NodeDiagnosticInfoPublicationService;
import com.nodeDiagnosticInfo.model.NodeDiagnosticData;
import com.nodeDiagnosticInfo.repository.InfoIdentifierExistsException;
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
public class BaseController extends com.grpc.DiagnosticInfoServiceGrpc.DiagnosticInfoServiceImplBase {

    private static final String SERVICE_NAMESPACE = "DiagnosticInfoService";

    private static final String ENDPOINT_LATENCY_METRIC_NAME_TEMPLATE = "%s-Latency";
    private static final String ENDPOINT_REQUESTS_METRIC_NAME_TEMPLATE = "%s-Requests";
    private static final String INFO_EXISTS_METRIC_NAME = "InfoExists";

    private static final String GET_INFO_ENDPOINT_NAME = "getDiagnosticinfo";
    private static final String PUBLISH_INFO_ENDPOINT_NAME = "PublishDiagnosticInfo";

    @Autowired
    private NodeDiagnosticInfoPublicationService infoPublicationService;

    @Autowired
    private com.nodeDiagnosticInfo.monitoring.cloudwatch.CloudwatchMetricsEmitter metricsEmitter;

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

    @RequestMapping(value = "getNodeDiagnosticInfo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNodeDiagnosticInfo(ModelMap model) {
        emitEndpointRequest(GET_INFO_ENDPOINT_NAME);
        final long startTimestamp = System.currentTimeMillis();

        final Optional<NodeDiagnosticData> nodediagnosticData = infoPublicationService.getNodeDiagnosticData("");
        if (!nodediagnosticData.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        final String jsonResponse = nodediagnosticData.toString();

        final long endTimestamp = System.currentTimeMillis();
        emitLatencyMetric(GET_INFO_ENDPOINT_NAME, endTimestamp - startTimestamp);

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @RequestMapping(value = {"/publishNodeDiagnosticInfo"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> publishNodeDiagnosticInfo(@RequestBody String jsonPayload) {
        emitEndpointRequest(PUBLISH_INFO_ENDPOINT_NAME);
        final long startTimestamp = System.currentTimeMillis();
        try {
            final  NodeDiagnosticData data = NodeDiagnosticData.FromJson(jsonPayload);
            infoPublicationService.publishNodeDiagnosticData(data);

            return new ResponseEntity<>(HttpStatusOK);
        } catch(InfoIdentifierExistsException e) {
            metricsEmitter.emitMetric(SERVICE_NAMESPACE, INFO_EXISTS_METRIC_NAME, 1.0);
            final String jsonMessage = String.format("{ message: \"%s\" }", e.getMessage());
            return new ResponseEntity<>(jsonMessage, HttpStatus.BAD_REQUEST);
        } finally {
            final long endTimestamp = System.currentTimeMillis();
            emitLatencyMetric(PUBLISH_INFO_ENDPOINT_NAME, endTimestamp - startTimestamp);
        }
    }

    /* RPC calls */

    @Override
    public void getNodeDiagnosticInfo(final infoIdentifier request,
                                      final StreamObserver<com.grpc.DiagnosticInfoServiceGrpc.NodeDiagnosticData> responseObserver) {
        final Optional<NodeDiagnosticData> optionaldata = infoPublicationService.getNodeDiagnosticData(request.getId());
        if (!optionaldata.isPresent()) {
            responseObserver.onError(new IllegalArgumentException("No data with identifier: " + request.getId()));
        }

        final NodeDiagnosticData data = optionaldata.get();
        final com.grpc.DiagnosticInfoServiceGrpc.NodeDiagnosticData protoData = com.grpc.DiagnosticInfoServiceGrpc.NodeDiagnosticData.newBuilder()
                .setId(data.getId())
                .setNodeId(data.getNodeId())
                .setVendor(data.getVendor())
                .setVersion(data.getVersion())
                .build();

        responseObserver.onNext(protoData);
        responseObserver.onCompleted();
    }

    @Override
    public void publishNodeDiagnosticInfo(final com.grpc.DiagnosticInfoServiceGrpc.NodeDiagnosticData requestData,
                                          final StreamObserver<com.grpc.DiagnosticInfoServiceGrpc.NodeDiagnosticData> responseObserver) {
        final NodeDiagnosticData data = new NodeDiagnosticData();
        try {
            infoPublicationService.publishNodeDiagnosticData(data);

            responseObserver.onCompleted();
        } catch(InfoIdentifierExistsException exception) {
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